<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Exam Instructions &mdash; ${exam.title}</title>
    <%@ include file="/common/head.jsp" %>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/dashboard.css">
</head>
<body>
<div class="app-shell">
    <%@ include file="/common/student-sidebar.jsp" %>
    <div class="app-main">
        <div class="app-topbar">
            <div style="display:flex; align-items:center;">
                <button class="sidebar-toggle icon-btn" aria-label="Toggle Navigation"><i class="fa-solid fa-bars"></i></button>
                <h1>Exam Instructions</h1>
            </div>
        </div>
        <div class="app-content">
            <div class="panel" style="max-width:720px;">
                <h2 style="font-size:1.4rem; margin-bottom:6px;">${exam.title}</h2>
                <p style="color:var(--color-text-muted); font-size:0.88rem; margin-bottom:24px;">${exam.description}</p>

                <div class="form-grid">
                    <div class="card-stat"><div class="stat-label">Duration</div><div class="stat-value text-mono">${exam.durationMinutes} min</div></div>
                    <div class="card-stat"><div class="stat-label">Total Marks</div><div class="stat-value text-mono">${exam.totalMarks}</div></div>
                    <div class="card-stat"><div class="stat-label">Passing Marks</div><div class="stat-value text-mono">${exam.passingMarks}</div></div>
                    <div class="card-stat"><div class="stat-label">Questions</div><div class="stat-value text-mono">${exam.questionCount}</div></div>
                </div>

                <div style="background:#F8FAFC; border-radius:12px; padding:20px; margin:24px 0; border:1px solid #EEF1F8;">
                    <h4 style="margin:0 0 12px; font-size:0.95rem;"><i class="fa-solid fa-circle-info" style="color:var(--color-indigo);"></i> Read Before Starting</h4>
                    <ul style="margin:0; padding-left:20px; font-size:0.87rem; color:var(--color-text-muted); line-height:2;">
                        <li>The exam will open in <strong>fullscreen</strong> mode. Do not exit fullscreen during the exam.</li>
                        <li>Do not switch browser tabs or minimize the window at any time.</li>
                        <li>Your <strong>webcam and microphone</strong> must be enabled throughout the exam.</li>
                        <li>Ensure your face is clearly visible in front of the camera at all times.</li>
                        <li>No other person should be visible in the camera frame.</li>
                        <li>The exam timer will auto-submit when time expires.</li>
                        <li>Answers are auto-saved every 30 seconds. Use the Save button to save immediately.</li>
                        <c:if test="${exam.shuffleQuestions}"><li>Questions are presented in randomized order for each candidate.</li></c:if>
                    </ul>
                </div>

                <c:if test="${exam.proctoringEnabled}">
                    <div style="background:rgba(244,63,94,0.06); border:1px solid rgba(244,63,94,0.2); border-radius:12px; padding:16px; margin-bottom:24px;">
                        <h4 style="margin:0 0 8px; color:#BE123C; font-size:0.9rem;"><i class="fa-solid fa-shield-halved"></i> AI Proctoring is Active</h4>
                        <p style="margin:0; font-size:0.84rem; color:#BE123C;">This exam is monitored by our AI proctoring system. Violation events (missing face, tab switching, multiple faces, etc.) are logged and affect your risk score. Exceeding the critical threshold will auto-submit your exam for review.</p>
                    </div>
                </c:if>

                <div style="display:flex; gap:14px;">
                    <form action="<%=request.getContextPath()%>/student/exam/start" method="post" style="flex:1;">
                        <input type="hidden" name="examId" value="${exam.examId}">
                        <button type="submit" class="btn-primary-solid btn-block" style="width:100%;">
                            <i class="fa-solid fa-play"></i> Begin Exam Now
                        </button>
                    </form>
                    <a href="<%=request.getContextPath()%>/student/exams" class="btn-secondary-outline">Cancel</a>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="<%=request.getContextPath()%>/assets/js/main.js"></script>
</body>
</html>
