package com.aiexam.controller;

import com.aiexam.dao.StudentDAO;
import com.aiexam.model.Exam;
import com.aiexam.model.Student;
import com.aiexam.service.ExamService;

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

@WebServlet(name = "StudentDashboardServlet", urlPatterns = {"/student/dashboard"})
public class StudentDashboardServlet extends HttpServlet {

    private final StudentDAO studentDAO = new StudentDAO();
    private final ExamService examService = new ExamService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        long userId = (Long) session.getAttribute("userId");
        try {
            Student student = studentDAO.findByUserId(userId);
            req.setAttribute("student", student);
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Could not load profile data.");
        }
        try {
            List<Exam> availableExams = examService.listAvailableForStudents();
            req.setAttribute("availableExams", availableExams);
            long proctoredCount = availableExams.stream().filter(Exam::isProctoringEnabled).count();
            req.setAttribute("proctoredExamsCount", proctoredCount);
        } catch (SQLException e) {
            req.setAttribute("availableExams", Collections.emptyList());
            req.setAttribute("proctoredExamsCount", 0);
        }
        req.setAttribute("activePage", "dashboard");
        // Completed-exam history and notifications are wired up in Phase 3
        req.getRequestDispatcher("/student/dashboard.jsp").forward(req, resp);
    }
}

