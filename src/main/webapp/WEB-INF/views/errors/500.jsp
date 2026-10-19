<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<%
    request.setAttribute("pageTitle", "500 Server Error - Career Recommendation System");
%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="container container-narrow" style="text-align: center; padding: 4rem 1rem;">
    <div style="width: 72px; height: 72px; border-radius: 50%; background: var(--warning-50); color: var(--warning-600); display: flex; align-items: center; justify-content: center; margin: 0 auto 1.5rem auto;">
        <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path>
            <line x1="12" y1="9" x2="12" y2="13"></line>
            <line x1="12" y1="17" x2="12.01" y2="17"></line>
        </svg>
    </div>

    <span class="badge badge-warning" style="margin-bottom: 0.5rem;">HTTP 500 Internal Server Error</span>
    <h1 style="font-size: 2.2rem; color: var(--navy-900); font-weight: 800; margin-bottom: 0.75rem;">
        An Unexpected Error Occurred
    </h1>

    <p style="color: var(--slate-600); font-size: 1.05rem; max-width: 520px; margin: 0 auto 2rem auto; line-height: 1.6;">
        The server encountered an internal issue while processing your request. Sensitive database error details have been logged safely on the server and are hidden to protect application security.
    </p>

    <div style="display: flex; gap: 1rem; justify-content: center;">
        <a href="<%= request.getContextPath() %>/" class="btn btn-secondary">Return Home</a>
        <a href="javascript:location.reload()" class="btn btn-primary">Retry Action</a>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
