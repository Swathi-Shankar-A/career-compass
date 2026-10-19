package com.career.dao;

import com.career.model.Question;
import com.career.model.QuestionOption;
import com.career.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for assessment questions and question options.
 */
public class QuestionDAO {

    /**
     * Retrieves all active assessment questions ordered by question_order ASC,
     * including their selectable options.
     */
    public List<Question> getActiveQuestions() {
        return getQuestionsInternal(true);
    }

    /**
     * Retrieves all assessment questions (both active and inactive) for admin management.
     */
    public List<Question> getAllQuestions() {
        return getQuestionsInternal(false);
    }

    private List<Question> getQuestionsInternal(boolean onlyActive) {
        Map<Integer, Question> questionMap = new LinkedHashMap<>();

        String sql = "SELECT q.id, q.question_order, q.question_text, q.category, q.is_active, q.created_at, q.updated_at, "
                   + "       o.id AS option_id, o.option_text, o.rating_value "
                   + "FROM assessment_questions q "
                   + "LEFT JOIN question_options o ON q.id = o.question_id ";
        if (onlyActive) {
            sql += "WHERE q.is_active = TRUE ";
        }
        sql += "ORDER BY q.question_order ASC, o.rating_value DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int qId = rs.getInt("id");
                Question q = questionMap.get(qId);
                if (q == null) {
                    q = new Question();
                    q.setId(qId);
                    q.setQuestionOrder(rs.getInt("question_order"));
                    q.setQuestionText(rs.getString("question_text"));
                    q.setCategory(rs.getString("category"));
                    q.setActive(rs.getBoolean("is_active"));
                    q.setCreatedAt(rs.getTimestamp("created_at"));
                    q.setUpdatedAt(rs.getTimestamp("updated_at"));
                    questionMap.put(qId, q);
                }

                int optId = rs.getInt("option_id");
                if (optId > 0) {
                    QuestionOption opt = new QuestionOption();
                    opt.setId(optId);
                    opt.setQuestionId(qId);
                    opt.setOptionText(rs.getString("option_text"));
                    opt.setRatingValue(rs.getInt("rating_value"));
                    q.addOption(opt);
                }
            }
        } catch (SQLException e) {
            System.err.println("[QuestionDAO] getQuestionsInternal error: " + e.getMessage());
        }

        return new ArrayList<>(questionMap.values());
    }

    public Question getQuestionById(int id) {
        String sql = "SELECT q.id, q.question_order, q.question_text, q.category, q.is_active, q.created_at, q.updated_at, "
                   + "       o.id AS option_id, o.option_text, o.rating_value "
                   + "FROM assessment_questions q "
                   + "LEFT JOIN question_options o ON q.id = o.question_id "
                   + "WHERE q.id = ? "
                   + "ORDER BY o.rating_value DESC";

        Question q = null;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    if (q == null) {
                        q = new Question();
                        q.setId(rs.getInt("id"));
                        q.setQuestionOrder(rs.getInt("question_order"));
                        q.setQuestionText(rs.getString("question_text"));
                        q.setCategory(rs.getString("category"));
                        q.setActive(rs.getBoolean("is_active"));
                        q.setCreatedAt(rs.getTimestamp("created_at"));
                        q.setUpdatedAt(rs.getTimestamp("updated_at"));
                    }
                    int optId = rs.getInt("option_id");
                    if (optId > 0) {
                        QuestionOption opt = new QuestionOption();
                        opt.setId(optId);
                        opt.setQuestionId(q.getId());
                        opt.setOptionText(rs.getString("option_text"));
                        opt.setRatingValue(rs.getInt("rating_value"));
                        q.addOption(opt);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[QuestionDAO] getQuestionById error: " + e.getMessage());
        }
        return q;
    }

    public boolean addQuestion(Question question, List<QuestionOption> options) {
        String insertQuestionSql = "INSERT INTO assessment_questions (question_order, question_text, category, is_active) VALUES (?, ?, ?, ?)";
        String insertOptionSql = "INSERT INTO question_options (question_id, option_text, rating_value) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            int newQuestionId;
            try (PreparedStatement qStmt = conn.prepareStatement(insertQuestionSql, Statement.RETURN_GENERATED_KEYS)) {
                qStmt.setInt(1, question.getQuestionOrder());
                qStmt.setString(2, question.getQuestionText());
                qStmt.setString(3, question.getCategory());
                qStmt.setBoolean(4, question.isActive());
                qStmt.executeUpdate();

                try (ResultSet rs = qStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        newQuestionId = rs.getInt(1);
                        question.setId(newQuestionId);
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            try (PreparedStatement optStmt = conn.prepareStatement(insertOptionSql)) {
                for (QuestionOption opt : options) {
                    optStmt.setInt(1, newQuestionId);
                    optStmt.setString(2, opt.getOptionText());
                    optStmt.setInt(3, opt.getRatingValue());
                    optStmt.addBatch();
                }
                optStmt.executeBatch();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("[QuestionDAO] addQuestion error: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    public boolean updateQuestion(Question question) {
        String sql = "UPDATE assessment_questions SET question_order = ?, question_text = ?, category = ?, is_active = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, question.getQuestionOrder());
            stmt.setString(2, question.getQuestionText());
            stmt.setString(3, question.getCategory());
            stmt.setBoolean(4, question.isActive());
            stmt.setInt(5, question.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[QuestionDAO] updateQuestion error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateOption(int optionId, String newOptionText) {
        String sql = "UPDATE question_options SET option_text = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newOptionText);
            stmt.setInt(2, optionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[QuestionDAO] updateOption error: " + e.getMessage());
            return false;
        }
    }

    public boolean toggleActiveStatus(int questionId, boolean newStatus) {
        String sql = "UPDATE assessment_questions SET is_active = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, newStatus);
            stmt.setInt(2, questionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[QuestionDAO] toggleActiveStatus error: " + e.getMessage());
            return false;
        }
    }

    public int countActiveQuestions() {
        String sql = "SELECT COUNT(*) FROM assessment_questions WHERE is_active = TRUE";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[QuestionDAO] countActiveQuestions error: " + e.getMessage());
        }
        return 0;
    }

    public int countTotalQuestions() {
        String sql = "SELECT COUNT(*) FROM assessment_questions";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[QuestionDAO] countTotalQuestions error: " + e.getMessage());
        }
        return 0;
    }

    public int getNextQuestionOrder() {
        String sql = "SELECT COALESCE(MAX(question_order), 0) + 1 FROM assessment_questions";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[QuestionDAO] getNextQuestionOrder error: " + e.getMessage());
        }
        return 1;
    }
}
