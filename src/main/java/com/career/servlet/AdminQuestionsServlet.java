package com.career.servlet;

import com.career.dao.QuestionDAO;
import com.career.model.Question;
import com.career.model.QuestionOption;
import com.career.service.QuestionService;
import com.career.util.JsonUtil;
import com.career.util.SecurityUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles administrator management of assessment questions:
 * - Listing all questions (active and inactive)
 * - Adding a new question with 4 options
 * - Editing question text, category, order, and option texts
 * - Soft activation/deactivation
 */
public class AdminQuestionsServlet extends HttpServlet {

    private QuestionDAO questionDAO;
    private QuestionService questionService;

    @Override
    public void init() throws ServletException {
        this.questionDAO = new QuestionDAO();
        this.questionService = new QuestionService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String editIdStr = request.getParameter("editId");
        Question editQuestion = null;
        if (editIdStr != null && !editIdStr.trim().isEmpty()) {
            try {
                int editId = Integer.parseInt(editIdStr.trim());
                editQuestion = questionDAO.getQuestionById(editId);
                request.setAttribute("editQuestion", editQuestion);
            } catch (NumberFormatException ignored) {}
        }

        List<Question> allQuestions = questionDAO.getAllQuestions();
        int activeCount = questionDAO.countActiveQuestions();
        int nextOrder = questionDAO.getNextQuestionOrder();

        SecurityUtil.getOrCreateCsrfToken(request);

        String accept = request.getHeader("Accept");
        String format = request.getParameter("format");
        if ((accept != null && accept.contains("application/json")) || "json".equalsIgnoreCase(format)) {
            response.setContentType("application/json;charset=UTF-8");
            StringBuilder sb = new StringBuilder("{");
            sb.append("\"activeCount\":").append(activeCount).append(",");
            sb.append("\"nextOrder\":").append(nextOrder).append(",");
            sb.append("\"questionCountValid\":").append(activeCount == QuestionService.REQUIRED_ACTIVE_QUESTIONS).append(",");
            sb.append("\"editQuestion\":").append(editQuestion != null ? JsonUtil.toJson(editQuestion) : "null").append(",");
            sb.append("\"questions\":").append(JsonUtil.questionsToJson(allQuestions));
            sb.append("}");
            response.getWriter().write(sb.toString());
            return;
        }

        request.setAttribute("questions", allQuestions);
        request.setAttribute("activeCount", activeCount);
        request.setAttribute("nextOrder", nextOrder);
        request.setAttribute("questionCountValid", (activeCount == QuestionService.REQUIRED_ACTIVE_QUESTIONS));

        request.getRequestDispatcher("/admin/questions.html").forward(request, response);
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
            request.setAttribute("errorMessage", "Session security validation failed. Please try again.");
            doGet(request, response);
            return;
        }

        String action = request.getParameter("action");

        if ("add".equals(action)) {
            handleAddQuestion(request, response, isJson);
        } else if ("edit".equals(action)) {
            handleEditQuestion(request, response, isJson);
        } else if ("toggle".equals(action)) {
            handleToggleStatus(request, response, isJson);
        } else {
            if (isJson) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JsonUtil.response(false, "Unknown action."));
                return;
            }
            response.sendRedirect(request.getContextPath() + "/admin/questions");
        }
    }

    private void handleAddQuestion(HttpServletRequest request, HttpServletResponse response, boolean isJson)
            throws ServletException, IOException {
        String questionText = request.getParameter("questionText");
        String category = request.getParameter("category");
        String orderStr = request.getParameter("questionOrder");
        String activeStr = request.getParameter("isActive");

        String opt4 = request.getParameter("optionText_4");
        String opt3 = request.getParameter("optionText_3");
        String opt2 = request.getParameter("optionText_2");
        String opt1 = request.getParameter("optionText_1");

        if (questionText == null || questionText.trim().isEmpty() ||
            category == null || category.trim().isEmpty() ||
            opt4 == null || opt4.trim().isEmpty() ||
            opt3 == null || opt3.trim().isEmpty() ||
            opt2 == null || opt2.trim().isEmpty() ||
            opt1 == null || opt1.trim().isEmpty()) {
            if (isJson) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JsonUtil.response(false, "All fields (question text, category, and 4 option labels) are required."));
                return;
            }
            request.setAttribute("errorMessage", "All fields are required.");
            doGet(request, response);
            return;
        }

        int order = questionDAO.getNextQuestionOrder();
        try {
            if (orderStr != null && !orderStr.trim().isEmpty()) {
                order = Integer.parseInt(orderStr.trim());
            }
        } catch (NumberFormatException ignored) {}

        boolean active = "true".equalsIgnoreCase(activeStr) || "1".equals(activeStr) || "on".equalsIgnoreCase(activeStr);

        Question q = new Question();
        q.setQuestionText(questionText.trim());
        q.setCategory(category.trim());
        q.setQuestionOrder(order);
        q.setActive(active);

        List<QuestionOption> options = new ArrayList<>();
        options.add(new QuestionOption(0, 0, opt4.trim(), 4));
        options.add(new QuestionOption(0, 0, opt3.trim(), 3));
        options.add(new QuestionOption(0, 0, opt2.trim(), 2));
        options.add(new QuestionOption(0, 0, opt1.trim(), 1));

        boolean success = questionDAO.addQuestion(q, options);
        if (success) {
            if (isJson) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JsonUtil.response(true, "Question and options added successfully.", request.getContextPath() + "/admin/questions?success=question_added"));
                return;
            }
            response.sendRedirect(request.getContextPath() + "/admin/questions?success=question_added");
        } else {
            if (isJson) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JsonUtil.response(false, "Database insert failed. Please try again."));
                return;
            }
            request.setAttribute("errorMessage", "Failed to add question.");
            doGet(request, response);
        }
    }

    private void handleEditQuestion(HttpServletRequest request, HttpServletResponse response, boolean isJson)
            throws ServletException, IOException {
        String idStr = request.getParameter("questionId");
        String questionText = request.getParameter("questionText");
        String category = request.getParameter("category");
        String orderStr = request.getParameter("questionOrder");
        String activeStr = request.getParameter("isActive");

        if (idStr == null || questionText == null || questionText.trim().isEmpty() ||
            category == null || category.trim().isEmpty()) {
            if (isJson) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JsonUtil.response(false, "Required question fields cannot be empty."));
                return;
            }
            request.setAttribute("errorMessage", "Required question fields cannot be empty.");
            doGet(request, response);
            return;
        }

        int id;
        int order;
        try {
            id = Integer.parseInt(idStr.trim());
            order = Integer.parseInt(orderStr.trim());
        } catch (NumberFormatException e) {
            if (isJson) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JsonUtil.response(false, "Invalid question ID or question order value."));
                return;
            }
            request.setAttribute("errorMessage", "Invalid question ID or question order value.");
            doGet(request, response);
            return;
        }

        boolean active = "true".equalsIgnoreCase(activeStr) || "1".equals(activeStr) || "on".equalsIgnoreCase(activeStr);

        Question q = new Question();
        q.setId(id);
        q.setQuestionOrder(order);
        q.setQuestionText(questionText.trim());
        q.setCategory(category.trim());
        q.setActive(active);

        boolean updated = questionDAO.updateQuestion(q);

        // Update the 4 option texts if provided
        for (int rating = 1; rating <= 4; rating++) {
            String optIdStr = request.getParameter("optionId_" + rating);
            String optText = request.getParameter("optionText_" + rating);
            if (optIdStr != null && optText != null && !optText.trim().isEmpty()) {
                try {
                    int optId = Integer.parseInt(optIdStr.trim());
                    questionDAO.updateOption(optId, optText.trim());
                } catch (NumberFormatException ignored) {}
            }
        }

        if (updated) {
            if (isJson) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JsonUtil.response(true, "Question updated successfully.", request.getContextPath() + "/admin/questions?success=question_updated"));
                return;
            }
            response.sendRedirect(request.getContextPath() + "/admin/questions?success=question_updated");
        } else {
            if (isJson) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JsonUtil.response(false, "Failed to update question."));
                return;
            }
            request.setAttribute("errorMessage", "Failed to update question.");
            doGet(request, response);
        }
    }

    private void handleToggleStatus(HttpServletRequest request, HttpServletResponse response, boolean isJson)
            throws ServletException, IOException {
        String idStr = request.getParameter("questionId");
        String currentStatusStr = request.getParameter("currentStatus");

        if (idStr != null) {
            try {
                int id = Integer.parseInt(idStr.trim());
                boolean currentStatus = Boolean.parseBoolean(currentStatusStr);
                boolean newStatus = !currentStatus;

                questionDAO.toggleActiveStatus(id, newStatus);
                if (isJson) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(JsonUtil.response(true, "Question status toggled.", request.getContextPath() + "/admin/questions?success=status_toggled"));
                    return;
                }
                response.sendRedirect(request.getContextPath() + "/admin/questions?success=status_toggled");
                return;
            } catch (NumberFormatException ignored) {}
        }

        if (isJson) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JsonUtil.response(false, "Invalid question ID."));
            return;
        }
        response.sendRedirect(request.getContextPath() + "/admin/questions");
    }
}
