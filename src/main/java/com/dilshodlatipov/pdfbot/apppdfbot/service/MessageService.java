package com.dilshodlatipov.pdfbot.apppdfbot.service;

import com.dilshodlatipov.pdfbot.apppdfbot.entity.Attachment;
import com.dilshodlatipov.pdfbot.apppdfbot.entity.UserEntity;
import com.dilshodlatipov.pdfbot.apppdfbot.enums.Language;
import com.dilshodlatipov.pdfbot.apppdfbot.utils.RestConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private static MessageSource messageSource;

    @Autowired
    public void set(MessageSource messageSource) {
        MessageService.messageSource = messageSource;
    }

    public static String message(UserEntity userEntity, String code, String... args) {
        return messageSource.getMessage(code, args, userEntity.getLanguage().getLocale());
    }

    public static String message(Language language, String code) {
        return messageSource.getMessage(code, null, language.getLocale());
    }


    /**
     * @param values additional data to add to the builder
     */
    public static SendMessage getTextForReply(UserEntity user, List<Attachment> attachments, String messageCode, String... values) {
        StringBuilder builder = new StringBuilder();
        builder.append(message(user, messageCode));
        for (int i = 0; i < attachments.size(); i++) {
            builder.append(i + 1).append(": ").append(attachments.get(i).getOriginalName()).append(";\n");
        }
        for (String value : values) {
            builder.append(value);
        }
        return SendMessage.builder()
                .chatId(user.getTelegramId())
                .text(builder.toString())
                .build();
    }
}
