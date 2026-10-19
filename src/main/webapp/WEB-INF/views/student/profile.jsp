<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.career.model.Student, com.career.util.SecurityUtil" %>
<%
    request.setAttribute("pageTitle", "Student Profile - Career Recommendation System");
    Student student = (Student) request.getAttribute("student");
    if (student == null) {
        student = (Student) session.getAttribute("studentUser");
    }
    String csrfToken = SecurityUtil.getOrCreateCsrfToken(request);
    String errorMessage = (String) request.getAttribute("errorMessage");
    String successMessage = (String) request.getAttribute("successMessage");
%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="container container-narrow">
    <div style="margin-bottom: 2rem;">
        <span class="badge badge-blue" style="margin-bottom: 0.5rem;">Academic Settings</span>
        <h1 style="font-size: 2rem; color: var(--navy-900); font-weight: 800;">
            Student Profile
        </h1>
        <p style="color: var(--slate-600); font-size: 0.95rem;">
            View and maintain your enrolled academic details and secure account settings.
        </p>
    </div>

    <% if (successMessage != null && !successMessage.isEmpty()) { %>
        <div class="alert alert-success">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                <polyline points="22 4 12 14.01 9 11.01"></polyline>
            </svg>
            <div><%= SecurityUtil.escapeHtml(successMessage) %></div>
        </div>
    <% } %>

    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
        <div class="alert alert-danger">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"></circle>
                <line x1="12" y1="8" x2="12" y2="12"></line>
                <line x1="12" y1="16" x2="12.01" y2="16"></line>
            </svg>
            <div><%= SecurityUtil.escapeHtml(errorMessage) %></div>
        </div>
    <% } %>

    <!-- Academic Information Card -->
    <div class="card" style="margin-bottom: 2rem;">
        <div class="card-header">
            <h2 style="font-size: 1.25rem; color: var(--navy-900); font-weight: 700;">Academic Information</h2>
            <span class="badge badge-slate">Enrolled Profile</span>
        </div>

        <form action="<%= request.getContextPath() %>/student/profile" method="POST">
            <input type="hidden" name="csrfToken" value="<%= csrfToken %>">
            <input type="hidden" name="action" value="updateProfile">

            <div class="form-group">
                <label for="fullName" class="form-label">Full Name</label>
                <input type="text" id="fullName" name="fullName" class="form-control"
                       value="<%= SecurityUtil.escapeHtml(student.getFullName()) %>" required>
            </div>

            <div class="form-group">
                <label for="email" class="form-label">Registered Email</label>
                <input type="email" id="email" class="form-control"
                       value="<%= SecurityUtil.escapeHtml(student.getEmail()) %>" readonly disabled>
                <div class="form-hint">Email address cannot be changed once registered (primary identifier).</div>
            </div>

            <div class="form-group">
                <label for="collegeName" class="form-label">College / University</label>
                <input type="text" id="collegeName" name="collegeName" class="form-control"
                       value="<%= SecurityUtil.escapeHtml(student.getCollegeName()) %>" required>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="degree" class="form-label">Degree / Department</label>
                    <select id="degree" name="degree" class="form-control" required>
                        <option value="B.Tech CSE" <%= "B.Tech CSE".equals(student.getDegree()) ? "selected" : "" %>>B.Tech Computer Science &amp; Engg</option>
                        <option value="B.Tech CSE (AI &amp; ML)" <%= "B.Tech CSE (AI &amp; ML)".equals(student.getDegree()) ? "selected" : "" %>>B.Tech CSE (AI &amp; ML)</option>
                        <option value="B.Tech CSE (Data Science)" <%= "B.Tech CSE (Data Science)".equals(student.getDegree()) ? "selected" : "" %>>B.Tech CSE (Data Science)</option>
                        <option value="B.Tech CSE (Cybersecurity)" <%= "B.Tech CSE (Cybersecurity)".equals(student.getDegree()) ? "selected" : "" %>>B.Tech CSE (Cybersecurity)</option>
                        <option value="B.Tech Information Technology" <%= "B.Tech Information Technology".equals(student.getDegree()) ? "selected" : "" %>>B.Tech Information Technology</option>
                        <option value="B.Tech CSE (IoT &amp; Systems)" <%= "B.Tech CSE (IoT &amp; Systems)".equals(student.getDegree()) ? "selected" : "" %>>B.Tech CSE (IoT &amp; Systems)</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="currentYear" class="form-label">Current Year</label>
                    <select id="currentYear" name="currentYear" class="form-control" required>
                        <option value="1" <%= (student.getCurrentYear() == 1) ? "selected" : "" %>>Year 1 (Freshman)</option>
                        <option value="2" <%= (student.getCurrentYear() == 2) ? "selected" : "" %>>Year 2 (Sophomore)</option>
                        <option value="3" <%= (student.getCurrentYear() == 3) ? "selected" : "" %>>Year 3 (Junior)</option>
                        <option value="4" <%= (student.getCurrentYear() == 4) ? "selected" : "" %>>Year 4 (Senior)</option>
                    </select>
                </div>
            </div>

            <button type="submit" class="btn btn-primary" style="margin-top: 0.5rem;">
                Save Profile Changes
            </button>
        </form>
    </div>

    <!-- Security & Password Change Card -->
    <div class="card">
        <div class="card-header">
            <h2 style="font-size: 1.25rem; color: var(--navy-900); font-weight: 700;">Account Password</h2>
            <span class="badge badge-blue">PBKDF2 SHA-256</span>
        </div>

        <form action="<%= request.getContextPath() %>/student/profile" method="POST">
            <input type="hidden" name="csrfToken" value="<%= csrfToken %>">
            <input type="hidden" name="action" value="changePassword">

            <div class="form-group">
                <label for="currentPassword" class="form-label">Current Password</label>
                <input type="password" id="currentPassword" name="currentPassword" class="form-control" required>
            </div>

            <div class="form-group">
                <label for="newPassword" class="form-label">New Password (min 6 characters)</label>
                <input type="password" id="newPassword" name="newPassword" class="form-control" minlength="6" required>
            </div>

            <div class="form-group">
                <label for="confirmNewPassword" class="form-label">Confirm New Password</label>
                <input type="password" id="confirmNewPassword" name="confirmNewPassword" class="form-control" minlength="6" required>
            </div>

            <button type="submit" class="btn btn-navy" style="margin-top: 0.5rem;">
                Update Password
            </button>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
