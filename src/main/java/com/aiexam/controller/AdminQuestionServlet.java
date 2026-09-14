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
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "AdminQuestionServlet", urlPatterns = {"/admin/questions"})
public class AdminQuestionServlet extends HttpServlet {

    private final QuestionService questionService = new QuestionService();
    private final ExamService examService = new ExamService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String examIdParam = req.getParameter("examId");

        try {
            List<Exam> exams = examService.listAll();
            req.setAttribute("exams", exams);

            if (examIdParam != null && !examIdParam.isEmpty()) {
                long examId = Long.parseLong(examIdParam);
                Exam exam = examService.getById(examId);
                List<Question> questions = questionService.listByExam(examId);
                req.setAttribute("selectedExam", exam);
                req.setAttribute("questions", questions);
            }
        } catch (NumberFormatException | SQLException e) {
            req.setAttribute("errorMessage", "Could not load questions.");
        }

        req.setAttribute("activePage", "questions");
        req.getRequestDispatcher("/admin/questions.jsp").forward(req, resp);
    }
}
