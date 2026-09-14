<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Admin Dashboard &mdash; AI Secure Exam</title>
    <%@ include file="/common/head.jsp" %>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/dashboard.css">
</head>
<body>
<div class="app-shell">
    <%@ include file="/common/admin-sidebar.jsp" %>

    <div class="app-main">
        <div class="app-topbar">
            <h1>Dashboard</h1>
            <div class="top-actions">
                <button class="icon-btn"><i class="fa-solid fa-bell"></i><span class="dot"></span></button>
                <div class="topbar-user">
                    <div class="topbar-avatar">${sessionScope.fullName != null ? sessionScope.fullName.substring(0,1) : 'A'}</div>
                    <div>
                        <div class="uname">${sessionScope.fullName}</div>
                        <div class="urole">Administrator</div>
                    </div>
                </div>
            </div>
        </div>

        <div class="app-content">

            <div class="stat-grid">
                <div class="card-stat">
                    <div class="stat-icon"><i class="fa-solid fa-user-graduate"></i></div>
                    <div class="stat-label">Total Students</div>
                    <div class="stat-value">${totalStudents}</div>
                </div>
                <div class="card-stat">
                    <div class="stat-icon"><i class="fa-solid fa-file-pen"></i></div>
                    <div class="stat-label">Total Exams</div>
                    <div class="stat-value">${totalExams}</div>
                </div>
                <div class="card-stat">
                    <div class="stat-icon"><i class="fa-solid fa-list-check"></i></div>
                    <div class="stat-label">Total Questions</div>
                    <div class="stat-value">${totalQuestions}</div>
                </div>
                <div class="card-stat">
                    <div class="stat-icon"><i class="fa-solid fa-triangle-exclamation"></i></div>
                    <div class="stat-label">Total Violations</div>
                    <div class="stat-value">${totalViolations != null ? totalViolations : 0}</div>
                </div>
            </div>

            <div class="dash-grid-2">
                <div class="panel">
                    <div class="panel-head">
                        <h3>Recent Activity</h3>
                        <span style="font-size:0.78rem; color:var(--color-text-muted);">Live audit trail</span>
                    </div>

                    <c:if test="${empty recentActivity}">
                        <div class="empty-state">
                            <i class="fa-solid fa-inbox"></i>
                            <p>No activity recorded yet. Actions like logins and registrations will appear here.</p>
                        </div>
                    </c:if>

                    <c:forEach var="log" items="${recentActivity}">
                        <div class="list-row">
                            <div class="meta">
                                <div class="dot-icon">
                                    <c:choose>
                                        <c:when test="${log.action == 'LOGIN_SUCCESS'}"><i class="fa-solid fa-right-to-bracket"></i></c:when>
                                        <c:when test="${log.action == 'LOGIN_FAILED'}"><i class="fa-solid fa-triangle-exclamation"></i></c:when>
                                        <c:when test="${log.action == 'REGISTER'}"><i class="fa-solid fa-user-plus"></i></c:when>
                                        <c:when test="${log.action == 'LOGOUT'}"><i class="fa-solid fa-right-from-bracket"></i></c:when>
                                        <c:otherwise><i class="fa-solid fa-circle-info"></i></c:otherwise>
                                    </c:choose>
                                </div>
                                <div>
                                    <div class="title">${log.userFullName != null ? log.userFullName : 'System'}</div>
                                    <div class="subtitle">${log.description}</div>
                                </div>
                            </div>
                            <div class="subtitle text-mono"><fmt:formatDate value="${log.createdAt}" pattern="MMM d, h:mm a"/></div>
                        </div>
                    </c:forEach>
                </div>

                <div class="panel">
                    <div class="panel-head">
                        <h3>Live Risk Monitor</h3>
                    </div>
                    <div style="display:flex; flex-direction:column; align-items:center; padding:10px 0;">
                        <div class="risk-ring theme-light" data-score="0" style="margin-bottom:14px;"></div>
                        <p style="font-size:0.84rem; color:var(--color-text-muted); text-align:center; margin:0;">
                            Live per-candidate risk scoring activates once the AI proctoring
                            engine ships in Phase 4. This panel will show real-time scores
                            for every active exam attempt.
                        </p>
                    </div>
                </div>
            </div>

        </div>
    </div>
</div>
<script src="<%=request.getContextPath()%>/assets/js/main.js"></script>
</body>
</html>
