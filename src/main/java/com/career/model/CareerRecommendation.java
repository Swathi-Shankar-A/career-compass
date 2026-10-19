package com.career.model;

/**
 * Represents a ranked career recommendation result generated for a student submission.
 */
public class CareerRecommendation {
    private int id;
    private int submissionId;
    private int careerId;
    private int rankOrder; // 1 to 5
    private int compatibilityScore; // 0 to 100
    private String matchReason;

    // Attached Career details
    private Career career;

    public CareerRecommendation() {}

    public CareerRecommendation(int id, int submissionId, int careerId, int rankOrder,
                                int compatibilityScore, String matchReason) {
        this.id = id;
        this.submissionId = submissionId;
        this.careerId = careerId;
        this.rankOrder = rankOrder;
        this.compatibilityScore = compatibilityScore;
        this.matchReason = matchReason;
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

    public int getCareerId() {
        return careerId;
    }

    public void setCareerId(int careerId) {
        this.careerId = careerId;
    }

    public int getRankOrder() {
        return rankOrder;
    }

    public void setRankOrder(int rankOrder) {
        this.rankOrder = rankOrder;
    }

    public int getCompatibilityScore() {
        return compatibilityScore;
    }

    public void setCompatibilityScore(int compatibilityScore) {
        this.compatibilityScore = compatibilityScore;
    }

    public String getMatchReason() {
        return matchReason;
    }

    public void setMatchReason(String matchReason) {
        this.matchReason = matchReason;
    }

    public Career getCareer() {
        return career;
    }

    public void setCareer(Career career) {
        this.career = career;
    }
}
