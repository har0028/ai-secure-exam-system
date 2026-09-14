package com.aiexam.model;

import java.sql.Timestamp;

public class ProctoringLog {

    private long logId;
    private long attemptId;
    private String eventType; // FACE_MISSING | MULTIPLE_FACES | LOOKING_AWAY | PHONE_DETECTED |
                               // TAB_SWITCH | WINDOW_BLUR | FULLSCREEN_EXIT | CAMERA_DISABLED |
                               // MIC_DISABLED | UNUSUAL_NOISE | OTHER
    private String severity; // INFO | WARNING | SERIOUS | CRITICAL
    private int scoreImpact;
    private String screenshotPath;
    private String details;
    private Timestamp occurredAt;

    // Convenience fields populated via JOIN for the live monitoring feed
    private String studentName;
    private String examTitle;

    public ProctoringLog() {
    }

    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(long attemptId) {
        this.attemptId = attemptId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public int getScoreImpact() {
        return scoreImpact;
    }

    public void setScoreImpact(int scoreImpact) {
        this.scoreImpact = scoreImpact;
    }

    public String getScreenshotPath() {
        return screenshotPath;
    }

    public void setScreenshotPath(String screenshotPath) {
        this.screenshotPath = screenshotPath;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Timestamp getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Timestamp occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getExamTitle() {
        return examTitle;
    }

    public void setExamTitle(String examTitle) {
        this.examTitle = examTitle;
    }
}
