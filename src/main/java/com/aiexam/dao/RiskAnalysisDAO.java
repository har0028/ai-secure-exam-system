package com.aiexam.dao;

import com.aiexam.model.RiskAnalysis;
import com.aiexam.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RiskAnalysisDAO {

    /**
     * Ensures a risk_analysis row exists for this attempt (created lazily
     * on first violation event) and returns it.
     */
    public RiskAnalysis findOrCreate(long attemptId) throws SQLException {
        RiskAnalysis existing = findByAttemptId(attemptId);
        if (existing != null) {
            return existing;
        }
        String sql = "INSERT INTO risk_analysis (attempt_id) VALUES (?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, attemptId);
            ps.executeUpdate();
        }
        return findByAttemptId(attemptId);
    }

    public RiskAnalysis findByAttemptId(long attemptId) throws SQLException {
        String sql = "SELECT * FROM risk_analysis WHERE attempt_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, attemptId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    /**
     * Increments the appropriate per-event-type counter, recomputes the
     * final risk score and level, and persists it - called every time a
     * proctoring event is logged.
     */
    public void recordEventAndRecompute(long attemptId, String eventType, int scoreImpact) throws SQLException {
        findOrCreate(attemptId);

        String counterColumn = mapEventTypeToColumn(eventType);
        String incrementSql = counterColumn != null
                ? "UPDATE risk_analysis SET " + counterColumn + " = " + counterColumn + " + 1, " +
                  "final_risk_score = LEAST(100, final_risk_score + ?) WHERE attempt_id = ?"
                : "UPDATE risk_analysis SET final_risk_score = LEAST(100, final_risk_score + ?) WHERE attempt_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(incrementSql)) {
            ps.setInt(1, scoreImpact);
            ps.setLong(2, attemptId);
            ps.executeUpdate();
        }

        updateRiskLevel(attemptId);
    }

    private void updateRiskLevel(long attemptId) throws SQLException {
        String sql = "UPDATE risk_analysis SET risk_level = CASE " +
                     "WHEN final_risk_score <= 25 THEN 'LOW' " +
                     "WHEN final_risk_score <= 50 THEN 'MEDIUM' " +
                     "WHEN final_risk_score <= 75 THEN 'HIGH' " +
                     "ELSE 'CRITICAL' END " +
                     "WHERE attempt_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, attemptId);
            ps.executeUpdate();
        }
    }

    public void markUnderReview(long attemptId) throws SQLException {
        String sql = "UPDATE risk_analysis SET review_status = 'UNDER_REVIEW' WHERE attempt_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, attemptId);
            ps.executeUpdate();
        }
    }

    public void markReviewed(long attemptId) throws SQLException {
        String sql = "UPDATE risk_analysis SET review_status = 'REVIEWED' WHERE attempt_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, attemptId);
            ps.executeUpdate();
        }
    }

    private String mapEventTypeToColumn(String eventType) {
        switch (eventType) {
            case "FACE_MISSING": return "face_missing_count";
            case "MULTIPLE_FACES": return "multiple_face_count";
            case "TAB_SWITCH":
            case "WINDOW_BLUR": return "tab_switch_count";
            case "FULLSCREEN_EXIT": return "fullscreen_exit_count";
            case "PHONE_DETECTED": return "phone_detect_count";
            default: return null;
        }
    }

    private RiskAnalysis mapRow(ResultSet rs) throws SQLException {
        RiskAnalysis r = new RiskAnalysis();
        r.setRiskId(rs.getLong("risk_id"));
        r.setAttemptId(rs.getLong("attempt_id"));
        r.setFaceMissingCount(rs.getInt("face_missing_count"));
        r.setMultipleFaceCount(rs.getInt("multiple_face_count"));
        r.setTabSwitchCount(rs.getInt("tab_switch_count"));
        r.setFullscreenExitCount(rs.getInt("fullscreen_exit_count"));
        r.setPhoneDetectCount(rs.getInt("phone_detect_count"));
        r.setFinalRiskScore(rs.getInt("final_risk_score"));
        r.setRiskLevel(rs.getString("risk_level"));
        r.setReviewStatus(rs.getString("review_status"));
        r.setUpdatedAt(rs.getTimestamp("updated_at"));
        return r;
    }
}
