package com.dilshodlatipov.pdfbot.apppdfbot.service.bot;

import com.dilshodlatipov.pdfbot.apppdfbot.entity.UserEntity;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface MergingPDFDocumentsService {
    SendMessage start(Message message, UserEntity user);

    SendMessage addPDF(Message message, UserEntity user);
}