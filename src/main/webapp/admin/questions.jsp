<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Questions &mdash; AI Secure Exam</title>
    <%@ include file="/common/head.jsp" %>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/dashboard.css">
</head>
<body>
<div class="app-shell">
    <%@ include file="/common/admin-sidebar.jsp" %>

    <div class="app-main">
        <div class="app-topbar">
            <h1>Questions</h1>
            <c:if test="${not empty selectedExam}">
                <div class="top-actions">
                    <a href="<%=request.getContextPath()%>/admin/questions/bulk-upload?examId=${selectedExam.examId}" class="btn-secondary-outline">
                        <i class="fa-solid fa-file-arrow-up"></i> Bulk Upload
                    </a>
                    <a href="<%=request.getContextPath()%>/admin/questions/create?examId=${selectedExam.examId}" class="btn-primary-solid">
                        <i class="fa-solid fa-circle-plus"></i> Add Question
                    </a>
                </div>
            </c:if>
        </div>

        <div class="app-content">
            <%@ include file="/common/flash-messages.jsp" %>
            <c:if test="${not empty errorMessage}">
                <div class="alert-banner error">${errorMessage}</div>
            </c:if>

            <div class="panel" style="margin-bottom:20px;">
                <form method="get" action="<%=request.getContextPath()%>/admin/questions" style="display:flex; gap:14px; align-items:flex-end;">
                    <div class="form-group" style="flex:1;">
                        <label for="examId">Select Exam</label>
                        <select id="examId" name="examId" class="form-control-custom" onchange="this.form.submit()">
                            <option value="">-- Choose an exam to view its question bank --</option>
                            <c:forEach var="ex" items="${exams}">
                                <option value="${ex.examId}" ${selectedExam != null && selectedExam.examId == ex.examId ? 'selected' : ''}>
                                    ${ex.title} (${ex.questionCount} questions)
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </form>
            </div>

            <c:if test="${empty selectedExam}">
                <div class="panel">
                    <div class="empty-state">
                        <i class="fa-solid fa-list-check"></i>
                        <p>Choose an exam above to view, add, or bulk-upload its questions.</p>
                    </div>
                </div>
            </c:if>

            <c:if test="${not empty selectedExam}">
                <div class="panel">
                    <div class="panel-head">
                        <h3>${selectedExam.title}</h3>
                        <span style="font-size:0.82rem; color:var(--color-text-muted);">${questions.size()} question(s)</span>
                    </div>

                    <c:choose>
                        <c:when test="${empty questions}">
                            <div class="empty-state">
                                <i class="fa-solid fa-circle-question"></i>
                                <p>No questions yet. Add one manually or use bulk upload to import a CSV.</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>Question</th>
                                        <th>Type</th>
                                        <th>Correct</th>
                                        <th>Marks</th>
                                        <th>Difficulty</th>
                                        <th></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="q" items="${questions}">
                                        <tr>
                                            <td>
                                                <div class="cell-title">${q.questionText}</div>
                                                <div class="cell-subtitle">${not empty q.category ? q.category : 'Uncategorized'}</div>
                                            </td>
                                            <td class="cell-subtitle">${q.questionType}</td>
                                            <td class="text-mono">${q.correctOption}</td>
                                            <td class="text-mono">${q.marks}</td>
                                            <td class="cell-subtitle">${q.difficulty}</td>
                                            <td>
                                                <div class="cell-actions">
                                                    <a class="btn-sm btn-light" href="<%=request.getContextPath()%>/admin/questions/edit?id=${q.questionId}">
                                                        <i class="fa-solid fa-pen"></i> Edit
                                                    </a>
                                                    <form method="post" action="<%=request.getContextPath()%>/admin/questions/delete"
                                                          onsubmit="return confirm('Delete this question?');" style="display:inline;">
                                                        <input type="hidden" name="id" value="${q.questionId}">
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
            </c:if>
        </div>
    </div>
</div>
<script src="<%=request.getContextPath()%>/assets/js/main.js"></script>
</body>
</html>
