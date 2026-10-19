package com.career.servlet;

import com.career.dao.AssessmentDAO;
import com.career.model.AssessmentSubmission;
import com.career.model.Student;
import com.career.service.QuestionService;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Student Dashboard Servlet. Displays personalized statistics,
 * recent recommendation overview, and assessment access.
 */
public class StudentDashboardServlet extends HttpServlet {

    private AssessmentDAO assessmentDAO;
    private QuestionService questionService;

    @Override
    public void init() throws ServletException {
        this.assessmentDAO = new AssessmentDAO();
        this.questionService = new QuestionService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Student student = (Student) session.getAttribute("studentUser");

        List<AssessmentSubmission> history = assessmentDAO.getSubmissionsByStudentId(student.getId());
        AssessmentSubmission latestSubmission = assessmentDAO.getLatestSubmissionByStudentId(student.getId());

        boolean assessmentReady = questionService.isAssessmentReady();
        int activeQuestionCount = questionService.getActiveQuestionsCount();

        request.setAttribute("history", history);
        request.setAttribute("totalSubmissions", history.size());
        request.setAttribute("latestSubmission", latestSubmission);
        request.setAttribute("assessmentReady", assessmentReady);
        request.setAttribute("activeQuestionCount", activeQuestionCount);

        request.getRequestDispatcher("/WEB-INF/views/student/dashboard.jsp").forward(request, response);
    }
}
