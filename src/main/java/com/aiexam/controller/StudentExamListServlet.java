package com.aiexam.controller;

import com.aiexam.model.Exam;
import com.aiexam.service.ExamService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Phase 2 scope: lists exams available to students (SCHEDULED/ACTIVE status).
 * The actual "Start Exam" action is wired up in Phase 3 with the exam engine.
 */
@WebServlet(name = "StudentExamListServlet", urlPatterns = {"/student/exams"})
public class StudentExamListServlet extends HttpServlet {

    private final ExamService examService = new ExamService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            List<Exam> availableExams = examService.listAvailableForStudents();
            req.setAttribute("availableExams", availableExams);
        } catch (SQLException e) {
            req.setAttribute("availableExams", Collections.emptyList());
            req.setAttribute("errorMessage", "Could not load available exams.");
        }
        req.setAttribute("activePage", "exams");
        req.getRequestDispatcher("/student/exams.jsp").forward(req, resp);
    }
}
