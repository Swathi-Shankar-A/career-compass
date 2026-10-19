package com.career.filter;

import com.career.model.Student;

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
 * Filter protecting all /student/* routes.
 * Ensures only authenticated students with role 'STUDENT' can access student pages.
 */
public class StudentAuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Prevent browser caching of sensitive authenticated student pages
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setDateHeader("Expires", 0);

        HttpSession session = req.getSession(false);
        Student student = (session != null) ? (Student) session.getAttribute("studentUser") : null;
        String role = (session != null) ? (String) session.getAttribute("userRole") : null;

        if (student == null || !"STUDENT".equals(role)) {
            // Unauthenticated or invalid role -> redirect to student login
            res.sendRedirect(req.getContextPath() + "/login?error=auth_required");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
