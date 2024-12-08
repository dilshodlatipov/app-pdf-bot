package com.dilshodlatipov.pdfbot.apppdfbot.config;

import com.dilshodlatipov.pdfbot.apppdfbot.service.bot.BotRouter;
import com.dilshodlatipov.pdfbot.apppdfbot.service.bot.PDFServiceBot;
import com.dilshodlatipov.pdfbot.apppdfbot.service.PDFService;
import com.dilshodlatipov.pdfbot.apppdfbot.service.bot.UserBotService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.telegram.telegrambots.facilities.filedownloader.TelegramFileDownloader;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Configuration
@RequiredArgsConstructor
public class BotConfig {
    @Value("${telegram.webhook-path}")
    private String botWebhookPath;
    @Value("${telegram.bot-token}")
    private String botToken;
    @Value("${telegram.bot-name}")
    private String botName;

    @Bean
    public SetWebhook setWebhook() {
        return SetWebhook.builder().url(botWebhookPath).build();
    }

    @Bean
    public PDFServiceBot pdfServiceBot(SetWebhook setWebhook, BotRouter botRouter) throws TelegramApiException {
        return new PDFServiceBot(botToken, setWebhook, botName, botWebhookPath, botRouter);
    }
}
