package com.aiexam.controller;

import com.aiexam.model.Question;
import com.aiexam.service.QuestionService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "AdminQuestionDeleteServlet", urlPatterns = {"/admin/questions/delete"})
public class AdminQuestionDeleteServlet extends HttpServlet {

    private final QuestionService questionService = new QuestionService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        long actingUserId = (Long) session.getAttribute("userId");

        long examId = 0;
        try {
            long questionId = Long.parseLong(req.getParameter("id"));
            Question q = questionService.getById(questionId);
            examId = (q != null) ? q.getExamId() : 0;

            QuestionService.Result result =
                    questionService.deleteQuestion(questionId, actingUserId, req.getRemoteAddr());
            session.setAttribute(result.success ? "flashSuccess" : "flashError", result.message);
        } catch (NumberFormatException | SQLException e) {
            session.setAttribute("flashError", "Invalid question reference.");
        }
        resp.sendRedirect(req.getContextPath() + "/admin/questions?examId=" + examId);
    }
}
