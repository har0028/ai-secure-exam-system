package com.aiexam.dao;

import com.aiexam.model.ProctoringLog;
import com.aiexam.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProctoringLogDAO {

    public long insert(long attemptId, String eventType, String severity, int scoreImpact,
                        String screenshotPath, String details) throws SQLException {
        String sql = "INSERT INTO proctoring_logs (attempt_id, event_type, severity, score_impact, " +
                     "screenshot_path, details) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, attemptId);
            ps.setString(2, eventType);
            ps.setString(3, severity);
            ps.setInt(4, scoreImpact);
            ps.setString(5, screenshotPath);
            ps.setString(6, details);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) return keys.getLong(1);
                }
            }
        }
        return -1L;
    }

    public List<ProctoringLog> findByAttemptId(long attemptId) throws SQLException {
        List<ProctoringLog> list = new ArrayList<>();
        String sql = "SELECT * FROM proctoring_logs WHERE attempt_id = ? ORDER BY occurred_at DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, attemptId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public int countByAttemptAndType(long attemptId, String eventType) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM proctoring_logs WHERE attempt_id = ? AND event_type = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, attemptId);
            ps.setString(2, eventType);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
            }
        }
        return 0;
    }

    /**
     * Most recent violation events across all currently active attempts -
     * powers the admin live "Violation Feed" widget.
     */
    public List<ProctoringLog> findRecentForActiveAttempts(int limit) throws SQLException {
        List<ProctoringLog> list = new ArrayList<>();
        String sql = "SELECT pl.*, u.full_name AS student_name, e.title AS exam_title " +
                     "FROM proctoring_logs pl " +
                     "JOIN exam_attempts a ON pl.attempt_id = a.attempt_id " +
                     "JOIN students s ON a.student_id = s.student_id " +
                     "JOIN users u ON s.user_id = u.user_id " +
                     "JOIN exams e ON a.exam_id = e.exam_id " +
                     "WHERE a.status = 'IN_PROGRESS' " +
                     "ORDER BY pl.occurred_at DESC LIMIT ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRowWithJoins(rs));
            }
        }
        return list;
    }

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM proctoring_logs";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("total");
        }
        return 0;
    }

    /** Event-type distribution across all logs - feeds the analytics pie/bar chart. */
    public List<Object[]> countGroupedByEventType() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT event_type, COUNT(*) AS total FROM proctoring_logs GROUP BY event_type ORDER BY total DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{rs.getString("event_type"), rs.getInt("total")});
            }
        }
        return list;
    }

    private ProctoringLog mapRow(ResultSet rs) throws SQLException {
        ProctoringLog log = new ProctoringLog();
        log.setLogId(rs.getLong("log_id"));
        log.setAttemptId(rs.getLong("attempt_id"));
        log.setEventType(rs.getString("event_type"));
        log.setSeverity(rs.getString("severity"));
        log.setScoreImpact(rs.getInt("score_impact"));
        log.setScreenshotPath(rs.getString("screenshot_path"));
        log.setDetails(rs.getString("details"));
        log.setOccurredAt(rs.getTimestamp("occurred_at"));
        return log;
    }

    private ProctoringLog mapRowWithJoins(ResultSet rs) throws SQLException {
        ProctoringLog log = mapRow(rs);
        log.setStudentName(rs.getString("student_name"));
        log.setExamTitle(rs.getString("exam_title"));
        return log;
    }
}
