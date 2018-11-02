package com.sbt.hackaton.web.messangerapi.telegram;

import com.sbt.hackaton.web.Command;
import com.sbt.hackaton.web.messages.AppMessage;
import com.sbt.hackaton.web.messages.ClientData;
import com.sbt.hackaton.web.messages.Sender;
import com.sbt.hackaton.web.questions.tree.QuestionService;
import com.sbt.hackaton.web.questions.tree.dto.QuestionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.rmi.server.UID;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class TelegramSender extends TelegramLongPollingBot {
    private static final Logger log = LoggerFactory.getLogger(TelegramSender.class);

    private QuestionService questionService;

    private QuestionDto currentQuestion;

    private boolean reviewMessage = false;
    
    private boolean botJobEnded = false;

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
                    if (incomingMessage.getCommand() != Command.KILL_BOT) {
                        sendToClient(incomingMessage);
                    } else {
                        botJobEnded = true;
                    }
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
        sendClientMessageInfoToService(update);
        if (!botJobEnded) {
            sendBotAnswer(update);
        }
    }

    private void sendBotAnswer(Update update) {
        SendMessage sendMessage = null;
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (reviewMessage){
                sendMessage = sendEndBotMessage(update.getMessage().getChatId().toString());
            }
            else {
                if (update.getMessage().getText().equalsIgnoreCase("/start")) {
                    currentQuestion = questionService.getRoot();
                    sendMessage = getSendMessage(update.getMessage().getChatId().toString(), currentQuestion);
                } else {
                    sendMessage = getSendMessage(update.getMessage().getChatId().toString(), currentQuestion);
                    sendMessage.setText("Пожалуйста, ответьте на вопрос: \n" + sendMessage.getText());

                }
            }
        } else if (update.hasCallbackQuery()) {
            reviewMessage = false;
            UUID id = UUID.fromString(update.getCallbackQuery().getData());

            if (questionService.isReviewAnswer(id)) {
                sendMessage = sendReviewMessage(update.getCallbackQuery().getMessage().getChatId().toString());
                reviewMessage = true;
            }
             else if (questionService.getAnswer(id).isTerminate()) {
                sendMessage = sendEndBotMessage(update.getCallbackQuery().getMessage().getChatId().toString());
            } else {
                currentQuestion = questionService.getNext(UUID.fromString(update.getCallbackQuery().getData()));
                sendMessage = getSendMessage(update.getCallbackQuery().getMessage().getChatId().toString(), currentQuestion);
            }
        }
        try {
            sendBotMessageInfoToService(sendMessage, update);
            execute(sendMessage); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendBotMessageInfoToService(SendMessage sendMessage, Update update) {
        AppMessage appMessage = new AppMessage(Command.SEND, Sender.BOT, Long.valueOf(sendMessage.getChatId()), sendMessage.getText(),
                new ClientData("Bot", null, null));
        queueToApp.add(appMessage);
    }


    private void sendClientMessageInfoToService(Update update) {
            Message message = null;
            String messageTxt = null;
            Sender sender = Sender.CLIENT;
            if (update.hasMessage() && update.getMessage().hasText()) {
                message = update.getMessage();
                messageTxt = message.getText();
            }
        else if(update.hasCallbackQuery()){
            message = update.getCallbackQuery().getMessage();
            messageTxt = questionService.getAnswer(UUID.fromString(update.getCallbackQuery().getData())).getAnswer();
        }
        long chatId = message.getChatId();
        ClientData clientData = getClientData(message);
        AppMessage appMessage = new AppMessage(Command.SEND, sender, chatId, messageTxt,
                clientData);
        queueToApp.add(appMessage);
    }

    private ClientData getClientData(Message message) {
        String clientFirstName = message.getFrom().getFirstName();
        String clientLastName = message.getFrom().getLastName();
        String clientUserName = message.getFrom().getUserName();
        return new ClientData(clientFirstName, clientLastName, clientUserName);
    }

    private SendMessage sendReviewMessage(String chatId) {
        SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                .setChatId(chatId)
                .setText("Спасибо за обращение!\n" +
                        "Ниже вы можете написать ваш отзыв.")
                .setParseMode("HTML");

        return message;
    }

    private SendMessage sendEndBotMessage(String chatId) {
        SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                .setChatId(chatId)
                .setText("Спасибо за "  + (reviewMessage ? "ваш отзыв" : "ответы на вопросы") +"!\n" +
                        "В ближайшее время с вами свяжется наш менеджер.")
                .setParseMode("HTML");
        botJobEnded = true;
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
        return "sber_vsp_3501_bot";
//        return "kfmnbot";
//        return "dmitry_katon_bot";
    }

    @Override
    public String getBotToken() {
        return "664158658:AAFGksPQCc1nP97f7spgvy6q8zGi31JtFfo"; //sber_vsp_3501_bot
//        return "784024922:AAFMkTWGXIKdZkQ2lz6RFxo2ikV-nGIL47M"; //kfmnbot
//        return "680522400:AAHyuF4mTpQgxz0_VUUcYQKNYDtk2O6Nnfo"; //dmitry_katon_bot
    }
}
