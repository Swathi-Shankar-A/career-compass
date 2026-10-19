package com.career.servlet;

import com.career.dao.CareerDAO;
import com.career.model.Career;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Serves comprehensive career profile and roadmap details.
 */
public class CareerDetailsServlet extends HttpServlet {

    private CareerDAO careerDAO;

    @Override
    public void init() throws ServletException {
        this.careerDAO = new CareerDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/student/dashboard");
            return;
        }

        try {
            int careerId = Integer.parseInt(idStr.trim());
            Career career = careerDAO.getCareerById(careerId);

            if (career == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Career not found.");
                return;
            }

            request.setAttribute("career", career);
            request.getRequestDispatcher("/WEB-INF/views/student/career-details.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/student/dashboard");
        }
    }
}
