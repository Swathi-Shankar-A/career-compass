package com.career.servlet;

import com.career.model.Admin;
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
        request.getRequestDispatcher("/admin/login.html").forward(request, response);
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
            response.sendRedirect(request.getContextPath() + "/admin/login?error=csrf");
            return;
        }

        String usernameOrEmail = request.getParameter("usernameOrEmail");
        String password = request.getParameter("password");

        if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty() || password == null || password.isEmpty()) {
            if (isJson) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JsonUtil.response(false, "Please provide administrator username/email and password."));
                return;
            }
            response.sendRedirect(request.getContextPath() + "/admin/login?error=empty_fields");
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

            if (isJson) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JsonUtil.response(true, "Admin authenticated successfully.", request.getContextPath() + "/admin/dashboard"));
                return;
            }
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } else {
            if (isJson) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JsonUtil.response(false, "Invalid administrator credentials. Access is restricted."));
                return;
            }
            response.sendRedirect(request.getContextPath() + "/admin/login?error=invalid_credentials");
        }
    }
}
