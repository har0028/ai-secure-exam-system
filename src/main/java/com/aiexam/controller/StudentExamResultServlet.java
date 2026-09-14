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

@WebServlet(name = "StudentExamResultServlet", urlPatterns = {"/student/exam/result"})
public class StudentExamResultServlet extends HttpServlet {

    private final ResultService resultService = new ResultService();
    private final StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        long userId = (Long) session.getAttribute("userId");

        try {
            long attemptId = Long.parseLong(req.getParameter("attemptId"));
            Result result = resultService.getByAttemptId(attemptId);
            Student student = studentDAO.findByUserId(userId);

            if (result == null) {
                req.setAttribute("pendingMessage",
                        "Your result is being generated. If this exam was just auto-submitted due to a " +
                        "proctoring violation, refresh in a moment.");
                req.getRequestDispatcher("/student/result-detail.jsp").forward(req, resp);
                return;
            }
            if (student == null || result.getStudentId() != student.getStudentId()) {
                resp.sendRedirect(req.getContextPath() + "/student/results");
                return;
            }

            req.setAttribute("result", result);
            req.setAttribute("activePage", "results");
            req.getRequestDispatcher("/student/result-detail.jsp").forward(req, resp);

        } catch (NumberFormatException | SQLException e) {
            resp.sendRedirect(req.getContextPath() + "/student/results");
        }
    }
}
