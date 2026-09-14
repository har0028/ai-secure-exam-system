<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>${not empty question ? 'Edit Question' : 'Add Question'} &mdash; AI Secure Exam</title>
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
                <h1>${not empty question ? 'Edit Question' : 'Add Question'}</h1>
            </div>
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

            <div class="panel" style="max-width:760px;">
                <p style="font-size:0.84rem; color:var(--color-text-muted); margin-top:0; margin-bottom:20px;">
                    Exam: <strong style="color:var(--color-text-dark);">${exam.title}</strong>
                </p>

                <form method="post" action="<%=request.getContextPath()%>/admin/questions/save">
                    <c:if test="${not empty question}">
                        <input type="hidden" name="questionId" value="${question.questionId}">
                    </c:if>
                    <c:if test="${empty question}">
                        <input type="hidden" name="examId" value="${exam.examId}">
                    </c:if>

                    <div class="form-group">
                        <label for="questionText">Question Text</label>
                        <textarea id="questionText" name="questionText" class="form-control-custom" rows="3" required>${question.questionText}</textarea>
                    </div>

                    <div class="form-grid" style="margin-top:18px;">
                        <div class="form-group">
                            <label for="questionType">Question Type</label>
                            <select id="questionType" name="questionType" class="form-control-custom">
                                <option value="MCQ" ${question.questionType == 'MCQ' ? 'selected' : ''}>MCQ</option>
                                <option value="TRUE_FALSE" ${question.questionType == 'TRUE_FALSE' ? 'selected' : ''}>True / False</option>
                                <option value="SINGLE_CORRECT" ${question.questionType == 'SINGLE_CORRECT' ? 'selected' : ''}>Single Correct</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="difficulty">Difficulty</label>
                            <select id="difficulty" name="difficulty" class="form-control-custom">
                                <option value="EASY" ${question.difficulty == 'EASY' ? 'selected' : ''}>Easy</option>
                                <option value="MEDIUM" ${question.difficulty == 'MEDIUM' || empty question ? 'selected' : ''}>Medium</option>
                                <option value="HARD" ${question.difficulty == 'HARD' ? 'selected' : ''}>Hard</option>
                            </select>
                        </div>
                    </div>

                    <div class="form-grid" style="margin-top:18px;">
                        <div class="form-group">
                            <label for="optionA">Option A</label>
                            <input type="text" id="optionA" name="optionA" class="form-control-custom" value="${question.optionA}" required>
                        </div>
                        <div class="form-group">
                            <label for="optionB">Option B</label>
                            <input type="text" id="optionB" name="optionB" class="form-control-custom" value="${question.optionB}" required>
                        </div>
                    </div>

                    <div class="form-grid" style="margin-top:18px;">
                        <div class="form-group">
                            <label for="optionC">Option C <span style="color:var(--color-text-muted); font-weight:400;">(optional)</span></label>
                            <input type="text" id="optionC" name="optionC" class="form-control-custom" value="${question.optionC}">
                        </div>
                        <div class="form-group">
                            <label for="optionD">Option D <span style="color:var(--color-text-muted); font-weight:400;">(optional)</span></label>
                            <input type="text" id="optionD" name="optionD" class="form-control-custom" value="${question.optionD}">
                        </div>
                    </div>

                    <div class="form-grid" style="margin-top:18px;">
                        <div class="form-group">
                            <label for="correctOption">Correct Option</label>
                            <select id="correctOption" name="correctOption" class="form-control-custom">
                                <option value="A" ${question.correctOption == 'A' ? 'selected' : ''}>A</option>
                                <option value="B" ${question.correctOption == 'B' ? 'selected' : ''}>B</option>
                                <option value="C" ${question.correctOption == 'C' ? 'selected' : ''}>C</option>
                                <option value="D" ${question.correctOption == 'D' ? 'selected' : ''}>D</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="marks">Marks</label>
                            <input type="number" id="marks" name="marks" min="1" class="form-control-custom"
                                   value="${not empty question ? question.marks : 1}" required>
                        </div>
                    </div>

                    <div class="form-group" style="margin-top:18px;">
                        <label for="category">Category <span style="color:var(--color-text-muted); font-weight:400;">(optional)</span></label>
                        <input type="text" id="category" name="category" class="form-control-custom"
                               placeholder="e.g. Data Structures" value="${question.category}">
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn-primary-solid">
                            <i class="fa-solid fa-check"></i> ${not empty question ? 'Save Changes' : 'Add Question'}
                        </button>
                        <a href="<%=request.getContextPath()%>/admin/questions?examId=${exam.examId}" class="btn-secondary-outline">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<script src="<%=request.getContextPath()%>/assets/js/main.js"></script>
</body>
</html>
