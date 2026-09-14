<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>${not empty student ? 'Edit Student' : 'Add Student'} &mdash; AI Secure Exam</title>
    <%@ include file="/common/head.jsp" %>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/dashboard.css">
</head>
<body>
<div class="app-shell">
    <%@ include file="/common/admin-sidebar.jsp" %>

    <div class="app-main">
        <div class="app-topbar">
            <h1>${not empty student ? 'Edit Student' : 'Add Student'}</h1>
            <div class="top-actions">
                <a href="<%=request.getContextPath()%>/admin/students" class="btn-secondary-outline">
                    <i class="fa-solid fa-arrow-left"></i> Back to Students
                </a>
            </div>
        </div>

        <div class="app-content">
            <c:if test="${not empty errorMessage}">
                <div class="alert-banner error">${errorMessage}</div>
            </c:if>

            <div class="panel" style="max-width:760px;">
                <form method="post" action="<%=request.getContextPath()%>/admin/students/save">
                    <c:if test="${not empty student}">
                        <input type="hidden" name="studentId" value="${student.studentId}">
                    </c:if>

                    <div class="form-grid">
                        <div class="form-group">
                            <label for="fullName">Full Name</label>
                            <input type="text" id="fullName" name="fullName" class="form-control-custom"
                                   value="${student.fullName}" required>
                        </div>
                        <div class="form-group">
                            <label for="rollNumber">Roll Number</label>
                            <input type="text" id="rollNumber" name="rollNumber" class="form-control-custom"
                                   value="${student.rollNumber}" required>
                        </div>
                    </div>

                    <c:if test="${empty student}">
                        <div class="form-grid" style="margin-top:18px;">
                            <div class="form-group">
                                <label for="email">Email Address</label>
                                <input type="email" id="email" name="email" class="form-control-custom"
                                       placeholder="student@example.com" required>
                            </div>
                            <div class="form-group">
                                <label for="password">Initial Password</label>
                                <input type="password" id="password" name="password" class="form-control-custom"
                                       placeholder="Temporary password" required>
                                <p class="hint">8+ characters with uppercase, lowercase, a number, and a special character.</p>
                            </div>
                        </div>
                    </c:if>
                    <c:if test="${not empty student}">
                        <div class="form-group" style="margin-top:18px;">
                            <label>Email Address</label>
                            <input type="email" class="form-control-custom" value="${student.email}" disabled>
                            <p class="hint">Email cannot be changed here. The student can request a password reset if needed.</p>
                        </div>
                    </c:if>

                    <div class="form-grid" style="margin-top:18px;">
                        <div class="form-group">
                            <label for="course">Course</label>
                            <input type="text" id="course" name="course" class="form-control-custom"
                                   placeholder="e.g. B.Tech" value="${student.course}">
                        </div>
                        <div class="form-group">
                            <label for="department">Department</label>
                            <input type="text" id="department" name="department" class="form-control-custom"
                                   placeholder="e.g. Computer Science" value="${student.department}">
                        </div>
                    </div>

                    <div class="form-grid" style="margin-top:18px;">
                        <div class="form-group">
                            <label for="phone">Phone</label>
                            <input type="text" id="phone" name="phone" class="form-control-custom"
                                   placeholder="10-digit phone number" value="${student.phone}">
                        </div>
                        <div class="form-group">
                            <label for="dateOfBirth">Date of Birth</label>
                            <input type="date" id="dateOfBirth" name="dateOfBirth" class="form-control-custom"
                                   value="<fmt:formatDate value="${student.dateOfBirth}" pattern="yyyy-MM-dd"/>">
                        </div>
                    </div>

                    <div class="form-group" style="margin-top:18px;">
                        <label for="address">Address</label>
                        <textarea id="address" name="address" class="form-control-custom" rows="3">${student.address}</textarea>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn-primary-solid">
                            <i class="fa-solid fa-check"></i> ${not empty student ? 'Save Changes' : 'Create Student'}
                        </button>
                        <a href="<%=request.getContextPath()%>/admin/students" class="btn-secondary-outline">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<script src="<%=request.getContextPath()%>/assets/js/main.js"></script>
</body>
</html>
