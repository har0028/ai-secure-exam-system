<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Available Exams &mdash; AI Secure Exam</title>
    <%@ include file="/common/head.jsp" %>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/dashboard.css">
</head>
<body>
<div class="app-shell">
    <%@ include file="/common/student-sidebar.jsp" %>

    <div class="app-main">
        <div class="app-topbar">
            <h1>Available Exams</h1>
            <div class="top-actions">
                <div class="topbar-user">
                    <div class="topbar-avatar">${sessionScope.fullName != null ? sessionScope.fullName.substring(0,1) : 'S'}</div>
                    <div>
                        <div class="uname">${sessionScope.fullName}</div>
                        <div class="urole">Student</div>
                    </div>
                </div>
            </div>
        </div>

        <div class="app-content">
            <c:if test="${not empty errorMessage}">
                <div class="alert-banner error">${errorMessage}</div>
            </c:if>

            <c:choose>
                <c:when test="${empty availableExams}">
                    <div class="panel">
                        <div class="empty-state">
                            <i class="fa-solid fa-file-circle-question"></i>
                            <p>No exams are currently scheduled. Check back soon.</p>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="stat-grid" style="grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));">
                        <c:forEach var="ex" items="${availableExams}">
                            <div class="panel">
                                <div style="display:flex; justify-content:space-between; align-items:flex-start; margin-bottom:14px;">
                                    <h3 style="font-size:1.02rem; max-width:75%;">${ex.title}</h3>
                                    <span class="badge-status ${ex.status.toLowerCase()}">${ex.status}</span>
                                </div>
                                <p style="font-size:0.84rem; color:var(--color-text-muted); margin:0 0 16px; line-height:1.5;">
                                    ${not empty ex.description ? ex.description : 'No description provided.'}
                                </p>
                                <div class="list-row">
                                    <div class="title">Duration</div>
                                    <div class="subtitle text-mono">${ex.durationMinutes} min</div>
                                </div>
                                <div class="list-row">
                                    <div class="title">Total Marks</div>
                                    <div class="subtitle text-mono">${ex.totalMarks}</div>
                                </div>
                                <div class="list-row">
                                    <div class="title">Passing Marks</div>
                                    <div class="subtitle text-mono">${ex.passingMarks}</div>
                                </div>
                                <c:if test="${not empty ex.scheduledStart}">
                                    <div class="list-row">
                                        <div class="title">Opens</div>
                                        <div class="subtitle text-mono"><fmt:formatDate value="${ex.scheduledStart}" pattern="MMM d, h:mm a"/></div>
                                    </div>
                                </c:if>
                                <c:if test="${ex.proctoringEnabled}">
                                    <div class="list-row">
                                        <div class="title"><i class="fa-solid fa-shield-halved" style="color:var(--color-indigo); margin-right:6px;"></i>AI Proctored</div>
                                        <div class="subtitle">Webcam required</div>
                                    </div>
                                </c:if>
                                <div style="margin-top:18px;">
                                    <button class="btn-primary-solid btn-block" style="opacity:0.6; cursor:not-allowed;" disabled
                                            title="The exam engine ships in Phase 3">
                                        <i class="fa-solid fa-play"></i> Start Exam (Phase 3)
                                    </button>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>
<script src="<%=request.getContextPath()%>/assets/js/main.js"></script>
</body>
</html>
