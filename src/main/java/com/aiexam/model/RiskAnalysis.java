package com.aiexam.model;

import java.sql.Timestamp;

public class RiskAnalysis {

    private long riskId;
    private long attemptId;
    private int faceMissingCount;
    private int multipleFaceCount;
    private int tabSwitchCount;
    private int fullscreenExitCount;
    private int phoneDetectCount;
    private int finalRiskScore;
    private String riskLevel; // LOW | MEDIUM | HIGH | CRITICAL
    private String reviewStatus; // CLEAR | UNDER_REVIEW | REVIEWED
    private Timestamp updatedAt;

    public RiskAnalysis() {
    }

    public long getRiskId() {
        return riskId;
    }

    public void setRiskId(long riskId) {
        this.riskId = riskId;
    }

    public long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(long attemptId) {
        this.attemptId = attemptId;
    }

    public int getFaceMissingCount() {
        return faceMissingCount;
    }

    public void setFaceMissingCount(int faceMissingCount) {
        this.faceMissingCount = faceMissingCount;
    }

    public int getMultipleFaceCount() {
        return multipleFaceCount;
    }

    public void setMultipleFaceCount(int multipleFaceCount) {
        this.multipleFaceCount = multipleFaceCount;
    }

    public int getTabSwitchCount() {
        return tabSwitchCount;
    }

    public void setTabSwitchCount(int tabSwitchCount) {
        this.tabSwitchCount = tabSwitchCount;
    }

    public int getFullscreenExitCount() {
        return fullscreenExitCount;
    }

    public void setFullscreenExitCount(int fullscreenExitCount) {
        this.fullscreenExitCount = fullscreenExitCount;
    }

    public int getPhoneDetectCount() {
        return phoneDetectCount;
    }

    public void setPhoneDetectCount(int phoneDetectCount) {
        this.phoneDetectCount = phoneDetectCount;
    }

    public int getFinalRiskScore() {
        return finalRiskScore;
    }

    public void setFinalRiskScore(int finalRiskScore) {
        this.finalRiskScore = finalRiskScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
