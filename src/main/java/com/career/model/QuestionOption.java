package com.career.model;

/**
 * Represents a selectable option for an assessment question.
 */
public class QuestionOption {
    private int id;
    private int questionId;
    private String optionText;
    private int ratingValue; // 1 to 4

    public QuestionOption() {}

    public QuestionOption(int id, int questionId, String optionText, int ratingValue) {
        this.id = id;
        this.questionId = questionId;
        this.optionText = optionText;
        this.ratingValue = ratingValue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public int getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(int ratingValue) {
        this.ratingValue = ratingValue;
    }
}
