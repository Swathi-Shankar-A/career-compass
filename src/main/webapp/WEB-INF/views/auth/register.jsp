<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.career.util.SecurityUtil" %>
<%
    request.setAttribute("pageTitle", "Student Registration - Career Recommendation System");
    String csrfToken = SecurityUtil.getOrCreateCsrfToken(request);
    String errorMessage = (String) request.getAttribute("errorMessage");
    String fullName = (String) request.getAttribute("fullName");
    String email = (String) request.getAttribute("email");
    String collegeName = (String) request.getAttribute("collegeName");
    String degree = (String) request.getAttribute("degree");
    Integer currentYear = (Integer) request.getAttribute("currentYear");
    if (degree == null) degree = "B.Tech CSE";
    if (currentYear == null) currentYear = 1;
%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="auth-wrapper">
    <div class="auth-card">
        <div class="auth-header">
            <h1>Create Student Account</h1>
            <p>Enter your academic details to access the career recommendation assessment.</p>
        </div>

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

        <form action="<%= request.getContextPath() %>/register" method="POST" id="registerForm" novalidate>
            <!-- CSRF Token Protection -->
            <input type="hidden" name="csrfToken" value="<%= csrfToken %>">

            <div class="form-group">
                <label for="fullName" class="form-label">Full Name <span class="required">*</span></label>
                <input type="text" id="fullName" name="fullName" class="form-control"
                       placeholder="e.g. John Doe" required
                       value="<%= (fullName != null) ? SecurityUtil.escapeHtml(fullName) : "" %>">
            </div>

            <div class="form-group">
                <label for="email" class="form-label">Email Address <span class="required">*</span></label>
                <input type="email" id="email" name="email" class="form-control"
                       placeholder="student@college.edu" required
                       value="<%= (email != null) ? SecurityUtil.escapeHtml(email) : "" %>">
                <div class="form-hint">Used for logging in and accessing your assessment history.</div>
            </div>

            <div class="form-group">
                <label for="collegeName" class="form-label">College / University Name <span class="required">*</span></label>
                <input type="text" id="collegeName" name="collegeName" class="form-control"
                       placeholder="e.g. National Institute of Technology" required
                       value="<%= (collegeName != null) ? SecurityUtil.escapeHtml(collegeName) : "" %>">
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="degree" class="form-label">Degree / Department <span class="required">*</span></label>
                    <select id="degree" name="degree" class="form-control" required>
                        <option value="B.Tech CSE" <%= "B.Tech CSE".equals(degree) ? "selected" : "" %>>B.Tech Computer Science &amp; Engg</option>
                        <option value="B.Tech CSE (AI &amp; ML)" <%= "B.Tech CSE (AI &amp; ML)".equals(degree) ? "selected" : "" %>>B.Tech CSE (AI &amp; ML)</option>
                        <option value="B.Tech CSE (Data Science)" <%= "B.Tech CSE (Data Science)".equals(degree) ? "selected" : "" %>>B.Tech CSE (Data Science)</option>
                        <option value="B.Tech CSE (Cybersecurity)" <%= "B.Tech CSE (Cybersecurity)".equals(degree) ? "selected" : "" %>>B.Tech CSE (Cybersecurity)</option>
                        <option value="B.Tech Information Technology" <%= "B.Tech Information Technology".equals(degree) ? "selected" : "" %>>B.Tech Information Technology</option>
                        <option value="B.Tech CSE (IoT &amp; Systems)" <%= "B.Tech CSE (IoT &amp; Systems)".equals(degree) ? "selected" : "" %>>B.Tech CSE (IoT &amp; Systems)</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="currentYear" class="form-label">Current Year of Study <span class="required">*</span></label>
                    <select id="currentYear" name="currentYear" class="form-control" required>
                        <option value="1" <%= (currentYear == 1) ? "selected" : "" %>>1st Year (Freshman)</option>
                        <option value="2" <%= (currentYear == 2) ? "selected" : "" %>>2nd Year (Sophomore)</option>
                        <option value="3" <%= (currentYear == 3) ? "selected" : "" %>>3rd Year (Junior)</option>
                        <option value="4" <%= (currentYear == 4) ? "selected" : "" %>>4th Year (Senior)</option>
                    </select>
                </div>
            </div>

            <div class="form-group">
                <label for="password" class="form-label">Password <span class="required">*</span></label>
                <input type="password" id="password" name="password" class="form-control"
                       placeholder="At least 6 characters" minlength="6" required>
                <div class="form-hint">Stored securely using PBKDF2 with SHA-256 and salted iterations.</div>
            </div>

            <div class="form-group">
                <label for="confirmPassword" class="form-label">Confirm Password <span class="required">*</span></label>
                <input type="password" id="confirmPassword" name="confirmPassword" class="form-control"
                       placeholder="Re-enter your password" minlength="6" required>
            </div>

            <button type="submit" class="btn btn-primary btn-block btn-lg" style="margin-top: 1.5rem;">
                Create Account &amp; Proceed
            </button>
        </form>

        <div class="auth-footer">
            Already have an account? <a href="<%= request.getContextPath() %>/login">Sign in here</a>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
