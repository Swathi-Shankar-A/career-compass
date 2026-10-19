package com.career.servlet;

import com.career.dao.AssessmentDAO;
import com.career.model.AssessmentSubmission;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles administrator inspection of a student's full assessment submission:
 * displays all 20 responses, snapshot question texts, selected options,
 * ratings, and the generated top 5 career recommendations.
 */
public class AdminStudentResponsesServlet extends HttpServlet {

    private AssessmentDAO assessmentDAO;

    @Override
    public void init() throws ServletException {
        this.assessmentDAO = new AssessmentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String subIdStr = request.getParameter("submissionId");
        if (subIdStr == null || subIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/students");
            return;
        }

        try {
            int submissionId = Integer.parseInt(subIdStr.trim());
            AssessmentSubmission submission = assessmentDAO.getSubmissionById(submissionId);

            if (submission == null) {
                response.sendRedirect(request.getContextPath() + "/admin/students?error=not_found");
                return;
            }

            request.setAttribute("submission", submission);
            request.setAttribute("responses", submission.getResponses());
            request.setAttribute("recommendations", submission.getRecommendations());

            request.getRequestDispatcher("/WEB-INF/views/admin/student-responses.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/students");
        }
    }
}
