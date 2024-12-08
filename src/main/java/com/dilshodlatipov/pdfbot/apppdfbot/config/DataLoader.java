package com.dilshodlatipov.pdfbot.apppdfbot.config;

import com.dilshodlatipov.pdfbot.apppdfbot.service.FontService;
import com.dilshodlatipov.pdfbot.apppdfbot.service.bot.PDFServiceBot;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final PDFServiceBot pdfServiceBot;
    private final SetWebhook setWebhook;
    private final FontService fontService;

    @Override
    public void run(String... args) throws Exception {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(pdfServiceBot, setWebhook);

        fontService.loadFonts();
        System.out.println("Fonts loaded");
    }
}
