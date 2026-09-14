package com.aiexam.dao;

import com.aiexam.model.Result;
import com.aiexam.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ResultDAO {

    public long insert(Result r) throws SQLException {
        String sql = "INSERT INTO results (attempt_id, exam_id, student_id, total_marks, obtained_marks, " +
                     "correct_answers, wrong_answers, unanswered, percentage, pass_status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, r.getAttemptId());
            ps.setLong(2, r.getExamId());
            ps.setLong(3, r.getStudentId());
            ps.setInt(4, r.getTotalMarks());
            ps.setInt(5, r.getObtainedMarks());
            ps.setInt(6, r.getCorrectAnswers());
            ps.setInt(7, r.getWrongAnswers());
            ps.setInt(8, r.getUnanswered());
            ps.setBigDecimal(9, r.getPercentage());
            ps.setString(10, r.getPassStatus());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) return keys.getLong(1);
                }
            }
        }
        return -1L;
    }

    public Result findByAttemptId(long attemptId) throws SQLException {
        String sql = "SELECT r.*, e.title AS exam_title, e.passing_marks, u.full_name AS student_name, " +
                     "s.roll_number AS student_roll_number, ra.review_status " +
                     "FROM results r " +
                     "JOIN exams e ON r.exam_id = e.exam_id " +
                     "JOIN students s ON r.student_id = s.student_id " +
                     "JOIN users u ON s.user_id = u.user_id " +
                     "LEFT JOIN risk_analysis ra ON ra.attempt_id = r.attempt_id " +
                     "WHERE r.attempt_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, attemptId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<Result> findByStudentId(long studentId) throws SQLException {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT r.*, e.title AS exam_title, e.passing_marks, u.full_name AS student_name, " +
                     "s.roll_number AS student_roll_number, ra.review_status " +
                     "FROM results r " +
                     "JOIN exams e ON r.exam_id = e.exam_id " +
                     "JOIN students s ON r.student_id = s.student_id " +
                     "JOIN users u ON s.user_id = u.user_id " +
                     "LEFT JOIN risk_analysis ra ON ra.attempt_id = r.attempt_id " +
                     "WHERE r.student_id = ? ORDER BY r.generated_at DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Result> findAll() throws SQLException {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT r.*, e.title AS exam_title, e.passing_marks, u.full_name AS student_name, " +
                     "s.roll_number AS student_roll_number, ra.review_status " +
                     "FROM results r " +
                     "JOIN exams e ON r.exam_id = e.exam_id " +
                     "JOIN students s ON r.student_id = s.student_id " +
                     "JOIN users u ON s.user_id = u.user_id " +
                     "LEFT JOIN risk_analysis ra ON ra.attempt_id = r.attempt_id " +
                     "ORDER BY r.generated_at DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Result> search(String keyword) throws SQLException {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT r.*, e.title AS exam_title, e.passing_marks, u.full_name AS student_name, " +
                     "s.roll_number AS student_roll_number, ra.review_status " +
                     "FROM results r " +
                     "JOIN exams e ON r.exam_id = e.exam_id " +
                     "JOIN students s ON r.student_id = s.student_id " +
                     "JOIN users u ON s.user_id = u.user_id " +
                     "LEFT JOIN risk_analysis ra ON ra.attempt_id = r.attempt_id " +
                     "WHERE u.full_name LIKE ? OR s.roll_number LIKE ? OR e.title LIKE ? " +
                     "ORDER BY r.generated_at DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM results";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("total");
        }
        return 0;
    }

    /** Average percentage across all results - used by the analytics dashboard. */
    public double averagePercentage() throws SQLException {
        String sql = "SELECT AVG(percentage) AS avg_pct FROM results";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble("avg_pct");
        }
        return 0;
    }

    public int countByPassStatus(String status) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM results WHERE pass_status = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
            }
        }
        return 0;
    }

    private Result mapRow(ResultSet rs) throws SQLException {
        Result r = new Result();
        r.setResultId(rs.getLong("result_id"));
        r.setAttemptId(rs.getLong("attempt_id"));
        r.setExamId(rs.getLong("exam_id"));
        r.setStudentId(rs.getLong("student_id"));
        r.setTotalMarks(rs.getInt("total_marks"));
        r.setObtainedMarks(rs.getInt("obtained_marks"));
        r.setCorrectAnswers(rs.getInt("correct_answers"));
        r.setWrongAnswers(rs.getInt("wrong_answers"));
        r.setUnanswered(rs.getInt("unanswered"));
        r.setPercentage(rs.getBigDecimal("percentage"));
        r.setPassStatus(rs.getString("pass_status"));
        r.setGeneratedAt(rs.getTimestamp("generated_at"));
        r.setExamTitle(rs.getString("exam_title"));
        r.setStudentName(rs.getString("student_name"));
        r.setStudentRollNumber(rs.getString("student_roll_number"));
        r.setPassingMarks(rs.getInt("passing_marks"));
        r.setReviewStatus(rs.getString("review_status"));
        return r;
    }
}
