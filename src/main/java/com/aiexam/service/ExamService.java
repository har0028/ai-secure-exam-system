package com.aiexam.service;

import com.aiexam.dao.ActivityLogDAO;
import com.aiexam.dao.ExamDAO;
import com.aiexam.model.Exam;
import com.aiexam.util.ValidationUtil;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class ExamService {

    private final ExamDAO examDAO = new ExamDAO();
    private final ActivityLogDAO activityLogDAO = new ActivityLogDAO();

    public static class Result {
        public final boolean success;
        public final String message;
        public final Long examId;

        public Result(boolean success, String message, Long examId) {
            this.success = success;
            this.message = message;
            this.examId = examId;
        }
    }

    public Result createExam(String title, String description, String category, int durationMinutes,
                              int totalMarks, int passingMarks, Timestamp scheduledStart, Timestamp scheduledEnd,
                              boolean shuffleQuestions, boolean shuffleOptions, boolean proctoringEnabled,
                              String status, long createdByAdminId, long actingUserId, String ip) {
        Result validation = validate(title, durationMinutes, totalMarks, passingMarks,
                scheduledStart, scheduledEnd);
        if (validation != null) {
            return validation;
        }
        try {
            Exam exam = new Exam();
            exam.setTitle(title.trim());
            exam.setDescription(description);
            exam.setCategory(category);
            exam.setDurationMinutes(durationMinutes);
            exam.setTotalMarks(totalMarks);
            exam.setPassingMarks(passingMarks);
            exam.setScheduledStart(scheduledStart);
            exam.setScheduledEnd(scheduledEnd);
            exam.setShuffleQuestions(shuffleQuestions);
            exam.setShuffleOptions(shuffleOptions);
            exam.setProctoringEnabled(proctoringEnabled);
            exam.setStatus(status);
            exam.setCreatedBy(createdByAdminId);

            long examId = examDAO.insert(exam);
            if (examId <= 0) {
                return new Result(false, "Failed to create exam.", null);
            }
            activityLogDAO.log(actingUserId, "ADMIN_CREATE_EXAM", "Created exam: " + title, ip);
            return new Result(true, "Exam created successfully.", examId);

        } catch (SQLException e) {
            return new Result(false, "A database error occurred while creating the exam.", null);
        }
    }

    public Result updateExam(long examId, String title, String description, String category,
                              int durationMinutes, int totalMarks, int passingMarks,
                              Timestamp scheduledStart, Timestamp scheduledEnd, boolean shuffleQuestions,
                              boolean shuffleOptions, boolean proctoringEnabled, String status,
                              long actingUserId, String ip) {
        Result validation = validate(title, durationMinutes, totalMarks, passingMarks,
                scheduledStart, scheduledEnd);
        if (validation != null) {
            return validation;
        }
        try {
            Exam exam = examDAO.findById(examId);
            if (exam == null) {
                return new Result(false, "Exam not found.", null);
            }
            exam.setTitle(title.trim());
            exam.setDescription(description);
            exam.setCategory(category);
            exam.setDurationMinutes(durationMinutes);
            exam.setTotalMarks(totalMarks);
            exam.setPassingMarks(passingMarks);
            exam.setScheduledStart(scheduledStart);
            exam.setScheduledEnd(scheduledEnd);
            exam.setShuffleQuestions(shuffleQuestions);
            exam.setShuffleOptions(shuffleOptions);
            exam.setProctoringEnabled(proctoringEnabled);
            exam.setStatus(status);

            boolean updated = examDAO.update(exam);
            if (!updated) {
                return new Result(false, "Failed to update exam.", null);
            }
            activityLogDAO.log(actingUserId, "ADMIN_UPDATE_EXAM", "Updated exam: " + title, ip);
            return new Result(true, "Exam updated successfully.", examId);

        } catch (SQLException e) {
            return new Result(false, "A database error occurred while updating the exam.", null);
        }
    }

    public Result deleteExam(long examId, long actingUserId, String ip) {
        try {
            Exam exam = examDAO.findById(examId);
            if (exam == null) {
                return new Result(false, "Exam not found.", null);
            }
            boolean deleted = examDAO.delete(examId);
            if (!deleted) {
                return new Result(false, "Failed to delete exam.", null);
            }
            activityLogDAO.log(actingUserId, "ADMIN_DELETE_EXAM", "Deleted exam: " + exam.getTitle(), ip);
            return new Result(true, "Exam deleted successfully.", null);

        } catch (SQLException e) {
            return new Result(false, "A database error occurred while deleting the exam.", null);
        }
    }

    public List<Exam> listAll() throws SQLException {
        return examDAO.findAll();
    }

    public List<Exam> search(String keyword) throws SQLException {
        if (!ValidationUtil.isNotBlank(keyword)) {
            return examDAO.findAll();
        }
        return examDAO.search(keyword.trim());
    }

    public List<Exam> listAvailableForStudents() throws SQLException {
        return examDAO.findAvailableForStudents();
    }

    public Exam getById(long examId) throws SQLException {
        return examDAO.findById(examId);
    }

    private Result validate(String title, int durationMinutes, int totalMarks, int passingMarks,
                             Timestamp scheduledStart, Timestamp scheduledEnd) {
        if (!ValidationUtil.isNotBlank(title)) {
            return new Result(false, "Exam title is required.", null);
        }
        if (durationMinutes <= 0) {
            return new Result(false, "Duration must be greater than zero minutes.", null);
        }
        if (totalMarks <= 0) {
            return new Result(false, "Total marks must be greater than zero.", null);
        }
        if (passingMarks < 0 || passingMarks > totalMarks) {
            return new Result(false, "Passing marks must be between 0 and the total marks.", null);
        }
        if (scheduledStart != null && scheduledEnd != null && !scheduledEnd.after(scheduledStart)) {
            return new Result(false, "The scheduled end time must be after the start time.", null);
        }
        return null;
    }
}
