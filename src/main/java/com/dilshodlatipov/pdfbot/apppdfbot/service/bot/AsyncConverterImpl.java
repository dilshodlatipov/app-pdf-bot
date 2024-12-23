package com.dilshodlatipov.pdfbot.apppdfbot.service.bot;

import com.dilshodlatipov.pdfbot.apppdfbot.config.TelegramFileDownloader;
import com.dilshodlatipov.pdfbot.apppdfbot.entity.Attachment;
import com.dilshodlatipov.pdfbot.apppdfbot.entity.PDFDocument;
import com.dilshodlatipov.pdfbot.apppdfbot.entity.UserEntity;
import com.dilshodlatipov.pdfbot.apppdfbot.enums.BotStatus;
import com.dilshodlatipov.pdfbot.apppdfbot.enums.DocumentStatus;
import com.dilshodlatipov.pdfbot.apppdfbot.exceptions.RestException;
import com.dilshodlatipov.pdfbot.apppdfbot.payload.Order;
import com.dilshodlatipov.pdfbot.apppdfbot.repository.AttachmentRepository;
import com.dilshodlatipov.pdfbot.apppdfbot.repository.PDFDocumentRepository;
import com.dilshodlatipov.pdfbot.apppdfbot.service.FontService;
import com.dilshodlatipov.pdfbot.apppdfbot.service.MessageService;
import com.dilshodlatipov.pdfbot.apppdfbot.service.PDFService;
import com.dilshodlatipov.pdfbot.apppdfbot.service.UserService;
import com.dilshodlatipov.pdfbot.apppdfbot.utils.MessageConstants;
import com.dilshodlatipov.pdfbot.apppdfbot.utils.RestConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncConverterImpl implements AsyncConverter {
    private final PDFServiceBot bot;
    private final PDFService pdfService;
    private final UserService userService;
    private final PDFDocumentRepository documentRepository;
    private final AttachmentRepository attachmentRepository;
    private final TelegramFileDownloader telegramFileDownloader;
    private final FontService fontService;
    @Value("${telegram.chat-id.file-store}")
    private String fileStore;
    private final String PDF_DELETED = "pdf.deleted";

    @Async
    @Override
    public void processDocument(Order order, UserEntity user) {
        if (order.getDocumentId() == null) {
            throw new RuntimeException(MessageConstants.DOCUMENT_ID_IS_NULL);
        }
        if (user == null) {
            throw new RuntimeException(MessageConstants.USER_CANNOT_BE_NULL);
        }
        PDFDocument document = documentRepository.findByIdAndDeletedFalseAndStatus(order.getDocumentId(), DocumentStatus.DRAFT).orElseThrow(
                () -> RestException.restThrow(MessageService.message(user.getLanguage(), PDF_DELETED), user.getTelegramId())
        );
        switch (document.getDocumentType()) {
            case IMAGES -> convertImagesToPdf(user, document);
            case TEXT -> convertTextToPDF(user, document);
            case MERGE -> convertFilesToPDF(user, document);
        }
    }

    @Override
    public void convertImagesToPdf(UserEntity user, PDFDocument document) {
        try {
            List<InputStream> photos = downloadAttachments(user, document, RestConstants.IMAGE_NOT_FOUND);
            InputStream pdfDocument = pdfService.createDocument(photos);
            Message message = sendFileTelegram(user, document, pdfDocument);

            document.setStatus(DocumentStatus.PROCESSED);
            document.setTelegramId(message.getDocument().getFileId());
            document.setTelegramUniqueId(message.getDocument().getFileUniqueId());
            documentRepository.save(document);

            userService.setStatus(user.getId(), BotStatus.PAGE_ZERO);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void convertTextToPDF(UserEntity user, PDFDocument document) {
        InputStream fontInputStream = fontService.getFontInputStream(document.getFont());

        try (InputStream inputStream = pdfService.createDocument(document.getDocumentText(), fontInputStream, RestConstants.FONT_SIZE)) {
            sendFileTelegram(user, document, inputStream);

            document.setStatus(DocumentStatus.PROCESSED);
            documentRepository.save(document);

            userService.setStatus(user, BotStatus.PAGE_ZERO);
        } catch (TelegramApiException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void convertFilesToPDF(UserEntity user, PDFDocument document) {
        try {
            List<InputStream> pdfAttachments = downloadAttachments(user, document, RestConstants.PDF_NOT_FOUND);
            InputStream pdfDocument = pdfService.merge(pdfAttachments);
            Message message = sendFileTelegram(user, document, pdfDocument);

            document.setStatus(DocumentStatus.PROCESSED);
            document.setTelegramId(message.getDocument().getFileId());
            document.setTelegramUniqueId(message.getDocument().getFileUniqueId());
            documentRepository.save(document);

            userService.setStatus(user.getId(), BotStatus.PAGE_ZERO);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private Message sendFileTelegram(UserEntity user, PDFDocument document, InputStream pdfDocument) throws TelegramApiException {
        SendDocument sendToFileStore = SendDocument.builder()
                .chatId(fileStore)
                .caption(user.getTelegramId().toString())
                .document(new InputFile(pdfDocument, document.getId() + ".pdf"))
                .build();
        Message message = bot.execute(sendToFileStore);

        SendDocument build = SendDocument.builder()
                .chatId(user.getTelegramId())
                .caption(user.getTelegramId().toString())
                .document(new InputFile(message.getDocument().getFileId()))
                .build();
        bot.execute(build);
        return message;
    }

    private List<InputStream> downloadAttachments(UserEntity user, PDFDocument document, String exMessageCode) {
        if (document == null)
            throw new RuntimeException("Document is null");
        List<Attachment> attachments = attachmentRepository.findAllByPdfDocument_IdAndDeletedFalse(document.getId());
        if (attachments == null || attachments.isEmpty())
            throw RestException.restThrow(MessageService.message(user.getLanguage(), RestConstants.INVALID_INPUT), user.getTelegramId());
        return attachments.stream().map(attachment -> {
            String telegramId = attachment.getTelegramId();
            GetFile file = GetFile.builder()
                    .fileId(telegramId)
                    .build();
            try {
                File execute = bot.execute(file);
                byte[] stream = telegramFileDownloader.downloadFile(execute.getFilePath());
                return (InputStream) new ByteArrayInputStream(stream);
            } catch (TelegramApiException e) {
                throw RestException.restThrow(MessageService.message(user, exMessageCode, attachment.getOriginalName()), user.getTelegramId());
            }
        }).toList();
    }
}
