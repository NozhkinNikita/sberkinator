package com.sbt.hackaton.web.messangerapi.telegram;

import com.sbt.hackaton.web.AppMessage;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class TelegramBotInitializer {

//    @Autowired
//    private TelegramSender telegramSender;

    @Resource(name = "queueToApp")
    private BlockingQueue<AppMessage> queueToApp;

    @Resource(name = "queueToClient")
    private BlockingQueue<AppMessage> queueToClient;
    private ExecutorService executor = Executors.newFixedThreadPool(1);

    @PostConstruct
    public void init() {
        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);

        botOptions.setProxyHost("113.89.54.75");
        botOptions.setProxyPort(4145);
        // Select proxy type: [HTTP|SOCKS4|SOCKS5] (default: NO_PROXY)
        botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS4);

        // Register bot
        try {
            botsApi.registerBot(new TelegramSender(botOptions, queueToApp, queueToClient, executor));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

//    public TelegramSender getTelegramSender() {
//        return telegramSender;
//    }
}
