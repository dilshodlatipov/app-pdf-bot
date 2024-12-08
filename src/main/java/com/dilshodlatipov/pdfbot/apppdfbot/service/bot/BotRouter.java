package com.dilshodlatipov.pdfbot.apppdfbot.service.bot;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface BotRouter {
    BotApiMethod<?> onWebhookUpdateReceived(Update update);
}
