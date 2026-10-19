package com.career.model;

/**
 * Represents the relevance mapping between a career path and an assessment question.
 */
public class CareerQuestionMapping {
    private int id;
    private int careerId;
    private int questionId;
    private double weight; // 1.0 to 3.0
    private String questionText;
    private String category;

    public CareerQuestionMapping() {}

    public CareerQuestionMapping(int id, int careerId, int questionId, double weight) {
        this.id = id;
        this.careerId = careerId;
        this.questionId = questionId;
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCareerId() {
        return careerId;
    }

    public void setCareerId(int careerId) {
        this.careerId = careerId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
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
}
