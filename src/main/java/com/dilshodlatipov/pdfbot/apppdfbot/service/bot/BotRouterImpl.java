package com.dilshodlatipov.pdfbot.apppdfbot.service.bot;

import com.dilshodlatipov.pdfbot.apppdfbot.entity.UserEntity;
import com.dilshodlatipov.pdfbot.apppdfbot.enums.BotStatus;
import com.dilshodlatipov.pdfbot.apppdfbot.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Objects;

@Service
public class BotRouterImpl implements BotRouter {
    private final UserBotService userBotService;
    private final ImageBotService imageBotService;
    private final UserService userService;
    private final MergingPDFDocumentsService merger;
    private final TextBotService textBotService;

    public BotRouterImpl(@Lazy UserBotService userBotService,
                         @Lazy ImageBotService imageBotService,
                         @Lazy UserService userService,
                         @Lazy MergingPDFDocumentsService merger,
                         @Lazy TextBotService textBotService) {
        this.userBotService = userBotService;
        this.imageBotService = imageBotService;
        this.userService = userService;
        this.merger = merger;
        this.textBotService = textBotService;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            UserEntity user = userService.checkUser(message.getFrom());
            if (message.isCommand()) {
                String command = message.getText();
                SendMessage sendMessage = switch (command) {
                    case "/start" -> userBotService.introduction(user);
                    case "/cancel" -> userBotService.cancelAction(user);
                    default -> null;
                };
                if (sendMessage != null) return sendMessage;
            }
            return switch (user.getStatus()) {
                case PAGE_ZERO -> isCommand(user, message);
                case LANGUAGE -> null;
                case IMAGES_ADDING -> imageBotService.addPhoto(message, user);
                case TEXT_CONVERT -> textBotService.addText(message, user);
                case CHOOSING_FONT -> textBotService.chooseFont(message, user);
                case MERGE -> merger.addPDF(message, user);
                case WAITING -> userBotService.waiting(user);
            };
        } else if (update.hasCallbackQuery()) {
            CallbackQuery query = update.getCallbackQuery();
            UserEntity user = userService.checkUser(query.getFrom());
            if (Objects.equals(user.getStatus(), BotStatus.LANGUAGE)) {
                try {
                    return userBotService.changeLanguage(user, query);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    private SendMessage isCommand(UserEntity user, Message message) {
        if (message.isCommand()) {
            String command = message.getText();
            return switch (command) {
                case "/image" -> imageBotService.start(message, user);
                case "/text" -> textBotService.start(message, user);
                case "/merge" -> merger.start(message, user);
                case "/language" -> userBotService.language(user);
                case "/help" -> userBotService.help(user);
                default -> throw new IllegalStateException("Unexpected value: " + command);
            };
        } else return null;
    }
}
