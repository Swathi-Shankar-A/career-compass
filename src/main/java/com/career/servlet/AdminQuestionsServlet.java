package com.career.servlet;

import com.career.dao.QuestionDAO;
import com.career.model.Question;
import com.career.model.QuestionOption;
import com.career.service.QuestionService;
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
        if (editIdStr != null && !editIdStr.trim().isEmpty()) {
            try {
                int editId = Integer.parseInt(editIdStr.trim());
                Question editQuestion = questionDAO.getQuestionById(editId);
                request.setAttribute("editQuestion", editQuestion);
            } catch (NumberFormatException ignored) {}
        }

        List<Question> allQuestions = questionDAO.getAllQuestions();
        int activeCount = questionDAO.countActiveQuestions();
        int nextOrder = questionDAO.getNextQuestionOrder();

        SecurityUtil.getOrCreateCsrfToken(request);

        request.setAttribute("questions", allQuestions);
        request.setAttribute("activeCount", activeCount);
        request.setAttribute("nextOrder", nextOrder);
        request.setAttribute("questionCountValid", (activeCount == QuestionService.REQUIRED_ACTIVE_QUESTIONS));

        request.getRequestDispatcher("/WEB-INF/views/admin/questions.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SecurityUtil.validateCsrfToken(request)) {
            request.setAttribute("errorMessage", "Session security validation failed. Please try again.");
            doGet(request, response);
            return;
        }

        String action = request.getParameter("action");

        if ("add".equals(action)) {
            handleAddQuestion(request, response);
        } else if ("edit".equals(action)) {
            handleEditQuestion(request, response);
        } else if ("toggle".equals(action)) {
            handleToggleStatus(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/questions");
        }
    }

    private void handleAddQuestion(HttpServletRequest request, HttpServletResponse response)
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
            request.setAttribute("errorMessage", "All question fields and all 4 option labels are required.");
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
        q.setQuestionOrder(order);
        q.setQuestionText(questionText.trim());
        q.setCategory(category.trim());
        q.setActive(active);

        List<QuestionOption> options = new ArrayList<>();
        options.add(new QuestionOption(0, 0, opt4.trim(), 4));
        options.add(new QuestionOption(0, 0, opt3.trim(), 3));
        options.add(new QuestionOption(0, 0, opt2.trim(), 2));
        options.add(new QuestionOption(0, 0, opt1.trim(), 1));

        boolean added = questionDAO.addQuestion(q, options);
        if (added) {
            response.sendRedirect(request.getContextPath() + "/admin/questions?success=question_added");
        } else {
            request.setAttribute("errorMessage", "Failed to add new question. Please check duplicate order or database errors.");
            doGet(request, response);
        }
    }

    private void handleEditQuestion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("questionId");
        String questionText = request.getParameter("questionText");
        String category = request.getParameter("category");
        String orderStr = request.getParameter("questionOrder");
        String activeStr = request.getParameter("isActive");

        if (idStr == null || questionText == null || questionText.trim().isEmpty() ||
            category == null || category.trim().isEmpty()) {
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
            response.sendRedirect(request.getContextPath() + "/admin/questions?success=question_updated");
        } else {
            request.setAttribute("errorMessage", "Failed to update question.");
            doGet(request, response);
        }
    }

    private void handleToggleStatus(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("questionId");
        String currentStatusStr = request.getParameter("currentStatus");

        if (idStr != null) {
            try {
                int id = Integer.parseInt(idStr.trim());
                boolean currentStatus = Boolean.parseBoolean(currentStatusStr);
                boolean newStatus = !currentStatus;

                questionDAO.toggleActiveStatus(id, newStatus);
                response.sendRedirect(request.getContextPath() + "/admin/questions?success=status_toggled");
                return;
            } catch (NumberFormatException ignored) {}
        }

        response.sendRedirect(request.getContextPath() + "/admin/questions");
    }
}
