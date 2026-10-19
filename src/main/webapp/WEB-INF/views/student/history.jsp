<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.career.model.AssessmentSubmission, com.career.model.CareerRecommendation, java.util.List, com.career.util.SecurityUtil" %>
<%
    request.setAttribute("pageTitle", "Assessment History - Career Recommendation System");
    List<AssessmentSubmission> history = (List<AssessmentSubmission>) request.getAttribute("history");
%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="container container-narrow">
    <div style="margin-bottom: 2rem; display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 1rem;">
        <div>
            <span class="badge badge-blue" style="margin-bottom: 0.5rem;">Historical Attempts</span>
            <h1 style="font-size: 2rem; color: var(--navy-900); font-weight: 800;">
                Assessment History
            </h1>
            <p style="color: var(--slate-600); font-size: 0.95rem;">
                Review all your previous career assessments and see how your recommendations have shaped over time.
            </p>
        </div>

        <div>
            <a href="<%= request.getContextPath() %>/student/assessment" class="btn btn-primary">
                Take New Assessment
            </a>
        </div>
    </div>

    <% if (history != null && !history.isEmpty()) { %>
        <div style="display: flex; flex-direction: column; gap: 1.25rem;">
            <% 
                int attemptNum = history.size();
                for (AssessmentSubmission sub : history) {
                    CareerRecommendation topRec = (sub.getRecommendations() != null && !sub.getRecommendations().isEmpty()) 
                            ? sub.getRecommendations().get(0) : null;
            %>
                <div class="card card-hover" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1.5rem;">
                    <div>
                        <div style="display: flex; align-items: center; gap: 0.75rem; margin-bottom: 0.35rem;">
                            <span class="badge badge-teal">Attempt #<%= attemptNum-- %></span>
                            <span style="font-size: 0.85rem; color: var(--slate-500);"><%= sub.getSubmissionDate() %></span>
                        </div>

                        <% if (topRec != null) { %>
                            <h2 style="font-size: 1.25rem; font-weight: 700; color: var(--navy-900); margin-bottom: 0.35rem;">
                                Top Recommendation: <%= SecurityUtil.escapeHtml(topRec.getCareer().getName()) %>
                            </h2>
                            <div style="display: flex; align-items: center; gap: 0.5rem; font-size: 0.9rem; color: var(--slate-600);">
                                <span>Compatibility Match:</span>
                                <strong style="color: var(--teal-700);"><%= topRec.getCompatibilityScore() %>%</strong>
                            </div>
                        <% } else { %>
                            <h2 style="font-size: 1.2rem; font-weight: 600; color: var(--slate-700);">Completed Assessment</h2>
                        <% } %>
                    </div>

                    <div>
                        <a href="<%= request.getContextPath() %>/student/results?submissionId=<%= sub.getId() %>" class="btn btn-outline-primary btn-sm">
                            <span>View Full Report</span>
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <line x1="5" y1="12" x2="19" y2="12"></line>
                                <polyline points="12 5 19 12 12 19"></polyline>
                            </svg>
                        </a>
                    </div>
                </div>
            <% } %>
        </div>
    <% } else { %>
        <div class="card" style="text-align: center; padding: 3rem 1.5rem;">
            <div style="width: 56px; height: 56px; border-radius: 50%; background: var(--blue-50); color: var(--blue-600); display: flex; align-items: center; justify-content: center; margin: 0 auto 1rem auto;">
                <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"></circle>
                    <polyline points="12 6 12 12 14 14"></polyline>
                </svg>
            </div>
            <h2 style="font-size: 1.3rem; color: var(--navy-900); font-weight: 700; margin-bottom: 0.5rem;">
                No Past Assessments Found
            </h2>
            <p style="color: var(--slate-600); max-width: 480px; margin: 0 auto 1.5rem auto; font-size: 0.95rem;">
                You haven't submitted any career assessments yet. Take our 20-question assessment to generate your personalized Top 5 career matches.
            </p>
            <a href="<%= request.getContextPath() %>/student/assessment" class="btn btn-primary">
                Take First Assessment
            </a>
        </div>
    <% } %>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
