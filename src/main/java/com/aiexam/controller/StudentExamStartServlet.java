package com.aiexam.controller;

import com.aiexam.dao.StudentDAO;
import com.aiexam.model.Student;
import com.aiexam.service.ExamAttemptService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "StudentExamStartServlet", urlPatterns = {"/student/exam/start"})
public class StudentExamStartServlet extends HttpServlet {

    private final ExamAttemptService attemptService = new ExamAttemptService();
    private final StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        long userId = (Long) session.getAttribute("userId");

        try {
            long examId = Long.parseLong(req.getParameter("examId"));
            Student student = studentDAO.findByUserId(userId);
            if (student == null) {
                resp.sendRedirect(req.getContextPath() + "/student/exams");
                return;
            }

            String userAgent = req.getHeader("User-Agent");
            ExamAttemptService.StartResult result = attemptService.startOrResumeExam(
                    examId, student.getStudentId(), req.getRemoteAddr(), userAgent);

            if (result.success) {
                resp.sendRedirect(req.getContextPath() + "/student/exam/take?attemptId=" + result.attempt.getAttemptId());
            } else {
                session.setAttribute("flashError", result.message);
                resp.sendRedirect(req.getContextPath() + "/student/exams");
            }
        } catch (NumberFormatException | SQLException e) {
            session.setAttribute("flashError", "Could not start the exam.");
            resp.sendRedirect(req.getContextPath() + "/student/exams");
        }
    }
}
