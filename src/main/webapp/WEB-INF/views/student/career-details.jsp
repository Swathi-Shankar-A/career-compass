<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.career.model.Career, com.career.model.CareerQuestionMapping, java.util.List, com.career.util.SecurityUtil" %>
<%
    Career career = (Career) request.getAttribute("career");
    if (career == null) {
        response.sendRedirect(request.getContextPath() + "/student/dashboard");
        return;
    }
    request.setAttribute("pageTitle", career.getName() + " - Career Roadmap & Details");
    List<CareerQuestionMapping> mappings = career.getMappings();
%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="container container-narrow">
    <div style="margin-bottom: 1.5rem;">
        <a href="javascript:history.back()" class="btn btn-secondary btn-sm">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="19" y1="12" x2="5" y2="12"></line>
                <polyline points="12 19 5 12 12 5"></polyline>
            </svg>
            <span>Back</span>
        </a>
    </div>

    <!-- Career Header Card -->
    <div class="card" style="margin-bottom: 2rem;">
        <div style="display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 1rem; margin-bottom: 1rem;">
            <div>
                <span class="badge badge-blue" style="margin-bottom: 0.5rem;">Career Path #<%= career.getId() %></span>
                <h1 style="font-size: 2rem; color: var(--navy-900); font-weight: 800;">
                    <%= SecurityUtil.escapeHtml(career.getName()) %>
                </h1>
            </div>
            <span class="badge badge-teal">B.Tech CSE Specialization</span>
        </div>

        <p style="color: var(--slate-700); font-size: 1.1rem; line-height: 1.6; margin-bottom: 1.5rem;">
            <%= SecurityUtil.escapeHtml(career.getShortDescription()) %>
        </p>

        <!-- Responsibilities -->
        <div style="margin-bottom: 1.5rem;">
            <h3 style="font-size: 1.15rem; color: var(--navy-900); font-weight: 700; margin-bottom: 0.5rem;">
                Typical Industry Responsibilities
            </h3>
            <p style="color: var(--slate-700); font-size: 0.95rem; line-height: 1.7; background: var(--slate-50); padding: 1rem; border-radius: var(--radius-md); border-left: 3px solid var(--blue-600);">
                <%= SecurityUtil.escapeHtml(career.getResponsibilities()) %>
            </p>
        </div>

        <!-- Skills Required -->
        <div style="margin-bottom: 1.5rem;">
            <h3 style="font-size: 1.15rem; color: var(--navy-900); font-weight: 700; margin-bottom: 0.5rem;">
                Required Technical &amp; Professional Skills
            </h3>
            <div class="rec-chips-group">
                <% 
                    String[] skills = career.getSkillsRequired().split(",");
                    for (String s : skills) { 
                %>
                    <span class="chip" style="font-size: 0.9rem; padding: 0.35rem 0.75rem;"><%= SecurityUtil.escapeHtml(s.trim()) %></span>
                <% } %>
            </div>
        </div>

        <!-- Tools & Tech -->
        <div style="margin-bottom: 1.5rem;">
            <h3 style="font-size: 1.15rem; color: var(--navy-900); font-weight: 700; margin-bottom: 0.5rem;">
                Recommended Tools &amp; Technologies
            </h3>
            <div class="rec-chips-group">
                <% 
                    String[] tools = career.getToolsTechnologies().split(",");
                    for (String t : tools) { 
                %>
                    <span class="chip chip-tool" style="font-size: 0.9rem; padding: 0.35rem 0.75rem;"><%= SecurityUtil.escapeHtml(t.trim()) %></span>
                <% } %>
            </div>
        </div>

        <!-- Beginner Learning Guidance -->
        <div style="margin-bottom: 1.5rem;">
            <h3 style="font-size: 1.15rem; color: var(--navy-900); font-weight: 700; margin-bottom: 0.5rem;">
                Beginner-Friendly Learning Roadmap
            </h3>
            <div style="background: var(--teal-50); border: 1px solid var(--teal-100); border-radius: var(--radius-md); padding: 1rem; color: var(--teal-700); font-size: 0.95rem; line-height: 1.6;">
                <strong>Next Step for Undergraduates:</strong> <%= SecurityUtil.escapeHtml(career.getLearningSuggestions()) %>
            </div>
        </div>

        <!-- Mapped Assessment Questions -->
        <% if (mappings != null && !mappings.isEmpty()) { %>
            <div style="margin-top: 2rem; border-top: 1px solid var(--slate-100); padding-top: 1.5rem;">
                <h3 style="font-size: 1.1rem; color: var(--navy-900); font-weight: 700; margin-bottom: 0.75rem;">
                    Assessment Topic Weightings for this Career
                </h3>
                <p style="color: var(--slate-500); font-size: 0.85rem; margin-bottom: 1rem;">
                    These are the specific questions mapped in our recommendation engine that determine your compatibility score for this path:
                </p>
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Category</th>
                                <th>Assessment Question</th>
                                <th>Algorithmic Weight</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (CareerQuestionMapping m : mappings) { %>
                                <tr>
                                    <td><span class="badge badge-blue"><%= SecurityUtil.escapeHtml(m.getCategory()) %></span></td>
                                    <td><%= SecurityUtil.escapeHtml(m.getQuestionText()) %></td>
                                    <td><strong style="color: var(--blue-700);"><%= m.getWeight() %>x</strong></td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        <% } %>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
