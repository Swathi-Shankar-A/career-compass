package com.career.dao;

import com.career.model.Student;
import com.career.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for student authentication, registration,
 * and profile management.
 */
public class StudentDAO {

    public boolean createStudent(Student student) throws SQLException {
        String sql = "INSERT INTO students (full_name, email, college_name, degree, current_year, password_hash) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, student.getFullName());
            stmt.setString(2, student.getEmail().toLowerCase().trim());
            stmt.setString(3, student.getCollegeName());
            stmt.setString(4, student.getDegree());
            stmt.setInt(5, student.getCurrentYear());
            stmt.setString(6, student.getPasswordHash());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        student.setId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    public Student findByEmail(String email) {
        if (email == null) return null;
        String sql = "SELECT id, full_name, email, college_name, degree, current_year, password_hash, created_at, updated_at "
                   + "FROM students WHERE LOWER(email) = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email.toLowerCase().trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToStudent(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[StudentDAO] findByEmail error: " + e.getMessage());
        }
        return null;
    }

    public Student findById(int id) {
        String sql = "SELECT id, full_name, email, college_name, degree, current_year, password_hash, created_at, updated_at "
                   + "FROM students WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToStudent(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[StudentDAO] findById error: " + e.getMessage());
        }
        return null;
    }

    public boolean updateProfile(Student student) {
        String sql = "UPDATE students SET full_name = ?, college_name = ?, degree = ?, current_year = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getFullName());
            stmt.setString(2, student.getCollegeName());
            stmt.setString(3, student.getDegree());
            stmt.setInt(4, student.getCurrentYear());
            stmt.setInt(5, student.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[StudentDAO] updateProfile error: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePassword(int studentId, String newPasswordHash) {
        String sql = "UPDATE students SET password_hash = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPasswordHash);
            stmt.setInt(2, studentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[StudentDAO] updatePassword error: " + e.getMessage());
            return false;
        }
    }

    public List<Student> listStudents(String search, int offset, int limit) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT id, full_name, email, college_name, degree, current_year, password_hash, created_at, updated_at "
                   + "FROM students ";
        boolean hasSearch = (search != null && !search.trim().isEmpty());
        if (hasSearch) {
            sql += "WHERE full_name LIKE ? OR email LIKE ? OR college_name LIKE ? ";
        }
        sql += "ORDER BY created_at DESC LIMIT ? OFFSET ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int p = 1;
            if (hasSearch) {
                String pattern = "%" + search.trim() + "%";
                stmt.setString(p++, pattern);
                stmt.setString(p++, pattern);
                stmt.setString(p++, pattern);
            }
            stmt.setInt(p++, limit);
            stmt.setInt(p++, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToStudent(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[StudentDAO] listStudents error: " + e.getMessage());
        }
        return list;
    }

    public int countStudents(String search) {
        String sql = "SELECT COUNT(*) FROM students";
        boolean hasSearch = (search != null && !search.trim().isEmpty());
        if (hasSearch) {
            sql += " WHERE full_name LIKE ? OR email LIKE ? OR college_name LIKE ?";
        }
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (hasSearch) {
                String pattern = "%" + search.trim() + "%";
                stmt.setString(1, pattern);
                stmt.setString(2, pattern);
                stmt.setString(3, pattern);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("[StudentDAO] countStudents error: " + e.getMessage());
        }
        return 0;
    }

    private Student mapRowToStudent(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("id"));
        s.setFullName(rs.getString("full_name"));
        s.setEmail(rs.getString("email"));
        s.setCollegeName(rs.getString("college_name"));
        s.setDegree(rs.getString("degree"));
        s.setCurrentYear(rs.getInt("current_year"));
        s.setPasswordHash(rs.getString("password_hash"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        s.setUpdatedAt(rs.getTimestamp("updated_at"));
        return s;
    }
}
