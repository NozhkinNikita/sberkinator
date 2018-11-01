package com.sbt.hackaton.web.questions.tree.dto;

import java.util.List;
import java.util.UUID;

public class QuestionDto {
    private UUID id;
    private String questionText;
    private List<AnswerDto> answers;

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

    public List<AnswerDto> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerDto> answers) {
        this.answers = answers;
    }

    @Override
    public String toString() {
        return "QuestionDto{" +
                "id=" + id +
                ", questionText='" + questionText + '\'' +
                ", answers=" + answers +
                '}';
    }
}
