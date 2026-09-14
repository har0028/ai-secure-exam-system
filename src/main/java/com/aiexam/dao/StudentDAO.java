package com.aiexam.dao;

import com.aiexam.model.Student;
import com.aiexam.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public void insert(long userId, String rollNumber) throws SQLException {
        String sql = "INSERT INTO students (user_id, roll_number) VALUES (?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, rollNumber);
            ps.executeUpdate();
        }
    }

    public Student findByUserId(long userId) throws SQLException {
        String sql = "SELECT s.*, u.full_name, u.email, u.profile_image " +
                     "FROM students s JOIN users u ON s.user_id = u.user_id " +
                     "WHERE s.user_id = ?";
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

    public Student findById(long studentId) throws SQLException {
        String sql = "SELECT s.*, u.full_name, u.email, u.profile_image " +
                     "FROM students s JOIN users u ON s.user_id = u.user_id " +
                     "WHERE s.student_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public List<Student> findAll() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.*, u.full_name, u.email, u.profile_image " +
                     "FROM students s JOIN users u ON s.user_id = u.user_id " +
                     "ORDER BY s.created_at DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public boolean update(Student s) throws SQLException {
        String sql = "UPDATE students SET roll_number=?, course=?, department=?, phone=?, " +
                     "date_of_birth=?, address=? WHERE student_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getRollNumber());
            ps.setString(2, s.getCourse());
            ps.setString(3, s.getDepartment());
            ps.setString(4, s.getPhone());
            ps.setDate(5, s.getDateOfBirth());
            ps.setString(6, s.getAddress());
            ps.setLong(7, s.getStudentId());
            return ps.executeUpdate() > 0;
        }
    }

    public List<Student> search(String keyword) throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.*, u.full_name, u.email, u.profile_image " +
                     "FROM students s JOIN users u ON s.user_id = u.user_id " +
                     "WHERE u.full_name LIKE ? OR u.email LIKE ? OR s.roll_number LIKE ? " +
                     "ORDER BY s.created_at DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM students";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setStudentId(rs.getLong("student_id"));
        s.setUserId(rs.getLong("user_id"));
        s.setRollNumber(rs.getString("roll_number"));
        s.setCourse(rs.getString("course"));
        s.setDepartment(rs.getString("department"));
        s.setPhone(rs.getString("phone"));
        s.setDateOfBirth(rs.getDate("date_of_birth"));
        s.setAddress(rs.getString("address"));
        s.setRiskLevel(rs.getString("risk_level"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        s.setFullName(rs.getString("full_name"));
        s.setEmail(rs.getString("email"));
        s.setProfileImage(rs.getString("profile_image"));
        return s;
    }
}
