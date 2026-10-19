package com.career.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a student's completed career assessment attempt.
 */
public class AssessmentSubmission {
    private int id;
    private int studentId;
    private Timestamp submissionDate;
    private String summaryNotes;

    // Joined fields for display
    private String studentName;
    private String studentEmail;
    private String studentCollege;
    private String studentDegree;
    private int studentYear;

    private List<StudentResponse> responses = new ArrayList<>();
    private List<CareerRecommendation> recommendations = new ArrayList<>();

    public AssessmentSubmission() {}

    public AssessmentSubmission(int id, int studentId, Timestamp submissionDate, String summaryNotes) {
        this.id = id;
        this.studentId = studentId;
        this.submissionDate = submissionDate;
        this.summaryNotes = summaryNotes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public Timestamp getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Timestamp submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getSummaryNotes() {
        return summaryNotes;
    }

    public void setSummaryNotes(String summaryNotes) {
        this.summaryNotes = summaryNotes;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStudentCollege() {
        return studentCollege;
    }

    public void setStudentCollege(String studentCollege) {
        this.studentCollege = studentCollege;
    }

    public String getStudentDegree() {
        return studentDegree;
    }

    public void setStudentDegree(String studentDegree) {
        this.studentDegree = studentDegree;
    }

    public int getStudentYear() {
        return studentYear;
    }

    public void setStudentYear(int studentYear) {
        this.studentYear = studentYear;
    }

    public List<StudentResponse> getResponses() {
        return responses;
    }

    public void setResponses(List<StudentResponse> responses) {
        this.responses = responses;
    }

    public List<CareerRecommendation> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<CareerRecommendation> recommendations) {
        this.recommendations = recommendations;
    }
}
