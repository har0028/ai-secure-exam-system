package com.aiexam.dao;

import com.aiexam.model.ActivityLog;
import com.aiexam.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Writes to the activity_logs audit trail. Called from AuthService and
 * controllers on every security-relevant action (login, logout, register,
 * password reset, exam start/submit, admin CRUD actions, etc.)
 */
public class ActivityLogDAO {

    public void log(Long userId, String action, String description, String ipAddress) {
        String sql = "INSERT INTO activity_logs (user_id, action, description, ip_address) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (userId != null) {
                ps.setLong(1, userId);
            } else {
                ps.setNull(1, java.sql.Types.BIGINT);
            }
            ps.setString(2, action);
            ps.setString(3, description);
            ps.setString(4, ipAddress);
            ps.executeUpdate();
        } catch (SQLException e) {
            // Audit logging must never break the primary user flow;
            // log to server console instead of propagating.
            System.err.println("ActivityLogDAO failed to write log: " + e.getMessage());
        }
    }

    /**
     * Returns the most recent activity entries, newest first, joined with
     * the acting user's name for display on the admin dashboard feed.
     */
    public List<ActivityLog> findRecent(int limit) throws SQLException {
        List<ActivityLog> list = new ArrayList<>();
        String sql = "SELECT al.*, u.full_name AS user_full_name " +
                     "FROM activity_logs al LEFT JOIN users u ON al.user_id = u.user_id " +
                     "ORDER BY al.created_at DESC LIMIT ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ActivityLog log = new ActivityLog();
                    log.setActivityId(rs.getLong("activity_id"));
                    long uid = rs.getLong("user_id");
                    log.setUserId(rs.wasNull() ? null : uid);
                    log.setAction(rs.getString("action"));
                    log.setDescription(rs.getString("description"));
                    log.setIpAddress(rs.getString("ip_address"));
                    log.setCreatedAt(rs.getTimestamp("created_at"));
                    log.setUserFullName(rs.getString("user_full_name"));
                    list.add(log);
                }
            }
        }
        return list;
    }
}

