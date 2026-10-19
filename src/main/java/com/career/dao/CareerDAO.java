package com.career.dao;

import com.career.model.Career;
import com.career.model.CareerQuestionMapping;
import com.career.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for Careers and Career-to-Question Mappings.
 */
public class CareerDAO {

    public List<Career> getActiveCareers() {
        return getCareersInternal(true);
    }

    public List<Career> getAllCareers() {
        return getCareersInternal(false);
    }

    private List<Career> getCareersInternal(boolean onlyActive) {
        List<Career> list = new ArrayList<>();
        String sql = "SELECT id, name, short_description, responsibilities, skills_required, tools_technologies, learning_suggestions, is_active, created_at "
                   + "FROM careers ";
        if (onlyActive) {
            sql += "WHERE is_active = TRUE ";
        }
        sql += "ORDER BY name ASC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToCareer(rs));
            }
        } catch (SQLException e) {
            System.err.println("[CareerDAO] getCareersInternal error: " + e.getMessage());
        }
        return list;
    }

    public Career getCareerById(int id) {
        String sql = "SELECT id, name, short_description, responsibilities, skills_required, tools_technologies, learning_suggestions, is_active, created_at "
                   + "FROM careers WHERE id = ?";
        Career career = null;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    career = mapRowToCareer(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[CareerDAO] getCareerById error: " + e.getMessage());
        }

        if (career != null) {
            career.setMappings(getMappingsForCareer(id));
        }
        return career;
    }

    public List<CareerQuestionMapping> getMappingsForCareer(int careerId) {
        List<CareerQuestionMapping> list = new ArrayList<>();
        String sql = "SELECT m.id, m.career_id, m.question_id, m.weight, q.question_text, q.category "
                   + "FROM career_question_mappings m "
                   + "JOIN assessment_questions q ON m.question_id = q.id "
                   + "WHERE m.career_id = ? "
                   + "ORDER BY m.weight DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, careerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CareerQuestionMapping map = new CareerQuestionMapping();
                    map.setId(rs.getInt("id"));
                    map.setCareerId(rs.getInt("career_id"));
                    map.setQuestionId(rs.getInt("question_id"));
                    map.setWeight(rs.getDouble("weight"));
                    map.setQuestionText(rs.getString("question_text"));
                    map.setCategory(rs.getString("category"));
                    list.add(map);
                }
            }
        } catch (SQLException e) {
            System.err.println("[CareerDAO] getMappingsForCareer error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Retrieves all career-question mappings grouped by careerId.
     * Used by the RecommendationEngine for fast calculation without N+1 queries.
     */
    public Map<Integer, List<CareerQuestionMapping>> getAllMappingsGroupedByCareer() {
        Map<Integer, List<CareerQuestionMapping>> map = new HashMap<>();
        String sql = "SELECT m.id, m.career_id, m.question_id, m.weight, q.question_text, q.category "
                   + "FROM career_question_mappings m "
                   + "JOIN assessment_questions q ON m.question_id = q.id "
                   + "ORDER BY m.career_id ASC, m.weight DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int cId = rs.getInt("career_id");
                List<CareerQuestionMapping> list = map.computeIfAbsent(cId, k -> new ArrayList<>());

                CareerQuestionMapping m = new CareerQuestionMapping();
                m.setId(rs.getInt("id"));
                m.setCareerId(cId);
                m.setQuestionId(rs.getInt("question_id"));
                m.setWeight(rs.getDouble("weight"));
                m.setQuestionText(rs.getString("question_text"));
                m.setCategory(rs.getString("category"));
                list.add(m);
            }
        } catch (SQLException e) {
            System.err.println("[CareerDAO] getAllMappingsGroupedByCareer error: " + e.getMessage());
        }
        return map;
    }

    public int countCareers() {
        String sql = "SELECT COUNT(*) FROM careers WHERE is_active = TRUE";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[CareerDAO] countCareers error: " + e.getMessage());
        }
        return 0;
    }

    public boolean updateCareer(Career career) {
        String sql = "UPDATE careers SET name = ?, short_description = ?, responsibilities = ?, "
                   + "skills_required = ?, tools_technologies = ?, learning_suggestions = ?, is_active = ? "
                   + "WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, career.getName());
            stmt.setString(2, career.getShortDescription());
            stmt.setString(3, career.getResponsibilities());
            stmt.setString(4, career.getSkillsRequired());
            stmt.setString(5, career.getToolsTechnologies());
            stmt.setString(6, career.getLearningSuggestions());
            stmt.setBoolean(7, career.isActive());
            stmt.setInt(8, career.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CareerDAO] updateCareer error: " + e.getMessage());
            return false;
        }
    }

    private Career mapRowToCareer(ResultSet rs) throws SQLException {
        Career c = new Career();
        c.setId(rs.getInt("id"));
        c.setName(rs.getString("name"));
        c.setShortDescription(rs.getString("short_description"));
        c.setResponsibilities(rs.getString("responsibilities"));
        c.setSkillsRequired(rs.getString("skills_required"));
        c.setToolsTechnologies(rs.getString("tools_technologies"));
        c.setLearningSuggestions(rs.getString("learning_suggestions"));
        c.setActive(rs.getBoolean("is_active"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        return c;
    }
}
