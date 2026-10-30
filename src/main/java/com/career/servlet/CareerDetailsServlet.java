package com.career.servlet;

import com.career.dao.CareerDAO;
import com.career.model.Career;
import com.career.util.JsonUtil;

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
        String accept = request.getHeader("Accept");
        String format = request.getParameter("format");
        boolean isJson = (accept != null && accept.contains("application/json")) || "json".equalsIgnoreCase(format);

        if (idStr == null || idStr.trim().isEmpty()) {
            if (isJson) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"success\":false,\"message\":\"Career ID is required.\"}");
                return;
            }
            response.sendRedirect(request.getContextPath() + "/student/dashboard");
            return;
        }

        try {
            int careerId = Integer.parseInt(idStr.trim());
            Career career = careerDAO.getCareerById(careerId);

            if (career == null) {
                if (isJson) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"success\":false,\"message\":\"Career not found.\"}");
                    return;
                }
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Career not found.");
                return;
            }

            if (isJson) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"success\":true,\"career\":" + JsonUtil.toJson(career) + "}");
                return;
            }

            request.setAttribute("career", career);
            request.getRequestDispatcher("/student/career-details.html").forward(request, response);
        } catch (NumberFormatException e) {
            if (isJson) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"success\":false,\"message\":\"Invalid Career ID.\"}");
                return;
            }
            response.sendRedirect(request.getContextPath() + "/student/dashboard");
        }
    }
}
