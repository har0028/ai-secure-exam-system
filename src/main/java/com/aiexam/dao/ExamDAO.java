package com.aiexam.dao;

import com.aiexam.model.Exam;
import com.aiexam.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ExamDAO {

    public long insert(Exam exam) throws SQLException {
        String sql = "INSERT INTO exams (title, description, category, duration_minutes, total_marks, " +
                     "passing_marks, scheduled_start, scheduled_end, shuffle_questions, shuffle_options, " +
                     "proctoring_enabled, status, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindExam(ps, exam);
            ps.setLong(13, exam.getCreatedBy());
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

    public boolean update(Exam exam) throws SQLException {
        String sql = "UPDATE exams SET title=?, description=?, category=?, duration_minutes=?, total_marks=?, " +
                     "passing_marks=?, scheduled_start=?, scheduled_end=?, shuffle_questions=?, shuffle_options=?, " +
                     "proctoring_enabled=?, status=? WHERE exam_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            bindExam(ps, exam);
            ps.setLong(13, exam.getExamId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(long examId) throws SQLException {
        String sql = "DELETE FROM exams WHERE exam_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, examId);
            return ps.executeUpdate() > 0;
        }
    }

    public Exam findById(long examId) throws SQLException {
        String sql = "SELECT e.*, " +
                     "(SELECT COUNT(*) FROM questions q WHERE q.exam_id = e.exam_id) AS question_count " +
                     "FROM exams e WHERE e.exam_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public List<Exam> findAll() throws SQLException {
        List<Exam> list = new ArrayList<>();
        String sql = "SELECT e.*, " +
                     "(SELECT COUNT(*) FROM questions q WHERE q.exam_id = e.exam_id) AS question_count " +
                     "FROM exams e ORDER BY e.created_at DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Exam> search(String keyword) throws SQLException {
        List<Exam> list = new ArrayList<>();
        String sql = "SELECT e.*, " +
                     "(SELECT COUNT(*) FROM questions q WHERE q.exam_id = e.exam_id) AS question_count " +
                     "FROM exams e WHERE e.title LIKE ? OR e.category LIKE ? ORDER BY e.created_at DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    /**
     * Exams visible to students: SCHEDULED or ACTIVE status only.
     */
    public List<Exam> findAvailableForStudents() throws SQLException {
        List<Exam> list = new ArrayList<>();
        String sql = "SELECT e.*, " +
                     "(SELECT COUNT(*) FROM questions q WHERE q.exam_id = e.exam_id) AS question_count " +
                     "FROM exams e WHERE e.status IN ('SCHEDULED','ACTIVE') ORDER BY e.scheduled_start ASC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM exams";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    private void bindExam(PreparedStatement ps, Exam exam) throws SQLException {
        ps.setString(1, exam.getTitle());
        ps.setString(2, exam.getDescription());
        ps.setString(3, exam.getCategory());
        ps.setInt(4, exam.getDurationMinutes());
        ps.setInt(5, exam.getTotalMarks());
        ps.setInt(6, exam.getPassingMarks());
        ps.setTimestamp(7, exam.getScheduledStart());
        ps.setTimestamp(8, exam.getScheduledEnd());
        ps.setBoolean(9, exam.isShuffleQuestions());
        ps.setBoolean(10, exam.isShuffleOptions());
        ps.setBoolean(11, exam.isProctoringEnabled());
        ps.setString(12, exam.getStatus());
    }

    private Exam mapRow(ResultSet rs) throws SQLException {
        Exam e = new Exam();
        e.setExamId(rs.getLong("exam_id"));
        e.setTitle(rs.getString("title"));
        e.setDescription(rs.getString("description"));
        e.setCategory(rs.getString("category"));
        e.setDurationMinutes(rs.getInt("duration_minutes"));
        e.setTotalMarks(rs.getInt("total_marks"));
        e.setPassingMarks(rs.getInt("passing_marks"));
        e.setScheduledStart(rs.getTimestamp("scheduled_start"));
        e.setScheduledEnd(rs.getTimestamp("scheduled_end"));
        e.setShuffleQuestions(rs.getBoolean("shuffle_questions"));
        e.setShuffleOptions(rs.getBoolean("shuffle_options"));
        e.setProctoringEnabled(rs.getBoolean("proctoring_enabled"));
        e.setStatus(rs.getString("status"));
        e.setCreatedBy(rs.getLong("created_by"));
        e.setCreatedAt(rs.getTimestamp("created_at"));
        e.setUpdatedAt(rs.getTimestamp("updated_at"));
        e.setQuestionCount(rs.getInt("question_count"));
        return e;
    }
}
