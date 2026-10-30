package com.career.servlet;

import com.career.dao.AssessmentDAO;
import com.career.model.AssessmentSubmission;
import com.career.model.Student;
import com.career.service.QuestionService;
import com.career.util.JsonUtil;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Student Dashboard Servlet. Serves dashboard HTML view or personalized JSON data.
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

        String accept = request.getHeader("Accept");
        String format = request.getParameter("format");
        if ((accept != null && accept.contains("application/json")) || "json".equalsIgnoreCase(format)) {
            response.setContentType("application/json;charset=UTF-8");
            StringBuilder sb = new StringBuilder("{");
            sb.append("\"student\":").append(JsonUtil.toJson(student)).append(",");
            sb.append("\"totalSubmissions\":").append(history.size()).append(",");
            sb.append("\"assessmentReady\":").append(assessmentReady).append(",");
            sb.append("\"activeQuestionCount\":").append(activeQuestionCount).append(",");
            sb.append("\"latestSubmission\":").append(latestSubmission != null ? JsonUtil.toJson(latestSubmission) : "null").append(",");
            sb.append("\"history\":").append(JsonUtil.submissionsToJson(history));
            sb.append("}");
            response.getWriter().write(sb.toString());
            return;
        }

        request.setAttribute("history", history);
        request.setAttribute("totalSubmissions", history.size());
        request.setAttribute("latestSubmission", latestSubmission);
        request.setAttribute("assessmentReady", assessmentReady);
        request.setAttribute("activeQuestionCount", activeQuestionCount);

        request.getRequestDispatcher("/student/dashboard.html").forward(request, response);
    }
}
