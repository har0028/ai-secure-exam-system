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
import java.util.List;

@WebServlet(name = "AdminExamServlet", urlPatterns = {"/admin/exams"})
public class AdminExamServlet extends HttpServlet {

    private final ExamService examService = new ExamService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String keyword = req.getParameter("q");
        try {
            List<Exam> exams = examService.search(keyword);
            req.setAttribute("exams", exams);
            req.setAttribute("searchKeyword", keyword);
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Could not load exams.");
        }
        req.setAttribute("activePage", "exams");
        req.getRequestDispatcher("/admin/exams.jsp").forward(req, resp);
    }
}
