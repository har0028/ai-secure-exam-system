package com.aiexam.controller;

import com.aiexam.dao.StudentDAO;
import com.aiexam.model.Exam;
import com.aiexam.model.ExamAttempt;
import com.aiexam.model.Student;
import com.aiexam.service.ExamAttemptService;
import com.aiexam.service.ExamService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

/**
 * GET /student/exam/instructions?examId= - shows exam rules, the
 * required webcam/fullscreen pre-checks, and the "Begin Exam" action that
 * posts to StudentExamStartServlet. Redirects straight to the exam-taking
 * page if the student already has an IN_PROGRESS attempt (resume case).
 */
@WebServlet(name = "StudentExamInstructionsServlet", urlPatterns = {"/student/exam/instructions"})
public class StudentExamInstructionsServlet extends HttpServlet {

    private final ExamService examService = new ExamService();
    private final ExamAttemptService attemptService = new ExamAttemptService();
    private final StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        long userId = (Long) session.getAttribute("userId");

        try {
            long examId = Long.parseLong(req.getParameter("examId"));
            Exam exam = examService.getById(examId);
            if (exam == null) {
                resp.sendRedirect(req.getContextPath() + "/student/exams");
                return;
            }
            if (!"SCHEDULED".equals(exam.getStatus()) && !"ACTIVE".equals(exam.getStatus())) {
                session.setAttribute("flashError", "This exam is not currently available.");
                resp.sendRedirect(req.getContextPath() + "/student/exams");
                return;
            }

            Student student = studentDAO.findByUserId(userId);
            ExamAttempt existing = findExistingAttempt(exam.getExamId(), student.getStudentId());
            if (existing != null) {
                if ("IN_PROGRESS".equals(existing.getStatus())) {
                    resp.sendRedirect(req.getContextPath() + "/student/exam/take?attemptId=" + existing.getAttemptId());
                    return;
                }
                session.setAttribute("flashError", "You have already attempted this exam.");
                resp.sendRedirect(req.getContextPath() + "/student/exams");
                return;
            }

            req.setAttribute("exam", exam);
            req.setAttribute("activePage", "exams");
            req.getRequestDispatcher("/student/exam-instructions.jsp").forward(req, resp);

        } catch (NumberFormatException | SQLException e) {
            resp.sendRedirect(req.getContextPath() + "/student/exams");
        }
    }

    private ExamAttempt findExistingAttempt(long examId, long studentId) throws SQLException {
        for (ExamAttempt a : attemptService.listByStudent(studentId)) {
            if (a.getExamId() == examId) {
                return a;
            }
        }
        return null;
    }
}
