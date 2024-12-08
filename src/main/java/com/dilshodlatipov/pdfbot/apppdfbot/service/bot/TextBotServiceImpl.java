package com.dilshodlatipov.pdfbot.apppdfbot.service.bot;

import com.dilshodlatipov.pdfbot.apppdfbot.entity.PDFDocument;
import com.dilshodlatipov.pdfbot.apppdfbot.entity.UserEntity;
import com.dilshodlatipov.pdfbot.apppdfbot.enums.BotStatus;
import com.dilshodlatipov.pdfbot.apppdfbot.enums.DocumentStatus;
import com.dilshodlatipov.pdfbot.apppdfbot.enums.DocumentType;
import com.dilshodlatipov.pdfbot.apppdfbot.exceptions.RestException;
import com.dilshodlatipov.pdfbot.apppdfbot.payload.Order;
import com.dilshodlatipov.pdfbot.apppdfbot.repository.PDFDocumentRepository;
import com.dilshodlatipov.pdfbot.apppdfbot.service.FontService;
import com.dilshodlatipov.pdfbot.apppdfbot.service.MessageService;
import com.dilshodlatipov.pdfbot.apppdfbot.service.UserService;
import com.dilshodlatipov.pdfbot.apppdfbot.utils.RestConstants;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TextBotServiceImpl implements TextBotService {
    private final UserService userService;
    private final PDFDocumentRepository documentRepository;
    private final PDFServiceBot bot;
    private final AsyncConverter asyncConverter;
    private final FontService fontService;

    @Override
    public SendMessage start(Message message, UserEntity user) {
        PDFDocument document = PDFDocument.builder()
                .createdById(user.getId())
                .updatedById(user.getId())
                .status(DocumentStatus.DRAFT)
                .documentType(DocumentType.TEXT)
                .build();

        documentRepository.save(document);
        userService.setStatus(user, BotStatus.TEXT_CONVERT);

        return SendMessage.builder()
                .chatId(user.getTelegramId())
                .text(MessageService.message(user, RestConstants.TEXT_START))
                .build();
    }

    @Override
    public SendMessage addText(Message message, UserEntity user) {
        PDFDocument document = documentRepository.findFirstByCreatedByIdAndStatusOrderByCreatedAtDesc(user.getId(), DocumentStatus.DRAFT)
                .orElseThrow(() -> RestException.restThrow(MessageService.message(user, RestConstants.INVALID_INPUT), user.getTelegramId()));

        if (!message.hasText())
            throw RestException.restThrow(MessageService.message(user, RestConstants.INVALID_INPUT), user.getTelegramId());

        document.setDocumentText(message.getText());
        documentRepository.save(document);

        userService.setStatus(user, BotStatus.CHOOSING_FONT);
        return SendMessage.builder()
                .chatId(user.getTelegramId())
                .text(MessageService.message(user, RestConstants.TEXT_FONT))
                .replyMarkup(
                        ReplyKeyboardMarkup.builder()
                                .keyboardRow(new KeyboardRow(List.of(
                                        KeyboardButton.builder().text(MessageService.message(user, RestConstants.TEXT_SKIP)).build()
                                )))
                                .resizeKeyboard(true)
                                .oneTimeKeyboard(true)
                                .build()
                )
                .build();
    }

    @Override
    public SendMessage chooseFont(Message message, UserEntity user) {
        PDFDocument document = documentRepository.findFirstByCreatedByIdAndStatusOrderByCreatedAtDesc(user.getId(), DocumentStatus.DRAFT)
                .orElseThrow(() -> RestException.restThrow(MessageService.message(user, RestConstants.INVALID_INPUT), user.getTelegramId()));

        if (!message.hasText())
            throw RestException.restThrow(MessageService.message(user, RestConstants.INVALID_INPUT), user.getTelegramId());

        if (!Objects.equals(message.getText(), MessageService.message(user, RestConstants.TEXT_SKIP)) && !fontService.checkFont(message.getText()))
            throw RestException.restThrow(MessageService.message(user, RestConstants.INVALID_INPUT), user.getTelegramId());

        userService.setStatus(user, BotStatus.WAITING);

        document.setFont(message.getText());
        documentRepository.save(document);

        asyncConverter.processDocument(Order.builder().documentId(document.getId()).build(), user);
        return SendMessage.builder()
                .chatId(user.getTelegramId())
                .text(MessageService.message(user, RestConstants.TEXT_WAIT))
                .replyMarkup(
                        ReplyKeyboardRemove.builder()
                                .removeKeyboard(true)
                                .build())
                .build();
    }
}
