package com.aiexam.service;

import com.aiexam.dao.ExamAttemptDAO;
import com.aiexam.dao.ProctoringLogDAO;
import com.aiexam.dao.RiskAnalysisDAO;
import com.aiexam.model.ExamAttempt;
import com.aiexam.model.RiskAnalysis;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Central AI-proctoring risk engine. Every violation event detected by the
 * client-side proctoring JS (face-api.js, tab/visibility listeners,
 * fullscreen listeners, etc.) is POSTed to ProctoringEventServlet, which
 * delegates here. This class:
 *   1. Maps the event type to a severity + score impact (per the project's
 *      scoring table: face missing +10, tab switch +15, fullscreen exit
 *      +20, multiple faces +30, phone detected +40).
 *   2. Persists the raw event to proctoring_logs.
 *   3. Updates the attempt's aggregated risk_analysis row.
 *   4. Mirrors the running score onto exam_attempts for fast dashboard reads.
 *   5. If the score crosses the CRITICAL threshold (76+), auto-submits the
 *      exam and flags the attempt for admin review rather than accusing the
 *      candidate outright - the result still gets generated normally.
 */
public class ProctoringService {

    private static final int AUTO_SUBMIT_THRESHOLD = 76;

    private final ProctoringLogDAO logDAO = new ProctoringLogDAO();
    private final RiskAnalysisDAO riskAnalysisDAO = new RiskAnalysisDAO();
    private final ExamAttemptDAO attemptDAO = new ExamAttemptDAO();
    private final ExamAttemptService examAttemptService = new ExamAttemptService();

    private static final Map<String, Integer> SCORE_IMPACT = new HashMap<>();
    private static final Map<String, String> SEVERITY = new HashMap<>();

    static {
        SCORE_IMPACT.put("FACE_MISSING", 10);
        SCORE_IMPACT.put("LOOKING_AWAY", 10);
        SCORE_IMPACT.put("TAB_SWITCH", 15);
        SCORE_IMPACT.put("WINDOW_BLUR", 15);
        SCORE_IMPACT.put("CAMERA_DISABLED", 15);
        SCORE_IMPACT.put("MIC_DISABLED", 10);
        SCORE_IMPACT.put("UNUSUAL_NOISE", 10);
        SCORE_IMPACT.put("FULLSCREEN_EXIT", 20);
        SCORE_IMPACT.put("MULTIPLE_FACES", 30);
        SCORE_IMPACT.put("PHONE_DETECTED", 40);
        SCORE_IMPACT.put("OTHER", 5);

        SEVERITY.put("FACE_MISSING", "WARNING");
        SEVERITY.put("LOOKING_AWAY", "WARNING");
        SEVERITY.put("TAB_SWITCH", "WARNING");
        SEVERITY.put("WINDOW_BLUR", "WARNING");
        SEVERITY.put("CAMERA_DISABLED", "SERIOUS");
        SEVERITY.put("MIC_DISABLED", "WARNING");
        SEVERITY.put("UNUSUAL_NOISE", "INFO");
        SEVERITY.put("FULLSCREEN_EXIT", "SERIOUS");
        SEVERITY.put("MULTIPLE_FACES", "CRITICAL");
        SEVERITY.put("PHONE_DETECTED", "CRITICAL");
        SEVERITY.put("OTHER", "INFO");
    }

    public static class EventResult {
        public final boolean success;
        public final int newRiskScore;
        public final String riskLevel;
        public final int violationCount;
        public final boolean autoSubmitted;
        public final String message;

        public EventResult(boolean success, int newRiskScore, String riskLevel, int violationCount,
                            boolean autoSubmitted, String message) {
            this.success = success;
            this.newRiskScore = newRiskScore;
            this.riskLevel = riskLevel;
            this.violationCount = violationCount;
            this.autoSubmitted = autoSubmitted;
            this.message = message;
        }
    }

    public EventResult logEvent(long attemptId, String eventType, String details,
                                 String screenshotPath, String ip) {
        try {
            ExamAttempt attempt = attemptDAO.findById(attemptId);
            if (attempt == null || !"IN_PROGRESS".equals(attempt.getStatus())) {
                // Attempt already ended - ignore stray events from a closing tab, etc.
                return new EventResult(false, 0, "LOW", 0, false, "Attempt is not active.");
            }

            int scoreImpact = SCORE_IMPACT.getOrDefault(eventType, 5);
            String severity = SEVERITY.getOrDefault(eventType, "INFO");

            logDAO.insert(attemptId, eventType, severity, scoreImpact, screenshotPath, details);
            riskAnalysisDAO.recordEventAndRecompute(attemptId, eventType, scoreImpact);

            RiskAnalysis risk = riskAnalysisDAO.findByAttemptId(attemptId);
            int newScore = risk != null ? risk.getFinalRiskScore() : 0;
            String riskLevel = risk != null ? risk.getRiskLevel() : "LOW";
            int violationCount = attempt.getViolationCount() + 1;

            attemptDAO.updateRiskScore(attemptId, newScore, violationCount);

            boolean autoSubmitted = false;
            String message = "Violation logged.";

            if (newScore >= AUTO_SUBMIT_THRESHOLD) {
                riskAnalysisDAO.markUnderReview(attemptId);
                ExamAttemptService.SubmitResult submitResult =
                        examAttemptService.submitExam(attemptId, true, ip);
                autoSubmitted = submitResult.success;
                message = "Critical risk threshold reached. Exam has been automatically submitted " +
                          "and flagged for administrator review.";
            }

            return new EventResult(true, newScore, riskLevel, violationCount, autoSubmitted, message);

        } catch (SQLException e) {
            return new EventResult(false, 0, "LOW", 0, false, "A database error occurred while logging the event.");
        }
    }
}
