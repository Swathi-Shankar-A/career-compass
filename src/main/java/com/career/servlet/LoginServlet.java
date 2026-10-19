package com.career.servlet;

import com.career.model.Student;
import com.career.service.AuthService;
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
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // CSRF Check
        if (!SecurityUtil.validateCsrfToken(request)) {
            request.setAttribute("errorMessage", "Session security validation failed. Please try logging in again.");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            return;
        }

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("errorMessage", "Please enter both your email address and password.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
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

            response.sendRedirect(request.getContextPath() + "/student/dashboard");
        } else {
            // Safe generic error message without revealing whether the email exists
            request.setAttribute("errorMessage", "Invalid email or password. Please verify your credentials and try again.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        }
    }
}
