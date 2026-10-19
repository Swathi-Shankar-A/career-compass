package com.career.servlet;

import com.career.dao.AssessmentDAO;
import com.career.model.AssessmentSubmission;
import com.career.model.Student;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Displays the Top 5 Career Recommendations for an assessment submission.
 */
public class RecommendationResultsServlet extends HttpServlet {

    private AssessmentDAO assessmentDAO;

    @Override
    public void init() throws ServletException {
        this.assessmentDAO = new AssessmentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Student student = (session != null) ? (Student) session.getAttribute("studentUser") : null;
        String role = (session != null) ? (String) session.getAttribute("userRole") : null;

        String subIdStr = request.getParameter("submissionId");
        AssessmentSubmission submission = null;

        if (subIdStr != null && !subIdStr.trim().isEmpty()) {
            try {
                int subId = Integer.parseInt(subIdStr.trim());
                submission = assessmentDAO.getSubmissionById(subId);
            } catch (NumberFormatException ignored) {}
        }

        // If no submissionId requested, retrieve the latest for this student
        if (submission == null && student != null) {
            submission = assessmentDAO.getLatestSubmissionByStudentId(student.getId());
        }

        if (submission == null) {
            response.sendRedirect(request.getContextPath() + "/student/dashboard?msg=no_assessment");
            return;
        }

        // Security check: ensure student can only view their own submission (admins can view any)
        if (student != null && !"ADMIN".equals(role) && submission.getStudentId() != student.getId()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: You cannot view another student's results.");
            return;
        }

        request.setAttribute("submission", submission);
        request.setAttribute("recommendations", submission.getRecommendations());
        request.setAttribute("responses", submission.getResponses());

        request.getRequestDispatcher("/WEB-INF/views/student/results.jsp").forward(request, response);
    }
}
