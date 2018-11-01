package com.sbt.hackaton.web.applicationapi.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbt.hackaton.web.AppMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(WebSocketHandler.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Resource(name = "queueToClient")
    private BlockingQueue<AppMessage> queueToClient;

    @Resource(name = "queueToApp")
    private BlockingQueue<AppMessage> queueToApp;

    private Map<String, List<WebSocketSession>> sessionsByBotName = new ConcurrentHashMap<>();
    private ExecutorService executor = Executors.newFixedThreadPool(1);

    @PostConstruct
    public void init() {
        executor.execute(() -> {
            while (!Thread.currentThread().isInterrupted())
            try {
                AppMessage incomingMessage = queueToApp.take();
                sendToApp(incomingMessage);
            } catch (InterruptedException e) {
                log.info("Executor thread is interrupted");
            }
        });
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        AppMessage messageToSend = parseMessage(message);
        if (messageToSend != null) {
            switch (messageToSend.getCommand()) {
                case SUBSCRIBE: {
                    sessionsByBotName.computeIfAbsent(messageToSend.getBotUserName(), k -> new LinkedList<>())
                            .add(session);
                    break;
                }
                case SEND: {
                    queueToClient.add(new AppMessage(messageToSend.getCommand(), messageToSend.getBotUserName(),
                            messageToSend.getChatId(), messageToSend.getMessage()));
                    break;
                }
                case UNSUBSCRIBE: {
                    sessionsByBotName.computeIfPresent(messageToSend.getBotUserName(), (k, v) -> {
                        v.remove(session);
                        return v;
                    });
                    break;
                }
                default: {
                    throw new IllegalArgumentException(String.format("Command %s is not supported", messageToSend.getCommand()));
                }
            }
        }
    }

    private void sendToApp(AppMessage message) {
        sessionsByBotName.computeIfPresent(message.getBotUserName(), (k, v) -> {
            v.forEach(webSocketSession -> sendMessage(webSocketSession, message));
            return v;
        });
        for(WebSocketSession webSocketSession : sessionsByBotName.get(message.getBotUserName())) {
            try {
                webSocketSession.sendMessage(new TextMessage(
                        objectMapper.writeValueAsString(message)));
            } catch (IOException e) {
                log.error("Cannot send message to application: {}. Exception: {}", message, e);
            }
        }
    }

    private AppMessage parseMessage(TextMessage message) {
        try {
            return objectMapper.readValue(message.getPayload(), AppMessage.class);
        } catch (IOException e) {
            log.error("Cannot parse web socket message= {}. Exception: {}", message.getPayloadLength(), e);
        }
        return null;
    }

    private String serializeMessage(AppMessage message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (IOException e) {
            log.error("Cannot send message to application: {}. Exception: {}", message, e);
        }
        return null;
    }

    private void sendMessage(WebSocketSession webSocketSession, AppMessage message) {
        try {
            webSocketSession.sendMessage(new TextMessage(
                    serializeMessage(message)));
        } catch (IOException e) {
            log.error("Error while sending message {}. Exception: {}", message, e);
        }
    }
}
