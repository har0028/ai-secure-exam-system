<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Exam Result &mdash; AI Secure Exam</title>
    <%@ include file="/common/head.jsp" %>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/dashboard.css">
</head>
<body>
<div class="app-shell">
    <%@ include file="/common/student-sidebar.jsp" %>
    <div class="app-main">
        <div class="app-topbar">
            <h1>Exam Result</h1>
            <div class="top-actions">
                <a href="<%=request.getContextPath()%>/student/results" class="btn-secondary-outline">
                    <i class="fa-solid fa-arrow-left"></i> All Results
                </a>
            </div>
        </div>
        <div class="app-content">
            <c:if test="${not empty pendingMessage}">
                <div class="panel" style="text-align:center; padding:40px;">
                    <i class="fa-solid fa-spinner fa-spin" style="font-size:2rem; color:var(--color-indigo); margin-bottom:16px;"></i>
                    <p>${pendingMessage}</p>
                </div>
            </c:if>

            <c:if test="${not empty result}">
                <div style="max-width:680px;">
                    <!-- Score card -->
                    <div class="panel" style="text-align:center; padding:40px; margin-bottom:20px; border-top: 4px solid ${result.passStatus == 'PASS' ? 'var(--color-risk-low)' : 'var(--color-risk-critical)'};">
                        <div style="font-size:0.85rem; color:var(--color-text-muted); margin-bottom:6px;">${result.examTitle}</div>
                        <div class="text-mono" style="font-size:3.5rem; font-weight:700; color:${result.passStatus == 'PASS' ? 'var(--color-risk-low)' : 'var(--color-risk-critical)'};">${result.percentage}%</div>
                        <div style="font-size:1.4rem; font-weight:700; margin-top:8px; color:${result.passStatus == 'PASS' ? 'var(--color-risk-low)' : 'var(--color-risk-critical)'};">
                            <c:if test="${result.passStatus == 'PASS'}"><i class="fa-solid fa-circle-check"></i> PASSED</c:if>
                            <c:if test="${result.passStatus != 'PASS'}"><i class="fa-solid fa-circle-xmark"></i> FAILED</c:if>
                        </div>
                        <div style="font-size:0.85rem; color:var(--color-text-muted); margin-top:8px;">
                            <fmt:formatDate value="${result.generatedAt}" pattern="MMMM d, yyyy 'at' h:mm a"/>
                        </div>
                    </div>

                    <!-- Stats grid -->
                    <div class="stat-grid" style="margin-bottom:20px;">
                        <div class="card-stat"><div class="stat-label">Obtained Marks</div><div class="stat-value text-mono">${result.obtainedMarks}</div></div>
                        <div class="card-stat"><div class="stat-label">Total Marks</div><div class="stat-value text-mono">${result.totalMarks}</div></div>
                        <div class="card-stat"><div class="stat-label">Correct Answers</div><div class="stat-value text-mono" style="color:var(--color-risk-low);">${result.correctAnswers}</div></div>
                        <div class="card-stat"><div class="stat-label">Wrong Answers</div><div class="stat-value text-mono" style="color:var(--color-risk-critical);">${result.wrongAnswers}</div></div>
                    </div>

                    <div class="panel">
                        <div class="list-row"><div class="title">Unanswered Questions</div><div class="subtitle text-mono">${result.unanswered}</div></div>
                        <div class="list-row"><div class="title">Passing Marks Required</div><div class="subtitle text-mono">${result.passingMarks}</div></div>
                        <c:if test="${not empty result.reviewStatus && result.reviewStatus != 'CLEAR'}">
                        <div class="list-row">
                            <div class="title"><i class="fa-solid fa-magnifying-glass" style="color:var(--color-risk-medium);"></i> Review Status</div>
                            <div><span class="badge-risk medium">${result.reviewStatus}</span></div>
                        </div>
                        </c:if>
                    </div>

                    <c:if test="${not empty result.reviewStatus && result.reviewStatus == 'UNDER_REVIEW'}">
                    <div class="panel" style="margin-top:16px; background:rgba(245,158,11,0.05); border-color:rgba(245,158,11,0.2);">
                        <p style="margin:0; font-size:0.86rem; color:#B45309; line-height:1.6;">
                            <strong><i class="fa-solid fa-triangle-exclamation"></i> This result is under review.</strong><br>
                            Our proctoring system detected unusual activity during your exam. An administrator will review the session recording and logs. Your result stands unless a manual review decision changes it.
                        </p>
                    </div>
                    </c:if>
                </div>
            </c:if>
        </div>
    </div>
</div>
<script src="<%=request.getContextPath()%>/assets/js/main.js"></script>
</body>
</html>
