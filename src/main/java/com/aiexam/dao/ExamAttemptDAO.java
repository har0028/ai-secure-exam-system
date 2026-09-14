package com.aiexam.dao;

import com.aiexam.model.ExamAttempt;
import com.aiexam.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ExamAttemptDAO {

    public long insert(long examId, long studentId, String ipAddress, String browserInfo) throws SQLException {
        String sql = "INSERT INTO exam_attempts (exam_id, student_id, start_time, status, ip_address, browser_info) " +
                     "VALUES (?, ?, NOW(), 'IN_PROGRESS', ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, examId);
            ps.setLong(2, studentId);
            ps.setString(3, ipAddress);
            ps.setString(4, browserInfo);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getLong(1);
                    }
                }
            }
        }
        return -1L;
    }

    public ExamAttempt findById(long attemptId) throws SQLException {
        String sql = "SELECT a.*, e.title AS exam_title, e.duration_minutes, " +
                     "u.full_name AS student_name, s.roll_number AS student_roll_number " +
                     "FROM exam_attempts a " +
                     "JOIN exams e ON a.exam_id = e.exam_id " +
                     "JOIN students s ON a.student_id = s.student_id " +
                     "JOIN users u ON s.user_id = u.user_id " +
                     "WHERE a.attempt_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, attemptId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * Finds an existing attempt for this exam+student pair, regardless of
     * status - used to detect "already started" or "already submitted"
     * before allowing a new attempt (the exam_id+student_id unique
     * constraint means at most one attempt per student per exam).
     */
    public ExamAttempt findByExamAndStudent(long examId, long studentId) throws SQLException {
        String sql = "SELECT a.*, e.title AS exam_title, e.duration_minutes, " +
                     "u.full_name AS student_name, s.roll_number AS student_roll_number " +
                     "FROM exam_attempts a " +
                     "JOIN exams e ON a.exam_id = e.exam_id " +
                     "JOIN students s ON a.student_id = s.student_id " +
                     "JOIN users u ON s.user_id = u.user_id " +
                     "WHERE a.exam_id = ? AND a.student_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, examId);
            ps.setLong(2, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public boolean updateStatus(long attemptId, String status) throws SQLException {
        String sql = "UPDATE exam_attempts SET status = ?, end_time = CASE WHEN ? IN ('SUBMITTED','AUTO_SUBMITTED') THEN NOW() ELSE end_time END WHERE attempt_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, status);
            ps.setLong(3, attemptId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateRiskScore(long attemptId, int riskScore, int violationCount) throws SQLException {
        String sql = "UPDATE exam_attempts SET current_risk_score = ?, violation_count = ? WHERE attempt_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, riskScore);
            ps.setInt(2, violationCount);
            ps.setLong(3, attemptId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * All attempts currently IN_PROGRESS, with exam/student details joined -
     * powers the admin live monitoring dashboard.
     */
    public List<ExamAttempt> findActiveAttempts() throws SQLException {
        List<ExamAttempt> list = new ArrayList<>();
        String sql = "SELECT a.*, e.title AS exam_title, e.duration_minutes, " +
                     "u.full_name AS student_name, s.roll_number AS student_roll_number " +
                     "FROM exam_attempts a " +
                     "JOIN exams e ON a.exam_id = e.exam_id " +
                     "JOIN students s ON a.student_id = s.student_id " +
                     "JOIN users u ON s.user_id = u.user_id " +
                     "WHERE a.status = 'IN_PROGRESS' " +
                     "ORDER BY a.current_risk_score DESC, a.start_time ASC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<ExamAttempt> findByStudentId(long studentId) throws SQLException {
        List<ExamAttempt> list = new ArrayList<>();
        String sql = "SELECT a.*, e.title AS exam_title, e.duration_minutes, " +
                     "u.full_name AS student_name, s.roll_number AS student_roll_number " +
                     "FROM exam_attempts a " +
                     "JOIN exams e ON a.exam_id = e.exam_id " +
                     "JOIN students s ON a.student_id = s.student_id " +
                     "JOIN users u ON s.user_id = u.user_id " +
                     "WHERE a.student_id = ? ORDER BY a.created_at DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public int countActive() throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM exam_attempts WHERE status = 'IN_PROGRESS'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("total");
        }
        return 0;
    }

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM exam_attempts";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("total");
        }
        return 0;
    }

    private ExamAttempt mapRow(ResultSet rs) throws SQLException {
        ExamAttempt a = new ExamAttempt();
        a.setAttemptId(rs.getLong("attempt_id"));
        a.setExamId(rs.getLong("exam_id"));
        a.setStudentId(rs.getLong("student_id"));
        a.setStartTime(rs.getTimestamp("start_time"));
        a.setEndTime(rs.getTimestamp("end_time"));
        a.setStatus(rs.getString("status"));
        a.setCurrentRiskScore(rs.getInt("current_risk_score"));
        a.setViolationCount(rs.getInt("violation_count"));
        a.setIpAddress(rs.getString("ip_address"));
        a.setBrowserInfo(rs.getString("browser_info"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        a.setExamTitle(rs.getString("exam_title"));
        a.setDurationMinutes(rs.getInt("duration_minutes"));
        a.setStudentName(rs.getString("student_name"));
        a.setStudentRollNumber(rs.getString("student_roll_number"));
        return a;
    }
}
