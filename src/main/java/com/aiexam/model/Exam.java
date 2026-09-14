package com.aiexam.model;

import java.sql.Timestamp;

public class Exam {

    private long examId;
    private String title;
    private String description;
    private String category;
    private int durationMinutes;
    private int totalMarks;
    private int passingMarks;
    private Timestamp scheduledStart;
    private Timestamp scheduledEnd;
    private boolean shuffleQuestions;
    private boolean shuffleOptions;
    private boolean proctoringEnabled;
    private String status; // DRAFT | SCHEDULED | ACTIVE | COMPLETED | CANCELLED
    private long createdBy;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Convenience field, not a DB column - populated by DAO when needed
    private int questionCount;

    public Exam() {
    }

    public long getExamId() {
        return examId;
    }

    public void setExamId(long examId) {
        this.examId = examId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public int getPassingMarks() {
        return passingMarks;
    }

    public void setPassingMarks(int passingMarks) {
        this.passingMarks = passingMarks;
    }

    public Timestamp getScheduledStart() {
        return scheduledStart;
    }

    public void setScheduledStart(Timestamp scheduledStart) {
        this.scheduledStart = scheduledStart;
    }

    public Timestamp getScheduledEnd() {
        return scheduledEnd;
    }

    public void setScheduledEnd(Timestamp scheduledEnd) {
        this.scheduledEnd = scheduledEnd;
    }

    public boolean isShuffleQuestions() {
        return shuffleQuestions;
    }

    public void setShuffleQuestions(boolean shuffleQuestions) {
        this.shuffleQuestions = shuffleQuestions;
    }

    public boolean isShuffleOptions() {
        return shuffleOptions;
    }

    public void setShuffleOptions(boolean shuffleOptions) {
        this.shuffleOptions = shuffleOptions;
    }

    public boolean isProctoringEnabled() {
        return proctoringEnabled;
    }

    public void setProctoringEnabled(boolean proctoringEnabled) {
        this.proctoringEnabled = proctoringEnabled;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }
}
