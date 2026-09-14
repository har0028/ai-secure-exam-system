package com.aiexam.controller;

import com.aiexam.dao.StudentDAO;
import com.aiexam.model.ExamAttempt;
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

@WebServlet(name = "StudentExamSubmitServlet", urlPatterns = {"/student/exam/submit"})
public class StudentExamSubmitServlet extends HttpServlet {

    private final ExamAttemptService attemptService = new ExamAttemptService();
    private final StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        long userId = (Long) session.getAttribute("userId");

        try {
            long attemptId = Long.parseLong(req.getParameter("attemptId"));
            ExamAttempt attempt = attemptService.getAttempt(attemptId);
            Student student = studentDAO.findByUserId(userId);

            if (attempt == null || student == null || attempt.getStudentId() != student.getStudentId()) {
                resp.sendRedirect(req.getContextPath() + "/student/exams");
                return;
            }

            ExamAttemptService.SubmitResult result =
                    attemptService.submitExam(attemptId, false, req.getRemoteAddr());

            if (!result.success) {
                session.setAttribute("flashError", result.message);
            }
            resp.sendRedirect(req.getContextPath() + "/student/exam/result?attemptId=" + attemptId);

        } catch (NumberFormatException | SQLException e) {
            resp.sendRedirect(req.getContextPath() + "/student/exams");
        }
    }
}
