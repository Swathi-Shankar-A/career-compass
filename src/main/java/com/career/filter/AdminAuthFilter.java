package com.career.filter;

import com.career.model.Admin;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Filter protecting all /admin/* routes.
 * Ensures only authenticated administrators with role 'ADMIN' can access admin functions.
 * Authenticated students attempting to access /admin/* are denied with HTTP 403.
 */
public class AdminAuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String relativePath = uri.substring(contextPath.length());

        // Allow public access to the admin login page
        if (relativePath.equals("/admin/login") || relativePath.equals("/admin/login.jsp")) {
            chain.doFilter(request, response);
            return;
        }

        // Prevent browser caching of admin dashboard and sensitive data
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setDateHeader("Expires", 0);

        HttpSession session = req.getSession(false);
        Admin admin = (session != null) ? (Admin) session.getAttribute("adminUser") : null;
        String role = (session != null) ? (String) session.getAttribute("userRole") : null;

        if (admin != null && "ADMIN".equals(role)) {
            // Authorized administrator
            chain.doFilter(request, response);
            return;
        }

        // If logged in as student, block with HTTP 403 Forbidden
        if (session != null && "STUDENT".equals(session.getAttribute("userRole"))) {
            req.setAttribute("errorMessage", "Access Denied: Students are not authorized to view the Administrative portal.");
            req.getRequestDispatcher("/WEB-INF/views/errors/403.jsp").forward(req, res);
            return;
        }

        // Not logged in -> redirect to admin login
        res.sendRedirect(contextPath + "/admin/login?error=admin_auth_required");
    }

    @Override
    public void destroy() {}
}
