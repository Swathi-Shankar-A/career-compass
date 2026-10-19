<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.career.util.SecurityUtil" %>
<%
    request.setAttribute("pageTitle", "Student Login - Career Recommendation System");
    String csrfToken = SecurityUtil.getOrCreateCsrfToken(request);
    String errorMessage = (String) request.getAttribute("errorMessage");
    String email = (String) request.getAttribute("email");
    String registered = request.getParameter("registered");
    String loggedOut = request.getParameter("logged_out");
    String errorParam = request.getParameter("error");
%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="auth-wrapper">
    <div class="auth-card">
        <div class="auth-header">
            <h1>Student Login</h1>
            <p>Access your student portal, take assessments, and review past recommendations.</p>
        </div>

        <% if ("true".equals(registered)) { %>
            <div class="alert alert-success">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                    <polyline points="22 4 12 14.01 9 11.01"></polyline>
                </svg>
                <div>Registration completed successfully! Please sign in with your credentials.</div>
            </div>
        <% } %>

        <% if ("true".equals(loggedOut)) { %>
            <div class="alert alert-info">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"></circle>
                    <line x1="12" y1="16" x2="12" y2="12"></line>
                    <line x1="12" y1="8" x2="12.01" y2="8"></line>
                </svg>
                <div>You have been safely logged out.</div>
            </div>
        <% } %>

        <% if ("auth_required".equals(errorParam)) { %>
            <div class="alert alert-warning">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"></circle>
                    <line x1="12" y1="8" x2="12" y2="12"></line>
                    <line x1="12" y1="16" x2="12.01" y2="16"></line>
                </svg>
                <div>Please sign in to access student features.</div>
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

        <form action="<%= request.getContextPath() %>/login" method="POST" novalidate>
            <input type="hidden" name="csrfToken" value="<%= csrfToken %>">

            <div class="form-group">
                <label for="email" class="form-label">Email Address</label>
                <input type="email" id="email" name="email" class="form-control"
                       placeholder="student@college.edu" required autofocus
                       value="<%= (email != null) ? SecurityUtil.escapeHtml(email) : "" %>">
            </div>

            <div class="form-group">
                <label for="password" class="form-label">Password</label>
                <input type="password" id="password" name="password" class="form-control"
                       placeholder="Enter your password" required>
            </div>

            <button type="submit" class="btn btn-primary btn-block btn-lg" style="margin-top: 1.5rem;">
                Sign In to Student Portal
            </button>
        </form>

        <div class="auth-footer">
            Don't have an account yet? <a href="<%= request.getContextPath() %>/register">Register as Student</a>
            <div style="margin-top: 0.75rem; font-size: 0.8rem;">
                Looking for administrative access? <a href="<%= request.getContextPath() %>/admin/login">Admin Login</a>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
