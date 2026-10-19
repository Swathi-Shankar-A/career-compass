<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.career.dao.CareerDAO, com.career.model.Career, java.util.List" %>
<%
    request.setAttribute("pageTitle", "Career Recommendation System for B.Tech CSE Students");
    CareerDAO careerDAO = new CareerDAO();
    List<Career> featuredCareers = careerDAO.getActiveCareers();
%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<!-- Hero Section -->
<section class="hero-section">
    <div class="container hero-content">
        <div class="hero-pill">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M12 2v20M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path>
            </svg>
            <span>Engineered specifically for B.Tech Computer Science Students</span>
        </div>

        <h1 class="hero-title">
            Discover the Career <br><span class="text-gradient">That Fits You</span>
        </h1>

        <p class="hero-description">
            Evaluate your technical inclinations, problem-solving preferences, and engineering interests through a structured 20-question assessment. Get deterministic, explainable career recommendations backed by an algorithmic compatibility scoring model.
        </p>

        <div class="hero-buttons">
            <a href="<%= request.getContextPath() %>/register" class="btn btn-primary btn-lg">
                <span>Get Started Free</span>
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="5" y1="12" x2="19" y2="12"></line>
                    <polyline points="12 5 19 12 12 19"></polyline>
                </svg>
            </a>
            <a href="<%= request.getContextPath() %>/login" class="btn btn-outline-white btn-lg">
                Student Login
            </a>
        </div>
    </div>
</section>

<!-- How It Works Section -->
<section style="padding: 4.5rem 0 3.5rem 0;">
    <div class="container">
        <div class="section-header">
            <span class="badge badge-blue" style="margin-bottom: 0.75rem;">Structured Methodology</span>
            <h2 class="section-title">How It Works</h2>
            <p class="section-subtitle">
                A simple three-step process designed to help CSE students explore industry paths without coding aptitude tests or complex psychological puzzles.
            </p>
        </div>

        <div class="steps-grid">
            <div class="step-card">
                <div class="step-number">1</div>
                <h3 class="step-title">Create Your Student Profile</h3>
                <p style="color: var(--slate-600); font-size: 0.95rem;">
                    Sign up with your academic details, including your college, degree specialization, and current year of study.
                </p>
            </div>

            <div class="step-card">
                <div class="step-number">2</div>
                <h3 class="step-title">Take the 20-Question Assessment</h3>
                <p style="color: var(--slate-600); font-size: 0.95rem;">
                    Rate your interest across 20 distinct computer science disciplines on a straightforward 4-point scale from "Very interested" to "Not very interested".
                </p>
            </div>

            <div class="step-card">
                <div class="step-number">3</div>
                <h3 class="step-title">Receive Your Top 5 Career Matches</h3>
                <p style="color: var(--slate-600); font-size: 0.95rem;">
                    Our deterministic scoring engine calculates weighted compatibility scores for 35 distinct careers, presenting skills, tools, and personalized reasons.
                </p>
            </div>
        </div>
    </div>
</section>

<!-- Career Categories & Explore Section -->
<section style="background: var(--slate-100); padding: 4.5rem 0;">
    <div class="container">
        <div class="section-header">
            <span class="badge badge-teal" style="margin-bottom: 0.75rem;">35 Seeded Career Profiles</span>
            <h2 class="section-title">Explore Computing Career Pathways</h2>
            <p class="section-subtitle">
                Covering software development, data science, artificial intelligence, cybersecurity, cloud infrastructure, quality assurance, and project coordination.
            </p>
        </div>

        <div class="careers-grid">
            <%
                int displayCount = Math.min(6, (featuredCareers != null) ? featuredCareers.size() : 0);
                for (int i = 0; i < displayCount; i++) {
                    Career c = featuredCareers.get(i);
            %>
                <div class="career-preview-card">
                    <div>
                        <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 0.5rem;">
                            <h3 class="career-preview-title"><%= c.getName() %></h3>
                            <span class="badge badge-blue">Path #<%= c.getId() %></span>
                        </div>
                        <p class="career-preview-desc"><%= c.getShortDescription() %></p>
                    </div>

                    <div>
                        <div class="rec-chips-group" style="margin-bottom: 1rem;">
                            <%
                                String[] skills = c.getSkillsRequired().split(",");
                                int sLimit = Math.min(3, skills.length);
                                for (int j = 0; j < sLimit; j++) {
                            %>
                                <span class="chip"><%= skills[j].trim() %></span>
                            <% } %>
                        </div>
                        <a href="<%= request.getContextPath() %>/student/career-details?id=<%= c.getId() %>" class="btn btn-outline-primary btn-sm btn-block">
                            View Roadmap &amp; Skills
                        </a>
                    </div>
                </div>
            <% } %>
        </div>

        <div style="text-align: center; margin-top: 3rem;">
            <a href="<%= request.getContextPath() %>/register" class="btn btn-primary btn-lg">
                Take Assessment to Find Your Best Match
            </a>
        </div>
    </div>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
