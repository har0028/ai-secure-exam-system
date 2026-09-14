<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Student Dashboard &mdash; AI Secure Exam</title>
    <%@ include file="/common/head.jsp" %>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/dashboard.css">
</head>
<body>
<div class="app-shell">
    <%@ include file="/common/student-sidebar.jsp" %>

    <div class="app-main">
        <div class="app-topbar">
            <h1>My Dashboard</h1>
            <div class="top-actions">
                <button class="icon-btn"><i class="fa-solid fa-bell"></i><span class="dot"></span></button>
                <div class="topbar-user">
                    <div class="topbar-avatar">${sessionScope.fullName != null ? sessionScope.fullName.substring(0,1) : 'S'}</div>
                    <div>
                        <div class="uname">${sessionScope.fullName}</div>
                        <div class="urole">${student.rollNumber != null ? student.rollNumber : 'Student'}</div>
                    </div>
                </div>
            </div>
        </div>

        <div class="app-content">

            <div class="glass-light" style="padding:26px; margin-bottom:24px; background:#fff; border:1px solid #EEF1F8;">
                <h2 style="font-size:1.3rem;">Welcome back, ${sessionScope.fullName}.</h2>
                <p style="color:var(--color-text-muted); margin-top:8px; font-size:0.92rem;">
                    Your available exams, results, and notifications will appear below once
                    exam scheduling (Phase 2) and the exam engine (Phase 3) are wired up.
                </p>
            </div>

            <div class="stat-grid">
                <div class="card-stat">
                    <div class="stat-icon"><i class="fa-solid fa-file-pen"></i></div>
                    <div class="stat-label">Available Exams</div>
                    <div class="stat-value">${availableExams.size()}</div>
                </div>
                <div class="card-stat">
                    <div class="stat-icon"><i class="fa-solid fa-clock"></i></div>
                    <div class="stat-label">Proctored Exams</div>
                    <div class="stat-value">${proctoredExamsCount}</div>
                </div>
                <div class="card-stat">
                    <div class="stat-icon"><i class="fa-solid fa-circle-check"></i></div>
                    <div class="stat-label">Completed Exams</div>
                    <div class="stat-value text-mono" style="color:#94A3B8; font-size:1.1rem;">Phase 3</div>
                </div>
                <div class="card-stat">
                    <div class="stat-icon"><i class="fa-solid fa-chart-simple"></i></div>
                    <div class="stat-label">Results</div>
                    <div class="stat-value text-mono" style="color:#94A3B8; font-size:1.1rem;">Phase 3</div>
                </div>
            </div>

            <div class="dash-grid-2">
                <div class="panel">
                    <div class="panel-head">
                        <h3>Available Exams</h3>
                        <a href="<%=request.getContextPath()%>/student/exams">View all</a>
                    </div>
                    <c:choose>
                        <c:when test="${empty availableExams}">
                            <div class="empty-state">
                                <i class="fa-solid fa-file-circle-question"></i>
                                <p>No exams are currently scheduled. Check back soon.</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="ex" items="${availableExams}" varStatus="loop" end="4">
                                <div class="list-row">
                                    <div class="meta">
                                        <div class="dot-icon"><i class="fa-solid fa-file-pen"></i></div>
                                        <div>
                                            <div class="title">${ex.title}</div>
                                            <div class="subtitle">${ex.durationMinutes} min &middot; ${ex.totalMarks} marks</div>
                                        </div>
                                    </div>
                                    <span class="badge-status ${ex.status.toLowerCase()}">${ex.status}</span>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="panel">
                    <div class="panel-head"><h3>My Profile</h3></div>
                    <c:if test="${student != null}">
                        <div class="list-row"><div class="title">Roll Number</div><div class="subtitle text-mono">${student.rollNumber}</div></div>
                        <div class="list-row"><div class="title">Email</div><div class="subtitle text-mono">${student.email}</div></div>
                        <div class="list-row"><div class="title">Course</div><div class="subtitle">${student.course != null ? student.course : 'Not set'}</div></div>
                    </c:if>
                </div>
            </div>

        </div>
    </div>
</div>
<script src="<%=request.getContextPath()%>/assets/js/main.js"></script>
</body>
</html>
