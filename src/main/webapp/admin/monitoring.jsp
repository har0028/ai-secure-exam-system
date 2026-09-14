<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Live Monitoring &mdash; AI Secure Exam</title>
    <%@ include file="/common/head.jsp" %>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/dashboard.css">
    <style>
        .monitor-card {
            background: var(--gradient-deep);
            border: 1px solid var(--glass-border-dark);
            border-radius: var(--radius-md);
            padding: 20px;
            color: var(--color-text-on-dark);
        }
        .monitor-card .student-name { font-weight: 700; font-size: 0.95rem; color: #fff; }
        .monitor-card .exam-name { font-size: 0.78rem; color: var(--color-text-on-dark-muted); margin-top: 2px; }
        .monitor-grid-cards {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
            gap: 16px;
            margin-bottom: 28px;
        }
        .violation-row {
            display: flex;
            align-items: flex-start;
            gap: 12px;
            padding: 12px 0;
            border-bottom: 1px solid #EEF1F8;
        }
        .violation-row:last-child { border-bottom: none; }
        .severity-dot {
            width: 10px; height: 10px; border-radius: 50%; flex-shrink: 0; margin-top: 5px;
        }
        .severity-dot.warning { background: var(--color-risk-medium); }
        .severity-dot.serious { background: var(--color-risk-high); }
        .severity-dot.critical { background: var(--color-risk-critical); }
        .severity-dot.info { background: #94A3B8; }
        .live-refresh { font-size: 0.76rem; color: var(--color-text-muted); }
    </style>
</head>
<body>
<div class="app-shell">
    <%@ include file="/common/admin-sidebar.jsp" %>
    <div class="app-main">
        <div class="app-topbar">
            <div style="display:flex; align-items:center;">
                <button class="sidebar-toggle icon-btn" aria-label="Toggle Navigation"><i class="fa-solid fa-bars"></i></button>
                <h1>Live Monitoring</h1>
            </div>
            <div class="top-actions">
                <span class="live-dot" style="color:var(--color-cyan); font-size:0.8rem; font-weight:600; display:flex; align-items:center; gap:6px;">
                    <span style="width:7px;height:7px;border-radius:50%;background:var(--color-cyan);display:inline-block; animation:pulseDot 1.6s infinite;"></span>
                    LIVE
                </span>
                <button onclick="location.reload()" class="btn-secondary-outline">
                    <i class="fa-solid fa-rotate"></i> Refresh
                </button>
            </div>
        </div>

        <div class="app-content">

            <!-- Active Candidates -->
            <div style="margin-bottom: 8px; display:flex; align-items:center; justify-content:space-between;">
                <h3 style="font-size:1rem; margin:0;">Active Exam Sessions</h3>
                <span class="live-refresh">Data as of <fmt:formatDate value="<%=new java.util.Date()%>" pattern="h:mm:ss a"/></span>
            </div>

            <c:choose>
                <c:when test="${empty activeAttempts}">
                    <div class="panel" style="margin-bottom:24px;">
                        <div class="empty-state">
                            <i class="fa-solid fa-users"></i>
                            <p>No exams are currently in progress. This feed will populate when students begin exams.</p>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="monitor-grid-cards">
                        <c:forEach var="a" items="${activeAttempts}">
                            <div class="monitor-card">
                                <div style="display:flex; justify-content:space-between; align-items:flex-start; margin-bottom:14px;">
                                    <div>
                                        <div class="student-name">${a.studentName}</div>
                                        <div class="exam-name">${a.examTitle}</div>
                                        <div style="font-size:0.72rem; color:var(--color-text-on-dark-muted); margin-top:4px; font-family:var(--font-mono);">${a.studentRollNumber}</div>
                                    </div>
                                    <div class="risk-ring" data-score="${a.currentRiskScore}" style="width:72px; height:72px;"></div>
                                </div>
                                <div style="display:flex; justify-content:space-between; font-size:0.8rem; color:var(--color-text-on-dark-muted); border-top:1px solid var(--glass-border-dark); padding-top:12px;">
                                    <span><i class="fa-solid fa-triangle-exclamation" style="color:var(--color-risk-medium); margin-right:4px;"></i>${a.violationCount} violations</span>
                                    <span><i class="fa-solid fa-clock" style="margin-right:4px;"></i>
                                        <fmt:formatDate value="${a.startTime}" pattern="h:mm a"/>
                                    </span>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>

            <!-- Violation Feed -->
            <div class="panel">
                <div class="panel-head">
                    <h3>Live Violation Feed</h3>
                    <span style="font-size:0.78rem; color:var(--color-text-muted);">Last 20 events from active sessions</span>
                </div>
                <c:choose>
                    <c:when test="${empty violationFeed}">
                        <div class="empty-state">
                            <i class="fa-solid fa-shield-check" style="color:var(--color-risk-low);"></i>
                            <p>No violations detected in active sessions. All clear.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="log" items="${violationFeed}">
                            <div class="violation-row">
                                <span class="severity-dot ${log.severity.toLowerCase()}"></span>
                                <div style="flex:1;">
                                    <div style="font-size:0.87rem; font-weight:600;">${log.studentName} &mdash; <span class="text-mono" style="font-size:0.8rem;">${log.eventType}</span></div>
                                    <div style="font-size:0.78rem; color:var(--color-text-muted);">${log.examTitle} &middot; ${not empty log.details ? log.details : ''}</div>
                                </div>
                                <div style="font-size:0.75rem; color:var(--color-text-muted); white-space:nowrap; margin-left:8px;">
                                    <fmt:formatDate value="${log.occurredAt}" pattern="h:mm:ss a"/>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>
<script src="<%=request.getContextPath()%>/assets/js/main.js"></script>
<script>
    // Auto-refresh every 30 seconds for live monitoring
    setTimeout(function () { location.reload(); }, 30000);
</script>
</body>
</html>
