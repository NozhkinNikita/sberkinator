package com.sbt.hackaton.web.applicationapi.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbt.hackaton.web.messages.AppMessage;
import com.sbt.hackaton.web.messages.*;
import com.sbt.hackaton.web.messages.ClientData;
import com.sbt.hackaton.web.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(WebSocketHandler.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Resource(name = "queueToClient")
    private BlockingQueue<AppMessage> queueToClient;

    @Resource(name = "queueToApp")
    private BlockingQueue<AppMessage> queueToApp;

    private Map<Long, List<WebSocketSession>> sessionsByChatId = new ConcurrentHashMap<>();
    private Map<Long, Map<Long, ClientData>> clientsByVspId = new ConcurrentHashMap<>();
    private Map<Long, List<ChatMessage>> messagesByChatId = new ConcurrentHashMap<>();

    private ExecutorService executor = Executors.newFixedThreadPool(1);

    @PostConstruct
    public void init() {
        executor.execute(() -> {
            while (!Thread.currentThread().isInterrupted())
                try {
                    AppMessage incomingMessage = queueToApp.take();
                    clientsByVspId.computeIfAbsent(/*incomingMessage.getVspId()*/123L, k -> new HashMap<>())
                            .put(incomingMessage.getChatId(), incomingMessage.getClientData());
                    messagesByChatId.computeIfAbsent(incomingMessage.getChatId(), k -> new LinkedList<>())
                            .add(new ChatMessage(ChatMessage.Sender.CLIENT, incomingMessage.getMessage()));
                    sendToApp(incomingMessage);
                } catch (InterruptedException e) {
                    log.info("Executor thread is interrupted");
                }
        });
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        AppMessage incomingMessage = parseMessage(message);
        if (incomingMessage != null) {
            switch (incomingMessage.getCommand()) {
                case SUBSCRIBE: {
                    Map<Long, ClientData> activeChats = clientsByVspId.get(incomingMessage.getVspId());
                    if (CollectionUtils.isEmpty(activeChats)) {
                        sendMessage(session, new SubscribeMessage(Command.CHATS_LIST, Collections.EMPTY_LIST));
                    } else {
                        sendMessage(session, new SubscribeMessage(Command.CHATS_LIST,
                                activeChats.entrySet().stream().map((entry) -> {
                                    sessionsByChatId.computeIfAbsent(entry.getKey(), k -> new LinkedList<>())
                                            .add(session);
                                        return new SubscribeMessage.ActiveChat(entry.getKey(), entry.getValue());})
                                        .collect(Collectors.toList())));
                    }
                    break;
            }
            case SEND: {
                messagesByChatId.computeIfAbsent(incomingMessage.getChatId(), k -> new LinkedList<>())
                        .add(new ChatMessage(ChatMessage.Sender.APP, incomingMessage.getMessage()));
                queueToClient.add(incomingMessage);
                break;
            }
            case UNSUBSCRIBE: {
                sessionsByChatId.computeIfPresent(incomingMessage.getChatId(), (k, v) -> {
                    v.remove(session);
                    return v;
                });
                break;
            }
                case GET_CHAT: {
                    sendMessage(session, new ChatHistory(Command.GET_CHAT, messagesByChatId.get(incomingMessage.getChatId())));
                    break;
                }
                case KILL_BOT: {
                    log.info("Killing bot for chatId= {}", incomingMessage.getChatId());
                    break;
                }
            default: {
                throw new IllegalArgumentException(String.format("Command %s is not supported", incomingMessage.getCommand()));
            }
        }
    }

}

    private void sendToApp(AppMessage message) {
        List<WebSocketSession> sessions = sessionsByChatId.get(message.getChatId());
        if (!CollectionUtils.isEmpty(sessions)) {
            sessions.forEach(webSocketSession -> {
                sendMessage(webSocketSession, message);
            });
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

    private String serializeMessage(Object message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (IOException e) {
            log.error("Cannot send message to application: {}. Exception: {}", message, e);
        }
        return null;
    }

    private void sendMessage(WebSocketSession webSocketSession, Message message) {
        try {
            if (webSocketSession.isOpen()) {
                webSocketSession.sendMessage(new TextMessage(
                        Objects.requireNonNull(serializeMessage(message))));
            }
        } catch (IOException e) {
            log.error("Error while sending message {}. Exception: {}", message, e);
        }
    }
}
