package com.dilshodlatipov.pdfbot.apppdfbot.service.bot;

import com.dilshodlatipov.pdfbot.apppdfbot.entity.UserEntity;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface TextBotService {
    SendMessage start(Message message, UserEntity user);

    SendMessage addText(Message message, UserEntity user);

    SendMessage chooseFont(Message message, UserEntity user);
}
