package com.sbt.hackaton.web.messages;

import com.sbt.hackaton.web.Command;

import java.util.List;
import java.util.Objects;

public class ChatHistory extends Message {
    private Command command;
    private List<ChatMessage> messages;

    public ChatHistory() {
    }

    public ChatHistory(Command command, List<ChatMessage> messages) {
        this.command = command;
        this.messages = messages;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatHistory that = (ChatHistory) o;
        return command == that.command &&
                Objects.equals(messages, that.messages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(command, messages);
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "ChatHistory{" +
                "command=" + command +
                ", messages=" + messages +
                '}';
    }
}
