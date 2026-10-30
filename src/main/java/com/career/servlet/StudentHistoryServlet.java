package com.career.servlet;

import com.career.dao.AssessmentDAO;
import com.career.model.AssessmentSubmission;
import com.career.model.Student;
import com.career.util.JsonUtil;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Displays previous assessment attempts and past career recommendations.
 */
public class StudentHistoryServlet extends HttpServlet {

    private AssessmentDAO assessmentDAO;

    @Override
    public void init() throws ServletException {
        this.assessmentDAO = new AssessmentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Student student = (Student) session.getAttribute("studentUser");

        List<AssessmentSubmission> history = assessmentDAO.getSubmissionsByStudentId(student.getId());

        String accept = request.getHeader("Accept");
        String format = request.getParameter("format");
        if ((accept != null && accept.contains("application/json")) || "json".equalsIgnoreCase(format)) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":true,\"history\":" + JsonUtil.submissionsToJson(history) + "}");
            return;
        }

        request.setAttribute("history", history);
        request.getRequestDispatcher("/student/history.html").forward(request, response);
    }
}
