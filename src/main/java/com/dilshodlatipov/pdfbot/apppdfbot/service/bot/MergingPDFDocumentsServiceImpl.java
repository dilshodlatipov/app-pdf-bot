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
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.dilshodlatipov.pdfbot.apppdfbot.service.MessageService.getTextForReply;
import static com.dilshodlatipov.pdfbot.apppdfbot.utils.RestConstants.INVALID_INPUT;

@Service
@RequiredArgsConstructor
public class MergingPDFDocumentsServiceImpl implements MergingPDFDocumentsService {

    private final PDFDocumentRepository documentRepository;
    private final AttachmentRepository attachmentRepository;
    private final UserService userService;
    private final AsyncConverter converter;

    @Override
    public SendMessage start(Message message, UserEntity user) {
        PDFDocument document = PDFDocument.builder()
                .createdById(user.getId())
                .updatedById(user.getId())
                .status(DocumentStatus.DRAFT)
                .documentType(DocumentType.MERGE)
                .build();

        documentRepository.save(document);
        userService.setStatus(user, BotStatus.MERGE);

        return SendMessage.builder()
                .chatId(user.getTelegramId())
                .text(MessageService.message(user, RestConstants.MERGE_START))
                .replyMarkup(
                        ReplyKeyboardMarkup.builder()
                                .keyboard(List.of(
                                        new KeyboardRow(List.of(
                                                KeyboardButton.builder()
                                                        .text(MessageService.message(user, RestConstants.REMOVE_LAST_PDF))
                                                        .build()
                                        )),
                                        new KeyboardRow(List.of(
                                                KeyboardButton.builder()
                                                        .text(MessageService.message(user, RestConstants.MERGE_PDF))
                                                        .build()
                                        ))
                                ))
                                .resizeKeyboard(true)
                                .build())
                .build();
    }

    @Override
    public SendMessage addPDF(Message message, UserEntity user) {
        PDFDocument document = documentRepository.findFirstByCreatedByIdAndStatusOrderByCreatedAtDesc(user.getId(), DocumentStatus.DRAFT)
                .orElseThrow(() -> RestException.restThrow(MessageService.message(user, INVALID_INPUT), user.getTelegramId()));

        if (message.hasText()) {
            return processText(message, user, document);
        } else if (!message.hasDocument()) {
            throw RestException.restThrow(MessageService.message(user, INVALID_INPUT), user.getTelegramId());
        }

        Document pdfAttachment = message.getDocument();

        if (!pdfAttachment.getMimeType().matches(RestConstants.PDF_MIME_REGEX))
            throw RestException.restThrow(MessageService.message(user, INVALID_INPUT), user.getTelegramId());

        Attachment attachment = Attachment.builder()
                .pdfDocument(document)
                .size(pdfAttachment.getFileSize())
                .telegramId(pdfAttachment.getFileId())
                .originalName(UUID.randomUUID().toString())
                .contentType("application/pdf")
                .build();

        List<Attachment> attachments = document.getAttachments();

        long sum = attachments.stream().collect(Collectors.summarizingLong(Attachment::getSize)).getSum();
        if (sum + attachment.getSize() >= RestConstants.TWENTY_MB)
            throw RestException.restThrow(MessageService.message(user, RestConstants.MERGE_LIMIT), user.getTelegramId());
        attachment.setCreatedById(user.getId());
        attachmentRepository.save(attachment);
        return getTextForReply(user, attachments, RestConstants.MERGE_LIST, String.valueOf(attachments.size() + 1), ": ", attachment.getOriginalName(), ";\n");
    }

    private SendMessage processText(Message message, UserEntity user, PDFDocument document) {
        if (Objects.equals(message.getText(), MessageService.message(user, RestConstants.REMOVE_LAST_PDF))) {
            attachmentRepository.deleteLastOne(user.getId());
            List<Attachment> attachments = document.getAttachments();
            return MessageService.getTextForReply(user, attachments, RestConstants.MERGE_LIST);
        } else if (Objects.equals(message.getText(), MessageService.message(user, RestConstants.MERGE_PDF))) {
            userService.setStatus(user, BotStatus.WAITING);

            converter.processDocument(new Order(document.getId()), user);

            return SendMessage.builder()
                    .chatId(user.getTelegramId())
                    .text(MessageService.message(user, RestConstants.MERGE_WAIT))
                    .replyMarkup(
                            ReplyKeyboardRemove.builder()
                                    .removeKeyboard(true)
                                    .build())
                    .build();
        } else
            throw RestException.restThrow(MessageService.message(user, RestConstants.INVALID_INPUT), user.getTelegramId());
    }
}
