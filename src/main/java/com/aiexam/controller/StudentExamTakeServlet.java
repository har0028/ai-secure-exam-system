package com.aiexam.controller;

import com.aiexam.dao.StudentAnswerDAO;
import com.aiexam.dao.StudentDAO;
import com.aiexam.model.Exam;
import com.aiexam.model.ExamAttempt;
import com.aiexam.model.Question;
import com.aiexam.model.Student;
import com.aiexam.model.StudentAnswer;
import com.aiexam.service.ExamAttemptService;
import com.aiexam.service.ExamService;
import com.aiexam.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "StudentExamTakeServlet", urlPatterns = {"/student/exam/take"})
public class StudentExamTakeServlet extends HttpServlet {

    private final ExamAttemptService attemptService = new ExamAttemptService();
    private final ExamService examService = new ExamService();
    private final StudentAnswerDAO answerDAO = new StudentAnswerDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
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
            if (!"IN_PROGRESS".equals(attempt.getStatus())) {
                resp.sendRedirect(req.getContextPath() + "/student/exam/result?attemptId=" + attemptId);
                return;
            }

            Exam exam = examService.getById(attempt.getExamId());
            List<Question> questions = attemptService.getQuestionsForAttempt(exam);
            List<StudentAnswer> existingAnswers = answerDAO.findByAttemptId(attemptId);

            // Build a JSON map of questionId -> {selectedOption, markedForReview} so
            // exam-engine.js can restore state on page load/refresh without another round trip.
            StringBuilder answersJson = new StringBuilder("{");
            for (int i = 0; i < existingAnswers.size(); i++) {
                StudentAnswer a = existingAnswers.get(i);
                if (i > 0) answersJson.append(",");
                answersJson.append("\"").append(a.getQuestionId()).append("\":")
                        .append(JsonUtil.object()
                                .put("selectedOption", a.getSelectedOption())
                                .put("markedForReview", a.isMarkedForReview())
                                .toString());
            }
            answersJson.append("}");

            req.setAttribute("attempt", attempt);
            req.setAttribute("exam", exam);
            req.setAttribute("questions", questions);
            req.setAttribute("existingAnswersJson", answersJson.toString());
            req.getRequestDispatcher("/student/exam-taking.jsp").forward(req, resp);

        } catch (NumberFormatException | SQLException e) {
            resp.sendRedirect(req.getContextPath() + "/student/exams");
        }
    }
}
