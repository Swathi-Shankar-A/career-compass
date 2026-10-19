package com.career.servlet;

import com.career.dao.AssessmentDAO;
import com.career.dao.CareerDAO;
import com.career.dao.QuestionDAO;
import com.career.dao.StudentDAO;
import com.career.model.AssessmentSubmission;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Admin Dashboard Servlet. Aggregates system metrics, checks question
 * integrity (exactly 20 required), and lists recent student submissions.
 */
public class AdminDashboardServlet extends HttpServlet {

    private StudentDAO studentDAO;
    private AssessmentDAO assessmentDAO;
    private QuestionDAO questionDAO;
    private CareerDAO careerDAO;

    @Override
    public void init() throws ServletException {
        this.studentDAO = new StudentDAO();
        this.assessmentDAO = new AssessmentDAO();
        this.questionDAO = new QuestionDAO();
        this.careerDAO = new CareerDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int totalStudents = studentDAO.countStudents("");
        int totalSubmissions = assessmentDAO.countSubmissions();
        int activeQuestions = questionDAO.countActiveQuestions();
        int totalQuestions = questionDAO.countTotalQuestions();
        int totalCareers = careerDAO.countCareers();

        List<AssessmentSubmission> recentSubmissions = assessmentDAO.getRecentSubmissions(5);

        boolean questionCountValid = (activeQuestions == 20);

        request.setAttribute("totalStudents", totalStudents);
        request.setAttribute("totalSubmissions", totalSubmissions);
        request.setAttribute("activeQuestions", activeQuestions);
        request.setAttribute("totalQuestions", totalQuestions);
        request.setAttribute("totalCareers", totalCareers);
        request.setAttribute("questionCountValid", questionCountValid);
        request.setAttribute("recentSubmissions", recentSubmissions);

        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
    }
}
