package com.dilshodlatipov.pdfbot.apppdfbot.controller;

import com.dilshodlatipov.pdfbot.apppdfbot.service.bot.PDFServiceBot;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@RequiredArgsConstructor
public class BotController {
    private final PDFServiceBot pdfServiceBot;

    @PostMapping("/callback")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return pdfServiceBot.onWebhookUpdateReceived(update);
    }
}
