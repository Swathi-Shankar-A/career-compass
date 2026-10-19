package com.career.servlet;

import com.career.service.AuthService;
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
        request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // CSRF validation
        if (!SecurityUtil.validateCsrfToken(request)) {
            request.setAttribute("errorMessage", "Security check failed (invalid or expired session token). Please try again.");
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
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
            response.sendRedirect(request.getContextPath() + "/login?registered=true");
        } else {
            request.setAttribute("errorMessage", result.getMessage());
            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            request.setAttribute("collegeName", collegeName);
            request.setAttribute("degree", degree);
            request.setAttribute("currentYear", currentYear);
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
        }
    }
}
