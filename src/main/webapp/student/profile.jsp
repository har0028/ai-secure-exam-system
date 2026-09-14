<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>My Profile &mdash; AI Secure Exam</title>
    <%@ include file="/common/head.jsp" %>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/dashboard.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/auth.css">
</head>
<body>
<div class="app-shell">
    <%@ include file="/common/student-sidebar.jsp" %>
    <div class="app-main">
        <div class="app-topbar">
            <div style="display:flex; align-items:center;">
                <button class="sidebar-toggle icon-btn" aria-label="Toggle Navigation"><i class="fa-solid fa-bars"></i></button>
                <h1>My Profile</h1>
            </div>
        </div>
        <div class="app-content">
            <%@ include file="/common/flash-messages.jsp" %>
            <c:if test="${not empty errorMessage}"><div class="alert-banner error">${errorMessage}</div></c:if>
            <div class="panel" style="max-width:560px;">
                <div style="display:flex; align-items:center; gap:18px; margin-bottom:24px; padding-bottom:20px; border-bottom:1px solid #EEF1F8;">
                    <div class="topbar-avatar" style="width:56px; height:56px; font-size:1.3rem;">${sessionScope.fullName.substring(0,1)}</div>
                    <div>
                        <div style="font-size:1.1rem; font-weight:700;">${sessionScope.fullName}</div>
                        <div style="font-size:0.84rem; color:var(--color-text-muted);">${student.email}</div>
                        <div style="font-size:0.78rem; color:var(--color-text-muted);">Roll: ${student.rollNumber}</div>
                    </div>
                </div>
                <div class="list-row"><div class="title">Course</div><div class="subtitle">${not empty student.course ? student.course : 'Not set'}</div></div>
                <div class="list-row"><div class="title">Department</div><div class="subtitle">${not empty student.department ? student.department : 'Not set'}</div></div>
                <div class="list-row"><div class="title">Phone</div><div class="subtitle">${not empty student.phone ? student.phone : 'Not set'}</div></div>

                <form method="post" action="<%=request.getContextPath()%>/student/profile" style="margin-top:24px; padding-top:20px; border-top:1px solid #EEF1F8;">
                    <h4 style="margin:0 0 16px; font-size:0.95rem;">Change Password</h4>
                    <div class="form-group" style="margin-bottom:14px;">
                        <label for="newPassword">New Password</label>
                        <input type="password" id="newPassword" name="newPassword" class="form-control-custom" placeholder="Leave blank to keep current password">
                    </div>
                    <div class="form-group" style="margin-bottom:20px;">
                        <label for="confirmPassword">Confirm New Password</label>
                        <input type="password" id="confirmPassword" name="confirmPassword" class="form-control-custom">
                    </div>
                    <button type="submit" class="btn-primary-solid">Update Password</button>
                </form>
            </div>
        </div>
    </div>
</div>
<script src="<%=request.getContextPath()%>/assets/js/main.js"></script>
</body>
</html>
