package com.sbt.hackaton.web.messangerapi.telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;

@Component
public class TelegramBotInitializer {

    @Autowired
    private TelegramSender telegramSender;

    @PostConstruct
    public void init() {
        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        // Register bot
        try {
            botsApi.registerBot(telegramSender);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public TelegramSender getTelegramSender() {
        return telegramSender;
    }
}
