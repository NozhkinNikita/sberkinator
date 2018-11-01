package com.sbt.hackaton.web.messages;

import java.util.Objects;

public class ChatMessage extends Message {

    private Sender sender;

    private String message;

    public ChatMessage() {
    }

    public ChatMessage(Sender sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
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
        ChatMessage that = (ChatMessage) o;
        return sender == that.sender &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, message);
    }

    public enum Sender {
        CLIENT, APP
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "sender=" + sender +
                ", message='" + message + '\'' +
                '}';
    }
}
