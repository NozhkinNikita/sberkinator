package com.sbt.hackaton.web.messages;

import com.sbt.hackaton.web.Command;

import java.util.Objects;

public class AppMessage extends Message {
    private Command command;
    private String botUserName;
    private long chatId;
    private long vspId;
    private String message;
    private ClientData clientData;

    public AppMessage() {
    }

    public AppMessage(Command command, String botUserName, long chatId, String message, ClientData clientData) {
        this.command = command;
        this.botUserName = botUserName;
        this.chatId = chatId;
        this.message = message;
        this.clientData = clientData;
    }

    public AppMessage(Command command, long chatId, String message, ClientData clientData) {
        this.command = command;
        this.chatId = chatId;
        this.message = message;
        this.clientData = clientData;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public String getBotUserName() {
        return botUserName;
    }

    public void setBotUserName(String botUserName) {
        this.botUserName = botUserName;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ClientData getClientData() {
        return clientData;
    }

    public void setClientData(ClientData clientData) {
        this.clientData = clientData;
    }

    public long getVspId() {
        return vspId;
    }

    public void setVspId(long vspId) {
        this.vspId = vspId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppMessage that = (AppMessage) o;
        return chatId == that.chatId &&
                vspId == that.vspId &&
                command == that.command &&
                Objects.equals(botUserName, that.botUserName) &&
                Objects.equals(message, that.message) &&
                Objects.equals(clientData, that.clientData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(command, botUserName, chatId, vspId, message, clientData);
    }

    @Override
    public String toString() {
        return "AppMessage{" +
                "command=" + command +
                ", botUserName='" + botUserName + '\'' +
                ", chatId=" + chatId +
                ", vspId=" + vspId +
                ", message='" + message + '\'' +
                ", clientData=" + clientData +
                '}';
    }
}
