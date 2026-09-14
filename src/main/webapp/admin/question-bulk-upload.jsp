<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Bulk Upload Questions &mdash; AI Secure Exam</title>
    <%@ include file="/common/head.jsp" %>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/dashboard.css">
</head>
<body>
<div class="app-shell">
    <%@ include file="/common/admin-sidebar.jsp" %>

    <div class="app-main">
        <div class="app-topbar">
            <h1>Bulk Upload Questions</h1>
            <div class="top-actions">
                <a href="<%=request.getContextPath()%>/admin/questions?examId=${exam.examId}" class="btn-secondary-outline">
                    <i class="fa-solid fa-arrow-left"></i> Back to Questions
                </a>
            </div>
        </div>

        <div class="app-content">
            <c:if test="${not empty errorMessage}">
                <div class="alert-banner error">${errorMessage}</div>
            </c:if>
            <c:if test="${not empty rowErrors}">
                <div class="alert-banner error" style="flex-direction:column; align-items:flex-start;">
                    <strong style="margin-bottom:6px;"><i class="fa-solid fa-triangle-exclamation"></i> Some rows were skipped:</strong>
                    <ul style="margin:0; padding-left:18px;">
                        <c:forEach var="rowErr" items="${rowErrors}">
                            <li>${rowErr}</li>
                        </c:forEach>
                    </ul>
                </div>
            </c:if>

            <div class="dash-grid-2">
                <div class="panel">
                    <div class="panel-head"><h3>Upload CSV</h3></div>
                    <p style="font-size:0.84rem; color:var(--color-text-muted); margin-top:0;">
                        Exam: <strong style="color:var(--color-text-dark);">${exam.title}</strong>
                    </p>

                    <form method="post" action="<%=request.getContextPath()%>/admin/questions/bulk-upload"
                          enctype="multipart/form-data">
                        <input type="hidden" name="examId" value="${exam.examId}">

                        <div class="form-group" style="margin-top:8px;">
                            <label for="csvFile">CSV File</label>
                            <input type="file" id="csvFile" name="csvFile" accept=".csv" class="form-control-custom" required>
                            <p class="hint">Maximum file size: 5 MB. UTF-8 encoded, comma-separated.</p>
                        </div>

                        <div class="form-actions">
                            <button type="submit" class="btn-primary-solid">
                                <i class="fa-solid fa-file-arrow-up"></i> Upload &amp; Import
                            </button>
                        </div>
                    </form>
                </div>

                <div class="panel">
                    <div class="panel-head"><h3>CSV Format</h3></div>
                    <p style="font-size:0.84rem; color:var(--color-text-muted); margin-top:0;">
                        The first row must be a header with these column names (any order):
                    </p>
                    <div style="background:#F8FAFC; border:1px solid #EEF1F8; border-radius:10px; padding:14px; overflow-x:auto;">
                        <code class="text-mono" style="font-size:0.76rem; white-space:nowrap;">
                            question_text,question_type,option_a,option_b,option_c,option_d,correct_option,marks,category,difficulty
                        </code>
                    </div>
                    <ul style="font-size:0.82rem; color:var(--color-text-muted); margin-top:14px; padding-left:18px; line-height:1.8;">
                        <li><strong>question_type:</strong> MCQ, TRUE_FALSE, or SINGLE_CORRECT</li>
                        <li><strong>correct_option:</strong> A, B, C, or D</li>
                        <li><strong>marks:</strong> a whole number (defaults to 1 if blank)</li>
                        <li><strong>difficulty:</strong> EASY, MEDIUM, or HARD (defaults to MEDIUM)</li>
                        <li>Wrap any field containing a comma in double quotes</li>
                        <li>Rows that fail validation are skipped and reported - the rest still import</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="<%=request.getContextPath()%>/assets/js/main.js"></script>
</body>
</html>
