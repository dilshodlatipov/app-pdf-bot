package com.dilshodlatipov.pdfbot.apppdfbot.service.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import com.dilshodlatipov.pdfbot.apppdfbot.entity.UserEntity;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface UserBotService {
    SendMessage introduction(UserEntity user);

    SendMessage cancelAction(UserEntity from);

    SendMessage language(UserEntity user);

    SendMessage waiting(UserEntity user);

    SendMessage help(UserEntity user);

    SendMessage changeLanguage(UserEntity user, CallbackQuery query) throws TelegramApiException;
}
