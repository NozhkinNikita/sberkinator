package com.sbt.hackaton.web.messangerapi.telegram;

import com.sbt.hackaton.web.messages.AppMessage;
import com.sbt.hackaton.web.messages.ClientData;
import com.sbt.hackaton.web.Command;
import com.sbt.hackaton.web.questions.tree.QuestionService;
import com.sbt.hackaton.web.questions.tree.dto.QuestionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class TelegramSender extends TelegramLongPollingBot {
    private static final Logger log = LoggerFactory.getLogger(TelegramSender.class);

    private QuestionService questionService;

    private QuestionDto currentQuestion;

    private BlockingQueue<AppMessage> queueToApp;

    private BlockingQueue<AppMessage> queueToClient;
    private ExecutorService executor;

    public TelegramSender(DefaultBotOptions options, BlockingQueue<AppMessage> queueToApp,
                          BlockingQueue<AppMessage> queueToClient, ExecutorService executor, QuestionService questionService) {
        super(options);
        this.queueToApp = queueToApp;
        this.queueToClient = queueToClient;
        this.executor = executor;
        this.questionService = questionService;

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
        SendMessage sendMessage = null;
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String clientFirstName = update.getMessage().getFrom().getFirstName();
            String clientLastName = update.getMessage().getFrom().getLastName();
            String clientUserName = update.getMessage().getFrom().getUserName();
            AppMessage appMessage = new AppMessage(Command.SEND, chatId, message,
                    new ClientData(clientFirstName, clientLastName, clientUserName));
            queueToApp.add(appMessage);
            if (message.equalsIgnoreCase("/start")) {
                 currentQuestion = questionService.getRoot();
                 sendMessage = getSendMessage(update.getMessage().getChatId().toString(), currentQuestion);
            } else {
                    sendMessage = getSendMessage(update.getMessage().getChatId().toString(), currentQuestion);
                    sendMessage.setText("Пожалуйста, ответьте на вопрос: \n" + sendMessage.getText());

            }
        } else if (update.hasCallbackQuery()) {
            UUID id = UUID.fromString(update.getCallbackQuery().getData());
            if (questionService.isTerminateAnswer(id)){
                sendMessage = sendEndQuestionMessage(update.getCallbackQuery().getMessage().getChatId().toString());
            } else {
                currentQuestion = questionService.getNext(UUID.fromString(update.getCallbackQuery().getData()));
                sendMessage = getSendMessage(update.getCallbackQuery().getMessage().getChatId().toString(), currentQuestion);
            }
        }
        try {
            execute(sendMessage); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private SendMessage sendEndQuestionMessage(String chatId) {
        SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                .setChatId(chatId)
                .setText("Спасибо за ответы на вопросы!/n" +
                        "В ближайшее время с вами свяжется наш менеджер.")
                .setParseMode("HTML");

        return message;
    }


    private SendMessage getSendMessage(String chatId, QuestionDto questionDto) {
        SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                .setChatId(chatId)
                .setText(questionDto.getQuestionText())
                .setParseMode("HTML");
        if (questionDto.getAnswers() != null && !questionDto.getAnswers().isEmpty()) {
            message.setReplyMarkup(handleQuestion(questionDto));
        }
        return message;
    }

    private InlineKeyboardMarkup handleQuestion(QuestionDto questionDto) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = questionDto.getAnswers().stream().map(e ->
        {
            InlineKeyboardButton btn = new InlineKeyboardButton();
            btn.setCallbackData(e.getId().toString());
            btn.setText(e.getAnswer());
            return Collections.singletonList(btn);
        }).collect(Collectors.toList());
        markupInline.setKeyboard(rowsInLine);
        return markupInline;
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
