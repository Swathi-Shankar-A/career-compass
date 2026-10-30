package com.career.servlet;

import com.career.dao.StudentDAO;
import com.career.model.Student;
import com.career.util.JsonUtil;
import com.career.util.PasswordUtil;
import com.career.util.SecurityUtil;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Handles student profile viewing, academic information updates,
 * and secure password changes.
 */
public class StudentProfileServlet extends HttpServlet {

    private StudentDAO studentDAO;

    @Override
    public void init() throws ServletException {
        this.studentDAO = new StudentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Student sessionStudent = (Student) session.getAttribute("studentUser");

        // Fresh load from DB
        Student student = studentDAO.findById(sessionStudent.getId());
        SecurityUtil.getOrCreateCsrfToken(request);

        String accept = request.getHeader("Accept");
        String format = request.getParameter("format");
        if ((accept != null && accept.contains("application/json")) || "json".equalsIgnoreCase(format)) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":true,\"student\":" + JsonUtil.toJson(student) + "}");
            return;
        }

        request.setAttribute("student", student);
        request.getRequestDispatcher("/student/profile.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accept = request.getHeader("Accept");
        String format = request.getParameter("format");
        boolean isJson = (accept != null && accept.contains("application/json")) || "json".equalsIgnoreCase(format);

        if (!SecurityUtil.validateCsrfToken(request)) {
            if (isJson) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JsonUtil.response(false, "Session security token validation failed. Please refresh and try again."));
                return;
            }
            request.setAttribute("errorMessage", "Session security token validation failed. Please try again.");
            doGet(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        Student sessionStudent = (Student) session.getAttribute("studentUser");
        String action = request.getParameter("action");

        if ("updateProfile".equals(action)) {
            String fullName = request.getParameter("fullName");
            String collegeName = request.getParameter("collegeName");
            String degree = request.getParameter("degree");
            String yearStr = request.getParameter("currentYear");

            if (fullName == null || fullName.trim().isEmpty() ||
                collegeName == null || collegeName.trim().isEmpty()) {
                if (isJson) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(JsonUtil.response(false, "Full Name and College Name are required."));
                    return;
                }
                request.setAttribute("errorMessage", "Full Name and College Name are required.");
                doGet(request, response);
                return;
            }

            int currentYear = 1;
            try {
                currentYear = Integer.parseInt(yearStr.trim());
                if (currentYear < 1 || currentYear > 4) currentYear = 1;
            } catch (Exception ignored) {}

            Student updated = new Student();
            updated.setId(sessionStudent.getId());
            updated.setFullName(fullName.trim());
            updated.setCollegeName(collegeName.trim());
            updated.setDegree((degree != null && !degree.trim().isEmpty()) ? degree.trim() : "B.Tech CSE");
            updated.setCurrentYear(currentYear);

            boolean success = studentDAO.updateProfile(updated);
            if (success) {
                // Update session object
                sessionStudent.setFullName(updated.getFullName());
                sessionStudent.setCollegeName(updated.getCollegeName());
                sessionStudent.setDegree(updated.getDegree());
                sessionStudent.setCurrentYear(updated.getCurrentYear());
                session.setAttribute("studentName", updated.getFullName());
                if (isJson) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(JsonUtil.response(true, "Your academic profile has been updated successfully."));
                    return;
                }
                request.setAttribute("successMessage", "Your academic profile has been updated successfully.");
            } else {
                if (isJson) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(JsonUtil.response(false, "Could not update profile. Please try again."));
                    return;
                }
                request.setAttribute("errorMessage", "Could not update profile. Please try again.");
            }
        } else if ("changePassword".equals(action)) {
            String currentPassword = request.getParameter("currentPassword");
            String newPassword = request.getParameter("newPassword");
            String confirmNewPassword = request.getParameter("confirmNewPassword");

            Student dbStudent = studentDAO.findById(sessionStudent.getId());

            if (!PasswordUtil.verifyPassword(currentPassword, dbStudent.getPasswordHash())) {
                if (isJson) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(JsonUtil.response(false, "Current password does not match our records."));
                    return;
                }
                request.setAttribute("errorMessage", "Current password does not match our records.");
                doGet(request, response);
                return;
            }

            if (newPassword == null || newPassword.length() < 6) {
                if (isJson) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(JsonUtil.response(false, "New password must be at least 6 characters long."));
                    return;
                }
                request.setAttribute("errorMessage", "New password must be at least 6 characters long.");
                doGet(request, response);
                return;
            }

            if (!newPassword.equals(confirmNewPassword)) {
                if (isJson) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(JsonUtil.response(false, "New password and confirmation password do not match."));
                    return;
                }
                request.setAttribute("errorMessage", "New password and confirmation password do not match.");
                doGet(request, response);
                return;
            }

            String newHash = PasswordUtil.hashPassword(newPassword);
            boolean passSuccess = studentDAO.updatePassword(sessionStudent.getId(), newHash);
            if (passSuccess) {
                if (isJson) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(JsonUtil.response(true, "Password changed successfully."));
                    return;
                }
                request.setAttribute("successMessage", "Password changed successfully.");
            } else {
                if (isJson) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(JsonUtil.response(false, "Password update failed. Please try again."));
                    return;
                }
                request.setAttribute("errorMessage", "Password update failed. Please try again.");
            }
        }

        doGet(request, response);
    }
}
