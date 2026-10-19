package com.career.dao;

import com.career.model.AssessmentSubmission;
import com.career.model.Career;
import com.career.model.CareerRecommendation;
import com.career.model.StudentResponse;
import com.career.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Assessment Submissions, Student Responses,
 * and Top 5 Career Recommendations. Uses atomic database transactions
 * for submission persistence.
 */
public class AssessmentDAO {

    /**
     * Persists a student's completed assessment atomically:
     * 1. Inserts assessment_submissions record
     * 2. Inserts all student_responses records
     * 3. Inserts top 5 career_recommendations records
     * 
     * All inside a single transaction with rollback on any failure.
     * 
     * @return generated submissionId, or -1 if transaction failed
     */
    public int saveSubmissionTransaction(int studentId, String summaryNotes,
                                         List<StudentResponse> responses,
                                         List<CareerRecommendation> recommendations) throws SQLException {
        String insertSubmissionSql = "INSERT INTO assessment_submissions (student_id, summary_notes) VALUES (?, ?)";
        String insertResponseSql = "INSERT INTO student_responses (submission_id, question_id, question_text_snapshot, selected_option_snapshot, rating_value) VALUES (?, ?, ?, ?, ?)";
        String insertRecommendationSql = "INSERT INTO career_recommendations (submission_id, career_id, rank_order, compatibility_score, match_reason) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false); // Begin Transaction

            int submissionId;

            // 1. Insert assessment_submissions
            try (PreparedStatement subStmt = conn.prepareStatement(insertSubmissionSql, Statement.RETURN_GENERATED_KEYS)) {
                subStmt.setInt(1, studentId);
                subStmt.setString(2, summaryNotes);
                subStmt.executeUpdate();

                try (ResultSet rs = subStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        submissionId = rs.getInt(1);
                    } else {
                        conn.rollback();
                        return -1;
                    }
                }
            }

            // 2. Insert student_responses in batch
            try (PreparedStatement respStmt = conn.prepareStatement(insertResponseSql)) {
                for (StudentResponse resp : responses) {
                    respStmt.setInt(1, submissionId);
                    respStmt.setInt(2, resp.getQuestionId());
                    respStmt.setString(3, resp.getQuestionTextSnapshot());
                    respStmt.setString(4, resp.getSelectedOptionSnapshot());
                    respStmt.setInt(5, resp.getRatingValue());
                    respStmt.addBatch();
                }
                respStmt.executeBatch();
            }

            // 3. Insert career_recommendations in batch
            try (PreparedStatement recStmt = conn.prepareStatement(insertRecommendationSql)) {
                for (CareerRecommendation rec : recommendations) {
                    recStmt.setInt(1, submissionId);
                    recStmt.setInt(2, rec.getCareerId());
                    recStmt.setInt(3, rec.getRankOrder());
                    recStmt.setInt(4, rec.getCompatibilityScore());
                    recStmt.setString(5, rec.getMatchReason());
                    recStmt.addBatch();
                }
                recStmt.executeBatch();
            }

            conn.commit(); // Commit Transaction
            return submissionId;
        } catch (SQLException e) {
            System.err.println("[AssessmentDAO] Transaction failed: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("[AssessmentDAO] Rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    public AssessmentSubmission getSubmissionById(int submissionId) {
        String sql = "SELECT s.id, s.student_id, s.submission_date, s.summary_notes, "
                   + "       st.full_name, st.email, st.college_name, st.degree, st.current_year "
                   + "FROM assessment_submissions s "
                   + "JOIN students st ON s.student_id = st.id "
                   + "WHERE s.id = ?";

        AssessmentSubmission sub = null;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, submissionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    sub = new AssessmentSubmission();
                    sub.setId(rs.getInt("id"));
                    sub.setStudentId(rs.getInt("student_id"));
                    sub.setSubmissionDate(rs.getTimestamp("submission_date"));
                    sub.setSummaryNotes(rs.getString("summary_notes"));
                    sub.setStudentName(rs.getString("full_name"));
                    sub.setStudentEmail(rs.getString("email"));
                    sub.setStudentCollege(rs.getString("college_name"));
                    sub.setStudentDegree(rs.getString("degree"));
                    sub.setStudentYear(rs.getInt("current_year"));
                }
            }
        } catch (SQLException e) {
            System.err.println("[AssessmentDAO] getSubmissionById error: " + e.getMessage());
        }

        if (sub != null) {
            sub.setResponses(getResponsesBySubmissionId(submissionId));
            sub.setRecommendations(getRecommendationsBySubmissionId(submissionId));
        }
        return sub;
    }

    public List<AssessmentSubmission> getSubmissionsByStudentId(int studentId) {
        List<AssessmentSubmission> list = new ArrayList<>();
        String sql = "SELECT s.id, s.student_id, s.submission_date, s.summary_notes "
                   + "FROM assessment_submissions s "
                   + "WHERE s.student_id = ? "
                   + "ORDER BY s.submission_date DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AssessmentSubmission sub = new AssessmentSubmission();
                    sub.setId(rs.getInt("id"));
                    sub.setStudentId(rs.getInt("student_id"));
                    sub.setSubmissionDate(rs.getTimestamp("submission_date"));
                    sub.setSummaryNotes(rs.getString("summary_notes"));
                    list.add(sub);
                }
            }
        } catch (SQLException e) {
            System.err.println("[AssessmentDAO] getSubmissionsByStudentId error: " + e.getMessage());
        }

        // Attach top recommendations for overview display
        for (AssessmentSubmission sub : list) {
            sub.setRecommendations(getRecommendationsBySubmissionId(sub.getId()));
        }
        return list;
    }

    public AssessmentSubmission getLatestSubmissionByStudentId(int studentId) {
        String sql = "SELECT s.id, s.student_id, s.submission_date, s.summary_notes "
                   + "FROM assessment_submissions s "
                   + "WHERE s.student_id = ? "
                   + "ORDER BY s.submission_date DESC LIMIT 1";

        int subId = -1;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    subId = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("[AssessmentDAO] getLatestSubmissionByStudentId error: " + e.getMessage());
        }

        if (subId > 0) {
            return getSubmissionById(subId);
        }
        return null;
    }

    public List<StudentResponse> getResponsesBySubmissionId(int submissionId) {
        List<StudentResponse> list = new ArrayList<>();
        String sql = "SELECT r.id, r.submission_id, r.question_id, r.question_text_snapshot, "
                   + "       r.selected_option_snapshot, r.rating_value, q.category "
                   + "FROM student_responses r "
                   + "LEFT JOIN assessment_questions q ON r.question_id = q.id "
                   + "WHERE r.submission_id = ? "
                   + "ORDER BY r.question_id ASC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, submissionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    StudentResponse resp = new StudentResponse();
                    resp.setId(rs.getInt("id"));
                    resp.setSubmissionId(rs.getInt("submission_id"));
                    resp.setQuestionId(rs.getInt("question_id"));
                    resp.setQuestionTextSnapshot(rs.getString("question_text_snapshot"));
                    resp.setSelectedOptionSnapshot(rs.getString("selected_option_snapshot"));
                    resp.setRatingValue(rs.getInt("rating_value"));
                    resp.setCategory(rs.getString("category"));
                    list.add(resp);
                }
            }
        } catch (SQLException e) {
            System.err.println("[AssessmentDAO] getResponsesBySubmissionId error: " + e.getMessage());
        }
        return list;
    }

    public List<CareerRecommendation> getRecommendationsBySubmissionId(int submissionId) {
        List<CareerRecommendation> list = new ArrayList<>();
        String sql = "SELECT rec.id, rec.submission_id, rec.career_id, rec.rank_order, "
                   + "       rec.compatibility_score, rec.match_reason, "
                   + "       c.name, c.short_description, c.responsibilities, c.skills_required, "
                   + "       c.tools_technologies, c.learning_suggestions "
                   + "FROM career_recommendations rec "
                   + "JOIN careers c ON rec.career_id = c.id "
                   + "WHERE rec.submission_id = ? "
                   + "ORDER BY rec.rank_order ASC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, submissionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CareerRecommendation rec = new CareerRecommendation();
                    rec.setId(rs.getInt("id"));
                    rec.setSubmissionId(rs.getInt("submission_id"));
                    rec.setCareerId(rs.getInt("career_id"));
                    rec.setRankOrder(rs.getInt("rank_order"));
                    rec.setCompatibilityScore(rs.getInt("compatibility_score"));
                    rec.setMatchReason(rs.getString("match_reason"));

                    Career career = new Career();
                    career.setId(rs.getInt("career_id"));
                    career.setName(rs.getString("name"));
                    career.setShortDescription(rs.getString("short_description"));
                    career.setResponsibilities(rs.getString("responsibilities"));
                    career.setSkillsRequired(rs.getString("skills_required"));
                    career.setToolsTechnologies(rs.getString("tools_technologies"));
                    career.setLearningSuggestions(rs.getString("learning_suggestions"));
                    career.setActive(true);

                    rec.setCareer(career);
                    list.add(rec);
                }
            }
        } catch (SQLException e) {
            System.err.println("[AssessmentDAO] getRecommendationsBySubmissionId error: " + e.getMessage());
        }
        return list;
    }

    public int countSubmissions() {
        String sql = "SELECT COUNT(*) FROM assessment_submissions";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[AssessmentDAO] countSubmissions error: " + e.getMessage());
        }
        return 0;
    }

    public List<AssessmentSubmission> getRecentSubmissions(int limit) {
        List<AssessmentSubmission> list = new ArrayList<>();
        String sql = "SELECT s.id, s.student_id, s.submission_date, s.summary_notes, "
                   + "       st.full_name, st.email, st.college_name, st.degree, st.current_year "
                   + "FROM assessment_submissions s "
                   + "JOIN students st ON s.student_id = st.id "
                   + "ORDER BY s.submission_date DESC LIMIT ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AssessmentSubmission sub = new AssessmentSubmission();
                    sub.setId(rs.getInt("id"));
                    sub.setStudentId(rs.getInt("student_id"));
                    sub.setSubmissionDate(rs.getTimestamp("submission_date"));
                    sub.setSummaryNotes(rs.getString("summary_notes"));
                    sub.setStudentName(rs.getString("full_name"));
                    sub.setStudentEmail(rs.getString("email"));
                    sub.setStudentCollege(rs.getString("college_name"));
                    sub.setStudentDegree(rs.getString("degree"));
                    sub.setStudentYear(rs.getInt("current_year"));
                    list.add(sub);
                }
            }
        } catch (SQLException e) {
            System.err.println("[AssessmentDAO] getRecentSubmissions error: " + e.getMessage());
        }

        for (AssessmentSubmission sub : list) {
            sub.setRecommendations(getRecommendationsBySubmissionId(sub.getId()));
        }
        return list;
    }
}
