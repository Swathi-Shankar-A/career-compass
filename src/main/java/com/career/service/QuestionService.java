package com.career.service;

import com.career.dao.QuestionDAO;
import com.career.model.Question;
import com.career.model.QuestionOption;

import java.util.List;

/**
 * Service class for managing assessment questions and enforcing the
 * requirement that exactly 20 active questions are maintained for valid assessments.
 */
public class QuestionService {

    public static final int REQUIRED_ACTIVE_QUESTIONS = 20;

    private final QuestionDAO questionDAO;

    public QuestionService() {
        this.questionDAO = new QuestionDAO();
    }

    public QuestionService(QuestionDAO questionDAO) {
        this.questionDAO = questionDAO;
    }

    /**
     * Verifies if the system is ready to deliver an assessment,
     * ensuring exactly 20 active questions are available.
     */
    public boolean isAssessmentReady() {
        return questionDAO.countActiveQuestions() == REQUIRED_ACTIVE_QUESTIONS;
    }

    public int getActiveQuestionsCount() {
        return questionDAO.countActiveQuestions();
    }

    public List<Question> getActiveQuestions() {
        return questionDAO.getActiveQuestions();
    }

    public List<Question> getAllQuestions() {
        return questionDAO.getAllQuestions();
    }

    public Question getQuestionById(int id) {
        return questionDAO.getQuestionById(id);
    }

    public boolean addQuestion(Question question, List<QuestionOption> options) {
        if (question == null || question.getQuestionText() == null || question.getQuestionText().trim().isEmpty()) {
            return false;
        }
        if (options == null || options.size() != 4) {
            return false;
        }
        return questionDAO.addQuestion(question, options);
    }

    public boolean updateQuestion(Question question) {
        if (question == null || question.getQuestionText() == null || question.getQuestionText().trim().isEmpty()) {
            return false;
        }
        return questionDAO.updateQuestion(question);
    }

    public boolean updateOption(int optionId, String newText) {
        if (newText == null || newText.trim().isEmpty()) {
            return false;
        }
        return questionDAO.updateOption(optionId, newText.trim());
    }

    public boolean toggleActiveStatus(int questionId, boolean newStatus) {
        return questionDAO.toggleActiveStatus(questionId, newStatus);
    }

    public int getNextOrder() {
        return questionDAO.getNextQuestionOrder();
    }
}
