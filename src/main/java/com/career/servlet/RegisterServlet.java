package com.career.servlet;

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
 * Handles student registration.
 */
public class RegisterServlet extends HttpServlet {

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

        // Generate CSRF token for the registration form
        SecurityUtil.getOrCreateCsrfToken(request);
        request.getRequestDispatcher("/register.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accept = request.getHeader("Accept");
        String format = request.getParameter("format");
        boolean isJson = (accept != null && accept.contains("application/json")) || "json".equalsIgnoreCase(format);

        // CSRF validation
        if (!SecurityUtil.validateCsrfToken(request)) {
            if (isJson) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JsonUtil.response(false, "Security check failed (invalid or expired session token). Please refresh and try again."));
                return;
            }
            response.sendRedirect(request.getContextPath() + "/register?error=csrf");
            return;
        }

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String collegeName = request.getParameter("collegeName");
        String degree = request.getParameter("degree");
        String yearStr = request.getParameter("currentYear");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        int currentYear = 1;
        try {
            if (yearStr != null && !yearStr.trim().isEmpty()) {
                currentYear = Integer.parseInt(yearStr.trim());
            }
        } catch (NumberFormatException e) {
            currentYear = 1;
        }

        AuthService.AuthResult result = authService.registerStudent(
                fullName, email, collegeName, degree, currentYear, password, confirmPassword);

        if (result.isSuccess()) {
            if (isJson) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JsonUtil.response(true, "Registration successful.", request.getContextPath() + "/login?registered=true"));
                return;
            }
            response.sendRedirect(request.getContextPath() + "/login?registered=true");
        } else {
            if (isJson) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JsonUtil.response(false, result.getMessage()));
                return;
            }
            response.sendRedirect(request.getContextPath() + "/register?error=" + SecurityUtil.escapeHtml(result.getMessage()));
        }
    }
}
