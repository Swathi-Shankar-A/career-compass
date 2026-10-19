<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.career.model.Question, com.career.model.QuestionOption, java.util.List, java.util.Map, com.career.util.SecurityUtil" %>
<%
    request.setAttribute("pageTitle", "Career Assessment (20 Questions) - Career Recommendation System");
    String csrfToken = SecurityUtil.getOrCreateCsrfToken(request);
    List<Question> questions = (List<Question>) request.getAttribute("questions");
    String errorMessage = (String) request.getAttribute("errorMessage");
    Map<Integer, Integer> prevAnswers = (Map<Integer, Integer>) request.getAttribute("previousAnswers");
    int totalQuestions = (questions != null) ? questions.size() : 20;
%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="container assessment-container">
    <!-- Assessment Header -->
    <div style="text-align: center; margin-bottom: 2rem;">
        <span class="badge badge-blue" style="margin-bottom: 0.5rem;">Standard 20-Topic Assessment</span>
        <h1 style="font-size: 2rem; color: var(--navy-900); font-weight: 800; margin-bottom: 0.5rem;">
            Computer Science Career Assessment
        </h1>
        <p style="color: var(--slate-600); font-size: 0.95rem; max-width: 600px; margin: 0 auto;">
            Rate your genuine interest in each of the following computing activities. There are no right or wrong answers; your choices guide our deterministic recommendation algorithm.
        </p>
    </div>

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

    <!-- Stepper Progress Card -->
    <div class="assessment-progress-wrapper">
        <div class="progress-info">
            <div>
                <span>Question <strong id="currentQuestionNum">1</strong> of <%= totalQuestions %></span>
            </div>
            <div>
                <span>Completed: <strong id="progressPercent">0%</strong></span>
            </div>
        </div>
        <div class="progress-bar-bg">
            <div class="progress-bar-fill" id="progressBarFill" style="width: 5%;"></div>
        </div>
    </div>

    <!-- The 20 Questions Form -->
    <form action="<%= request.getContextPath() %>/student/assessment" method="POST" id="assessmentForm">
        <input type="hidden" name="csrfToken" value="<%= csrfToken %>">

        <% 
            if (questions != null) {
                for (int i = 0; i < questions.size(); i++) {
                    Question q = questions.get(i);
                    Integer savedRating = (prevAnswers != null) ? prevAnswers.get(q.getId()) : null;
        %>
            <div class="assessment-step" data-step="<%= i %>" style="<%= (i == 0) ? "" : "display: none;" %>">
                <div class="question-card">
                    <div class="question-meta">
                        <span class="question-category"><%= SecurityUtil.escapeHtml(q.getCategory()) %></span>
                        <span class="badge badge-slate">Topic #<%= q.getQuestionOrder() %></span>
                    </div>

                    <h2 class="question-heading">
                        <%= SecurityUtil.escapeHtml(q.getQuestionText()) %>
                    </h2>

                    <div class="options-list">
                        <% 
                            List<QuestionOption> options = q.getOptions();
                            for (QuestionOption opt : options) {
                                boolean isChecked = (savedRating != null && savedRating == opt.getRatingValue());
                        %>
                            <label class="option-card <%= isChecked ? "selected" : "" %>">
                                <input type="radio" name="q_<%= q.getId() %>" value="<%= opt.getRatingValue() %>"
                                       <%= isChecked ? "checked" : "" %> required>
                                <span class="option-text"><%= SecurityUtil.escapeHtml(opt.getOptionText()) %></span>
                            </label>
                        <% } %>
                    </div>
                </div>
            </div>
        <% 
                } 
            } 
        %>

        <!-- Navigation Controls -->
        <div class="assessment-nav">
            <button type="button" id="prevQuestionBtn" class="btn btn-secondary" style="display: none;">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="19" y1="12" x2="5" y2="12"></line>
                    <polyline points="12 19 5 12 12 5"></polyline>
                </svg>
                <span>Previous</span>
            </button>

            <button type="button" id="nextQuestionBtn" class="btn btn-primary">
                <span>Next Question</span>
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="5" y1="12" x2="19" y2="12"></line>
                    <polyline points="12 5 19 12 12 19"></polyline>
                </svg>
            </button>

            <button type="button" id="reviewQuestionsBtn" class="btn btn-navy" style="display: none;">
                <span>Review &amp; Submit Assessment</span>
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="20 6 9 17 4 12"></polyline>
                </svg>
            </button>
        </div>

        <!-- Confirmation Modal Dialog Before Submission -->
        <div class="modal-overlay" id="confirmSubmitModal">
            <div class="modal-box">
                <button type="button" class="modal-close" aria-label="Close modal">&times;</button>
                <div style="text-align: center; margin-bottom: 1.5rem;">
                    <div style="width: 56px; height: 56px; border-radius: 50%; background: var(--teal-50); color: var(--teal-600); display: flex; align-items: center; justify-content: center; margin: 0 auto 1rem auto;">
                        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                            <polyline points="22 4 12 14.01 9 11.01"></polyline>
                        </svg>
                    </div>
                    <h3 style="font-size: 1.35rem; color: var(--navy-900); font-weight: 700; margin-bottom: 0.5rem;">
                        Submit Your Assessment?
                    </h3>
                    <p style="color: var(--slate-600); font-size: 0.95rem;">
                        You have answered all 20 questions! Once submitted, our deterministic recommendation algorithm will evaluate your preferences against 35 career profiles and calculate your Top 5 career matches.
                    </p>
                </div>

                <div style="background: var(--slate-50); border: 1px solid var(--slate-200); border-radius: var(--radius-md); padding: 1rem; margin-bottom: 1.5rem; font-size: 0.85rem; color: var(--slate-600);">
                    &bull; All 20 responses will be saved to your student profile history.<br>
                    &bull; You can review or retake the assessment at any time.
                </div>

                <div style="display: flex; gap: 1rem; justify-content: flex-end;">
                    <button type="button" class="btn btn-secondary modal-cancel-btn">
                        Go Back &amp; Review
                    </button>
                    <button type="submit" id="finalSubmitBtn" class="btn btn-primary">
                        Confirm &amp; Generate Results
                    </button>
                </div>
            </div>
        </div>
    </form>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
