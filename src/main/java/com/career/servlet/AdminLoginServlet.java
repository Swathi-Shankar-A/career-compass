package com.career.servlet;

import com.career.model.Admin;
import com.career.service.AuthService;
import com.career.util.SecurityUtil;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Handles Administrator authentication. Authenticates against MySQL database.
 */
public class AdminLoginServlet extends HttpServlet {

    private AuthService authService;

    @Override
    public void init() throws ServletException {
        this.authService = new AuthService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("adminUser") != null) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        SecurityUtil.getOrCreateCsrfToken(request);
        request.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SecurityUtil.validateCsrfToken(request)) {
            request.setAttribute("errorMessage", "Session security token validation failed. Please try again.");
            request.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(request, response);
            return;
        }

        String usernameOrEmail = request.getParameter("usernameOrEmail");
        String password = request.getParameter("password");

        if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("errorMessage", "Please provide administrator username/email and password.");
            request.setAttribute("usernameOrEmail", usernameOrEmail);
            request.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(request, response);
            return;
        }

        Admin admin = authService.authenticateAdmin(usernameOrEmail, password);

        if (admin != null) {
            // Prevent Session Fixation
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession newSession = request.getSession(true);

            newSession.setAttribute("adminUser", admin);
            newSession.setAttribute("userRole", "ADMIN");
            newSession.setAttribute("adminId", admin.getId());
            newSession.setAttribute("adminName", admin.getFullName());
            newSession.setAttribute("adminUsername", admin.getUsername());

            SecurityUtil.getOrCreateCsrfToken(request);

            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } else {
            request.setAttribute("errorMessage", "Invalid administrator credentials. Access is restricted.");
            request.setAttribute("usernameOrEmail", usernameOrEmail);
            request.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(request, response);
        }
    }
}
