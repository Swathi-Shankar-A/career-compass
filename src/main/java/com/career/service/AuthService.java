package com.career.service;

import com.career.dao.AdminDAO;
import com.career.dao.StudentDAO;
import com.career.model.Admin;
import com.career.model.Student;
import com.career.util.PasswordUtil;

import java.sql.SQLException;
import java.util.regex.Pattern;

/**
 * Service class handling authentication, registration validation,
 * and secure credential verification.
 */
public class AuthService {

    private final StudentDAO studentDAO;
    private final AdminDAO adminDAO;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    public AuthService() {
        this.studentDAO = new StudentDAO();
        this.adminDAO = new AdminDAO();
    }

    public AuthService(StudentDAO studentDAO, AdminDAO adminDAO) {
        this.studentDAO = studentDAO;
        this.adminDAO = adminDAO;
    }

    /**
     * Validates and registers a new student.
     * 
     * @return Result object containing success flag and message.
     */
    public AuthResult registerStudent(String fullName, String email, String collegeName,
                                      String degree, int currentYear, String password, String confirmPassword) {
        // 1. Validate required fields
        if (fullName == null || fullName.trim().isEmpty()) {
            return new AuthResult(false, "Full Name is required.");
        }
        if (email == null || email.trim().isEmpty()) {
            return new AuthResult(false, "Email address is required.");
        }
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return new AuthResult(false, "Please provide a valid email address.");
        }
        if (collegeName == null || collegeName.trim().isEmpty()) {
            return new AuthResult(false, "College name is required.");
        }
        if (degree == null || degree.trim().isEmpty()) {
            degree = "B.Tech CSE";
        }
        if (currentYear < 1 || currentYear > 4) {
            return new AuthResult(false, "Current year must be between 1 and 4.");
        }
        if (password == null || password.length() < 6) {
            return new AuthResult(false, "Password must be at least 6 characters long.");
        }
        if (!password.equals(confirmPassword)) {
            return new AuthResult(false, "Passwords do not match.");
        }

        // 2. Check for duplicate email
        if (studentDAO.findByEmail(email.trim()) != null) {
            return new AuthResult(false, "An account with this email address already exists.");
        }

        // 3. Hash password securely
        String passwordHash = PasswordUtil.hashPassword(password);

        Student student = new Student();
        student.setFullName(fullName.trim());
        student.setEmail(email.trim().toLowerCase());
        student.setCollegeName(collegeName.trim());
        student.setDegree(degree.trim());
        student.setCurrentYear(currentYear);
        student.setPasswordHash(passwordHash);

        try {
            boolean created = studentDAO.createStudent(student);
            if (created) {
                return new AuthResult(true, "Registration successful! You may now log in.", student);
            } else {
                return new AuthResult(false, "Failed to register student. Please try again.");
            }
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("duplicate")) {
                return new AuthResult(false, "An account with this email address already exists.");
            }
            return new AuthResult(false, "Database error during registration: " + e.getMessage());
        }
    }

    /**
     * Authenticates a student via email and password.
     */
    public Student authenticateStudent(String email, String password) {
        if (email == null || password == null) {
            return null;
        }
        Student student = studentDAO.findByEmail(email.trim());
        if (student == null) {
            return null;
        }
        if (PasswordUtil.verifyPassword(password, student.getPasswordHash())) {
            return student;
        }
        return null;
    }

    /**
     * Authenticates an administrator via username or email and password.
     */
    public Admin authenticateAdmin(String usernameOrEmail, String password) {
        if (usernameOrEmail == null || password == null) {
            return null;
        }
        Admin admin = adminDAO.findByUsername(usernameOrEmail.trim());
        if (admin == null) {
            admin = adminDAO.findByEmail(usernameOrEmail.trim());
        }
        if (admin == null) {
            return null;
        }
        if (PasswordUtil.verifyPassword(password, admin.getPasswordHash())) {
            return admin;
        }
        return null;
    }

    public static class AuthResult {
        private final boolean success;
        private final String message;
        private final Student student;

        public AuthResult(boolean success, String message) {
            this(success, message, null);
        }

        public AuthResult(boolean success, String message, Student student) {
            this.success = success;
            this.message = message;
            this.student = student;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public Student getStudent() {
            return student;
        }
    }
}
