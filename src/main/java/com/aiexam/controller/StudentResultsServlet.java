package com.aiexam.controller;

import com.aiexam.dao.StudentDAO;
import com.aiexam.model.Result;
import com.aiexam.model.Student;
import com.aiexam.service.ResultService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@WebServlet(name = "StudentResultsServlet", urlPatterns = {"/student/results"})
public class StudentResultsServlet extends HttpServlet {

    private final ResultService resultService = new ResultService();
    private final StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        long userId = (Long) session.getAttribute("userId");

        try {
            Student student = studentDAO.findByUserId(userId);
            List<Result> results = student != null ? resultService.listByStudent(student.getStudentId())
                                                     : Collections.emptyList();
            req.setAttribute("results", results);
        } catch (SQLException e) {
            req.setAttribute("results", Collections.emptyList());
            req.setAttribute("errorMessage", "Could not load your results.");
        }
        req.setAttribute("activePage", "results");
        req.getRequestDispatcher("/student/results.jsp").forward(req, resp);
    }
}
