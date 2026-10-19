<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.career.model.AssessmentSubmission, com.career.model.CareerRecommendation, com.career.model.Career, java.util.List, com.career.util.SecurityUtil" %>
<%
    request.setAttribute("pageTitle", "Your Top 5 Career Matches - Career Recommendation System");
    AssessmentSubmission submission = (AssessmentSubmission) request.getAttribute("submission");
    List<CareerRecommendation> recommendations = (List<CareerRecommendation>) request.getAttribute("recommendations");
%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="container">
    <!-- Top Action Bar (No-Print) -->
    <div class="no-print" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; flex-wrap: wrap; gap: 1rem;">
        <a href="<%= request.getContextPath() %>/student/dashboard" class="btn btn-secondary btn-sm">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="19" y1="12" x2="5" y2="12"></line>
                <polyline points="12 19 5 12 12 5"></polyline>
            </svg>
            <span>Back to Dashboard</span>
        </a>

        <div style="display: flex; gap: 0.75rem;">
            <a href="<%= request.getContextPath() %>/student/assessment" class="btn btn-outline-primary btn-sm">
                Retake Assessment
            </a>
            <a href="<%= request.getContextPath() %>/student/history" class="btn btn-secondary btn-sm">
                Assessment History
            </a>
            <button type="button" onclick="window.print()" class="btn btn-navy btn-sm">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="6 9 6 2 18 2 18 9"></polyline>
                    <path d="M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2"></path>
                    <rect x="6" y="14" width="12" height="8"></rect>
                </svg>
                <span>Print Report</span>
            </button>
        </div>
    </div>

    <!-- Results Hero Banner -->
    <section class="results-hero">
        <span class="badge badge-teal" style="margin-bottom: 0.75rem;">Algorithmic Recommendation Engine</span>
        <h1>Your Top 5 Career Matches</h1>
        <p class="results-summary-text">
            <% if (submission != null && submission.getSummaryNotes() != null) { %>
                <%= SecurityUtil.escapeHtml(submission.getSummaryNotes()) %>
            <% } else { %>
                Based on your ratings across 20 computer science disciplines, here are your five highest-scoring career pathways.
            <% } %>
        </p>

        <div style="margin-top: 1.25rem; font-size: 0.85rem; color: var(--slate-300);">
            Assessment submitted on: <%= (submission != null) ? submission.getSubmissionDate() : "Recently" %> &bull;
            Evaluated against 35 standardized B.Tech CSE paths.
        </div>
    </section>

    <!-- Important Notice Disclaimer -->
    <div class="alert alert-info no-print" style="margin-bottom: 2rem;">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"></circle>
            <line x1="12" y1="16" x2="12" y2="12"></line>
            <line x1="12" y1="8" x2="12.01" y2="8"></line>
        </svg>
        <div>
            <strong>Guidance Note:</strong> Compatibility scores represent algorithmic alignment with your stated preferences across technical topics. These scores are designed for career discovery and academic exploration, not as a guarantee of future employment or success.
        </div>
    </div>

    <!-- The Top 5 Ranked Recommendations -->
    <div class="recommendations-list">
        <% 
            if (recommendations != null && !recommendations.isEmpty()) {
                for (CareerRecommendation rec : recommendations) {
                    Career career = rec.getCareer();
        %>
            <div class="rec-card">
                <!-- Rank Circle -->
                <div class="rec-rank-circle">
                    <div>#<%= rec.getRankOrder() %></div>
                    <span>Rank</span>
                </div>

                <!-- Career Content -->
                <div class="rec-content">
                    <div class="rec-header">
                        <h2 class="rec-title"><%= SecurityUtil.escapeHtml(career.getName()) %></h2>
                        <div class="rec-score-pill">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
                            </svg>
                            <span><%= rec.getCompatibilityScore() %>% Compatibility Match</span>
                        </div>
                    </div>

                    <p class="rec-desc">
                        <%= SecurityUtil.escapeHtml(career.getShortDescription()) %>
                    </p>

                    <!-- Why this matches you -->
                    <div class="rec-reason">
                        <strong>Why this matches your profile:</strong> <%= SecurityUtil.escapeHtml(rec.getMatchReason()) %>
                    </div>

                    <!-- Skills & Tools -->
                    <div>
                        <div style="font-size: 0.8rem; font-weight: 700; color: var(--slate-500); text-transform: uppercase; margin-bottom: 0.35rem; letter-spacing: 0.05em;">
                            Essential Skills &amp; Recommended Tools
                        </div>
                        <div class="rec-chips-group">
                            <% 
                                String[] skills = career.getSkillsRequired().split(",");
                                for (String s : skills) { 
                            %>
                                <span class="chip"><%= SecurityUtil.escapeHtml(s.trim()) %></span>
                            <% } %>
                            <% 
                                String[] tools = career.getToolsTechnologies().split(",");
                                for (String t : tools) { 
                            %>
                                <span class="chip chip-tool"><%= SecurityUtil.escapeHtml(t.trim()) %></span>
                            <% } %>
                        </div>
                    </div>

                    <!-- Beginner Next-Step Suggestion -->
                    <div style="margin-top: 0.5rem; font-size: 0.85rem; color: var(--teal-700); background: var(--teal-50); padding: 0.6rem 0.85rem; border-radius: var(--radius-sm); border: 1px solid var(--teal-100);">
                        <strong>Suggested Beginner Next Step:</strong> <%= SecurityUtil.escapeHtml(career.getLearningSuggestions()) %>
                    </div>
                </div>

                <!-- Card Actions -->
                <div class="rec-actions no-print">
                    <a href="<%= request.getContextPath() %>/student/career-details?id=<%= career.getId() %>" class="btn btn-outline-primary btn-sm">
                        <span>Career Roadmap</span>
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <line x1="5" y1="12" x2="19" y2="12"></line>
                            <polyline points="12 5 19 12 12 19"></polyline>
                        </svg>
                    </a>
                </div>
            </div>
        <% 
                } 
            } else { 
        %>
            <div class="card" style="text-align: center; padding: 3rem;">
                <p style="color: var(--slate-600);">No recommendation records found for this assessment.</p>
                <a href="<%= request.getContextPath() %>/student/assessment" class="btn btn-primary" style="margin-top: 1rem;">
                    Take Assessment Now
                </a>
            </div>
        <% } %>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
