package com.aiexam.controller;

import com.aiexam.model.Exam;
import com.aiexam.service.ExamService;
import com.aiexam.service.QuestionService;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

/**
 * GET  /admin/questions/bulk-upload?examId= -> upload form
 * POST /admin/questions/bulk-upload         -> parses the uploaded CSV and
 *      inserts every valid row via QuestionService.bulkUploadFromCsv
 *
 * Expected CSV header (case-insensitive, order-independent):
 * question_text,question_type,option_a,option_b,option_c,option_d,correct_option,marks,category,difficulty
 */
@WebServlet(name = "AdminQuestionBulkUploadServlet", urlPatterns = {"/admin/questions/bulk-upload"})
@MultipartConfig(
        maxFileSize = 5 * 1024 * 1024,       // 5 MB per file
        maxRequestSize = 10 * 1024 * 1024    // 10 MB per request
)
public class AdminQuestionBulkUploadServlet extends HttpServlet {

    private final QuestionService questionService = new QuestionService();
    private final ExamService examService = new ExamService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            long examId = Long.parseLong(req.getParameter("examId"));
            Exam exam = examService.getById(examId);
            if (exam == null) {
                resp.sendRedirect(req.getContextPath() + "/admin/questions");
                return;
            }
            req.setAttribute("exam", exam);
        } catch (NumberFormatException | SQLException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/questions");
            return;
        }
        req.setAttribute("activePage", "questions");
        req.getRequestDispatcher("/admin/question-bulk-upload.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        long actingUserId = (Long) session.getAttribute("userId");
        String ip = req.getRemoteAddr();

        long examId;
        try {
            examId = Long.parseLong(req.getParameter("examId"));
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/questions");
            return;
        }

        Part filePart = req.getPart("csvFile");
        if (filePart == null || filePart.getSize() == 0) {
            req.setAttribute("errorMessage", "Please choose a CSV file to upload.");
            forwardBackToForm(req, resp, examId);
            return;
        }

        QuestionService.BulkUploadResult result;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(filePart.getInputStream(), StandardCharsets.UTF_8))) {
            result = questionService.bulkUploadFromCsv(examId, reader, actingUserId, ip);
        }

        if (result.success) {
            session.setAttribute("flashSuccess", result.message);
            if (!result.rowErrors.isEmpty()) {
                session.setAttribute("flashRowErrors", result.rowErrors);
            }
            resp.sendRedirect(req.getContextPath() + "/admin/questions?examId=" + examId);
        } else {
            req.setAttribute("errorMessage", result.message);
            req.setAttribute("rowErrors", result.rowErrors);
            forwardBackToForm(req, resp, examId);
        }
    }

    private void forwardBackToForm(HttpServletRequest req, HttpServletResponse resp, long examId)
            throws ServletException, IOException {
        try {
            req.setAttribute("exam", examService.getById(examId));
        } catch (SQLException ignored) {
        }
        req.setAttribute("activePage", "questions");
        req.getRequestDispatcher("/admin/question-bulk-upload.jsp").forward(req, resp);
    }
}
