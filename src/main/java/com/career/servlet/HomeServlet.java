package com.career.servlet;

import com.career.dao.CareerDAO;
import com.career.model.Career;
import com.career.util.JsonUtil;

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
        List<Career> careers = careerDAO.getActiveCareers();

        String accept = request.getHeader("Accept");
        String format = request.getParameter("format");
        if ((accept != null && accept.contains("application/json")) || "json".equalsIgnoreCase(format)) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"featuredCareers\":" + JsonUtil.careersToJson(careers) + "}");
            return;
        }

        request.setAttribute("featuredCareers", careers);
        request.getRequestDispatcher("/index.html").forward(request, response);
    }
}
