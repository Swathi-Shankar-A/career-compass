package com.career.servlet;

import com.career.dao.CareerDAO;
import com.career.model.Career;
import com.career.util.JsonUtil;
import com.career.util.SecurityUtil;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles administrator view and management of all 35 seeded careers,
 * their question mappings, weights, and beginner learning paths.
 */
public class AdminCareersServlet extends HttpServlet {

    private CareerDAO careerDAO;

    @Override
    public void init() throws ServletException {
        this.careerDAO = new CareerDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String careerIdStr = request.getParameter("id");
        Career selectedCareer = null;
        if (careerIdStr != null && !careerIdStr.trim().isEmpty()) {
            try {
                int cId = Integer.parseInt(careerIdStr.trim());
                selectedCareer = careerDAO.getCareerById(cId);
                request.setAttribute("selectedCareer", selectedCareer);
            } catch (NumberFormatException ignored) {}
        }

        List<Career> careers = careerDAO.getAllCareers();
        SecurityUtil.getOrCreateCsrfToken(request);

        String accept = request.getHeader("Accept");
        String format = request.getParameter("format");
        if ((accept != null && accept.contains("application/json")) || "json".equalsIgnoreCase(format)) {
            response.setContentType("application/json;charset=UTF-8");
            StringBuilder sb = new StringBuilder("{");
            sb.append("\"totalCareers\":").append(careers.size()).append(",");
            sb.append("\"selectedCareer\":").append(selectedCareer != null ? JsonUtil.toJson(selectedCareer) : "null").append(",");
            sb.append("\"careers\":").append(JsonUtil.careersToJson(careers));
            sb.append("}");
            response.getWriter().write(sb.toString());
            return;
        }

        request.setAttribute("careers", careers);
        request.setAttribute("totalCareers", careers.size());
        request.getRequestDispatcher("/admin/careers.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accept = request.getHeader("Accept");
        String format = request.getParameter("format");
        boolean isJson = (accept != null && accept.contains("application/json")) || "json".equalsIgnoreCase(format);

        if (!SecurityUtil.validateCsrfToken(request)) {
            if (isJson) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JsonUtil.response(false, "Session security validation failed. Please refresh and try again."));
                return;
            }
            request.setAttribute("errorMessage", "Session security validation failed.");
            doGet(request, response);
            return;
        }

        String idStr = request.getParameter("careerId");
        String name = request.getParameter("name");
        String shortDesc = request.getParameter("shortDescription");
        String responsibilities = request.getParameter("responsibilities");
        String skillsRequired = request.getParameter("skillsRequired");
        String toolsTech = request.getParameter("toolsTechnologies");
        String learningSuggestions = request.getParameter("learningSuggestions");
        String activeStr = request.getParameter("isActive");

        if (idStr != null && name != null && !name.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(idStr.trim());
                boolean active = "true".equalsIgnoreCase(activeStr) || "1".equals(activeStr) || "on".equalsIgnoreCase(activeStr);

                Career career = new Career(id, name.trim(), shortDesc.trim(), responsibilities.trim(),
                        skillsRequired.trim(), toolsTech.trim(), learningSuggestions.trim(), active);

                boolean success = careerDAO.updateCareer(career);
                if (success) {
                    if (isJson) {
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().write(JsonUtil.response(true, "Career profile updated successfully.", request.getContextPath() + "/admin/careers?id=" + id + "&success=updated"));
                        return;
                    }
                    response.sendRedirect(request.getContextPath() + "/admin/careers?id=" + id + "&success=updated");
                    return;
                }
            } catch (NumberFormatException ignored) {}
        }

        if (isJson) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JsonUtil.response(false, "Failed to update career."));
            return;
        }
        response.sendRedirect(request.getContextPath() + "/admin/careers");
    }
}
