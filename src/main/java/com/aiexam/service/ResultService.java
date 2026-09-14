package com.aiexam.service;

import com.aiexam.dao.ResultDAO;
import com.aiexam.model.Result;
import com.aiexam.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

public class ResultService {

    private final ResultDAO resultDAO = new ResultDAO();

    public Result getByAttemptId(long attemptId) throws SQLException {
        return resultDAO.findByAttemptId(attemptId);
    }

    public List<Result> listByStudent(long studentId) throws SQLException {
        return resultDAO.findByStudentId(studentId);
    }

    public List<Result> listAll() throws SQLException {
        return resultDAO.findAll();
    }

    public List<Result> search(String keyword) throws SQLException {
        if (!ValidationUtil.isNotBlank(keyword)) {
            return resultDAO.findAll();
        }
        return resultDAO.search(keyword.trim());
    }

    public int countAll() throws SQLException {
        return resultDAO.countAll();
    }

    public double averagePercentage() throws SQLException {
        return resultDAO.averagePercentage();
    }

    public int countPassed() throws SQLException {
        return resultDAO.countByPassStatus("PASS");
    }

    public int countFailed() throws SQLException {
        return resultDAO.countByPassStatus("FAIL");
    }
}
