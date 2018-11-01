package com.sbt.hackaton.web.messages;

import com.sbt.hackaton.web.Command;

import java.util.List;
import java.util.Objects;

public class SubscribeMessage extends Message {
    private Command command;
    private List<ActiveChat> activeChats;

    public SubscribeMessage() {
    }

    public SubscribeMessage(Command command, List<ActiveChat> activeChats) {
        this.command = command;
        this.activeChats = activeChats;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public List<ActiveChat> getActiveChats() {
        return activeChats;
    }

    public void setActiveChats(List<ActiveChat> activeChats) {
        this.activeChats = activeChats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscribeMessage that = (SubscribeMessage) o;
        return command == that.command &&
                Objects.equals(activeChats, that.activeChats);
    }

    @Override
    public int hashCode() {
        return Objects.hash(command, activeChats);
    }

    @Override
    public String toString() {
        return "SubscribeMessage{" +
                "command=" + command +
                ", activeChats=" + activeChats +
                '}';
    }

    public static class ActiveChat {
        private Long chatId;
        private ClientData clientData;

        public ActiveChat() {
        }

        public ActiveChat(long chatId, ClientData clientData) {
            this.chatId = chatId;
            this.clientData = clientData;
        }

        public Long getChatId() {
            return chatId;
        }

        public void setChatId(Long chatId) {
            this.chatId = chatId;
        }

        public ClientData getClientData() {
            return clientData;
        }

        public void setClientData(ClientData clientData) {
            this.clientData = clientData;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ActiveChat that = (ActiveChat) o;
            return Objects.equals(chatId, that.chatId) &&
                    Objects.equals(clientData, that.clientData);
        }

        @Override
        public int hashCode() {
            return Objects.hash(chatId, clientData);
        }

        @Override
        public String toString() {
            return "ActiveChat{" +
                    "chatId=" + chatId +
                    ", clientData=" + clientData +
                    '}';
        }
    }
}

