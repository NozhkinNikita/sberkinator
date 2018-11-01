package com.sbt.hackaton.web.questions.tree;

public class AnswerNode {
    private String key;
    private String answer;
    private boolean isTerminate;
    private QuestionNode questionNode;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public void setTerminate(boolean terminate) {
        isTerminate = terminate;
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
                "key='" + key + '\'' +
                ", answer='" + answer + '\'' +
                ", isTerminate=" + isTerminate +
                ", questionNode=" + questionNode +
                '}';
    }
}
