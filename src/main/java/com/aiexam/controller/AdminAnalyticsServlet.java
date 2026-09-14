package com.aiexam.controller;

import com.aiexam.dao.ExamAttemptDAO;
import com.aiexam.dao.ExamDAO;
import com.aiexam.dao.ProctoringLogDAO;
import com.aiexam.dao.QuestionDAO;
import com.aiexam.dao.StudentDAO;
import com.aiexam.service.ResultService;
import com.aiexam.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "AdminAnalyticsServlet", urlPatterns = {"/admin/analytics"})
public class AdminAnalyticsServlet extends HttpServlet {

    private final StudentDAO studentDAO = new StudentDAO();
    private final ExamDAO examDAO = new ExamDAO();
    private final QuestionDAO questionDAO = new QuestionDAO();
    private final ExamAttemptDAO attemptDAO = new ExamAttemptDAO();
    private final ResultService resultService = new ResultService();
    private final ProctoringLogDAO proctoringLogDAO = new ProctoringLogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try { req.setAttribute("totalStudents", studentDAO.countAll()); } catch (SQLException e) { req.setAttribute("totalStudents", 0); }
        try { req.setAttribute("totalExams", examDAO.countAll()); } catch (SQLException e) { req.setAttribute("totalExams", 0); }
        try { req.setAttribute("totalAttempts", attemptDAO.countAll()); } catch (SQLException e) { req.setAttribute("totalAttempts", 0); }
        try { req.setAttribute("totalResults", resultService.countAll()); } catch (SQLException e) { req.setAttribute("totalResults", 0); }
        try { req.setAttribute("totalViolations", proctoringLogDAO.countAll()); } catch (SQLException e) { req.setAttribute("totalViolations", 0); }
        try { req.setAttribute("passCount", resultService.countPassed()); } catch (SQLException e) { req.setAttribute("passCount", 0); }
        try { req.setAttribute("failCount", resultService.countFailed()); } catch (SQLException e) { req.setAttribute("failCount", 0); }
        try { req.setAttribute("avgPercentage", String.format("%.1f", resultService.averagePercentage())); } catch (SQLException e) { req.setAttribute("avgPercentage", "0.0"); }

        // Build JSON arrays for Chart.js
        try {
            List<Object[]> violationGroups = proctoringLogDAO.countGroupedByEventType();
            StringBuilder labels = new StringBuilder("[");
            StringBuilder data = new StringBuilder("[");
            for (int i = 0; i < violationGroups.size(); i++) {
                Object[] row = violationGroups.get(i);
                if (i > 0) { labels.append(","); data.append(","); }
                labels.append("\"").append(JsonUtil.escape((String) row[0])).append("\"");
                data.append(row[1]);
            }
            labels.append("]"); data.append("]");
            req.setAttribute("violationLabelsJson", labels.toString());
            req.setAttribute("violationDataJson", data.toString());
        } catch (SQLException e) {
            req.setAttribute("violationLabelsJson", "[]");
            req.setAttribute("violationDataJson", "[]");
        }

        req.setAttribute("activePage", "analytics");
        req.getRequestDispatcher("/admin/analytics.jsp").forward(req, resp);
    }
}
