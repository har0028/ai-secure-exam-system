<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Settings &mdash; AI Secure Exam</title>
    <%@ include file="/common/head.jsp" %>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/dashboard.css">
</head>
<body>
<div class="app-shell">
    <%@ include file="/common/admin-sidebar.jsp" %>
    <div class="app-main">
        <div class="app-topbar">
            <div style="display:flex; align-items:center;">
                <button class="sidebar-toggle icon-btn" aria-label="Toggle Navigation"><i class="fa-solid fa-bars"></i></button>
                <h1>Settings</h1>
            </div>
        </div>
        <div class="app-content">
            <div class="panel" style="max-width:560px;">
                <div class="panel-head"><h3>System Configuration</h3></div>
                <div class="list-row"><div class="title">Application Version</div><div class="subtitle text-mono">1.0.0</div></div>
                <div class="list-row"><div class="title">Database</div><div class="subtitle text-mono">ai_secure_exam_system</div></div>
                <div class="list-row"><div class="title">Session Timeout</div><div class="subtitle text-mono">30 minutes</div></div>
                <div class="list-row"><div class="title">Risk Threshold (Auto-Submit)</div><div class="subtitle text-mono">Score &ge; 76</div></div>
                <div class="list-row"><div class="title">Auto-Save Interval</div><div class="subtitle text-mono">30 seconds</div></div>
                <div class="list-row"><div class="title">Face Detection Sample Rate</div><div class="subtitle text-mono">Every 3 seconds</div></div>
                <div class="list-row"><div class="title">Admin Account</div><div class="subtitle">${sessionScope.fullName} (${sessionScope.email})</div></div>
                <p style="font-size:0.82rem; color:var(--color-text-muted); margin-top:16px;">
                    System-level settings (SMTP for password-reset emails, storage paths for screen recordings, etc.) are configured via
                    <code>src/main/resources/db.properties</code> and Tomcat context.xml. See the README for full deployment guidance.
                </p>
            </div>
        </div>
    </div>
</div>
<script src="<%=request.getContextPath()%>/assets/js/main.js"></script>
</body>
</html>
