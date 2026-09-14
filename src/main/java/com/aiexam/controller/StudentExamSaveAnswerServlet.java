package com.aiexam.controller;

import com.aiexam.service.ExamAttemptService;
import com.aiexam.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * AJAX endpoint called by exam-engine.js on every answer change and on the
 * 30-second autosave timer. Always responds 200 with a JSON {success, ...}
 * body (errors are reported in the JSON, not via HTTP status) so the
 * client-side retry logic stays simple.
 */
@WebServlet(name = "StudentExamSaveAnswerServlet", urlPatterns = {"/student/exam/save-answer"})
public class StudentExamSaveAnswerServlet extends HttpServlet {

    private final ExamAttemptService attemptService = new ExamAttemptService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");

        String result;
        try {
            long attemptId = Long.parseLong(req.getParameter("attemptId"));
            long questionId = Long.parseLong(req.getParameter("questionId"));
            String selectedOption = req.getParameter("selectedOption");
            boolean markedForReview = "true".equals(req.getParameter("markedForReview"));

            boolean saved = attemptService.saveAnswer(attemptId, questionId, selectedOption, markedForReview);
            result = JsonUtil.object()
                    .put("success", saved)
                    .put("message", saved ? "Saved" : "This exam is no longer active.")
                    .toString();
        } catch (NumberFormatException e) {
            result = JsonUtil.object().put("success", false).put("message", "Invalid request.").toString();
        }

        try (PrintWriter out = resp.getWriter()) {
            out.write(result);
        }
    }
}
