package com.career.dao;

import com.career.model.Admin;
import com.career.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Data Access Object for Administrator accounts.
 */
public class AdminDAO {

    public Admin findByUsername(String username) {
        if (username == null) return null;
        String sql = "SELECT id, username, email, full_name, password_hash, created_at "
                   + "FROM admins WHERE LOWER(username) = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username.toLowerCase().trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToAdmin(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[AdminDAO] findByUsername error: " + e.getMessage());
        }
        return null;
    }

    public Admin findByEmail(String email) {
        if (email == null) return null;
        String sql = "SELECT id, username, email, full_name, password_hash, created_at "
                   + "FROM admins WHERE LOWER(email) = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email.toLowerCase().trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToAdmin(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[AdminDAO] findByEmail error: " + e.getMessage());
        }
        return null;
    }

    public Admin findById(int id) {
        String sql = "SELECT id, username, email, full_name, password_hash, created_at FROM admins WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToAdmin(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[AdminDAO] findById error: " + e.getMessage());
        }
        return null;
    }

    public boolean createAdmin(Admin admin) throws SQLException {
        String sql = "INSERT INTO admins (username, email, full_name, password_hash) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, admin.getUsername().toLowerCase().trim());
            stmt.setString(2, admin.getEmail().toLowerCase().trim());
            stmt.setString(3, admin.getFullName());
            stmt.setString(4, admin.getPasswordHash());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        admin.setId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    public int countAdmins() {
        String sql = "SELECT COUNT(*) FROM admins";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[AdminDAO] countAdmins error: " + e.getMessage());
        }
        return 0;
    }

    private Admin mapRowToAdmin(ResultSet rs) throws SQLException {
        Admin a = new Admin();
        a.setId(rs.getInt("id"));
        a.setUsername(rs.getString("username"));
        a.setEmail(rs.getString("email"));
        a.setFullName(rs.getString("full_name"));
        a.setPasswordHash(rs.getString("password_hash"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        return a;
    }
}
