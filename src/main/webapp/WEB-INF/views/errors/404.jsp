<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<%
    request.setAttribute("pageTitle", "404 Page Not Found - Career Recommendation System");
%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="container container-narrow" style="text-align: center; padding: 4rem 1rem;">
    <div style="width: 72px; height: 72px; border-radius: 50%; background: var(--blue-50); color: var(--blue-600); display: flex; align-items: center; justify-content: center; margin: 0 auto 1.5rem auto;">
        <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"></circle>
            <path d="M16 16s-1.5-2-4-2-4 2-4 2"></path>
            <line x1="9" y1="9" x2="9.01" y2="9"></line>
            <line x1="15" y1="9" x2="15.01" y2="9"></line>
        </svg>
    </div>

    <span class="badge badge-blue" style="margin-bottom: 0.5rem;">HTTP 404 Error</span>
    <h1 style="font-size: 2.2rem; color: var(--navy-900); font-weight: 800; margin-bottom: 0.75rem;">
        Page Not Found
    </h1>

    <p style="color: var(--slate-600); font-size: 1.05rem; max-width: 520px; margin: 0 auto 2rem auto; line-height: 1.6;">
        The requested URL was not found on this server. Please check the address or use the navigation links below to return to the application.
    </p>

    <div style="display: flex; gap: 1rem; justify-content: center;">
        <a href="<%= request.getContextPath() %>/" class="btn btn-secondary">Go to Homepage</a>
        <a href="<%= request.getContextPath() %>/student/dashboard" class="btn btn-primary">Student Dashboard</a>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
