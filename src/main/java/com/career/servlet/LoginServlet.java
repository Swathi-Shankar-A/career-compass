package com.career.servlet;

import com.career.model.Student;
import com.career.service.AuthService;
import com.career.util.JsonUtil;
import com.career.util.SecurityUtil;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Handles student authentication. Authenticates against MySQL database.
 */
public class LoginServlet extends HttpServlet {

    private AuthService authService;

    @Override
    public void init() throws ServletException {
        this.authService = new AuthService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("studentUser") != null) {
            response.sendRedirect(request.getContextPath() + "/student/dashboard");
            return;
        }

        SecurityUtil.getOrCreateCsrfToken(request);
        request.getRequestDispatcher("/login.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accept = request.getHeader("Accept");
        String format = request.getParameter("format");
        boolean isJson = (accept != null && accept.contains("application/json")) || "json".equalsIgnoreCase(format);

        // CSRF Check
        if (!SecurityUtil.validateCsrfToken(request)) {
            if (isJson) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JsonUtil.response(false, "Session security validation failed. Please refresh and try again."));
                return;
            }
            response.sendRedirect(request.getContextPath() + "/login?error=csrf");
            return;
        }

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            if (isJson) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JsonUtil.response(false, "Please enter both your email address and password."));
                return;
            }
            response.sendRedirect(request.getContextPath() + "/login?error=empty_fields");
            return;
        }

        Student student = authService.authenticateStudent(email, password);

        if (student != null) {
            // Prevent Session Fixation: Invalidate old session and generate a fresh one
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession newSession = request.getSession(true);

            // Store student credentials and role
            newSession.setAttribute("studentUser", student);
            newSession.setAttribute("userRole", "STUDENT");
            newSession.setAttribute("studentId", student.getId());
            newSession.setAttribute("studentName", student.getFullName());
            newSession.setAttribute("studentEmail", student.getEmail());

            // Initialize new CSRF token for the authenticated session
            SecurityUtil.getOrCreateCsrfToken(request);

            if (isJson) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JsonUtil.response(true, "Authentication successful.", request.getContextPath() + "/student/dashboard"));
                return;
            }
            response.sendRedirect(request.getContextPath() + "/student/dashboard");
        } else {
            if (isJson) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JsonUtil.response(false, "Invalid email or password. Please verify your credentials and try again."));
                return;
            }
            response.sendRedirect(request.getContextPath() + "/login?error=invalid_credentials");
        }
    }
}
