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
import java.net.Authenticator;
import java.net.PasswordAuthentication;
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
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("octopod", "adgjmptw".toCharArray());
            }
        });


        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);

        botOptions.setProxyHost("94.177.171.154");
        botOptions.setProxyPort(2080);
        // Select proxy type: [HTTP|SOCKS4|SOCKS5] (default: NO_PROXY)
        botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);

        // Register bot
//        try {
//            botsApi.registerBot(new TelegramSender(botOptions, queueToApp, queueToClient, executor));
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
    }

//    public TelegramSender getTelegramSender() {
//        return telegramSender;
//    }
}
