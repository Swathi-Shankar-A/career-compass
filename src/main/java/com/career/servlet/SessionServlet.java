package com.career.servlet;

import com.career.model.Admin;
import com.career.model.Student;
import com.career.util.JsonUtil;
import com.career.util.SecurityUtil;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Returns active authentication status, user role, user profile summary,
 * and current CSRF token for dynamic JavaScript clients.
 */
public class SessionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");

        String csrfToken = SecurityUtil.getOrCreateCsrfToken(request);
        HttpSession session = request.getSession(false);

        StringBuilder sb = new StringBuilder("{");
        sb.append("\"csrfToken\":\"").append(JsonUtil.escape(csrfToken)).append("\"");

        if (session != null && "STUDENT".equals(session.getAttribute("userRole")) && session.getAttribute("studentUser") != null) {
            Student s = (Student) session.getAttribute("studentUser");
            sb.append(",\"authenticated\":true");
            sb.append(",\"role\":\"STUDENT\"");
            sb.append(",\"id\":").append(s.getId());
            sb.append(",\"fullName\":\"").append(JsonUtil.escape(s.getFullName())).append("\"");
            sb.append(",\"email\":\"").append(JsonUtil.escape(s.getEmail())).append("\"");
            sb.append(",\"collegeName\":\"").append(JsonUtil.escape(s.getCollegeName())).append("\"");
            sb.append(",\"degree\":\"").append(JsonUtil.escape(s.getDegree())).append("\"");
            sb.append(",\"currentYear\":").append(s.getCurrentYear());
        } else if (session != null && "ADMIN".equals(session.getAttribute("userRole")) && session.getAttribute("adminUser") != null) {
            Admin a = (Admin) session.getAttribute("adminUser");
            sb.append(",\"authenticated\":true");
            sb.append(",\"role\":\"ADMIN\"");
            sb.append(",\"id\":").append(a.getId());
            sb.append(",\"username\":\"").append(JsonUtil.escape(a.getUsername())).append("\"");
            sb.append(",\"fullName\":\"").append(JsonUtil.escape(a.getFullName())).append("\"");
        } else {
            sb.append(",\"authenticated\":false");
            sb.append(",\"role\":null");
        }
        sb.append("}");

        response.getWriter().write(sb.toString());
    }
}
