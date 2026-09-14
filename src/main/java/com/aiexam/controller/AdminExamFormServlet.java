package com.aiexam.controller;

import com.aiexam.dao.AdminDAO;
import com.aiexam.model.Admin;
import com.aiexam.model.Exam;
import com.aiexam.service.ExamService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * GET  /admin/exams/create -> blank form
 * GET  /admin/exams/edit?id= -> pre-filled form
 * POST /admin/exams/save   -> create or update depending on hidden "examId" field
 */
@WebServlet(name = "AdminExamFormServlet", urlPatterns = {
        "/admin/exams/create", "/admin/exams/edit", "/admin/exams/save"})
public class AdminExamFormServlet extends HttpServlet {

    private final ExamService examService = new ExamService();
    private final AdminDAO adminDAO = new AdminDAO();

    // HTML <input type="datetime-local"> sends values like "2026-06-25T10:00"
    private static final DateTimeFormatter HTML_DATETIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (req.getServletPath().endsWith("/edit")) {
            try {
                long examId = Long.parseLong(req.getParameter("id"));
                Exam exam = examService.getById(examId);
                if (exam == null) {
                    resp.sendRedirect(req.getContextPath() + "/admin/exams");
                    return;
                }
                req.setAttribute("exam", exam);
            } catch (NumberFormatException | SQLException e) {
                resp.sendRedirect(req.getContextPath() + "/admin/exams");
                return;
            }
        }
        req.setAttribute("activePage", "exams");
        req.getRequestDispatcher("/admin/exam-form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        long actingUserId = (Long) session.getAttribute("userId");
        String ip = req.getRemoteAddr();

        String examIdParam = req.getParameter("examId");
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String category = req.getParameter("category");
        String status = req.getParameter("status");
        boolean shuffleQuestions = "on".equals(req.getParameter("shuffleQuestions"));
        boolean shuffleOptions = "on".equals(req.getParameter("shuffleOptions"));
        boolean proctoringEnabled = "on".equals(req.getParameter("proctoringEnabled"));

        int durationMinutes = parseIntSafe(req.getParameter("durationMinutes"), 0);
        int totalMarks = parseIntSafe(req.getParameter("totalMarks"), 0);
        int passingMarks = parseIntSafe(req.getParameter("passingMarks"), 0);
        Timestamp scheduledStart = parseHtmlDateTime(req.getParameter("scheduledStart"));
        Timestamp scheduledEnd = parseHtmlDateTime(req.getParameter("scheduledEnd"));

        boolean isEdit = examIdParam != null && !examIdParam.isEmpty();

        ExamService.Result result;
        if (isEdit) {
            long examId = Long.parseLong(examIdParam);
            result = examService.updateExam(examId, title, description, category, durationMinutes,
                    totalMarks, passingMarks, scheduledStart, scheduledEnd, shuffleQuestions,
                    shuffleOptions, proctoringEnabled, status, actingUserId, ip);
        } else {
            long createdByAdminId = resolveAdminId(actingUserId);
            result = examService.createExam(title, description, category, durationMinutes, totalMarks,
                    passingMarks, scheduledStart, scheduledEnd, shuffleQuestions, shuffleOptions,
                    proctoringEnabled, status, createdByAdminId, actingUserId, ip);
        }

        if (result.success) {
            session.setAttribute("flashSuccess", result.message);
            resp.sendRedirect(req.getContextPath() + "/admin/exams");
        } else {
            req.setAttribute("errorMessage", result.message);
            req.setAttribute("activePage", "exams");
            if (isEdit) {
                try {
                    req.setAttribute("exam", examService.getById(Long.parseLong(examIdParam)));
                } catch (SQLException ignored) {
                }
            }
            req.getRequestDispatcher("/admin/exam-form.jsp").forward(req, resp);
        }
    }

    private long resolveAdminId(long userId) {
        try {
            Admin admin = adminDAO.findByUserId(userId);
            return admin != null ? admin.getAdminId() : 0;
        } catch (SQLException e) {
            return 0;
        }
    }

    private int parseIntSafe(String value, int fallback) {
        try {
            return value != null ? Integer.parseInt(value.trim()) : fallback;
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private Timestamp parseHtmlDateTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            LocalDateTime ldt = LocalDateTime.parse(value.trim(), HTML_DATETIME_FORMAT);
            return Timestamp.valueOf(ldt);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
