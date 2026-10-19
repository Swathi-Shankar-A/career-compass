<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.career.model.Student, com.career.model.AssessmentSubmission, com.career.model.CareerRecommendation, java.util.List, java.util.Map, com.career.util.SecurityUtil" %>
<%
    request.setAttribute("pageTitle", "Student Directory & Responses - Career Recommendation System");
    List<Student> students = (List<Student>) request.getAttribute("students");
    Student selectedStudent = (Student) request.getAttribute("selectedStudent");
    List<AssessmentSubmission> studentSubmissions = (List<AssessmentSubmission>) request.getAttribute("studentSubmissions");
    Map<Integer, Integer> submissionCounts = (Map<Integer, Integer>) request.getAttribute("submissionCounts");
    String search = (String) request.getAttribute("search");
    Integer totalCount = (Integer) request.getAttribute("totalCount");
    if (totalCount == null) totalCount = 0;
%>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="container">
    <div style="display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 1rem; margin-bottom: 2rem;">
        <div>
            <span class="badge badge-teal" style="margin-bottom: 0.5rem;">Student Auditing</span>
            <h1 style="font-size: 2rem; color: var(--navy-900); font-weight: 800;">
                Registered Students Directory
            </h1>
            <p style="color: var(--slate-600); font-size: 0.95rem;">
                Browse registered engineering students, inspect their profiles, and audit their 20-question assessment responses.
            </p>
        </div>

        <!-- Search Form -->
        <form action="<%= request.getContextPath() %>/admin/students" method="GET" style="display: flex; gap: 0.5rem;">
            <input type="text" name="search" class="form-control" placeholder="Search by name or email..."
                   value="<%= (search != null) ? SecurityUtil.escapeHtml(search) : "" %>" style="width: 260px;">
            <button type="submit" class="btn btn-primary btn-sm">Search</button>
            <% if (search != null && !search.isEmpty()) { %>
                <a href="<%= request.getContextPath() %>/admin/students" class="btn btn-secondary btn-sm">Clear</a>
            <% } %>
        </form>
    </div>

    <!-- Selected Student Profile Details (If opened) -->
    <% if (selectedStudent != null) { %>
        <div class="card" style="margin-bottom: 2rem; border-color: var(--blue-400); background: #f8fbff;">
            <div class="card-header">
                <div>
                    <span class="badge badge-blue">Student Profile #<%= selectedStudent.getId() %></span>
                    <h2 style="font-size: 1.35rem; color: var(--navy-900); font-weight: 700; margin-top: 0.25rem;">
                        <%= SecurityUtil.escapeHtml(selectedStudent.getFullName()) %>
                    </h2>
                </div>
                <a href="<%= request.getContextPath() %>/admin/students" class="btn btn-secondary btn-sm">Close Profile</a>
            </div>

            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; font-size: 0.95rem; margin-bottom: 1.5rem;">
                <div><strong>Email:</strong> <%= SecurityUtil.escapeHtml(selectedStudent.getEmail()) %></div>
                <div><strong>College:</strong> <%= SecurityUtil.escapeHtml(selectedStudent.getCollegeName()) %></div>
                <div><strong>Degree:</strong> <%= SecurityUtil.escapeHtml(selectedStudent.getDegree()) %></div>
                <div><strong>Year of Study:</strong> Year <%= selectedStudent.getCurrentYear() %></div>
                <div><strong>Registered On:</strong> <%= selectedStudent.getCreatedAt() %></div>
            </div>

            <!-- Submissions by this student -->
            <h3 style="font-size: 1.1rem; color: var(--navy-900); font-weight: 700; margin-bottom: 0.75rem;">
                Assessment Submissions by this Student (<%= (studentSubmissions != null) ? studentSubmissions.size() : 0 %>)
            </h3>

            <% if (studentSubmissions != null && !studentSubmissions.isEmpty()) { %>
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Submission ID</th>
                                <th>Submission Date</th>
                                <th>Top Recommended Career</th>
                                <th>Compatibility Score</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (AssessmentSubmission sub : studentSubmissions) { 
                                CareerRecommendation top = (sub.getRecommendations() != null && !sub.getRecommendations().isEmpty()) 
                                        ? sub.getRecommendations().get(0) : null;
                            %>
                                <tr>
                                    <td><strong>#<%= sub.getId() %></strong></td>
                                    <td><%= sub.getSubmissionDate() %></td>
                                    <td>
                                        <% if (top != null) { %>
                                            <span class="badge badge-teal"><%= SecurityUtil.escapeHtml(top.getCareer().getName()) %></span>
                                        <% } else { %>
                                            N/A
                                        <% } %>
                                    </td>
                                    <td>
                                        <% if (top != null) { %>
                                            <strong style="color: var(--teal-700);"><%= top.getCompatibilityScore() %>%</strong>
                                        <% } %>
                                    </td>
                                    <td>
                                        <a href="<%= request.getContextPath() %>/admin/student-responses?submissionId=<%= sub.getId() %>" class="btn btn-primary btn-sm">
                                            View 20 Answers &amp; Top 5 Results
                                        </a>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            <% } else { %>
                <div style="color: var(--slate-500); font-size: 0.9rem;">This student has not submitted any assessments yet.</div>
            <% } %>
        </div>
    <% } %>

    <!-- Students Directory Table -->
    <div class="card">
        <div class="card-header">
            <div>
                <h2 style="font-size: 1.25rem; color: var(--navy-900); font-weight: 700;">All Enrolled Students</h2>
                <div style="font-size: 0.85rem; color: var(--slate-500);">Total matches: <%= totalCount %></div>
            </div>
        </div>

        <% if (students != null && !students.isEmpty()) { %>
            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Student ID</th>
                            <th>Full Name</th>
                            <th>Email Address</th>
                            <th>College Name</th>
                            <th>Degree / Branch</th>
                            <th>Year</th>
                            <th>Assessments Taken</th>
                            <th style="text-align: right;">Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Student s : students) { 
                            int subCount = (submissionCounts != null && submissionCounts.containsKey(s.getId())) 
                                    ? submissionCounts.get(s.getId()) : 0;
                        %>
                            <tr>
                                <td><strong>#<%= s.getId() %></strong></td>
                                <td><strong><%= SecurityUtil.escapeHtml(s.getFullName()) %></strong></td>
                                <td><%= SecurityUtil.escapeHtml(s.getEmail()) %></td>
                                <td><%= SecurityUtil.escapeHtml(s.getCollegeName()) %></td>
                                <td><%= SecurityUtil.escapeHtml(s.getDegree()) %></td>
                                <td>Year <%= s.getCurrentYear() %></td>
                                <td>
                                    <span class="badge <%= (subCount > 0) ? "badge-teal" : "badge-slate" %>">
                                        <%= subCount %> Completed
                                    </span>
                                </td>
                                <td style="text-align: right;">
                                    <a href="<%= request.getContextPath() %>/admin/students?studentId=<%= s.getId() %>" class="btn btn-secondary btn-sm">
                                        View Profile &amp; Submissions
                                    </a>
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        <% } else { %>
            <div style="text-align: center; padding: 2.5rem; color: var(--slate-500);">
                No students found matching your search criteria.
            </div>
        <% } %>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
