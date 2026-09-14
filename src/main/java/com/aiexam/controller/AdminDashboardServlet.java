package com.aiexam.controller;

import com.aiexam.dao.ActivityLogDAO;
import com.aiexam.dao.ExamAttemptDAO;
import com.aiexam.dao.ExamDAO;
import com.aiexam.dao.ProctoringLogDAO;
import com.aiexam.dao.QuestionDAO;
import com.aiexam.dao.StudentDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {

    private final StudentDAO studentDAO = new StudentDAO();
    private final ExamDAO examDAO = new ExamDAO();
    private final QuestionDAO questionDAO = new QuestionDAO();
    private final ExamAttemptDAO attemptDAO = new ExamAttemptDAO();
    private final ProctoringLogDAO proctoringLogDAO = new ProctoringLogDAO();
    private final ActivityLogDAO activityLogDAO = new ActivityLogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try { req.setAttribute("totalStudents", studentDAO.countAll()); } catch (SQLException e) { req.setAttribute("totalStudents", 0); }
        try { req.setAttribute("totalExams", examDAO.countAll()); } catch (SQLException e) { req.setAttribute("totalExams", 0); }
        try { req.setAttribute("totalQuestions", questionDAO.countAll()); } catch (SQLException e) { req.setAttribute("totalQuestions", 0); }
        try { req.setAttribute("totalViolations", proctoringLogDAO.countAll()); } catch (SQLException e) { req.setAttribute("totalViolations", 0); }
        try { req.setAttribute("recentActivity", activityLogDAO.findRecent(8)); } catch (SQLException e) { req.setAttribute("recentActivity", java.util.Collections.emptyList()); }
        req.setAttribute("activePage", "dashboard");
        req.getRequestDispatcher("/admin/dashboard.jsp").forward(req, resp);
    }
}
