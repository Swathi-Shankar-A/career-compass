<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<%@ page import="com.career.util.SecurityUtil" %>
<%
    request.setAttribute("pageTitle", "403 Access Denied - Career Recommendation System");
    String errorMessage = (String) request.getAttribute("errorMessage");
    if (errorMessage == null) {
        errorMessage = "You do not have permission to access the requested resource or administrative functionality.";
    }
%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="container container-narrow" style="text-align: center; padding: 4rem 1rem;">
    <div style="width: 72px; height: 72px; border-radius: 50%; background: var(--danger-50); color: var(--danger-600); display: flex; align-items: center; justify-content: center; margin: 0 auto 1.5rem auto;">
        <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
            <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
        </svg>
    </div>

    <span class="badge badge-danger" style="margin-bottom: 0.5rem;">HTTP 403 Forbidden</span>
    <h1 style="font-size: 2.2rem; color: var(--navy-900); font-weight: 800; margin-bottom: 0.75rem;">
        Access Denied
    </h1>

    <p style="color: var(--slate-600); font-size: 1.05rem; max-width: 520px; margin: 0 auto 2rem auto; line-height: 1.6;">
        <%= SecurityUtil.escapeHtml(errorMessage) %>
    </p>

    <div style="display: flex; gap: 1rem; justify-content: center;">
        <a href="<%= request.getContextPath() %>/" class="btn btn-secondary">Return Home</a>
        <a href="<%= request.getContextPath() %>/login" class="btn btn-primary">Sign In with Authorized Account</a>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
