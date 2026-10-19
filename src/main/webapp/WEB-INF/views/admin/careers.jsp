<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.career.model.Career, com.career.model.CareerQuestionMapping, java.util.List, com.career.util.SecurityUtil" %>
<%
    request.setAttribute("pageTitle", "Career Catalog Management - Career Recommendation System");
    String csrfToken = SecurityUtil.getOrCreateCsrfToken(request);
    List<Career> careers = (List<Career>) request.getAttribute("careers");
    Career selectedCareer = (Career) request.getAttribute("selectedCareer");
    Integer totalCareers = (Integer) request.getAttribute("totalCareers");
    String success = request.getParameter("success");
    if (totalCareers == null) totalCareers = 35;
%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="container">
    <div style="display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 1rem; margin-bottom: 2rem;">
        <div>
            <span class="badge badge-teal" style="margin-bottom: 0.5rem;">Standardized Career Taxonomy</span>
            <h1 style="font-size: 2rem; color: var(--navy-900); font-weight: 800;">
                All 35 B.Tech CSE Career Paths
            </h1>
            <p style="color: var(--slate-600); font-size: 0.95rem;">
                Browse and inspect the 35 standardized engineering careers seeded in MySQL with their respective algorithmic question mappings.
            </p>
        </div>
        <div>
            <span class="badge badge-blue" style="font-size: 0.9rem; padding: 0.4rem 1rem;">
                Total Seeded Careers: <%= totalCareers %>
            </span>
        </div>
    </div>

    <% if ("updated".equals(success)) { %>
        <div class="alert alert-success">Career profile details updated successfully.</div>
    <% } %>

    <!-- Selected Career Detail Card (If active) -->
    <% if (selectedCareer != null) { %>
        <div class="card" style="margin-bottom: 2.5rem; border-color: var(--blue-400); background: #f8fbff;">
            <div class="card-header">
                <div>
                    <span class="badge badge-blue">Career Path #<%= selectedCareer.getId() %></span>
                    <h2 style="font-size: 1.4rem; color: var(--navy-900); font-weight: 700; margin-top: 0.25rem;">
                        <%= SecurityUtil.escapeHtml(selectedCareer.getName()) %>
                    </h2>
                </div>
                <a href="<%= request.getContextPath() %>/admin/careers" class="btn btn-secondary btn-sm">Close Inspector</a>
            </div>

            <form action="<%= request.getContextPath() %>/admin/careers" method="POST">
                <input type="hidden" name="csrfToken" value="<%= csrfToken %>">
                <input type="hidden" name="careerId" value="<%= selectedCareer.getId() %>">

                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Career Name</label>
                        <input type="text" name="name" class="form-control" value="<%= SecurityUtil.escapeHtml(selectedCareer.getName()) %>" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Active Status</label>
                        <select name="isActive" class="form-control">
                            <option value="true" <%= selectedCareer.isActive() ? "selected" : "" %>>Active (Included in Recommendation Engine)</option>
                            <option value="false" <%= !selectedCareer.isActive() ? "selected" : "" %>>Inactive</option>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label">Short Description</label>
                    <textarea name="shortDescription" class="form-control" rows="2" required><%= SecurityUtil.escapeHtml(selectedCareer.getShortDescription()) %></textarea>
                </div>

                <div class="form-group">
                    <label class="form-label">Typical Industry Responsibilities</label>
                    <textarea name="responsibilities" class="form-control" rows="2" required><%= SecurityUtil.escapeHtml(selectedCareer.getResponsibilities()) %></textarea>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Required Skills (comma separated)</label>
                        <input type="text" name="skillsRequired" class="form-control" value="<%= SecurityUtil.escapeHtml(selectedCareer.getSkillsRequired()) %>" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Recommended Tools &amp; Technologies (comma separated)</label>
                        <input type="text" name="toolsTechnologies" class="form-control" value="<%= SecurityUtil.escapeHtml(selectedCareer.getToolsTechnologies()) %>" required>
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label">Beginner Learning Roadmap / Suggestions</label>
                    <textarea name="learningSuggestions" class="form-control" rows="2" required><%= SecurityUtil.escapeHtml(selectedCareer.getLearningSuggestions()) %></textarea>
                </div>

                <!-- Mapped Questions for this career -->
                <div style="margin: 1.5rem 0;">
                    <h3 style="font-size: 1rem; color: var(--navy-900); font-weight: 700; margin-bottom: 0.5rem;">
                        Mapped Assessment Questions &amp; Weights
                    </h3>
                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Category</th>
                                    <th>Assessment Topic</th>
                                    <th>Weight Factor</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (CareerQuestionMapping m : selectedCareer.getMappings()) { %>
                                    <tr>
                                        <td><span class="badge badge-blue"><%= SecurityUtil.escapeHtml(m.getCategory()) %></span></td>
                                        <td><%= SecurityUtil.escapeHtml(m.getQuestionText()) %></td>
                                        <td><strong><%= m.getWeight() %>x</strong></td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div style="display: flex; gap: 0.75rem; justify-content: flex-end;">
                    <a href="<%= request.getContextPath() %>/admin/careers" class="btn btn-secondary">Cancel</a>
                    <button type="submit" class="btn btn-primary">Update Career Specifications</button>
                </div>
            </form>
        </div>
    <% } %>

    <!-- All 35 Careers Table -->
    <div class="card">
        <div class="table-responsive">
            <table class="data-table">
                <thead>
                    <tr>
                        <th style="width: 60px;">ID</th>
                        <th>Career Title</th>
                        <th>Core Description</th>
                        <th>Required Key Skills</th>
                        <th>Status</th>
                        <th style="text-align: right; width: 140px;">Action</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                        if (careers != null) {
                            for (Career c : careers) { 
                    %>
                        <tr>
                            <td><strong>#<%= c.getId() %></strong></td>
                            <td><strong><%= SecurityUtil.escapeHtml(c.getName()) %></strong></td>
                            <td style="max-width: 380px; font-size: 0.85rem; color: var(--slate-700);">
                                <%= SecurityUtil.escapeHtml(c.getShortDescription()) %>
                            </td>
                            <td style="max-width: 280px;">
                                <div class="rec-chips-group">
                                    <% 
                                        String[] skills = c.getSkillsRequired().split(",");
                                        int sLimit = Math.min(2, skills.length);
                                        for (int j = 0; j < sLimit; j++) {
                                    %>
                                        <span class="chip" style="font-size: 0.75rem;"><%= SecurityUtil.escapeHtml(skills[j].trim()) %></span>
                                    <% } %>
                                </div>
                            </td>
                            <td>
                                <% if (c.isActive()) { %>
                                    <span class="badge badge-success">Active</span>
                                <% } else { %>
                                    <span class="badge badge-slate">Inactive</span>
                                <% } %>
                            </td>
                            <td style="text-align: right;">
                                <a href="<%= request.getContextPath() %>/admin/careers?id=<%= c.getId() %>" class="btn btn-outline-primary btn-sm">
                                    Inspect &amp; Edit
                                </a>
                            </td>
                        </tr>
                    <% 
                            }
                        } 
                    %>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
