<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>${not empty exam ? 'Edit Exam' : 'Create Exam'} &mdash; AI Secure Exam</title>
    <%@ include file="/common/head.jsp" %>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/dashboard.css">
</head>
<body>
<div class="app-shell">
    <%@ include file="/common/admin-sidebar.jsp" %>

    <div class="app-main">
        <div class="app-topbar">
            <div style="display:flex; align-items:center;">
                <button class="sidebar-toggle icon-btn" aria-label="Toggle Navigation"><i class="fa-solid fa-bars"></i></button>
                <h1>${not empty exam ? 'Edit Exam' : 'Create Exam'}</h1>
            </div>
            <div class="top-actions">
                <a href="<%=request.getContextPath()%>/admin/exams" class="btn-secondary-outline">
                    <i class="fa-solid fa-arrow-left"></i> Back to Exams
                </a>
            </div>
        </div>

        <div class="app-content">
            <c:if test="${not empty errorMessage}">
                <div class="alert-banner error">${errorMessage}</div>
            </c:if>

            <div class="panel" style="max-width:840px;">
                <form method="post" action="<%=request.getContextPath()%>/admin/exams/save">
                    <c:if test="${not empty exam}">
                        <input type="hidden" name="examId" value="${exam.examId}">
                    </c:if>

                    <div class="form-group">
                        <label for="title">Exam Title</label>
                        <input type="text" id="title" name="title" class="form-control-custom"
                               value="${exam.title}" required>
                    </div>

                    <div class="form-group" style="margin-top:18px;">
                        <label for="description">Description</label>
                        <textarea id="description" name="description" class="form-control-custom" rows="3">${exam.description}</textarea>
                    </div>

                    <div class="form-grid" style="margin-top:18px;">
                        <div class="form-group">
                            <label for="category">Category</label>
                            <input type="text" id="category" name="category" class="form-control-custom"
                                   placeholder="e.g. Computer Science" value="${exam.category}">
                        </div>
                        <div class="form-group">
                            <label for="status">Status</label>
                            <select id="status" name="status" class="form-control-custom">
                                <option value="DRAFT" ${exam.status == 'DRAFT' ? 'selected' : ''}>DRAFT</option>
                                <option value="SCHEDULED" ${exam.status == 'SCHEDULED' ? 'selected' : ''}>SCHEDULED</option>
                                <option value="ACTIVE" ${exam.status == 'ACTIVE' ? 'selected' : ''}>ACTIVE</option>
                                <option value="COMPLETED" ${exam.status == 'COMPLETED' ? 'selected' : ''}>COMPLETED</option>
                                <option value="CANCELLED" ${exam.status == 'CANCELLED' ? 'selected' : ''}>CANCELLED</option>
                            </select>
                        </div>
                    </div>

                    <div class="form-grid" style="margin-top:18px;">
                        <div class="form-group">
                            <label for="durationMinutes">Duration (minutes)</label>
                            <input type="number" id="durationMinutes" name="durationMinutes" min="1" class="form-control-custom"
                                   value="${not empty exam ? exam.durationMinutes : 60}" required>
                        </div>
                        <div class="form-group">
                            <label for="totalMarks">Total Marks</label>
                            <input type="number" id="totalMarks" name="totalMarks" min="1" class="form-control-custom"
                                   value="${not empty exam ? exam.totalMarks : 100}" required>
                        </div>
                    </div>

                    <div class="form-grid" style="margin-top:18px;">
                        <div class="form-group">
                            <label for="passingMarks">Passing Marks</label>
                            <input type="number" id="passingMarks" name="passingMarks" min="0" class="form-control-custom"
                                   value="${not empty exam ? exam.passingMarks : 40}" required>
                        </div>
                        <div></div>
                    </div>

                    <div class="form-grid" style="margin-top:18px;">
                        <div class="form-group">
                            <label for="scheduledStart">Scheduled Start</label>
                            <input type="datetime-local" id="scheduledStart" name="scheduledStart" class="form-control-custom"
                                   value="<fmt:formatDate value="${exam.scheduledStart}" pattern="yyyy-MM-dd'T'HH:mm"/>">
                        </div>
                        <div class="form-group">
                            <label for="scheduledEnd">Scheduled End</label>
                            <input type="datetime-local" id="scheduledEnd" name="scheduledEnd" class="form-control-custom"
                                   value="<fmt:formatDate value="${exam.scheduledEnd}" pattern="yyyy-MM-dd'T'HH:mm"/>">
                        </div>
                    </div>

                    <div style="margin-top:8px;">
                        <label class="form-check">
                            <input type="checkbox" name="shuffleQuestions" ${exam == null || exam.shuffleQuestions ? 'checked' : ''}>
                            Shuffle question order per candidate
                        </label>
                        <label class="form-check">
                            <input type="checkbox" name="shuffleOptions" ${exam == null || exam.shuffleOptions ? 'checked' : ''}>
                            Shuffle answer option order per candidate
                        </label>
                        <label class="form-check">
                            <input type="checkbox" name="proctoringEnabled" ${exam == null || exam.proctoringEnabled ? 'checked' : ''}>
                            Enable AI proctoring for this exam
                        </label>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn-primary-solid">
                            <i class="fa-solid fa-check"></i> ${not empty exam ? 'Save Changes' : 'Create Exam'}
                        </button>
                        <a href="<%=request.getContextPath()%>/admin/exams" class="btn-secondary-outline">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<script src="<%=request.getContextPath()%>/assets/js/main.js"></script>
</body>
</html>
