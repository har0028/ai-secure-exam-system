package com.aiexam.service;

import com.aiexam.dao.ActivityLogDAO;
import com.aiexam.dao.ExamAttemptDAO;
import com.aiexam.dao.ExamDAO;
import com.aiexam.dao.QuestionDAO;
import com.aiexam.dao.ResultDAO;
import com.aiexam.dao.StudentAnswerDAO;
import com.aiexam.model.Exam;
import com.aiexam.model.ExamAttempt;
import com.aiexam.model.Question;
import com.aiexam.model.Result;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;

/**
 * Owns the exam-taking lifecycle: starting an attempt, autosaving answers,
 * and submitting + automatically evaluating the attempt into a Result.
 */
public class ExamAttemptService {

    private final ExamDAO examDAO = new ExamDAO();
    private final QuestionDAO questionDAO = new QuestionDAO();
    private final ExamAttemptDAO attemptDAO = new ExamAttemptDAO();
    private final StudentAnswerDAO answerDAO = new StudentAnswerDAO();
    private final ResultDAO resultDAO = new ResultDAO();
    private final ActivityLogDAO activityLogDAO = new ActivityLogDAO();

    public static class StartResult {
        public final boolean success;
        public final String message;
        public final ExamAttempt attempt;

        public StartResult(boolean success, String message, ExamAttempt attempt) {
            this.success = success;
            this.message = message;
            this.attempt = attempt;
        }
    }

    public static class SubmitResult {
        public final boolean success;
        public final String message;
        public final Long attemptId;

        public SubmitResult(boolean success, String message, Long attemptId) {
            this.success = success;
            this.message = message;
            this.attemptId = attemptId;
        }
    }

    /**
     * Starts a new attempt, or resumes an existing IN_PROGRESS one (e.g. the
     * student refreshed the page). Refuses to start if the exam isn't in a
     * student-visible status, or if the student already has a SUBMITTED /
     * AUTO_SUBMITTED attempt for this exam (one attempt per exam, per the
     * unique constraint on exam_attempts).
     */
    public StartResult startOrResumeExam(long examId, long studentId, String ip, String userAgent) {
        try {
            Exam exam = examDAO.findById(examId);
            if (exam == null) {
                return new StartResult(false, "Exam not found.", null);
            }
            if (!"SCHEDULED".equals(exam.getStatus()) && !"ACTIVE".equals(exam.getStatus())) {
                return new StartResult(false, "This exam is not currently available.", null);
            }

            ExamAttempt existing = attemptDAO.findByExamAndStudent(examId, studentId);
            if (existing != null) {
                if ("IN_PROGRESS".equals(existing.getStatus())) {
                    return new StartResult(true, "Resuming your in-progress attempt.", existing);
                }
                return new StartResult(false,
                        "You have already attempted this exam. Multiple attempts are not allowed.", null);
            }

            long attemptId = attemptDAO.insert(examId, studentId, ip, userAgent);
            if (attemptId <= 0) {
                return new StartResult(false, "Failed to start the exam. Please try again.", null);
            }
            activityLogDAO.log(null, "EXAM_STARTED",
                    "Student #" + studentId + " started exam #" + examId, ip);

            ExamAttempt attempt = attemptDAO.findById(attemptId);
            return new StartResult(true, "Exam started.", attempt);

        } catch (SQLException e) {
            return new StartResult(false, "A database error occurred while starting the exam.", null);
        }
    }

    /**
     * Autosave entry point - called from the AJAX timer every ~30 seconds
     * and on explicit answer/navigation changes. Silently no-ops if the
     * attempt is no longer IN_PROGRESS (e.g. it was already auto-submitted
     * by the proctoring threshold) so a stray autosave can't resurrect it.
     */
    public boolean saveAnswer(long attemptId, long questionId, String selectedOption, boolean markedForReview) {
        try {
            ExamAttempt attempt = attemptDAO.findById(attemptId);
            if (attempt == null || !"IN_PROGRESS".equals(attempt.getStatus())) {
                return false;
            }
            answerDAO.upsert(attemptId, questionId, selectedOption, markedForReview);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Submits the attempt: evaluates every answer against the correct
     * option, aggregates the score, writes the Result row, and flips the
     * attempt to SUBMITTED or AUTO_SUBMITTED.
     */
    public SubmitResult submitExam(long attemptId, boolean isAutoSubmit, String ip) {
        try {
            ExamAttempt attempt = attemptDAO.findById(attemptId);
            if (attempt == null) {
                return new SubmitResult(false, "Attempt not found.", null);
            }
            if (!"IN_PROGRESS".equals(attempt.getStatus())) {
                // Already submitted (e.g. double-submit race) - return success idempotently
                return new SubmitResult(true, "This exam has already been submitted.", attemptId);
            }

            Exam exam = examDAO.findById(attempt.getExamId());
            if (exam == null) {
                return new SubmitResult(false, "Associated exam not found.", null);
            }

            answerDAO.evaluateAttempt(attemptId);
            StudentAnswerDAO.ScoreSummary summary = answerDAO.computeScoreSummary(attemptId, exam.getExamId());

            BigDecimal percentage = exam.getTotalMarks() > 0
                    ? BigDecimal.valueOf(summary.obtainedMarks)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(exam.getTotalMarks()), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            String passStatus = summary.obtainedMarks >= exam.getPassingMarks() ? "PASS" : "FAIL";

            Result result = new Result();
            result.setAttemptId(attemptId);
            result.setExamId(exam.getExamId());
            result.setStudentId(attempt.getStudentId());
            result.setTotalMarks(exam.getTotalMarks());
            result.setObtainedMarks(summary.obtainedMarks);
            result.setCorrectAnswers(summary.correctAnswers);
            result.setWrongAnswers(summary.wrongAnswers);
            result.setUnanswered(summary.unanswered);
            result.setPercentage(percentage);
            result.setPassStatus(passStatus);

            long resultId = resultDAO.insert(result);
            if (resultId <= 0) {
                return new SubmitResult(false, "Failed to generate the result.", null);
            }

            String finalStatus = isAutoSubmit ? "AUTO_SUBMITTED" : "SUBMITTED";
            attemptDAO.updateStatus(attemptId, finalStatus);

            activityLogDAO.log(null,
                    isAutoSubmit ? "EXAM_AUTO_SUBMITTED" : "EXAM_SUBMITTED",
                    "Attempt #" + attemptId + " for exam #" + exam.getExamId() +
                    " scored " + summary.obtainedMarks + "/" + exam.getTotalMarks(), ip);

            return new SubmitResult(true, "Exam submitted successfully.", attemptId);

        } catch (SQLException e) {
            return new SubmitResult(false, "A database error occurred while submitting the exam.", null);
        }
    }

    public ExamAttempt getAttempt(long attemptId) throws SQLException {
        return attemptDAO.findById(attemptId);
    }

    /**
     * Returns the exam's question set ready for delivery to the student:
     * shuffled per-attempt if the exam has shuffle_questions enabled.
     */
    public List<Question> getQuestionsForAttempt(Exam exam) throws SQLException {
        List<Question> questions = questionDAO.findByExamId(exam.getExamId());
        if (exam.isShuffleQuestions()) {
            java.util.Collections.shuffle(questions);
        }
        return questions;
    }

    public List<ExamAttempt> listByStudent(long studentId) throws SQLException {
        return attemptDAO.findByStudentId(studentId);
    }

    public List<ExamAttempt> listActiveAttempts() throws SQLException {
        return attemptDAO.findActiveAttempts();
    }
}
