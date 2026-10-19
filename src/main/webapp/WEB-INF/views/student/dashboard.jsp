<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.career.model.Student, com.career.model.AssessmentSubmission, com.career.model.CareerRecommendation, java.util.List, com.career.util.SecurityUtil" %>
<%
    request.setAttribute("pageTitle", "Student Dashboard - Career Recommendation System");
    Student student = (Student) session.getAttribute("studentUser");
    AssessmentSubmission latest = (AssessmentSubmission) request.getAttribute("latestSubmission");
    Integer totalSubmissions = (Integer) request.getAttribute("totalSubmissions");
    Boolean assessmentReady = (Boolean) request.getAttribute("assessmentReady");
    Integer activeQuestionCount = (Integer) request.getAttribute("activeQuestionCount");
    String errorMessage = (String) request.getAttribute("errorMessage");
    String msg = request.getParameter("msg");
    if (totalSubmissions == null) totalSubmissions = 0;
    if (assessmentReady == null) assessmentReady = true;
    if (activeQuestionCount == null) activeQuestionCount = 20;
%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="container">
    <!-- Welcome Header -->
    <div style="margin-bottom: 2rem;">
        <div style="display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 1rem;">
            <div>
                <span class="badge badge-blue" style="margin-bottom: 0.5rem;">Student Portal</span>
                <h1 style="font-size: 2rem; color: var(--navy-900); font-weight: 800;">
                    Welcome back, <%= SecurityUtil.escapeHtml(student.getFullName()) %>!
                </h1>
                <p style="color: var(--slate-600); font-size: 1rem; margin-top: 0.25rem;">
                    <%= SecurityUtil.escapeHtml(student.getCollegeName()) %> &bull;
                    <%= SecurityUtil.escapeHtml(student.getDegree()) %> &bull;
                    Year <%= student.getCurrentYear() %>
                </p>
            </div>

            <div>
                <a href="<%= request.getContextPath() %>/student/assessment" class="btn btn-primary btn-lg">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                        <polyline points="14 2 14 8 20 8"></polyline>
                        <line x1="16" y1="13" x2="8" y2="13"></line>
                        <line x1="16" y1="17" x2="8" y2="17"></line>
                        <polyline points="10 9 9 9 8 9"></polyline>
                    </svg>
                    <span><%= (totalSubmissions > 0) ? "Retake Assessment" : "Start Career Assessment" %></span>
                </a>
            </div>
        </div>
    </div>

    <% if ("no_assessment".equals(msg)) { %>
        <div class="alert alert-info">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"></circle>
                <line x1="12" y1="16" x2="12" y2="12"></line>
                <line x1="12" y1="8" x2="12.01" y2="8"></line>
            </svg>
            <div>You have not taken an assessment yet. Please complete the 20-question assessment to view your recommendations!</div>
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

    <!-- Summary / Latest Recommendation Card -->
    <% if (latest != null && latest.getRecommendations() != null && !latest.getRecommendations().isEmpty()) { 
        CareerRecommendation topRec = latest.getRecommendations().get(0);
    %>
        <div class="card" style="background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%); border-color: #bae6fd; margin-bottom: 2rem;">
            <div style="display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 1rem;">
                <div>
                    <span class="badge badge-teal" style="margin-bottom: 0.5rem;">Latest Assessment Result</span>
                    <h2 style="font-size: 1.5rem; color: var(--navy-900); font-weight: 700; margin-bottom: 0.35rem;">
                        Top Career Match: <%= SecurityUtil.escapeHtml(topRec.getCareer().getName()) %>
                    </h2>
                    <p style="color: var(--slate-700); font-size: 0.95rem; max-width: 700px; margin-bottom: 0.75rem;">
                        <%= SecurityUtil.escapeHtml(topRec.getCareer().getShortDescription()) %>
                    </p>
                    <div style="font-size: 0.85rem; color: var(--slate-600);">
                        Assessed on: <strong><%= latest.getSubmissionDate() %></strong> &bull;
                        Match Compatibility: <strong style="color: var(--teal-700);"><%= topRec.getCompatibilityScore() %>%</strong>
                    </div>
                </div>

                <div style="display: flex; flex-direction: column; gap: 0.5rem; align-items: flex-end;">
                    <a href="<%= request.getContextPath() %>/student/results?submissionId=<%= latest.getId() %>" class="btn btn-primary">
                        View Full Top 5 Report
                    </a>
                    <a href="<%= request.getContextPath() %>/student/history" class="btn btn-secondary btn-sm">
                        View All <%= totalSubmissions %> Attempts
                    </a>
                </div>
            </div>
        </div>
    <% } else { %>
        <div class="card" style="margin-bottom: 2rem; text-align: center; padding: 3rem 1.5rem;">
            <div style="width: 64px; height: 64px; border-radius: 50%; background: var(--blue-100); color: var(--blue-600); display: flex; align-items: center; justify-content: center; margin: 0 auto 1.25rem auto;">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"></circle>
                    <polygon points="16.24 7.76 14.12 14.12 7.76 16.24 9.88 9.88 16.24 7.76"></polygon>
                </svg>
            </div>
            <h2 style="font-size: 1.4rem; color: var(--navy-900); font-weight: 700; margin-bottom: 0.5rem;">
                No Career Assessment Completed Yet
            </h2>
            <p style="color: var(--slate-600); max-width: 550px; margin: 0 auto 1.5rem auto; font-size: 0.95rem;">
                Answer 20 multiple-choice questions rating your interests across various areas of Computer Science to generate your personalized Top 5 career matches.
            </p>
            <a href="<%= request.getContextPath() %>/student/assessment" class="btn btn-primary btn-lg">
                Begin Your 20-Question Assessment
            </a>
        </div>
    <% } %>

    <!-- Action Cards Grid -->
    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: 1.5rem;">
        <div class="card card-hover">
            <div style="display: flex; align-items: center; gap: 0.75rem; margin-bottom: 0.75rem;">
                <div style="width: 40px; height: 40px; border-radius: var(--radius-md); background: var(--blue-50); color: var(--blue-600); display: flex; align-items: center; justify-content: center;">
                    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                        <polyline points="14 2 14 8 20 8"></polyline>
                        <line x1="16" y1="13" x2="8" y2="13"></line>
                        <line x1="16" y1="17" x2="8" y2="17"></line>
                    </svg>
                </div>
                <h3 style="font-size: 1.15rem; color: var(--navy-900); font-weight: 700;">Career Assessment</h3>
            </div>
            <p style="color: var(--slate-600); font-size: 0.9rem; margin-bottom: 1.25rem;">
                20 beginner-friendly questions assessing technical and organizational preferences.
            </p>
            <a href="<%= request.getContextPath() %>/student/assessment" class="btn btn-outline-primary btn-sm btn-block">
                <%= (totalSubmissions > 0) ? "Retake Assessment" : "Take Assessment" %>
            </a>
        </div>

        <div class="card card-hover">
            <div style="display: flex; align-items: center; gap: 0.75rem; margin-bottom: 0.75rem;">
                <div style="width: 40px; height: 40px; border-radius: var(--radius-md); background: var(--teal-50); color: var(--teal-600); display: flex; align-items: center; justify-content: center;">
                    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <circle cx="12" cy="12" r="10"></circle>
                        <polyline points="12 6 12 12 14 14"></polyline>
                    </svg>
                </div>
                <h3 style="font-size: 1.15rem; color: var(--navy-900); font-weight: 700;">Assessment History</h3>
            </div>
            <p style="color: var(--slate-600); font-size: 0.9rem; margin-bottom: 1.25rem;">
                View your previous assessment scores, dates, and how your interests have evolved.
            </p>
            <a href="<%= request.getContextPath() %>/student/history" class="btn btn-secondary btn-sm btn-block">
                View Past Submissions (<%= totalSubmissions %>)
            </a>
        </div>

        <div class="card card-hover">
            <div style="display: flex; align-items: center; gap: 0.75rem; margin-bottom: 0.75rem;">
                <div style="width: 40px; height: 40px; border-radius: var(--radius-md); background: var(--slate-100); color: var(--slate-700); display: flex; align-items: center; justify-content: center;">
                    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                        <circle cx="12" cy="7" r="4"></circle>
                    </svg>
                </div>
                <h3 style="font-size: 1.15rem; color: var(--navy-900); font-weight: 700;">Academic Profile</h3>
            </div>
            <p style="color: var(--slate-600); font-size: 0.9rem; margin-bottom: 1.25rem;">
                Update your student name, college, year of study, or change your password.
            </p>
            <a href="<%= request.getContextPath() %>/student/profile" class="btn btn-secondary btn-sm btn-block">
                Manage Profile
            </a>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
