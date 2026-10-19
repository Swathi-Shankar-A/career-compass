package com.career.servlet;

import com.career.dao.AssessmentDAO;
import com.career.model.AssessmentSubmission;
import com.career.model.Student;

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

        request.setAttribute("history", history);
        request.getRequestDispatcher("/WEB-INF/views/student/history.jsp").forward(request, response);
    }
}
