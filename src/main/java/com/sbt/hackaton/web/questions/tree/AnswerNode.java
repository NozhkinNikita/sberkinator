package com.sbt.hackaton.web.questions.tree;

import java.util.UUID;

public class AnswerNode {
    private UUID id;
    private String answer;
    private boolean isTerminate;
    private QuestionNode questionNode;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isTerminate() {
        return isTerminate;
    }

    public void setIsTerminate(boolean isTerminate) {
        this.isTerminate = isTerminate;
    }

    public QuestionNode getQuestionNode() {
        return questionNode;
    }

    public void setQuestionNode(QuestionNode questionNode) {
        this.questionNode = questionNode;
    }

    @Override
    public String toString() {
        return "AnswerNode{" +
                "id=" + id +
                ", answer='" + answer + '\'' +
                ", isTerminate=" + isTerminate +
                ", questionNode=" + questionNode +
                '}';
    }
}
