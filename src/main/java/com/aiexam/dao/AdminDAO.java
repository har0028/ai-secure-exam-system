package com.aiexam.dao;

import com.aiexam.model.Admin;
import com.aiexam.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {

    public void insert(long userId, String designation) throws SQLException {
        String sql = "INSERT INTO admins (user_id, designation) VALUES (?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, designation);
            ps.executeUpdate();
        }
    }

    public Admin findByUserId(long userId) throws SQLException {
        String sql = "SELECT a.*, u.full_name, u.email " +
                     "FROM admins a JOIN users u ON a.user_id = u.user_id " +
                     "WHERE a.user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    private Admin mapRow(ResultSet rs) throws SQLException {
        Admin a = new Admin();
        a.setAdminId(rs.getLong("admin_id"));
        a.setUserId(rs.getLong("user_id"));
        a.setDesignation(rs.getString("designation"));
        a.setDepartment(rs.getString("department"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        a.setFullName(rs.getString("full_name"));
        a.setEmail(rs.getString("email"));
        return a;
    }
}
