<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>My Results &mdash; AI Secure Exam</title>
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
                <h1>My Results</h1>
            </div>
        </div>
        <div class="app-content">
            <%@ include file="/common/flash-messages.jsp" %>
            <c:if test="${not empty errorMessage}"><div class="alert-banner error">${errorMessage}</div></c:if>
            <div class="panel">
                <c:choose>
                    <c:when test="${empty results}">
                        <div class="empty-state">
                            <i class="fa-solid fa-chart-simple"></i>
                            <p>You haven't completed any exams yet. Take an exam to see your results here.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <table class="data-table">
                            <thead><tr><th>Exam</th><th>Score</th><th>Percentage</th><th>Status</th><th>Date</th><th></th></tr></thead>
                            <tbody>
                            <c:forEach var="r" items="${results}">
                                <tr>
                                    <td><div class="cell-title">${r.examTitle}</div></td>
                                    <td class="text-mono">${r.obtainedMarks}/${r.totalMarks}</td>
                                    <td class="text-mono">${r.percentage}%</td>
                                    <td><span class="badge-status ${r.passStatus == 'PASS' ? 'active' : 'cancelled'}">${r.passStatus}</span></td>
                                    <td class="cell-subtitle"><fmt:formatDate value="${r.generatedAt}" pattern="MMM d, yyyy"/></td>
                                    <td><a class="btn-sm btn-light" href="<%=request.getContextPath()%>/student/exam/result?attemptId=${r.attemptId}"><i class="fa-solid fa-eye"></i> View</a></td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>
<script src="<%=request.getContextPath()%>/assets/js/main.js"></script>
</body>
</html>
