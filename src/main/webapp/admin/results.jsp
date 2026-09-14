<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Results &mdash; AI Secure Exam</title>
    <%@ include file="/common/head.jsp" %>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/dashboard.css">
</head>
<body>
<div class="app-shell">
    <%@ include file="/common/admin-sidebar.jsp" %>
    <div class="app-main">
        <div class="app-topbar">
            <h1>Results</h1>
        </div>
        <div class="app-content">
            <%@ include file="/common/flash-messages.jsp" %>
            <c:if test="${not empty errorMessage}"><div class="alert-banner error">${errorMessage}</div></c:if>
            <div class="panel">
                <div class="table-toolbar">
                    <form class="search-box" method="get" action="<%=request.getContextPath()%>/admin/results">
                        <i class="fa-solid fa-magnifying-glass"></i>
                        <input type="text" name="q" placeholder="Search by student, roll number, or exam" value="${searchKeyword}">
                    </form>
                    <span style="font-size:0.82rem; color:var(--color-text-muted);">${results.size()} result(s)</span>
                </div>
                <c:choose>
                    <c:when test="${empty results}">
                        <div class="empty-state">
                            <i class="fa-solid fa-clipboard-list"></i>
                            <p>No results yet. Results are generated automatically when students submit exams.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Student</th>
                                    <th>Exam</th>
                                    <th>Score</th>
                                    <th>Percentage</th>
                                    <th>Status</th>
                                    <th>Review</th>
                                    <th>Date</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="r" items="${results}">
                                    <tr>
                                        <td>
                                            <div class="cell-title">${r.studentName}</div>
                                            <div class="cell-subtitle text-mono">${r.studentRollNumber}</div>
                                        </td>
                                        <td><div class="cell-title">${r.examTitle}</div></td>
                                        <td class="text-mono">${r.obtainedMarks}/${r.totalMarks}</td>
                                        <td class="text-mono">${r.percentage}%</td>
                                        <td>
                                            <span class="badge-status ${r.passStatus == 'PASS' ? 'active' : 'cancelled'}">
                                                ${r.passStatus}
                                            </span>
                                        </td>
                                        <td>
                                            <c:if test="${not empty r.reviewStatus && r.reviewStatus != 'CLEAR'}">
                                                <span class="badge-risk ${r.reviewStatus == 'UNDER_REVIEW' ? 'high' : 'low'}">
                                                    ${r.reviewStatus}
                                                </span>
                                            </c:if>
                                            <c:if test="${empty r.reviewStatus || r.reviewStatus == 'CLEAR'}">
                                                <span class="badge-risk low">CLEAR</span>
                                            </c:if>
                                        </td>
                                        <td class="cell-subtitle">
                                            <fmt:formatDate value="${r.generatedAt}" pattern="MMM d, yyyy"/>
                                        </td>
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
