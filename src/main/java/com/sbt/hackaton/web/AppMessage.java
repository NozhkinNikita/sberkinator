package com.sbt.hackaton.web;

import java.util.Objects;

public class AppMessage {
    private Command command;
    private String botUserName;
    private long chatId;
    private String message;

    public AppMessage() {
    }

    public AppMessage(Command command, String botUserName, long chatId, String message) {
        this.command = command;
        this.botUserName = botUserName;
        this.chatId = chatId;
        this.message = message;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppMessage that = (AppMessage) o;
        return chatId == that.chatId &&
                Objects.equals(command, that.command) &&
                Objects.equals(botUserName, that.botUserName) &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(command, botUserName, chatId, message);
    }

    @Override
    public String toString() {
        return "AppMessage{" +
                "command='" + command + '\'' +
                ", botUserName='" + botUserName + '\'' +
                ", chatId=" + chatId +
                ", message='" + message + '\'' +
                '}';
    }
}
