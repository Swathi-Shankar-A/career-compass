package com.career.servlet;

import com.career.dao.CareerDAO;
import com.career.model.Career;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Public Landing Page Servlet.
 */
public class HomeServlet extends HttpServlet {

    private CareerDAO careerDAO;

    @Override
    public void init() throws ServletException {
        this.careerDAO = new CareerDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Load a selection of featured careers for landing page display
        List<Career> careers = careerDAO.getActiveCareers();
        request.setAttribute("featuredCareers", careers);
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}
