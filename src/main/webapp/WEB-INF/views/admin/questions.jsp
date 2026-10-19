<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.career.model.Question, com.career.model.QuestionOption, java.util.List, com.career.util.SecurityUtil" %>
<%
    request.setAttribute("pageTitle", "Question Management - Career Recommendation System");
    String csrfToken = SecurityUtil.getOrCreateCsrfToken(request);
    List<Question> questions = (List<Question>) request.getAttribute("questions");
    Question editQuestion = (Question) request.getAttribute("editQuestion");
    Integer activeCount = (Integer) request.getAttribute("activeCount");
    Integer nextOrder = (Integer) request.getAttribute("nextOrder");
    Boolean questionCountValid = (Boolean) request.getAttribute("questionCountValid");
    String errorMessage = (String) request.getAttribute("errorMessage");
    String success = request.getParameter("success");

    if (activeCount == null) activeCount = 20;
    if (nextOrder == null) nextOrder = 21;
    if (questionCountValid == null) questionCountValid = true;
%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="container">
    <div style="display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 1rem; margin-bottom: 2rem;">
        <div>
            <span class="badge badge-teal" style="margin-bottom: 0.5rem;">Assessment Configuration</span>
            <h1 style="font-size: 2rem; color: var(--navy-900); font-weight: 800;">
                Assessment Questions Management
            </h1>
            <p style="color: var(--slate-600); font-size: 0.95rem;">
                Maintain questions, edit option labels, and manage active status. <strong>Exactly 20 active questions</strong> are required for student assessments.
            </p>
        </div>

        <div>
            <button type="button" onclick="openModal('addQuestionModal')" class="btn btn-primary">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="12" y1="5" x2="12" y2="19"></line>
                    <line x1="5" y1="12" x2="19" y2="12"></line>
                </svg>
                <span>Add New Question</span>
            </button>
        </div>
    </div>

    <!-- Active Count Warning / Success -->
    <% if (!questionCountValid) { %>
        <div class="alert alert-warning" role="alert">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path>
                <line x1="12" y1="9" x2="12" y2="13"></line>
                <line x1="12" y1="17" x2="12.01" y2="17"></line>
            </svg>
            <div>
                <strong>Warning:</strong> Currently active questions: <strong><%= activeCount %></strong>. The system requires exactly <strong>20 active questions</strong> for valid student assessments. Please activate or deactivate questions below to restore the 20-question requirement.
            </div>
        </div>
    <% } else { %>
        <div class="alert alert-success" style="margin-bottom: 1.5rem;">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                <polyline points="22 4 12 14.01 9 11.01"></polyline>
            </svg>
            <div>Integrity check passed: Exactly <strong>20 active questions</strong> are configured. Student assessments are open.</div>
        </div>
    <% } %>

    <% if ("question_added".equals(success)) { %>
        <div class="alert alert-success">Question and all 4 options added successfully.</div>
    <% } else if ("question_updated".equals(success)) { %>
        <div class="alert alert-success">Question text and option labels updated successfully.</div>
    <% } else if ("status_toggled".equals(success)) { %>
        <div class="alert alert-info">Question active status toggled successfully.</div>
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

    <!-- Questions Table -->
    <div class="card">
        <div class="table-responsive">
            <table class="data-table">
                <thead>
                    <tr>
                        <th style="width: 70px;">Order</th>
                        <th>Category</th>
                        <th>Question Text</th>
                        <th>Active Status</th>
                        <th style="text-align: right; width: 220px;">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                        if (questions != null) {
                            for (Question q : questions) { 
                    %>
                        <tr style="<%= q.isActive() ? "" : "background: #fafafa; opacity: 0.75;" %>">
                            <td><strong>#<%= q.getQuestionOrder() %></strong></td>
                            <td><span class="badge badge-blue"><%= SecurityUtil.escapeHtml(q.getCategory()) %></span></td>
                            <td style="max-width: 480px;">
                                <strong><%= SecurityUtil.escapeHtml(q.getQuestionText()) %></strong>
                                <div style="font-size: 0.8rem; color: var(--slate-500); margin-top: 0.25rem;">
                                    Options: 
                                    <% for (QuestionOption o : q.getOptions()) { %>
                                        <span class="chip" style="font-size: 0.75rem;"><%= o.getRatingValue() %>: <%= SecurityUtil.escapeHtml(o.getOptionText()) %></span>
                                    <% } %>
                                </div>
                            </td>
                            <td>
                                <% if (q.isActive()) { %>
                                    <span class="badge badge-success">Active</span>
                                <% } else { %>
                                    <span class="badge badge-slate">Deactivated</span>
                                <% } %>
                            </td>
                            <td style="text-align: right;">
                                <div style="display: inline-flex; gap: 0.5rem;">
                                    <a href="<%= request.getContextPath() %>/admin/questions?editId=<%= q.getId() %>" class="btn btn-secondary btn-sm">
                                        Edit
                                    </a>

                                    <form action="<%= request.getContextPath() %>/admin/questions" method="POST" style="display: inline;"
                                          onsubmit="return confirm('<%= q.isActive() ? "Deactivate" : "Activate" %> this question? (Note: Student assessments require exactly 20 active questions).');">
                                        <input type="hidden" name="csrfToken" value="<%= csrfToken %>">
                                        <input type="hidden" name="action" value="toggle">
                                        <input type="hidden" name="questionId" value="<%= q.getId() %>">
                                        <input type="hidden" name="currentStatus" value="<%= q.isActive() %>">
                                        <button type="submit" class="btn <%= q.isActive() ? "btn-secondary" : "btn-primary" %> btn-sm">
                                            <%= q.isActive() ? "Deactivate" : "Activate" %>
                                        </button>
                                    </form>
                                </div>
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

<!-- ======================================================================= -->
<!-- MODAL: ADD QUESTION                                                     -->
<!-- ======================================================================= -->
<div class="modal-overlay" id="addQuestionModal">
    <div class="modal-box">
        <button type="button" class="modal-close" aria-label="Close modal">&times;</button>
        <h2 style="font-size: 1.4rem; color: var(--navy-900); font-weight: 700; margin-bottom: 0.35rem;">Add Assessment Question</h2>
        <p style="color: var(--slate-600); font-size: 0.85rem; margin-bottom: 1.5rem;">Configure question text, category, order, and all 4 rating options.</p>

        <form action="<%= request.getContextPath() %>/admin/questions" method="POST">
            <input type="hidden" name="csrfToken" value="<%= csrfToken %>">
            <input type="hidden" name="action" value="add">

            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Question Order</label>
                    <input type="number" name="questionOrder" class="form-control" value="<%= nextOrder %>" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Category</label>
                    <input type="text" name="category" class="form-control" placeholder="e.g. Web Development" required>
                </div>
            </div>

            <div class="form-group">
                <label class="form-label">Question Text</label>
                <textarea name="questionText" class="form-control" rows="2" placeholder="e.g. How interested are you in building modern websites?" required></textarea>
            </div>

            <div style="font-size: 0.9rem; font-weight: 700; color: var(--navy-900); margin: 1rem 0 0.5rem 0;">Answer Options Configuration</div>
            
            <div class="form-group">
                <label class="form-label">Option 4 (Rating = 4 / Highest)</label>
                <input type="text" name="optionText_4" class="form-control" value="Very interested" required>
            </div>
            <div class="form-group">
                <label class="form-label">Option 3 (Rating = 3)</label>
                <input type="text" name="optionText_3" class="form-control" value="Moderately interested" required>
            </div>
            <div class="form-group">
                <label class="form-label">Option 2 (Rating = 2)</label>
                <input type="text" name="optionText_2" class="form-control" value="Somewhat interested" required>
            </div>
            <div class="form-group">
                <label class="form-label">Option 1 (Rating = 1 / Lowest)</label>
                <input type="text" name="optionText_1" class="form-control" value="Not very interested" required>
            </div>

            <div class="form-group">
                <label style="display: flex; align-items: center; gap: 0.5rem; cursor: pointer;">
                    <input type="checkbox" name="isActive" value="true" checked>
                    <span style="font-size: 0.9rem; font-weight: 600;">Set as Active Question immediately</span>
                </label>
            </div>

            <div style="display: flex; gap: 0.75rem; justify-content: flex-end; margin-top: 1.5rem;">
                <button type="button" class="btn btn-secondary modal-cancel-btn">Cancel</button>
                <button type="submit" class="btn btn-primary">Save Question</button>
            </div>
        </form>
    </div>
</div>

<!-- ======================================================================= -->
<!-- MODAL: EDIT QUESTION                                                    -->
<!-- ======================================================================= -->
<% if (editQuestion != null) { %>
<div class="modal-overlay active" id="editQuestionModal">
    <div class="modal-box">
        <a href="<%= request.getContextPath() %>/admin/questions" class="modal-close" aria-label="Close modal">&times;</a>
        <h2 style="font-size: 1.4rem; color: var(--navy-900); font-weight: 700; margin-bottom: 0.35rem;">
            Edit Question #<%= editQuestion.getQuestionOrder() %>
        </h2>
        <p style="color: var(--slate-600); font-size: 0.85rem; margin-bottom: 1.5rem;">
            Update question text, category, or option labels. Historical assessment response records will retain their original snapshots.
        </p>

        <form action="<%= request.getContextPath() %>/admin/questions" method="POST">
            <input type="hidden" name="csrfToken" value="<%= csrfToken %>">
            <input type="hidden" name="action" value="edit">
            <input type="hidden" name="questionId" value="<%= editQuestion.getId() %>">

            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Question Order</label>
                    <input type="number" name="questionOrder" class="form-control" value="<%= editQuestion.getQuestionOrder() %>" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Category</label>
                    <input type="text" name="category" class="form-control" value="<%= SecurityUtil.escapeHtml(editQuestion.getCategory()) %>" required>
                </div>
            </div>

            <div class="form-group">
                <label class="form-label">Question Text</label>
                <textarea name="questionText" class="form-control" rows="3" required><%= SecurityUtil.escapeHtml(editQuestion.getQuestionText()) %></textarea>
            </div>

            <div style="font-size: 0.9rem; font-weight: 700; color: var(--navy-900); margin: 1rem 0 0.5rem 0;">Option Labels</div>
            <% 
                for (QuestionOption opt : editQuestion.getOptions()) { 
            %>
                <div class="form-group">
                    <label class="form-label">Option for Rating <%= opt.getRatingValue() %></label>
                    <input type="hidden" name="optionId_<%= opt.getRatingValue() %>" value="<%= opt.getId() %>">
                    <input type="text" name="optionText_<%= opt.getRatingValue() %>" class="form-control"
                           value="<%= SecurityUtil.escapeHtml(opt.getOptionText()) %>" required>
                </div>
            <% } %>

            <div class="form-group">
                <label style="display: flex; align-items: center; gap: 0.5rem; cursor: pointer;">
                    <input type="checkbox" name="isActive" value="true" <%= editQuestion.isActive() ? "checked" : "" %>>
                    <span style="font-size: 0.9rem; font-weight: 600;">Active Question</span>
                </label>
            </div>

            <div style="display: flex; gap: 0.75rem; justify-content: flex-end; margin-top: 1.5rem;">
                <a href="<%= request.getContextPath() %>/admin/questions" class="btn btn-secondary">Cancel</a>
                <button type="submit" class="btn btn-primary">Update Question</button>
            </div>
        </form>
    </div>
</div>
<% } %>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
