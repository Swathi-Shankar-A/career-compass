package com.career.model;

/**
 * Represents a student's answer to an assessment question,
 * preserving a snapshot of the question text and selected option.
 */
public class StudentResponse {
    private int id;
    private int submissionId;
    private int questionId;
    private String questionTextSnapshot;
    private String selectedOptionSnapshot;
    private int ratingValue; // 1 to 4
    private String category; // Optional category for display

    public StudentResponse() {}

    public StudentResponse(int id, int submissionId, int questionId,
                           String questionTextSnapshot, String selectedOptionSnapshot, int ratingValue) {
        this.id = id;
        this.submissionId = submissionId;
        this.questionId = questionId;
        this.questionTextSnapshot = questionTextSnapshot;
        this.selectedOptionSnapshot = selectedOptionSnapshot;
        this.ratingValue = ratingValue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(int submissionId) {
        this.submissionId = submissionId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getQuestionTextSnapshot() {
        return questionTextSnapshot;
    }

    public void setQuestionTextSnapshot(String questionTextSnapshot) {
        this.questionTextSnapshot = questionTextSnapshot;
    }

    public String getSelectedOptionSnapshot() {
        return selectedOptionSnapshot;
    }

    public void setSelectedOptionSnapshot(String selectedOptionSnapshot) {
        this.selectedOptionSnapshot = selectedOptionSnapshot;
    }

    public int getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(int ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
