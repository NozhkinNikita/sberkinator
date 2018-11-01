package com.sbt.hackaton.web.questions.tree.dto;

public class AnswerDto {
    private final String key;
    private final String answer;
    private final boolean isTerminate;

    public AnswerDto(String key, String answer, boolean isTerminate) {
        this.key = key;
        this.answer = answer;
        this.isTerminate = isTerminate;
    }

    public String getKey() {
        return key;
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
                "key='" + key + '\'' +
                ", answer='" + answer + '\'' +
                ", isTerminate=" + isTerminate +
                '}';
    }
}
