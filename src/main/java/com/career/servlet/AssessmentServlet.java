package com.career.servlet;

import com.career.dao.AssessmentDAO;
import com.career.model.CareerRecommendation;
import com.career.model.Question;
import com.career.model.QuestionOption;
import com.career.model.Student;
import com.career.model.StudentResponse;
import com.career.service.QuestionService;
import com.career.service.RecommendationEngine;
import com.career.util.JsonUtil;
import com.career.util.SecurityUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Handles the 20-question career assessment flow:
 * GET: Serves the questions HTML page or JSON questions list.
 * POST: Evaluates responses, computes recommendations, and saves in database transaction.
 */
public class AssessmentServlet extends HttpServlet {

    private QuestionService questionService;
    private RecommendationEngine recommendationEngine;
    private AssessmentDAO assessmentDAO;

    @Override
    public void init() throws ServletException {
        this.questionService = new QuestionService();
        this.recommendationEngine = new RecommendationEngine();
        this.assessmentDAO = new AssessmentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accept = request.getHeader("Accept");
        String format = request.getParameter("format");
        boolean isJson = (accept != null && accept.contains("application/json")) || "json".equalsIgnoreCase(format);

        // Enforce the requirement: Exactly 20 active questions must be configured
        if (!questionService.isAssessmentReady()) {
            int activeCount = questionService.getActiveQuestionsCount();
            String errorMsg = "The assessment is temporarily unavailable because exactly 20 active questions are required. Currently active: "
                    + activeCount + ". Please check back shortly.";
            if (isJson) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"ready\":false,\"activeCount\":" + activeCount + ",\"message\":\"" + JsonUtil.escape(errorMsg) + "\"}");
                return;
            }
            request.setAttribute("errorMessage", errorMsg);
            request.getRequestDispatcher("/student/dashboard.html").forward(request, response);
            return;
        }

        List<Question> questions = questionService.getActiveQuestions();
        SecurityUtil.getOrCreateCsrfToken(request);

        if (isJson) {
            response.setContentType("application/json;charset=UTF-8");
            StringBuilder sb = new StringBuilder("{");
            sb.append("\"ready\":true,");
            sb.append("\"totalQuestions\":").append(questions.size()).append(",");
            sb.append("\"questions\":").append(JsonUtil.questionsToJson(questions));
            sb.append("}");
            response.getWriter().write(sb.toString());
            return;
        }

        request.setAttribute("questions", questions);
        request.setAttribute("totalQuestions", questions.size());
        request.getRequestDispatcher("/student/assessment.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accept = request.getHeader("Accept");
        String format = request.getParameter("format");
        boolean isJson = (accept != null && accept.contains("application/json")) || "json".equalsIgnoreCase(format);

        // 1. CSRF token validation
        if (!SecurityUtil.validateCsrfToken(request)) {
            if (isJson) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JsonUtil.response(false, "Session security validation failed. Please refresh and try again."));
                return;
            }
            request.setAttribute("errorMessage", "Session security validation failed. Please try submitting again.");
            doGet(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        Student student = (Student) session.getAttribute("studentUser");

        // 2. Load active questions from database
        List<Question> questions = questionService.getActiveQuestions();
        if (questions.size() != QuestionService.REQUIRED_ACTIVE_QUESTIONS) {
            String msg = "Assessment configuration error: exactly 20 active questions are required.";
            if (isJson) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JsonUtil.response(false, msg));
                return;
            }
            request.setAttribute("errorMessage", msg);
            doGet(request, response);
            return;
        }

        Map<Integer, Integer> ratings = new HashMap<>();
        List<StudentResponse> responseSnapshots = new ArrayList<>();
        List<Integer> unansweredQuestions = new ArrayList<>();

        for (Question q : questions) {
            String paramName = "q_" + q.getId();
            String selectedValStr = request.getParameter(paramName);

            if (selectedValStr == null || selectedValStr.trim().isEmpty()) {
                unansweredQuestions.add(q.getQuestionOrder());
                continue;
            }

            int ratingValue;
            try {
                ratingValue = Integer.parseInt(selectedValStr.trim());
            } catch (NumberFormatException e) {
                unansweredQuestions.add(q.getQuestionOrder());
                continue;
            }

            if (ratingValue < 1 || ratingValue > 4) {
                unansweredQuestions.add(q.getQuestionOrder());
                continue;
            }

            ratings.put(q.getId(), ratingValue);

            // Find matching option text for historical snapshot
            String selectedOptionText = "Rating " + ratingValue;
            for (QuestionOption opt : q.getOptions()) {
                if (opt.getRatingValue() == ratingValue) {
                    selectedOptionText = opt.getOptionText();
                    break;
                }
            }

            StudentResponse resp = new StudentResponse();
            resp.setQuestionId(q.getId());
            resp.setQuestionTextSnapshot(q.getQuestionText());
            resp.setSelectedOptionSnapshot(selectedOptionText);
            resp.setRatingValue(ratingValue);
            resp.setCategory(q.getCategory());
            responseSnapshots.add(resp);
        }

        // 3. Validation: Reject incomplete assessments
        if (!unansweredQuestions.isEmpty()) {
            String errorMsg = "Please complete all 20 questions before submitting. Unanswered topic(s): "
                    + unansweredQuestions.toString();
            if (isJson) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JsonUtil.response(false, errorMsg));
                return;
            }
            request.setAttribute("errorMessage", errorMsg);
            request.setAttribute("questions", questions);
            request.setAttribute("totalQuestions", questions.size());
            request.setAttribute("previousAnswers", ratings);
            request.getRequestDispatcher("/student/assessment.html").forward(request, response);
            return;
        }

        // 4. Run the Core Java deterministic recommendation engine
        RecommendationEngine.AssessmentResult result = recommendationEngine.evaluate(ratings);
        List<CareerRecommendation> recommendations = result.getRecommendations();
        String summary = result.getPersonalizedSummary();

        // 5. Persist submission, responses, and recommendations in an atomic transaction
        try {
            int submissionId = assessmentDAO.saveSubmissionTransaction(
                    student.getId(), summary, responseSnapshots, recommendations);

            if (submissionId > 0) {
                String resultsUrl = request.getContextPath() + "/student/results?submissionId=" + submissionId;
                if (isJson) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"success\":true,\"submissionId\":" + submissionId + ",\"redirect\":\"" + resultsUrl + "\"}");
                    return;
                }
                response.sendRedirect(resultsUrl);
            } else {
                if (isJson) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(JsonUtil.response(false, "Database transaction failed. Please retry your submission."));
                    return;
                }
                request.setAttribute("errorMessage", "Database transaction failed. Please retry your submission.");
                doGet(request, response);
            }
        } catch (SQLException e) {
            String errMsg = "An error occurred while saving your assessment: " + e.getMessage();
            if (isJson) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JsonUtil.response(false, errMsg));
                return;
            }
            request.setAttribute("errorMessage", errMsg);
            doGet(request, response);
        }
    }
}
