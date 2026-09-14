package com.aiexam.controller;

import com.aiexam.service.ProctoringService;
import com.aiexam.util.JsonUtil;
import com.aiexam.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

/**
 * AJAX endpoint that proctoring.js POSTs to whenever it detects a violation
 * (face missing, multiple faces, tab switch, fullscreen exit, etc). Always
 * responds 200 with a JSON body carrying the updated risk score so the
 * client can refresh its on-screen risk ring and react if auto-submitted.
 */
@WebServlet(name = "ProctoringEventServlet", urlPatterns = {"/proctoring/event"})
public class ProctoringEventServlet extends HttpServlet {

    private final ProctoringService proctoringService = new ProctoringService();

    private static final Set<String> VALID_EVENT_TYPES = Set.of(
            "FACE_MISSING", "MULTIPLE_FACES", "LOOKING_AWAY", "PHONE_DETECTED",
            "TAB_SWITCH", "WINDOW_BLUR", "FULLSCREEN_EXIT", "CAMERA_DISABLED",
            "MIC_DISABLED", "UNUSUAL_NOISE", "OTHER");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");

        String attemptIdParam = req.getParameter("attemptId");
        String eventType = req.getParameter("eventType");
        String details = req.getParameter("details");

        String result;
        if (attemptIdParam == null || eventType == null || !VALID_EVENT_TYPES.contains(eventType.toUpperCase())) {
            result = JsonUtil.object().put("success", false).put("message", "Invalid event.").toString();
        } else {
            try {
                long attemptId = Long.parseLong(attemptIdParam);
                String safeDetails = ValidationUtil.sanitize(details);
                ProctoringService.EventResult eventResult = proctoringService.logEvent(
                        attemptId, eventType.toUpperCase(), safeDetails, null, req.getRemoteAddr());

                result = JsonUtil.object()
                        .put("success", eventResult.success)
                        .put("riskScore", eventResult.newRiskScore)
                        .put("riskLevel", eventResult.riskLevel)
                        .put("violationCount", eventResult.violationCount)
                        .put("autoSubmitted", eventResult.autoSubmitted)
                        .put("message", eventResult.message)
                        .toString();
            } catch (NumberFormatException e) {
                result = JsonUtil.object().put("success", false).put("message", "Invalid attempt reference.").toString();
            }
        }

        try (PrintWriter out = resp.getWriter()) {
            out.write(result);
        }
    }
}
