<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.career.model.AssessmentSubmission, com.career.model.StudentResponse, com.career.model.CareerRecommendation, java.util.List, com.career.util.SecurityUtil" %>
<%
    request.setAttribute("pageTitle", "Student Response Audit - Career Recommendation System");
    AssessmentSubmission submission = (AssessmentSubmission) request.getAttribute("submission");
    List<StudentResponse> responses = (List<StudentResponse>) request.getAttribute("responses");
    List<CareerRecommendation> recommendations = (List<CareerRecommendation>) request.getAttribute("recommendations");
%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="container">
    <div style="margin-bottom: 1.5rem; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
        <a href="<%= request.getContextPath() %>/admin/students?studentId=<%= (submission != null) ? submission.getStudentId() : "" %>" class="btn btn-secondary btn-sm">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="19" y1="12" x2="5" y2="12"></line>
                <polyline points="12 19 5 12 12 5"></polyline>
            </svg>
            <span>Back to Student Profile</span>
        </a>

        <button type="button" onclick="window.print()" class="btn btn-navy btn-sm">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="6 9 6 2 18 2 18 9"></polyline>
                <path d="M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2"></path>
                <rect x="6" y="14" width="12" height="8"></rect>
            </svg>
            <span>Print Audit Report</span>
        </button>
    </div>

    <% if (submission != null) { %>
        <!-- Submission & Student Meta -->
        <div class="card" style="margin-bottom: 2rem;">
            <div style="display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 1rem; margin-bottom: 1rem;">
                <div>
                    <span class="badge badge-teal">Submission Record #<%= submission.getId() %></span>
                    <h1 style="font-size: 1.6rem; color: var(--navy-900); font-weight: 800; margin-top: 0.25rem;">
                        Student Responses: <%= SecurityUtil.escapeHtml(submission.getStudentName()) %>
                    </h1>
                </div>
                <div style="font-size: 0.85rem; color: var(--slate-500); text-align: right;">
                    Submitted: <strong><%= submission.getSubmissionDate() %></strong>
                </div>
            </div>

            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; font-size: 0.9rem; background: var(--slate-50); padding: 1rem; border-radius: var(--radius-md);">
                <div><strong>Email:</strong> <%= SecurityUtil.escapeHtml(submission.getStudentEmail()) %></div>
                <div><strong>College:</strong> <%= SecurityUtil.escapeHtml(submission.getStudentCollege()) %></div>
                <div><strong>Degree:</strong> <%= SecurityUtil.escapeHtml(submission.getStudentDegree()) %></div>
                <div><strong>Year:</strong> Year <%= submission.getStudentYear() %></div>
            </div>

            <% if (submission.getSummaryNotes() != null && !submission.getSummaryNotes().isEmpty()) { %>
                <div style="margin-top: 1rem; font-size: 0.95rem; color: var(--slate-700); line-height: 1.6;">
                    <strong>Algorithmic Profile Summary:</strong> <%= SecurityUtil.escapeHtml(submission.getSummaryNotes()) %>
                </div>
            <% } %>
        </div>

        <!-- Top 5 Recommended Careers for this Submission -->
        <div class="card" style="margin-bottom: 2rem;">
            <div class="card-header">
                <div>
                    <h2 style="font-size: 1.25rem; color: var(--navy-900); font-weight: 700;">
                        Generated Top 5 Recommendations
                    </h2>
                    <div style="font-size: 0.85rem; color: var(--slate-500);">Computed by Core Java RecommendationEngine.</div>
                </div>
            </div>

            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th style="width: 70px;">Rank</th>
                            <th>Career Name</th>
                            <th>Match Score</th>
                            <th>Algorithmic Match Reason</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% 
                            if (recommendations != null) {
                                for (CareerRecommendation rec : recommendations) { 
                        %>
                            <tr>
                                <td><div class="badge badge-navy">#<%= rec.getRankOrder() %></div></td>
                                <td><strong><%= SecurityUtil.escapeHtml(rec.getCareer().getName()) %></strong></td>
                                <td><strong style="color: var(--teal-700);"><%= rec.getCompatibilityScore() %>%</strong></td>
                                <td style="font-size: 0.85rem; color: var(--slate-700);"><%= SecurityUtil.escapeHtml(rec.getMatchReason()) %></td>
                            </tr>
                        <% 
                                }
                            } 
                        %>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- All 20 Student Responses Snapshot -->
        <div class="card">
            <div class="card-header">
                <div>
                    <h2 style="font-size: 1.25rem; color: var(--navy-900); font-weight: 700;">
                        Full Assessment Responses (All 20 Questions)
                    </h2>
                    <div style="font-size: 0.85rem; color: var(--slate-500);">
                        Historical snapshot of question texts and student's chosen option ratings.
                    </div>
                </div>
            </div>

            <% if (responses != null && !responses.isEmpty()) { %>
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th style="width: 60px;">Q#</th>
                                <th>Category</th>
                                <th>Question Text Snapshot</th>
                                <th>Selected Option</th>
                                <th style="width: 120px;">Rating (1-4)</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% 
                                int qNum = 1;
                                for (StudentResponse r : responses) { 
                            %>
                                <tr>
                                    <td><strong>#<%= qNum++ %></strong></td>
                                    <td><span class="badge badge-blue"><%= (r.getCategory() != null) ? SecurityUtil.escapeHtml(r.getCategory()) : "General" %></span></td>
                                    <td style="max-width: 440px;"><%= SecurityUtil.escapeHtml(r.getQuestionTextSnapshot()) %></td>
                                    <td><strong><%= SecurityUtil.escapeHtml(r.getSelectedOptionSnapshot()) %></strong></td>
                                    <td>
                                        <% if (r.getRatingValue() == 4) { %>
                                            <span class="badge badge-success">4 / 4 (Very)</span>
                                        <% } else if (r.getRatingValue() == 3) { %>
                                            <span class="badge badge-teal">3 / 4 (Moderate)</span>
                                        <% } else if (r.getRatingValue() == 2) { %>
                                            <span class="badge badge-warning">2 / 4 (Somewhat)</span>
                                        <% } else { %>
                                            <span class="badge badge-slate">1 / 4 (Not very)</span>
                                        <% } %>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            <% } %>
        </div>
    <% } %>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
