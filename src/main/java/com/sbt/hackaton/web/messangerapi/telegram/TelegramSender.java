package com.sbt.hackaton.web.messangerapi.telegram;

import com.sbt.hackaton.web.AppMessage;
import com.sbt.hackaton.web.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TelegramSender extends TelegramLongPollingBot {
    private static final Logger log = LoggerFactory.getLogger(TelegramSender.class);

    private BlockingQueue<AppMessage> queueToApp;

    private BlockingQueue<AppMessage> queueToClient;
    private ExecutorService executor = Executors.newFixedThreadPool(1);

    public TelegramSender(DefaultBotOptions options, BlockingQueue<AppMessage> queueToApp,
                          BlockingQueue<AppMessage> queueToClient, ExecutorService executor) {
        super(options);
        this.queueToApp = queueToApp;
        this.queueToClient = queueToClient;
        this.executor = executor;

        init();
    }

    public void init() {
        executor.execute(() -> {
            while (!Thread.currentThread().isInterrupted())
                try {
                    AppMessage incomingMessage = queueToClient.take();
                    sendToClient(incomingMessage);
                } catch (InterruptedException e) {
                    log.info("Executor thread is interrupted");
                }
        });
    }

    private void sendToClient(AppMessage message) {
        SendMessage sendMessage = new SendMessage()
                .setChatId(message.getChatId())
                .setText(message.getMessage());
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Exception: ", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            queueToApp.add(new AppMessage(Command.SEND, getBotUsername(), chatId, message));
        }
    }

    @Override
    public String getBotUsername() {
        return "dmitry_katon_bot";
    }

    @Override
    public String getBotToken() {
        return "680522400:AAHyuF4mTpQgxz0_VUUcYQKNYDtk2O6Nnfo";
    }
}
