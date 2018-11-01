package com.sbt.hackaton.web.questions.tree;

import java.util.List;
import java.util.UUID;

public class QuestionNode {
    private UUID id;
    private String questionText;
    private List<AnswerNode> answers;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<AnswerNode> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerNode> answers) {
        this.answers = answers;
    }

    @Override
    public String toString() {
        return "QuestionNode{" +
                "id=" + id +
                ", questionText='" + questionText + '\'' +
                ", answers=" + answers +
                '}';
    }
}
