package com.career.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an assessment question in the Career Recommendation System.
 */
public class Question {
    private int id;
    private int questionOrder;
    private String questionText;
    private String category;
    private boolean active;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private List<QuestionOption> options = new ArrayList<>();

    public Question() {}

    public Question(int id, int questionOrder, String questionText, String category, boolean active) {
        this.id = id;
        this.questionOrder = questionOrder;
        this.questionText = questionText;
        this.category = category;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuestionOrder() {
        return questionOrder;
    }

    public void setQuestionOrder(int questionOrder) {
        this.questionOrder = questionOrder;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<QuestionOption> getOptions() {
        return options;
    }

    public void setOptions(List<QuestionOption> options) {
        this.options = options;
    }

    public void addOption(QuestionOption option) {
        this.options.add(option);
    }
}
