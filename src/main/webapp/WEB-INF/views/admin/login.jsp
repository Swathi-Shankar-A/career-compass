<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.career.util.SecurityUtil" %>
<%
    request.setAttribute("pageTitle", "Administrator Login - Career Recommendation System");
    String csrfToken = SecurityUtil.getOrCreateCsrfToken(request);
    String errorMessage = (String) request.getAttribute("errorMessage");
    String usernameOrEmail = (String) request.getAttribute("usernameOrEmail");
    String loggedOut = request.getParameter("logged_out");
    String errorParam = request.getParameter("error");
%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="auth-wrapper">
    <div class="auth-card" style="border-top: 4px solid var(--teal-600);">
        <div class="auth-header">
            <span class="badge badge-teal" style="margin-bottom: 0.5rem;">Restricted Portal</span>
            <h1>Administrator Login</h1>
            <p>Authorized access for managing questions, reviewing student submissions, and system configuration.</p>
        </div>

        <% if ("true".equals(loggedOut)) { %>
            <div class="alert alert-info">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"></circle>
                    <line x1="12" y1="16" x2="12" y2="12"></line>
                    <line x1="12" y1="8" x2="12.01" y2="8"></line>
                </svg>
                <div>Administrator logged out successfully.</div>
            </div>
        <% } %>

        <% if ("admin_auth_required".equals(errorParam)) { %>
            <div class="alert alert-warning">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"></circle>
                    <line x1="12" y1="8" x2="12" y2="12"></line>
                    <line x1="12" y1="16" x2="12.01" y2="16"></line>
                </svg>
                <div>Administrator credentials required to access the admin area.</div>
            </div>
        <% } %>

        <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
            <div class="alert alert-danger" role="alert">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"></circle>
                    <line x1="12" y1="8" x2="12" y2="12"></line>
                    <line x1="12" y1="16" x2="12.01" y2="16"></line>
                </svg>
                <div><%= SecurityUtil.escapeHtml(errorMessage) %></div>
            </div>
        <% } %>

        <form action="<%= request.getContextPath() %>/admin/login" method="POST" novalidate>
            <input type="hidden" name="csrfToken" value="<%= csrfToken %>">

            <div class="form-group">
                <label for="usernameOrEmail" class="form-label">Admin Username or Email</label>
                <input type="text" id="usernameOrEmail" name="usernameOrEmail" class="form-control"
                       placeholder="e.g. admin" required autofocus
                       value="<%= (usernameOrEmail != null) ? SecurityUtil.escapeHtml(usernameOrEmail) : "" %>">
            </div>

            <div class="form-group">
                <label for="password" class="form-label">Admin Password</label>
                <input type="password" id="password" name="password" class="form-control"
                       placeholder="Enter admin password" required>
            </div>

            <button type="submit" class="btn btn-navy btn-block btn-lg" style="margin-top: 1.5rem;">
                Authenticate Administrator
            </button>
        </form>

        <div class="auth-footer" style="font-size: 0.85rem; color: var(--slate-500);">
            Student looking to take an assessment? <a href="<%= request.getContextPath() %>/login">Student Portal</a>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
