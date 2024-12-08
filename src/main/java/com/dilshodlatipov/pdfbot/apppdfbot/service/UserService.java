package com.dilshodlatipov.pdfbot.apppdfbot.service;

import com.dilshodlatipov.pdfbot.apppdfbot.entity.UserEntity;
import com.dilshodlatipov.pdfbot.apppdfbot.enums.BotStatus;

import java.util.UUID;

public interface UserService {
    UserEntity save(org.telegram.telegrambots.meta.api.objects.User user);

    UserEntity checkUser(org.telegram.telegrambots.meta.api.objects.User user);

    void setStatus(UserEntity user, BotStatus status);

    UserEntity save(UserEntity user);

    void setStatus(UUID userId, BotStatus botStatus);
}
