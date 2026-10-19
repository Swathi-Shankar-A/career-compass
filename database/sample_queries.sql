-- =====================================================================
-- Sample Queries for Testing and Viva Demonstrations
-- Career Recommendation System for B.Tech CSE Students
-- =====================================================================

USE career_recommendation_system;

-- 1. Check all registered students
SELECT id, full_name, email, college_name, degree, current_year, created_at 
FROM students 
ORDER BY id DESC;

-- 2. Verify exactly 20 active questions exist
SELECT COUNT(*) AS active_question_count 
FROM assessment_questions 
WHERE is_active = TRUE;

-- 3. View all assessment questions with their category and order
SELECT question_order, category, question_text, is_active 
FROM assessment_questions 
ORDER BY question_order ASC;

-- 4. View question options and ratings for question 1
SELECT q.question_order, q.question_text, o.option_text, o.rating_value 
FROM assessment_questions q
JOIN question_options o ON q.id = o.question_id
WHERE q.id = 1
ORDER BY o.rating_value DESC;

-- 5. Check all 35 seeded careers
SELECT id, name, short_description 
FROM careers 
ORDER BY id ASC;

-- 6. Total count of careers
SELECT COUNT(*) AS total_careers FROM careers WHERE is_active = TRUE;

-- 7. View career-to-question mappings for a specific career (e.g. Frontend Developer)
SELECT c.name AS career_name, q.question_order, q.question_text, q.category, m.weight
FROM career_question_mappings m
JOIN careers c ON m.career_id = c.id
JOIN assessment_questions q ON m.question_id = q.id
WHERE c.name = 'Frontend Developer'
ORDER BY m.weight DESC;

-- 8. View all submissions with student details
SELECT s.id AS submission_id, st.full_name, st.email, st.college_name, s.submission_date
FROM assessment_submissions s
JOIN students st ON s.student_id = st.id
ORDER BY s.submission_date DESC;

-- 9. View student responses for a specific submission (e.g. submission_id = 1)
SELECT r.question_id, r.question_text_snapshot, r.selected_option_snapshot, r.rating_value
FROM student_responses r
WHERE r.submission_id = 1
ORDER BY r.question_id ASC;

-- 10. View the Top 5 Career Recommendations for a specific submission
SELECT rec.rank_order, c.name AS recommended_career, rec.compatibility_score, rec.match_reason
FROM career_recommendations rec
JOIN careers c ON rec.career_id = c.id
WHERE rec.submission_id = 1
ORDER BY rec.rank_order ASC;

-- 11. Count assessment submissions per student
SELECT st.id, st.full_name, st.email, COUNT(s.id) AS total_assessments
FROM students st
LEFT JOIN assessment_submissions s ON st.id = s.student_id
GROUP BY st.id, st.full_name, st.email
ORDER BY total_assessments DESC;

-- 12. Most frequently recommended top-1 careers (Aggregate Insights)
SELECT c.name AS career_name, COUNT(*) AS top_recommendation_count
FROM career_recommendations rec
JOIN careers c ON rec.career_id = c.id
WHERE rec.rank_order = 1
GROUP BY c.name
ORDER BY top_recommendation_count DESC;

-- 13. System summary counts (Admin Dashboard Query)
SELECT 
    (SELECT COUNT(*) FROM students) AS total_students,
    (SELECT COUNT(*) FROM assessment_submissions) AS total_submissions,
    (SELECT COUNT(*) FROM assessment_questions WHERE is_active = TRUE) AS active_questions,
    (SELECT COUNT(*) FROM careers WHERE is_active = TRUE) AS active_careers;
