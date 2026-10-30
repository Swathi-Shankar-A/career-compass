package com.career.util;

import com.career.model.Admin;
import com.career.model.AssessmentSubmission;
import com.career.model.Career;
import com.career.model.CareerQuestionMapping;
import com.career.model.CareerRecommendation;
import com.career.model.Question;
import com.career.model.QuestionOption;
import com.career.model.Student;
import com.career.model.StudentResponse;

import java.util.Collection;
import java.util.Map;

/**
 * Lightweight, zero-dependency JSON serialization utility tailored for the
 * Career Recommendation System models and API responses.
 */
public final class JsonUtil {

    private JsonUtil() {}

    /**
     * Escapes special characters for a JSON string literal.
     */
    public static String escape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 32 || (c >= 0x7F && c <= 0x9F)) {
                        String hex = Integer.toHexString(c);
                        sb.append("\\u");
                        for (int k = 0; k < 4 - hex.length(); k++) sb.append('0');
                        sb.append(hex);
                    } else {
                        sb.append(c);
                    }
                    break;
            }
        }
        return sb.toString();
    }

    public static String toJson(Student s) {
        if (s == null) return "null";
        return "{" +
                "\"id\":" + s.getId() + "," +
                "\"fullName\":\"" + escape(s.getFullName()) + "\"," +
                "\"email\":\"" + escape(s.getEmail()) + "\"," +
                "\"collegeName\":\"" + escape(s.getCollegeName()) + "\"," +
                "\"degree\":\"" + escape(s.getDegree()) + "\"," +
                "\"currentYear\":" + s.getCurrentYear() + "," +
                "\"createdAt\":\"" + (s.getCreatedAt() != null ? s.getCreatedAt().toString() : "") + "\"" +
                "}";
    }

    public static String toJson(Admin a) {
        if (a == null) return "null";
        return "{" +
                "\"id\":" + a.getId() + "," +
                "\"username\":\"" + escape(a.getUsername()) + "\"," +
                "\"email\":\"" + escape(a.getEmail()) + "\"," +
                "\"fullName\":\"" + escape(a.getFullName()) + "\"" +
                "}";
    }

    public static String toJson(QuestionOption opt) {
        if (opt == null) return "null";
        return "{" +
                "\"id\":" + opt.getId() + "," +
                "\"questionId\":" + opt.getQuestionId() + "," +
                "\"optionText\":\"" + escape(opt.getOptionText()) + "\"," +
                "\"ratingValue\":" + opt.getRatingValue() +
                "}";
    }

    public static String toJson(Question q) {
        if (q == null) return "null";
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"id\":").append(q.getId()).append(",");
        sb.append("\"questionOrder\":").append(q.getQuestionOrder()).append(",");
        sb.append("\"questionText\":\"").append(escape(q.getQuestionText())).append("\",");
        sb.append("\"category\":\"").append(escape(q.getCategory())).append("\",");
        sb.append("\"active\":").append(q.isActive()).append(",");
        sb.append("\"options\":[");
        if (q.getOptions() != null) {
            for (int i = 0; i < q.getOptions().size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(toJson(q.getOptions().get(i)));
            }
        }
        sb.append("]}");
        return sb.toString();
    }

    public static String toJson(CareerQuestionMapping m) {
        if (m == null) return "null";
        return "{" +
                "\"id\":" + m.getId() + "," +
                "\"careerId\":" + m.getCareerId() + "," +
                "\"questionId\":" + m.getQuestionId() + "," +
                "\"weight\":" + m.getWeight() + "," +
                "\"questionText\":\"" + escape(m.getQuestionText()) + "\"," +
                "\"category\":\"" + escape(m.getCategory()) + "\"" +
                "}";
    }

    public static String toJson(Career c) {
        if (c == null) return "null";
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"id\":").append(c.getId()).append(",");
        sb.append("\"name\":\"").append(escape(c.getName())).append("\",");
        sb.append("\"shortDescription\":\"").append(escape(c.getShortDescription())).append("\",");
        sb.append("\"responsibilities\":\"").append(escape(c.getResponsibilities())).append("\",");
        sb.append("\"skillsRequired\":\"").append(escape(c.getSkillsRequired())).append("\",");
        sb.append("\"toolsTechnologies\":\"").append(escape(c.getToolsTechnologies())).append("\",");
        sb.append("\"learningSuggestions\":\"").append(escape(c.getLearningSuggestions())).append("\",");
        sb.append("\"active\":").append(c.isActive()).append(",");
        sb.append("\"mappings\":[");
        if (c.getMappings() != null) {
            for (int i = 0; i < c.getMappings().size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(toJson(c.getMappings().get(i)));
            }
        }
        sb.append("]}");
        return sb.toString();
    }

    public static String toJson(StudentResponse r) {
        if (r == null) return "null";
        return "{" +
                "\"id\":" + r.getId() + "," +
                "\"submissionId\":" + r.getSubmissionId() + "," +
                "\"questionId\":" + r.getQuestionId() + "," +
                "\"questionTextSnapshot\":\"" + escape(r.getQuestionTextSnapshot()) + "\"," +
                "\"selectedOptionSnapshot\":\"" + escape(r.getSelectedOptionSnapshot()) + "\"," +
                "\"ratingValue\":" + r.getRatingValue() + "," +
                "\"category\":\"" + escape(r.getCategory()) + "\"" +
                "}";
    }

    public static String toJson(CareerRecommendation rec) {
        if (rec == null) return "null";
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"id\":").append(rec.getId()).append(",");
        sb.append("\"submissionId\":").append(rec.getSubmissionId()).append(",");
        sb.append("\"careerId\":").append(rec.getCareerId()).append(",");
        sb.append("\"rankOrder\":").append(rec.getRankOrder()).append(",");
        sb.append("\"compatibilityScore\":").append(rec.getCompatibilityScore()).append(",");
        sb.append("\"matchReason\":\"").append(escape(rec.getMatchReason())).append("\",");
        sb.append("\"career\":").append(toJson(rec.getCareer()));
        sb.append("}");
        return sb.toString();
    }

    public static String toJson(AssessmentSubmission sub) {
        if (sub == null) return "null";
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"id\":").append(sub.getId()).append(",");
        sb.append("\"studentId\":").append(sub.getStudentId()).append(",");
        sb.append("\"submissionDate\":\"").append(sub.getSubmissionDate() != null ? sub.getSubmissionDate().toString() : "").append("\",");
        sb.append("\"summaryNotes\":\"").append(escape(sub.getSummaryNotes())).append("\",");
        sb.append("\"studentName\":\"").append(escape(sub.getStudentName())).append("\",");
        sb.append("\"studentEmail\":\"").append(escape(sub.getStudentEmail())).append("\",");
        sb.append("\"studentCollege\":\"").append(escape(sub.getStudentCollege())).append("\",");
        sb.append("\"studentDegree\":\"").append(escape(sub.getStudentDegree())).append("\",");
        sb.append("\"studentYear\":").append(sub.getStudentYear()).append(",");
        sb.append("\"responses\":[");
        if (sub.getResponses() != null) {
            for (int i = 0; i < sub.getResponses().size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(toJson(sub.getResponses().get(i)));
            }
        }
        sb.append("],\"recommendations\":[");
        if (sub.getRecommendations() != null) {
            for (int i = 0; i < sub.getRecommendations().size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(toJson(sub.getRecommendations().get(i)));
            }
        }
        sb.append("]}");
        return sb.toString();
    }

    public static String questionsToJson(Collection<Question> questions) {
        if (questions == null) return "[]";
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Question q : questions) {
            if (!first) sb.append(",");
            sb.append(toJson(q));
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }

    public static String careersToJson(Collection<Career> careers) {
        if (careers == null) return "[]";
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Career c : careers) {
            if (!first) sb.append(",");
            sb.append(toJson(c));
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }

    public static String submissionsToJson(Collection<AssessmentSubmission> subs) {
        if (subs == null) return "[]";
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (AssessmentSubmission s : subs) {
            if (!first) sb.append(",");
            sb.append(toJson(s));
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }

    public static String studentsToJson(Collection<Student> students) {
        if (students == null) return "[]";
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Student s : students) {
            if (!first) sb.append(",");
            sb.append(toJson(s));
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }

    public static String mapIntIntToJson(Map<Integer, Integer> map) {
        if (map == null) return "{}";
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":").append(entry.getValue());
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    public static String response(boolean success, String message) {
        return "{\"success\":" + success + ",\"message\":\"" + escape(message) + "\"}";
    }

    public static String response(boolean success, String message, String redirectUrl) {
        return "{\"success\":" + success + ",\"message\":\"" + escape(message) + "\",\"redirect\":\"" + escape(redirectUrl) + "\"}";
    }
}
