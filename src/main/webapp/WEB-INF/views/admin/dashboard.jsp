<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.career.model.AssessmentSubmission, com.career.model.CareerRecommendation, java.util.List, com.career.util.SecurityUtil" %>
<%
    request.setAttribute("pageTitle", "Admin Dashboard - Career Recommendation System");
    Integer totalStudents = (Integer) request.getAttribute("totalStudents");
    Integer totalSubmissions = (Integer) request.getAttribute("totalSubmissions");
    Integer activeQuestions = (Integer) request.getAttribute("activeQuestions");
    Integer totalQuestions = (Integer) request.getAttribute("totalQuestions");
    Integer totalCareers = (Integer) request.getAttribute("totalCareers");
    Boolean questionCountValid = (Boolean) request.getAttribute("questionCountValid");
    List<AssessmentSubmission> recentSubmissions = (List<AssessmentSubmission>) request.getAttribute("recentSubmissions");

    if (totalStudents == null) totalStudents = 0;
    if (totalSubmissions == null) totalSubmissions = 0;
    if (activeQuestions == null) activeQuestions = 20;
    if (totalQuestions == null) totalQuestions = 20;
    if (totalCareers == null) totalCareers = 35;
    if (questionCountValid == null) questionCountValid = true;
%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="container">
    <!-- Admin Top Header -->
    <div style="display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 1rem; margin-bottom: 2rem;">
        <div>
            <span class="badge badge-teal" style="margin-bottom: 0.5rem;">System Administration</span>
            <h1 style="font-size: 2rem; color: var(--navy-900); font-weight: 800;">
                Administrator Control Center
            </h1>
            <p style="color: var(--slate-600); font-size: 0.95rem;">
                Overview of student engagement, assessment question integrity, and recommendation metrics.
            </p>
        </div>

        <div style="display: flex; gap: 0.75rem;">
            <a href="<%= request.getContextPath() %>/admin/questions" class="btn btn-primary btn-sm">
                Manage Questions
            </a>
            <a href="<%= request.getContextPath() %>/admin/students" class="btn btn-secondary btn-sm">
                Student Responses
            </a>
            <a href="<%= request.getContextPath() %>/admin/careers" class="btn btn-secondary btn-sm">
                Careers (35)
            </a>
        </div>
    </div>

    <!-- Active Question Count Integrity Check Warning -->
    <% if (!questionCountValid) { %>
        <div class="alert alert-warning" role="alert" style="border-left: 4px solid var(--warning-600);">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path>
                <line x1="12" y1="9" x2="12" y2="13"></line>
                <line x1="12" y1="17" x2="12.01" y2="17"></line>
            </svg>
            <div>
                <strong>Assessment System Notice:</strong> Exactly 20 active questions are required for the standard career assessment. Currently active: <strong><%= activeQuestions %></strong>. 
                New student assessments are paused until exactly 20 questions are active.
                <a href="<%= request.getContextPath() %>/admin/questions" style="text-decoration: underline; font-weight: 600; margin-left: 0.5rem;">Configure Questions</a>
            </div>
        </div>
    <% } %>

    <!-- Database-Derived Metrics Cards -->
    <div class="admin-metrics-grid">
        <div class="metric-card">
            <div class="metric-icon metric-icon-blue">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                    <circle cx="9" cy="7" r="4"></circle>
                    <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                    <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                </svg>
            </div>
            <div>
                <div class="metric-value"><%= totalStudents %></div>
                <div class="metric-label">Registered Students</div>
            </div>
        </div>

        <div class="metric-card">
            <div class="metric-icon metric-icon-teal">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="9 11 12 14 22 4"></polyline>
                    <path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"></path>
                </svg>
            </div>
            <div>
                <div class="metric-value"><%= totalSubmissions %></div>
                <div class="metric-label">Total Submissions</div>
            </div>
        </div>

        <div class="metric-card">
            <div class="metric-icon <%= questionCountValid ? "metric-icon-navy" : "metric-icon-amber" %>">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"></circle>
                    <line x1="12" y1="16" x2="12" y2="12"></line>
                    <line x1="12" y1="8" x2="12.01" y2="8"></line>
                </svg>
            </div>
            <div>
                <div class="metric-value">
                    <%= activeQuestions %> <span style="font-size: 1rem; color: var(--slate-400); font-weight: 500;">/ <%= totalQuestions %></span>
                </div>
                <div class="metric-label">Active Questions <%= questionCountValid ? "(Exact 20)" : "(Needs 20)" %></div>
            </div>
        </div>

        <div class="metric-card">
            <div class="metric-icon metric-icon-blue">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polygon points="12 2 2 7 12 12 22 7 12 2"></polygon>
                    <polyline points="2 17 12 22 22 17"></polyline>
                    <polyline points="2 12 12 17 22 12"></polyline>
                </svg>
            </div>
            <div>
                <div class="metric-value"><%= totalCareers %></div>
                <div class="metric-label">Seeded Careers (35)</div>
            </div>
        </div>
    </div>

    <!-- Recent Submissions Section -->
    <div class="card" style="margin-bottom: 2.5rem;">
        <div class="card-header">
            <div>
                <h2 style="font-size: 1.25rem; color: var(--navy-900); font-weight: 700;">Recent Assessment Submissions</h2>
                <div style="font-size: 0.85rem; color: var(--slate-500);">Real-time submissions processed by the recommendation engine.</div>
            </div>
            <a href="<%= request.getContextPath() %>/admin/students" class="btn btn-secondary btn-sm">
                View All Students
            </a>
        </div>

        <% if (recentSubmissions != null && !recentSubmissions.isEmpty()) { %>
            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Submission ID</th>
                            <th>Student Name</th>
                            <th>Email Address</th>
                            <th>College &amp; Degree</th>
                            <th>Top Recommended Career</th>
                            <th>Date / Time</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (AssessmentSubmission s : recentSubmissions) { 
                            CareerRecommendation top = (s.getRecommendations() != null && !s.getRecommendations().isEmpty()) 
                                    ? s.getRecommendations().get(0) : null;
                        %>
                            <tr>
                                <td><strong>#<%= s.getId() %></strong></td>
                                <td><strong><%= SecurityUtil.escapeHtml(s.getStudentName()) %></strong></td>
                                <td><%= SecurityUtil.escapeHtml(s.getStudentEmail()) %></td>
                                <td><%= SecurityUtil.escapeHtml(s.getStudentCollege()) %> (<%= SecurityUtil.escapeHtml(s.getStudentDegree()) %>)</td>
                                <td>
                                    <% if (top != null) { %>
                                        <span class="badge badge-teal"><%= SecurityUtil.escapeHtml(top.getCareer().getName()) %></span>
                                        <span style="font-size: 0.8rem; font-weight: 600; color: var(--teal-700); margin-left: 0.25rem;"><%= top.getCompatibilityScore() %>%</span>
                                    <% } else { %>
                                        <span style="color: var(--slate-400);">Processing</span>
                                    <% } %>
                                </td>
                                <td><%= s.getSubmissionDate() %></td>
                                <td>
                                    <a href="<%= request.getContextPath() %>/admin/student-responses?submissionId=<%= s.getId() %>" class="btn btn-outline-primary btn-sm">
                                        Inspect Responses
                                    </a>
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        <% } else { %>
            <div style="text-align: center; padding: 2.5rem; color: var(--slate-500);">
                No assessment submissions recorded yet. Once registered students complete the assessment, submissions will appear here.
            </div>
        <% } %>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
