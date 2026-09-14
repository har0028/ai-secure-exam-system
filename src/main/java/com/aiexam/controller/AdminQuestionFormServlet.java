package com.aiexam.controller;

import com.aiexam.model.Exam;
import com.aiexam.model.Question;
import com.aiexam.service.ExamService;
import com.aiexam.service.QuestionService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

/**
 * GET  /admin/questions/create?examId= -> blank form for a specific exam
 * GET  /admin/questions/edit?id=        -> pre-filled form
 * POST /admin/questions/save            -> create or update depending on hidden "questionId"
 */
@WebServlet(name = "AdminQuestionFormServlet", urlPatterns = {
        "/admin/questions/create", "/admin/questions/edit", "/admin/questions/save"})
public class AdminQuestionFormServlet extends HttpServlet {

    private final QuestionService questionService = new QuestionService();
    private final ExamService examService = new ExamService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            if (req.getServletPath().endsWith("/edit")) {
                long questionId = Long.parseLong(req.getParameter("id"));
                Question question = questionService.getById(questionId);
                if (question == null) {
                    resp.sendRedirect(req.getContextPath() + "/admin/questions");
                    return;
                }
                req.setAttribute("question", question);
                req.setAttribute("exam", examService.getById(question.getExamId()));
            } else {
                long examId = Long.parseLong(req.getParameter("examId"));
                Exam exam = examService.getById(examId);
                if (exam == null) {
                    resp.sendRedirect(req.getContextPath() + "/admin/questions");
                    return;
                }
                req.setAttribute("exam", exam);
            }
        } catch (NumberFormatException | SQLException | NullPointerException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/questions");
            return;
        }
        req.setAttribute("activePage", "questions");
        req.getRequestDispatcher("/admin/question-form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        long actingUserId = (Long) session.getAttribute("userId");
        String ip = req.getRemoteAddr();

        String questionIdParam = req.getParameter("questionId");
        String examIdParam = req.getParameter("examId");
        String questionText = req.getParameter("questionText");
        String questionType = req.getParameter("questionType");
        String optionA = req.getParameter("optionA");
        String optionB = req.getParameter("optionB");
        String optionC = req.getParameter("optionC");
        String optionD = req.getParameter("optionD");
        String correctOption = req.getParameter("correctOption");
        String category = req.getParameter("category");
        String difficulty = req.getParameter("difficulty");
        int marks = parseIntSafe(req.getParameter("marks"), 1);

        boolean isEdit = questionIdParam != null && !questionIdParam.isEmpty();
        long examId = isEdit ? -1 : Long.parseLong(examIdParam);

        QuestionService.Result result;
        if (isEdit) {
            long questionId = Long.parseLong(questionIdParam);
            result = questionService.updateQuestion(questionId, questionText, questionType, optionA,
                    optionB, optionC, optionD, correctOption, marks, category, difficulty,
                    actingUserId, ip);
        } else {
            result = questionService.createQuestion(examId, questionText, questionType, optionA,
                    optionB, optionC, optionD, correctOption, marks, category, difficulty,
                    actingUserId, ip);
        }

        long redirectExamId = isEdit ? resolveExamIdForQuestion(questionIdParam) : examId;

        if (result.success) {
            session.setAttribute("flashSuccess", result.message);
            resp.sendRedirect(req.getContextPath() + "/admin/questions?examId=" + redirectExamId);
        } else {
            req.setAttribute("errorMessage", result.message);
            req.setAttribute("activePage", "questions");
            try {
                if (isEdit) {
                    req.setAttribute("question", questionService.getById(Long.parseLong(questionIdParam)));
                    req.setAttribute("exam", examService.getById(redirectExamId));
                } else {
                    req.setAttribute("exam", examService.getById(examId));
                }
            } catch (SQLException ignored) {
            }
            req.getRequestDispatcher("/admin/question-form.jsp").forward(req, resp);
        }
    }

    private long resolveExamIdForQuestion(String questionIdParam) {
        try {
            Question q = questionService.getById(Long.parseLong(questionIdParam));
            return q != null ? q.getExamId() : 0;
        } catch (SQLException | NumberFormatException e) {
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
}
