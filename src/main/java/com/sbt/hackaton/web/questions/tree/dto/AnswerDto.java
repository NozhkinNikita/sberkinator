package com.sbt.hackaton.web.questions.tree.dto;

import java.util.UUID;

public class AnswerDto {
    private final UUID id;
    private final String answer;
    private final boolean isTerminate;

    public AnswerDto(UUID id, String answer, boolean isTerminate) {
        this.id = id;
        this.answer = answer;
        this.isTerminate = isTerminate;
    }

    public UUID getId() {
        return id;
    }

    public String getAnswer() {
        return answer;
    }

    public boolean isTerminate() {
        return isTerminate;
    }

    @Override
    public String toString() {
        return "AnswerDto{" +
                "id=" + id +
                ", answer='" + answer + '\'' +
                ", isTerminate=" + isTerminate +
                '}';
    }
}
