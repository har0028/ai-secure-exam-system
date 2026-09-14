<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Exams &mdash; AI Secure Exam</title>
    <%@ include file="/common/head.jsp" %>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/dashboard.css">
</head>
<body>
<div class="app-shell">
    <%@ include file="/common/admin-sidebar.jsp" %>

    <div class="app-main">
        <div class="app-topbar">
            <h1>Exams</h1>
            <div class="top-actions">
                <a href="<%=request.getContextPath()%>/admin/exams/create" class="btn-primary-solid">
                    <i class="fa-solid fa-circle-plus"></i> Create Exam
                </a>
            </div>
        </div>

        <div class="app-content">
            <%@ include file="/common/flash-messages.jsp" %>
            <c:if test="${not empty errorMessage}">
                <div class="alert-banner error">${errorMessage}</div>
            </c:if>

            <div class="panel">
                <div class="table-toolbar">
                    <form class="search-box" method="get" action="<%=request.getContextPath()%>/admin/exams">
                        <i class="fa-solid fa-magnifying-glass"></i>
                        <input type="text" name="q" placeholder="Search by title or category" value="${searchKeyword}">
                    </form>
                    <span style="font-size:0.82rem; color:var(--color-text-muted);">${exams.size()} exam(s)</span>
                </div>

                <c:choose>
                    <c:when test="${empty exams}">
                        <div class="empty-state">
                            <i class="fa-solid fa-file-pen"></i>
                            <p>No exams found. Create your first exam to start building question banks.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Title</th>
                                    <th>Duration</th>
                                    <th>Marks</th>
                                    <th>Questions</th>
                                    <th>Status</th>
                                    <th>Scheduled</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="e" items="${exams}">
                                    <tr>
                                        <td>
                                            <div class="cell-title">${e.title}</div>
                                            <div class="cell-subtitle">${not empty e.category ? e.category : 'Uncategorized'}</div>
                                        </td>
                                        <td class="text-mono">${e.durationMinutes} min</td>
                                        <td class="text-mono">${e.passingMarks}/${e.totalMarks}</td>
                                        <td>
                                            <a href="<%=request.getContextPath()%>/admin/questions?examId=${e.examId}"
                                               style="color:var(--color-indigo); font-weight:600; font-size:0.85rem;">
                                                ${e.questionCount} question(s)
                                            </a>
                                        </td>
                                        <td><span class="badge-status ${e.status.toLowerCase()}">${e.status}</span></td>
                                        <td class="cell-subtitle">
                                            <c:if test="${not empty e.scheduledStart}">
                                                <fmt:formatDate value="${e.scheduledStart}" pattern="MMM d, h:mm a"/>
                                            </c:if>
                                            <c:if test="${empty e.scheduledStart}">Not scheduled</c:if>
                                        </td>
                                        <td>
                                            <div class="cell-actions">
                                                <a class="btn-sm btn-light" href="<%=request.getContextPath()%>/admin/exams/edit?id=${e.examId}">
                                                    <i class="fa-solid fa-pen"></i> Edit
                                                </a>
                                                <form method="post" action="<%=request.getContextPath()%>/admin/exams/delete"
                                                      onsubmit="return confirm('Delete this exam? All its questions and attempts will also be deleted.');" style="display:inline;">
                                                    <input type="hidden" name="id" value="${e.examId}">
                                                    <button type="submit" class="btn-sm btn-danger">
                                                        <i class="fa-solid fa-trash"></i> Delete
                                                    </button>
                                                </form>
                                            </div>
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
