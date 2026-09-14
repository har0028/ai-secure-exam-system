package com.aiexam.dao;

import com.aiexam.model.Question;
import com.aiexam.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {

    public long insert(Question q) throws SQLException {
        String sql = "INSERT INTO questions (exam_id, question_text, question_type, option_a, option_b, " +
                     "option_c, option_d, correct_option, marks, category, difficulty) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindQuestion(ps, q);
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

    /**
     * Inserts many questions in a single batch - used by the bulk-upload (CSV) flow.
     * Returns the number of rows successfully inserted.
     */
    public int insertBatch(List<Question> questions) throws SQLException {
        String sql = "INSERT INTO questions (exam_id, question_text, question_type, option_a, option_b, " +
                     "option_c, option_d, correct_option, marks, category, difficulty) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                for (Question q : questions) {
                    bindQuestion(ps, q);
                    ps.addBatch();
                }
                int[] results = ps.executeBatch();
                con.commit();
                int total = 0;
                for (int r : results) {
                    if (r > 0 || r == Statement.SUCCESS_NO_INFO) total++;
                }
                return total;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public boolean update(Question q) throws SQLException {
        String sql = "UPDATE questions SET question_text=?, question_type=?, option_a=?, option_b=?, " +
                     "option_c=?, option_d=?, correct_option=?, marks=?, category=?, difficulty=? " +
                     "WHERE question_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, q.getQuestionText());
            ps.setString(2, q.getQuestionType());
            ps.setString(3, q.getOptionA());
            ps.setString(4, q.getOptionB());
            ps.setString(5, q.getOptionC());
            ps.setString(6, q.getOptionD());
            ps.setString(7, q.getCorrectOption());
            ps.setInt(8, q.getMarks());
            ps.setString(9, q.getCategory());
            ps.setString(10, q.getDifficulty());
            ps.setLong(11, q.getQuestionId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(long questionId) throws SQLException {
        String sql = "DELETE FROM questions WHERE question_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, questionId);
            return ps.executeUpdate() > 0;
        }
    }

    public Question findById(long questionId) throws SQLException {
        String sql = "SELECT * FROM questions WHERE question_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public List<Question> findByExamId(long examId) throws SQLException {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE exam_id = ? ORDER BY question_id ASC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    /**
     * Returns a random subset of `limit` questions from the exam's question
     * pool - the foundation for the "Question Pool System" feature. If the
     * exam has fewer questions than `limit`, all of them are returned.
     * Randomization happens in SQL (ORDER BY RAND()) so it is independent
     * per attempt.
     */
    public List<Question> findRandomPoolForExam(long examId, int limit) throws SQLException {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE exam_id = ? ORDER BY RAND() LIMIT ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, examId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public int countByExamId(long examId) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM questions WHERE exam_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM questions";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    /**
     * Distinct category names already in use, for populating the
     * "Question Categories" filter/dropdown in the admin UI.
     */
    public List<String> findDistinctCategories() throws SQLException {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM questions WHERE category IS NOT NULL ORDER BY category ASC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString("category"));
            }
        }
        return list;
    }

    private void bindQuestion(PreparedStatement ps, Question q) throws SQLException {
        ps.setLong(1, q.getExamId());
        ps.setString(2, q.getQuestionText());
        ps.setString(3, q.getQuestionType());
        ps.setString(4, q.getOptionA());
        ps.setString(5, q.getOptionB());
        ps.setString(6, q.getOptionC());
        ps.setString(7, q.getOptionD());
        ps.setString(8, q.getCorrectOption());
        ps.setInt(9, q.getMarks());
        ps.setString(10, q.getCategory());
        ps.setString(11, q.getDifficulty());
    }

    private Question mapRow(ResultSet rs) throws SQLException {
        Question q = new Question();
        q.setQuestionId(rs.getLong("question_id"));
        q.setExamId(rs.getLong("exam_id"));
        q.setQuestionText(rs.getString("question_text"));
        q.setQuestionType(rs.getString("question_type"));
        q.setOptionA(rs.getString("option_a"));
        q.setOptionB(rs.getString("option_b"));
        q.setOptionC(rs.getString("option_c"));
        q.setOptionD(rs.getString("option_d"));
        q.setCorrectOption(rs.getString("correct_option"));
        q.setMarks(rs.getInt("marks"));
        q.setCategory(rs.getString("category"));
        q.setDifficulty(rs.getString("difficulty"));
        q.setCreatedAt(rs.getTimestamp("created_at"));
        return q;
    }
}
