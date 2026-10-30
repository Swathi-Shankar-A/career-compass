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
 * Filter protecting all /admin/* routes (excluding /admin/login).
 * Ensures only authenticated administrators with role 'ADMIN' can access admin pages.
 */
public class AdminAuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Prevent browser caching of sensitive admin pages
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setDateHeader("Expires", 0);

        String path = req.getRequestURI();
        if (path.endsWith("/admin/login") || path.endsWith("/admin/login.html")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        Admin admin = (session != null) ? (Admin) session.getAttribute("adminUser") : null;
        String role = (session != null) ? (String) session.getAttribute("userRole") : null;

        if (admin == null || !"ADMIN".equals(role)) {
            String accept = req.getHeader("Accept");
            if (accept != null && accept.contains("application/json")) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.setContentType("application/json;charset=UTF-8");
                res.getWriter().write("{\"authenticated\":false,\"redirect\":\"" + req.getContextPath() + "/admin/login?error=admin_auth_required\"}");
                return;
            }
            res.sendRedirect(req.getContextPath() + "/admin/login?error=admin_auth_required");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
