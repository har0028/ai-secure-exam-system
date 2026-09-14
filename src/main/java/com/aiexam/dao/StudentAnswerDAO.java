package com.aiexam.dao;

import com.aiexam.model.StudentAnswer;
import com.aiexam.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentAnswerDAO {

    /**
     * Saves or updates a single answer for an attempt+question pair. Relies
     * on the (attempt_id, question_id) UNIQUE constraint and MySQL's
     * INSERT ... ON DUPLICATE KEY UPDATE, which makes this safe to call
     * repeatedly from the autosave timer without a separate exists-check.
     */
    public void upsert(long attemptId, long questionId, String selectedOption, boolean markedForReview)
            throws SQLException {
        String sql = "INSERT INTO student_answers (attempt_id, question_id, selected_option, is_marked_for_review) " +
                     "VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE selected_option = VALUES(selected_option), " +
                     "is_marked_for_review = VALUES(is_marked_for_review), answered_at = CURRENT_TIMESTAMP";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, attemptId);
            ps.setLong(2, questionId);
            if (selectedOption != null && !selectedOption.isEmpty()) {
                ps.setString(3, selectedOption);
            } else {
                ps.setNull(3, java.sql.Types.CHAR);
            }
            ps.setBoolean(4, markedForReview);
            ps.executeUpdate();
        }
    }

    public List<StudentAnswer> findByAttemptId(long attemptId) throws SQLException {
        List<StudentAnswer> list = new ArrayList<>();
        String sql = "SELECT * FROM student_answers WHERE attempt_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, attemptId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    /**
     * Marks every answered row's is_correct flag by comparing against the
     * question's correct_option - called once at submission time during
     * automatic evaluation.
     */
    public void evaluateAttempt(long attemptId) throws SQLException {
        String sql = "UPDATE student_answers sa " +
                     "JOIN questions q ON sa.question_id = q.question_id " +
                     "SET sa.is_correct = (sa.selected_option = q.correct_option) " +
                     "WHERE sa.attempt_id = ? AND sa.selected_option IS NOT NULL";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, attemptId);
            ps.executeUpdate();
        }
    }

    public static class ScoreSummary {
        public final int correctAnswers;
        public final int wrongAnswers;
        public final int unanswered;
        public final int obtainedMarks;

        public ScoreSummary(int correctAnswers, int wrongAnswers, int unanswered, int obtainedMarks) {
            this.correctAnswers = correctAnswers;
            this.wrongAnswers = wrongAnswers;
            this.unanswered = unanswered;
            this.obtainedMarks = obtainedMarks;
        }
    }

    /**
     * Aggregates correct/wrong/unanswered counts and total marks obtained
     * for an attempt, joined against the exam's full question set so that
     * unanswered questions (no row in student_answers at all) are also
     * counted correctly. Must be called after evaluateAttempt().
     */
    public ScoreSummary computeScoreSummary(long attemptId, long examId) throws SQLException {
        String sql =
            "SELECT " +
            "  SUM(CASE WHEN sa.is_correct = TRUE THEN 1 ELSE 0 END) AS correct_count, " +
            "  SUM(CASE WHEN sa.selected_option IS NOT NULL AND sa.is_correct = FALSE THEN 1 ELSE 0 END) AS wrong_count, " +
            "  SUM(CASE WHEN sa.selected_option IS NULL THEN 1 ELSE 0 END) AS unanswered_count, " +
            "  SUM(CASE WHEN sa.is_correct = TRUE THEN q.marks ELSE 0 END) AS obtained_marks " +
            "FROM questions q " +
            "LEFT JOIN student_answers sa ON sa.question_id = q.question_id AND sa.attempt_id = ? " +
            "WHERE q.exam_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, attemptId);
            ps.setLong(2, examId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ScoreSummary(
                            rs.getInt("correct_count"),
                            rs.getInt("wrong_count"),
                            rs.getInt("unanswered_count"),
                            rs.getInt("obtained_marks"));
                }
            }
        }
        return new ScoreSummary(0, 0, 0, 0);
    }

    private StudentAnswer mapRow(ResultSet rs) throws SQLException {
        StudentAnswer a = new StudentAnswer();
        a.setAnswerId(rs.getLong("answer_id"));
        a.setAttemptId(rs.getLong("attempt_id"));
        a.setQuestionId(rs.getLong("question_id"));
        a.setSelectedOption(rs.getString("selected_option"));
        a.setMarkedForReview(rs.getBoolean("is_marked_for_review"));
        Object isCorrect = rs.getObject("is_correct");
        a.setIsCorrect(isCorrect != null ? rs.getBoolean("is_correct") : null);
        a.setAnsweredAt(rs.getTimestamp("answered_at"));
        return a;
    }
}
