package com.dilshodlatipov.pdfbot.apppdfbot.service.bot;

import com.dilshodlatipov.pdfbot.apppdfbot.entity.Attachment;
import com.dilshodlatipov.pdfbot.apppdfbot.entity.PDFDocument;
import com.dilshodlatipov.pdfbot.apppdfbot.entity.UserEntity;
import com.dilshodlatipov.pdfbot.apppdfbot.enums.BotStatus;
import com.dilshodlatipov.pdfbot.apppdfbot.enums.DocumentStatus;
import com.dilshodlatipov.pdfbot.apppdfbot.enums.DocumentType;
import com.dilshodlatipov.pdfbot.apppdfbot.exceptions.RestException;
import com.dilshodlatipov.pdfbot.apppdfbot.payload.Order;
import com.dilshodlatipov.pdfbot.apppdfbot.repository.AttachmentRepository;
import com.dilshodlatipov.pdfbot.apppdfbot.repository.PDFDocumentRepository;
import com.dilshodlatipov.pdfbot.apppdfbot.service.MessageService;
import com.dilshodlatipov.pdfbot.apppdfbot.service.UserService;
import com.dilshodlatipov.pdfbot.apppdfbot.utils.RestConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageBotServiceImpl implements ImageBotService {
    private final UserService userService;
    private final PDFDocumentRepository documentRepository;
    private final AttachmentRepository attachmentRepository;
    private final AsyncConverter converter;

    @Override
    public SendMessage start(Message message, UserEntity user) {
        PDFDocument document = PDFDocument.builder()
                .createdById(user.getId())
                .documentType(DocumentType.IMAGES)
                .status(DocumentStatus.DRAFT)
                .build();

        documentRepository.save(document);
        userService.setStatus(user, BotStatus.IMAGES_ADDING);

        return SendMessage.builder()
                .chatId(user.getTelegramId())
                .text(MessageService.message(user, RestConstants.IMAGE_START))
                .replyMarkup(
                        ReplyKeyboardMarkup.builder()
                                .keyboard(List.of(
                                        new KeyboardRow(List.of(
                                                KeyboardButton.builder()
                                                        .text(MessageService.message(user, RestConstants.REMOVE_LAST_IMAGE))
                                                        .build()
                                        )),
                                        new KeyboardRow(List.of(
                                                KeyboardButton.builder()
                                                        .text(MessageService.message(user, RestConstants.TO_PDF))
                                                        .build()
                                        ))
                                ))
                                .resizeKeyboard(true)
                                .build())
                .build();
    }

    @Override
    public SendMessage addPhoto(Message message, UserEntity user) {
        PDFDocument document = documentRepository.findFirstByCreatedByIdAndStatusOrderByCreatedAtDesc(user.getId(), DocumentStatus.DRAFT)
                .orElseThrow(() -> RestException.restThrow(MessageService.message(user, RestConstants.INVALID_INPUT), user.getTelegramId()));

        if (message.hasText()) {
            return processText(message, user, document);
        } else if (!message.hasPhoto() && !message.hasDocument()) {
            throw RestException.restThrow(MessageService.message(user, RestConstants.IMAGE_START), user.getTelegramId());
        }

        Attachment attachment = null;
        if (message.hasPhoto()) {
            PhotoSize photo = message.getPhoto().get(message.getPhoto().size() - 1);
            attachment = Attachment.builder()
                    .pdfDocument(document)
                    .size(photo.getFileSize())
                    .telegramId(photo.getFileId())
                    .originalName(UUID.randomUUID().toString())
                    .contentType("image/jpg")
                    .build();
        } else if (message.hasDocument()) {
            Document photo = message.getDocument();
            if (!photo.getMimeType().matches(RestConstants.SUPPORTED_IMAGE_TYPES_REGEX))
                throw RestException.restThrow(MessageService.message(user, RestConstants.IMAGE_UNSUPPORTED), user.getTelegramId());
            attachment = Attachment.builder()
                    .pdfDocument(document)
                    .size(photo.getFileSize())
                    .telegramId(photo.getFileId())
                    .originalName(photo.getFileName())
                    .contentType(photo.getMimeType())
                    .build();
        }
        List<Attachment> attachments = document.getAttachments();
        long sum = attachments.stream().collect(Collectors.summarizingLong(Attachment::getSize)).getSum();
        assert attachment != null;
        if (sum + attachment.getSize() >= RestConstants.TWENTY_MB)
            throw RestException.restThrow(MessageService.message(user, RestConstants.MERGE_LIMIT), user.getTelegramId());
        attachment.setCreatedById(user.getId());
        attachmentRepository.save(attachment);
        return MessageService.getTextForReply(user, attachments, RestConstants.IMAGE_LIST, String.valueOf(attachments.size() + 1), ": ", attachment.getOriginalName(), ";\n");
    }

    private SendMessage processText(Message message, UserEntity user, PDFDocument document) {
        if (Objects.equals(message.getText(), MessageService.message(user, RestConstants.REMOVE_LAST_IMAGE))) {
            attachmentRepository.deleteLastOne(user.getId());
            List<Attachment> attachments = document.getAttachments();
            return MessageService.getTextForReply(user, attachments, RestConstants.IMAGE_LIST);
        } else if (Objects.equals(message.getText(), MessageService.message(user, RestConstants.TO_PDF))) {
            userService.setStatus(user, BotStatus.WAITING);

            converter.processDocument(new Order(document.getId()), user);

            return SendMessage.builder()
                    .chatId(user.getTelegramId())
                    .text(MessageService.message(user, RestConstants.IMAGE_WAIT))
                    .replyMarkup(
                            ReplyKeyboardRemove.builder()
                                    .removeKeyboard(true)
                                    .build())
                    .build();
        } else
            throw RestException.restThrow(MessageService.message(user, RestConstants.INVALID_INPUT), user.getTelegramId());
    }
}
