package com.aiexam.controller;

import com.aiexam.dao.ExamAttemptDAO;
import com.aiexam.dao.ProctoringLogDAO;
import com.aiexam.model.ExamAttempt;
import com.aiexam.model.ProctoringLog;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@WebServlet(name = "AdminMonitoringServlet", urlPatterns = {"/admin/monitoring"})
public class AdminMonitoringServlet extends HttpServlet {

    private final ExamAttemptDAO attemptDAO = new ExamAttemptDAO();
    private final ProctoringLogDAO proctoringLogDAO = new ProctoringLogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            List<ExamAttempt> activeAttempts = attemptDAO.findActiveAttempts();
            req.setAttribute("activeAttempts", activeAttempts);
        } catch (SQLException e) {
            req.setAttribute("activeAttempts", Collections.emptyList());
        }
        try {
            List<ProctoringLog> violationFeed = proctoringLogDAO.findRecentForActiveAttempts(20);
            req.setAttribute("violationFeed", violationFeed);
        } catch (SQLException e) {
            req.setAttribute("violationFeed", Collections.emptyList());
        }
        req.setAttribute("activePage", "monitoring");
        req.getRequestDispatcher("/admin/monitoring.jsp").forward(req, resp);
    }
}
