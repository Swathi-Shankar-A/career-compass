package com.career.service;

import com.career.dao.CareerDAO;
import com.career.dao.QuestionDAO;
import com.career.model.Career;
import com.career.model.CareerQuestionMapping;
import com.career.model.CareerRecommendation;
import com.career.model.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Transparent, deterministic, and explainable career recommendation engine.
 * 
 * Algorithm Explanation for Viva:
 * 1. For each career, identify its relevant mapped questions and their assigned weights (w_i).
 * 2. Multiply each question weight by the student's rating (r_i, scale 1 to 4):
 *      Earned Weighted Score = Sum(w_i * r_i)
 * 3. Calculate the maximum possible weighted score for this career (assuming rating 4 for all):
 *      Max Possible Score = Sum(w_i * 4.0)
 * 4. Compute the normalized Compatibility Percentage:
 *      Compatibility Score = round((Earned Weighted Score / Max Possible Score) * 100)
 * 5. Sort all careers in descending order of compatibility score.
 *    Tie-breaker: Alphabetical order of career name (guarantees deterministic, reproducible output).
 * 6. Return the top 5 careers and attach an explainable match reason highlighting
 *    the student's highest-matching interests.
 */
public class RecommendationEngine {

    private final CareerDAO careerDAO;
    private final QuestionDAO questionDAO;

    public RecommendationEngine() {
        this.careerDAO = new CareerDAO();
        this.questionDAO = new QuestionDAO();
    }

    public RecommendationEngine(CareerDAO careerDAO, QuestionDAO questionDAO) {
        this.careerDAO = careerDAO;
        this.questionDAO = questionDAO;
    }

    /**
     * Internal container for scoring a career before final ranking.
     */
    public static class ScoredCareer {
        private final Career career;
        private final int compatibilityScore;
        private final String matchReason;

        public ScoredCareer(Career career, int compatibilityScore, String matchReason) {
            this.career = career;
            this.compatibilityScore = compatibilityScore;
            this.matchReason = matchReason;
        }

        public Career getCareer() {
            return career;
        }

        public int getCompatibilityScore() {
            return compatibilityScore;
        }

        public String getMatchReason() {
            return matchReason;
        }
    }

    /**
     * Result bundle containing top 5 career recommendations and a personalized summary.
     */
    public static class AssessmentResult {
        private final List<CareerRecommendation> recommendations;
        private final String personalizedSummary;

        public AssessmentResult(List<CareerRecommendation> recommendations, String personalizedSummary) {
            this.recommendations = recommendations;
            this.personalizedSummary = personalizedSummary;
        }

        public List<CareerRecommendation> getRecommendations() {
            return recommendations;
        }

        public String getPersonalizedSummary() {
            return personalizedSummary;
        }
    }

    /**
     * Evaluates a student's responses to all questions and generates the Top 5 recommendations.
     * 
     * @param studentRatings Map of questionId -> ratingValue (1 to 4)
     * @return AssessmentResult containing top 5 recommendations and summary
     */
    public AssessmentResult evaluate(Map<Integer, Integer> studentRatings) {
        if (studentRatings == null || studentRatings.isEmpty()) {
            throw new IllegalArgumentException("Student ratings cannot be null or empty.");
        }

        // Fetch all active careers and their mappings from database
        List<Career> careers = careerDAO.getActiveCareers();
        Map<Integer, List<CareerQuestionMapping>> allMappings = careerDAO.getAllMappingsGroupedByCareer();

        // Load active questions for category and text lookups
        List<Question> activeQuestions = questionDAO.getActiveQuestions();
        Map<Integer, Question> questionMap = new HashMap<>();
        for (Question q : activeQuestions) {
            questionMap.put(q.getId(), q);
        }

        List<ScoredCareer> scoredList = new ArrayList<>();

        for (Career career : careers) {
            List<CareerQuestionMapping> mappings = allMappings.get(career.getId());
            if (mappings == null || mappings.isEmpty()) {
                // If a career has no mappings, default score is 0
                scoredList.add(new ScoredCareer(career, 0, "No assessment criteria configured for this career."));
                continue;
            }

            double earnedWeightedScore = 0.0;
            double maxPossibleScore = 0.0;
            List<String> matchingPillars = new ArrayList<>();

            for (CareerQuestionMapping mapping : mappings) {
                int qId = mapping.getQuestionId();
                double weight = mapping.getWeight();
                int studentRating = studentRatings.getOrDefault(qId, 1);

                earnedWeightedScore += (weight * studentRating);
                maxPossibleScore += (weight * 4.0);

                // Collect strong matching interests (rating 3 or 4) for explanation
                if (studentRating >= 3) {
                    Question q = questionMap.get(qId);
                    String topic = (q != null) ? q.getCategory() : ("Question " + qId);
                    String ratingLabel = (studentRating == 4) ? "Very interested" : "Moderately interested";
                    matchingPillars.add(topic + " (" + ratingLabel + ")");
                }
            }

            int finalScore = 0;
            if (maxPossibleScore > 0) {
                double ratio = earnedWeightedScore / maxPossibleScore;
                finalScore = (int) Math.round(ratio * 100.0);
                if (finalScore > 100) finalScore = 100;
                if (finalScore < 0) finalScore = 0;
            }

            // Build explainable match reason
            String matchReason;
            if (!matchingPillars.isEmpty()) {
                matchReason = "Strong alignment with your interest in " + String.join(", ", matchingPillars) + ".";
            } else {
                matchReason = "Compatibility based on overall foundational engineering interests.";
            }

            scoredList.add(new ScoredCareer(career, finalScore, matchReason));
        }

        // Deterministic sort: Primary = compatibilityScore DESC, Secondary = career.name ASC
        Collections.sort(scoredList, new Comparator<ScoredCareer>() {
            @Override
            public int compare(ScoredCareer o1, ScoredCareer o2) {
                int scoreComp = Integer.compare(o2.getCompatibilityScore(), o1.getCompatibilityScore());
                if (scoreComp != 0) {
                    return scoreComp;
                }
                return o1.getCareer().getName().compareToIgnoreCase(o2.getCareer().getName());
            }
        });

        // Select top 5
        int count = Math.min(5, scoredList.size());
        List<CareerRecommendation> recommendations = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ScoredCareer sc = scoredList.get(i);
            CareerRecommendation rec = new CareerRecommendation();
            rec.setCareerId(sc.getCareer().getId());
            rec.setCareer(sc.getCareer());
            rec.setRankOrder(i + 1);
            rec.setCompatibilityScore(sc.getCompatibilityScore());
            rec.setMatchReason(sc.getMatchReason());
            recommendations.add(rec);
        }

        // Generate personalized overall summary
        String personalizedSummary = generatePersonalizedSummary(studentRatings, questionMap, recommendations);

        return new AssessmentResult(recommendations, personalizedSummary);
    }

    private String generatePersonalizedSummary(Map<Integer, Integer> studentRatings,
                                              Map<Integer, Question> questionMap,
                                              List<CareerRecommendation> top5) {
        Set<String> highInterests = new LinkedHashSet<>();
        for (Map.Entry<Integer, Integer> entry : studentRatings.entrySet()) {
            if (entry.getValue() >= 4) {
                Question q = questionMap.get(entry.getKey());
                if (q != null) {
                    highInterests.add(q.getCategory());
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        if (!highInterests.isEmpty()) {
            sb.append("Your assessment demonstrates distinctive strengths and enthusiasm in ");
            int idx = 0;
            int total = Math.min(3, highInterests.size());
            for (String interest : highInterests) {
                if (idx > 0 && idx == total - 1) {
                    sb.append(" and ");
                } else if (idx > 0) {
                    sb.append(", ");
                }
                sb.append(interest);
                idx++;
                if (idx >= 3) break;
            }
            sb.append(". ");
        } else {
            sb.append("Your responses indicate a balanced foundational interest across various areas of Computer Science. ");
        }

        if (!top5.isEmpty()) {
            sb.append("Your top recommendation is **")
              .append(top5.get(0).getCareer().getName())
              .append("** with a compatibility score of ")
              .append(top5.get(0).getCompatibilityScore())
              .append("%, followed closely by other promising paths in related technical domains.");
        }

        return sb.toString();
    }
}
