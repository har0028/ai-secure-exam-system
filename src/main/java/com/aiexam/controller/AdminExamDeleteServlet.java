package com.aiexam.controller;

import com.aiexam.service.ExamService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "AdminExamDeleteServlet", urlPatterns = {"/admin/exams/delete"})
public class AdminExamDeleteServlet extends HttpServlet {

    private final ExamService examService = new ExamService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        long actingUserId = (Long) session.getAttribute("userId");

        try {
            long examId = Long.parseLong(req.getParameter("id"));
            ExamService.Result result = examService.deleteExam(examId, actingUserId, req.getRemoteAddr());
            session.setAttribute(result.success ? "flashSuccess" : "flashError", result.message);
        } catch (NumberFormatException e) {
            session.setAttribute("flashError", "Invalid exam reference.");
        }
        resp.sendRedirect(req.getContextPath() + "/admin/exams");
    }
}
