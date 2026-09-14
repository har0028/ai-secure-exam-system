<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>${exam.title} &mdash; AI Secure Exam</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Sora:wght@600;700&family=Inter:wght@400;500;600&family=JetBrains+Mono:wght@500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/exam.css">
    <script>
        window.EXAM_ATTEMPT_ID = ${attempt.attemptId};
        window.EXAM_DURATION_MINUTES = ${exam.durationMinutes};
        window.EXAM_START_TIME_MS = ${attempt.startTime.time};
        window.CONTEXT_PATH = '<%=request.getContextPath()%>';
        window.EXISTING_ANSWERS = ${existingAnswersJson};
    </script>
</head>
<body class="exam-body">

<!-- Fullscreen warning overlay -->
<div id="fullscreen-warning" style="display:none; position:fixed; inset:0; background:rgba(11,17,48,0.92); z-index:9999; align-items:center; justify-content:center; flex-direction:column; gap:20px;">
    <i class="fa-solid fa-expand" style="font-size:3rem; color:var(--color-cyan);"></i>
    <h2 style="color:#fff; margin:0;">Fullscreen Required</h2>
    <p style="color:var(--color-text-on-dark-muted); text-align:center; max-width:420px;">You exited fullscreen mode. This violation has been logged. Click below to return to fullscreen and continue your exam.</p>
    <button onclick="returnToFullscreen()" class="btn-gradient">Return to Fullscreen</button>
</div>

<!-- Proctoring warning overlay -->
<div id="proctoring-warning" style="display:none; position:fixed; top:20px; left:50%; transform:translateX(-50%); background:#BE123C; color:#fff; padding:14px 24px; border-radius:12px; z-index:9000; font-weight:600; font-size:0.9rem; max-width:480px; text-align:center; box-shadow:0 8px 30px rgba(0,0,0,0.4);">
    <i class="fa-solid fa-triangle-exclamation" style="margin-right:8px;"></i>
    <span id="proctoring-warning-text">Violation detected.</span>
</div>

<div class="exam-shell">
    <!-- Top bar -->
    <div class="exam-topbar">
        <div class="exam-brand">
            <i class="fa-solid fa-shield-halved"></i> AI Secure Exam
        </div>
        <div class="exam-meta">
            <span class="exam-title">${exam.title}</span>
            <span class="candidate-name">${sessionScope.fullName}</span>
        </div>
        <div class="exam-controls">
            <span id="save-indicator" class="save-indicator">Auto-save active</span>
            <div class="timer-box">
                <i class="fa-solid fa-clock"></i>
                <span id="exam-timer" class="text-mono">--:--</span>
            </div>
        </div>
    </div>

    <div class="exam-body-grid">
        <!-- Left: Questions -->
        <div class="exam-question-area">
            <c:forEach var="q" items="${questions}" varStatus="loop">
            <div class="question-card" data-question-id="${q.questionId}" style="display:none;">
                <div class="question-header">
                    <span class="q-num">Question ${loop.index + 1} of ${questions.size()}</span>
                    <span class="q-marks">${q.marks} mark(s)</span>
                    <span class="badge-difficulty ${q.difficulty.toLowerCase()}">${q.difficulty}</span>
                </div>
                <div class="question-text">${q.questionText}</div>
                <div class="options-grid">
                    <c:if test="${not empty q.optionA}">
                    <label class="option-label">
                        <input type="radio" name="answer_${q.questionId}" value="A">
                        <span class="option-letter">A</span>
                        <span class="option-text">${q.optionA}</span>
                    </label>
                    </c:if>
                    <c:if test="${not empty q.optionB}">
                    <label class="option-label">
                        <input type="radio" name="answer_${q.questionId}" value="B">
                        <span class="option-letter">B</span>
                        <span class="option-text">${q.optionB}</span>
                    </label>
                    </c:if>
                    <c:if test="${not empty q.optionC}">
                    <label class="option-label">
                        <input type="radio" name="answer_${q.questionId}" value="C">
                        <span class="option-letter">C</span>
                        <span class="option-text">${q.optionC}</span>
                    </label>
                    </c:if>
                    <c:if test="${not empty q.optionD}">
                    <label class="option-label">
                        <input type="radio" name="answer_${q.questionId}" value="D">
                        <span class="option-letter">D</span>
                        <span class="option-text">${q.optionD}</span>
                    </label>
                    </c:if>
                </div>
                <div class="question-actions">
                    <button id="btn-mark" class="btn-sm btn-light">&#9734; Mark for Review</button>
                    <div style="display:flex; gap:10px;">
                        <button id="btn-prev" class="btn-sm btn-light"><i class="fa-solid fa-arrow-left"></i> Prev</button>
                        <button id="btn-next" class="btn-sm btn-primary">Next <i class="fa-solid fa-arrow-right"></i></button>
                    </div>
                </div>
            </div>
            </c:forEach>
        </div>

        <!-- Right: Sidebar (palette + webcam + risk) -->
        <div class="exam-sidebar">
            <c:if test="${exam.proctoringEnabled}">
            <div class="webcam-panel">
                <div style="font-size:0.75rem; color:var(--color-text-on-dark-muted); margin-bottom:8px;">
                    <span class="live-dot">WEBCAM LIVE</span>
                </div>
                <video id="proctoring-video" autoplay muted playsinline class="webcam-feed"></video>
                <canvas id="proctoring-canvas" style="display:none;"></canvas>
                <div style="margin-top:12px; text-align:center;">
                    <span style="font-size:0.74rem; color:var(--color-text-on-dark-muted);">Risk Score</span>
                    <div id="risk-ring-container" class="risk-ring" data-score="0" style="margin:8px auto;"></div>
                </div>
            </div>
            </c:if>

            <div class="palette-panel">
                <div style="font-size:0.78rem; font-weight:700; color:var(--color-text-on-dark-muted); margin-bottom:10px; text-transform:uppercase; letter-spacing:0.05em;">Question Navigator</div>
                <div id="question-palette" class="question-palette"></div>
                <div class="palette-legend">
                    <span class="legend-item"><span class="dot answered"></span>Answered</span>
                    <span class="legend-item"><span class="dot marked"></span>Marked</span>
                    <span class="legend-item"><span class="dot"></span>Not visited</span>
                </div>
            </div>

            <div style="padding:16px; border-top:1px solid var(--glass-border-dark); margin-top:auto;">
                <button id="btn-submit" class="btn-gradient btn-block">
                    <i class="fa-solid fa-paper-plane"></i> Submit Exam
                </button>
            </div>
        </div>
    </div>
</div>

<!-- Hidden submit form -->
<form id="submit-form" action="<%=request.getContextPath()%>/student/exam/submit" method="post" style="display:none;">
    <input type="hidden" name="attemptId" value="${attempt.attemptId}">
</form>

<script src="<%=request.getContextPath()%>/assets/js/main.js"></script>
<script src="https://cdn.jsdelivr.net/npm/face-api.js@0.22.2/dist/face-api.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/js/proctoring.js"></script>
<script src="<%=request.getContextPath()%>/assets/js/exam-engine.js"></script>
</body>
</html>
