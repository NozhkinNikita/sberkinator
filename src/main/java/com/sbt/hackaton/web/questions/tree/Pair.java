package com.sbt.hackaton.web.questions.tree;

import java.util.UUID;

public class Pair {
    private final UUID questionId;
    private final String answerKey;

    public Pair(UUID questionId, String answerKey) {
        this.questionId = questionId;
        this.answerKey = answerKey;
    }

    public UUID getQuestionId() {
        return questionId;
    }

    public String getAnswerKey() {
        return answerKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair pair = (Pair) o;

        if (!questionId.equals(pair.questionId)) return false;
        return answerKey.equals(pair.answerKey);
    }

    @Override
    public int hashCode() {
        int result = questionId.hashCode();
        result = 31 * result + answerKey.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "questionId=" + questionId +
                ", answerKey='" + answerKey + '\'' +
                '}';
    }
}
