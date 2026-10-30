package com.career.servlet;

import com.career.dao.AssessmentDAO;
import com.career.dao.StudentDAO;
import com.career.model.AssessmentSubmission;
import com.career.model.Student;
import com.career.util.JsonUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles administrator listing and searching of registered students.
 */
public class AdminStudentsServlet extends HttpServlet {

    private StudentDAO studentDAO;
    private AssessmentDAO assessmentDAO;

    @Override
    public void init() throws ServletException {
        this.studentDAO = new StudentDAO();
        this.assessmentDAO = new AssessmentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String search = request.getParameter("search");
        String studentIdStr = request.getParameter("studentId");
        Student selectedStudent = null;
        List<AssessmentSubmission> studentSubmissions = null;

        // If specific student selected, show their detailed profile & submission history
        if (studentIdStr != null && !studentIdStr.trim().isEmpty()) {
            try {
                int sId = Integer.parseInt(studentIdStr.trim());
                selectedStudent = studentDAO.findById(sId);
                if (selectedStudent != null) {
                    studentSubmissions = assessmentDAO.getSubmissionsByStudentId(sId);
                    request.setAttribute("selectedStudent", selectedStudent);
                    request.setAttribute("studentSubmissions", studentSubmissions);
                }
            } catch (NumberFormatException ignored) {}
        }

        List<Student> students = studentDAO.listStudents(search, 0, 100);
        int totalCount = studentDAO.countStudents(search);

        // Fetch submission counts for students in this page
        Map<Integer, Integer> submissionCounts = new HashMap<>();
        for (Student s : students) {
            List<AssessmentSubmission> subs = assessmentDAO.getSubmissionsByStudentId(s.getId());
            submissionCounts.put(s.getId(), subs.size());
        }

        String accept = request.getHeader("Accept");
        String format = request.getParameter("format");
        if ((accept != null && accept.contains("application/json")) || "json".equalsIgnoreCase(format)) {
            response.setContentType("application/json;charset=UTF-8");
            StringBuilder sb = new StringBuilder("{");
            sb.append("\"totalCount\":").append(totalCount).append(",");
            sb.append("\"search\":\"").append(JsonUtil.escape(search)).append("\",");
            sb.append("\"submissionCounts\":").append(JsonUtil.mapIntIntToJson(submissionCounts)).append(",");
            sb.append("\"selectedStudent\":").append(selectedStudent != null ? JsonUtil.toJson(selectedStudent) : "null").append(",");
            sb.append("\"studentSubmissions\":").append(studentSubmissions != null ? JsonUtil.submissionsToJson(studentSubmissions) : "[]").append(",");
            sb.append("\"students\":").append(JsonUtil.studentsToJson(students));
            sb.append("}");
            response.getWriter().write(sb.toString());
            return;
        }

        request.setAttribute("students", students);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("search", search);
        request.setAttribute("submissionCounts", submissionCounts);

        request.getRequestDispatcher("/admin/students.html").forward(request, response);
    }
}
