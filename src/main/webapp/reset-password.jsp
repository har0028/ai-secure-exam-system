<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Set New Password &mdash; AI Secure Exam</title>
    <%@ include file="/common/head.jsp" %>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/auth.css">
</head>
<body>
<div class="auth-shell">
    <div class="glass-dark auth-card">
        <div class="auth-brand">
            <span class="brand-mark"><i class="fa-solid fa-shield-halved" style="color:#fff;font-size:0.9rem;"></i></span>
            AI Secure Exam
        </div>
        <h2>Set a new password</h2>
        <p class="sub">Choose a strong new password for your account.</p>

        <c:if test="${not empty errorMessage}">
            <div class="alert-banner error"><i class="fa-solid fa-circle-exclamation"></i> <span>${errorMessage}</span></div>
        </c:if>

        <form action="<%=request.getContextPath()%>/reset-password" method="post">
            <input type="hidden" name="token" value="${token}">
            <div class="auth-field">
                <label for="newPassword">New Password</label>
                <div class="auth-input-wrap">
                    <input type="password" id="newPassword" name="newPassword" class="auth-input"
                           placeholder="Create a new password" required data-password-strength="#pwHint">
                    <button type="button" class="auth-toggle-visibility" data-target="newPassword"><i class="fa-solid fa-eye"></i></button>
                </div>
                <p class="password-hint" id="pwHint">Use 8+ characters with uppercase, lowercase, a number, and a special character.</p>
            </div>
            <div class="auth-field">
                <label for="confirmPassword">Confirm New Password</label>
                <div class="auth-input-wrap">
                    <input type="password" id="confirmPassword" name="confirmPassword" class="auth-input"
                           placeholder="Re-enter new password" required
                           data-confirm-target="newPassword" data-confirm-msg="#confirmMsg">
                    <button type="button" class="auth-toggle-visibility" data-target="confirmPassword"><i class="fa-solid fa-eye"></i></button>
                </div>
                <p class="password-hint" id="confirmMsg"></p>
            </div>
            <button type="submit" class="btn-gradient btn-block">Update Password</button>
        </form>

        <div class="auth-footer-link">
            <a href="<%=request.getContextPath()%>/login">Back to Sign In</a>
        </div>
    </div>
</div>
<script src="<%=request.getContextPath()%>/assets/js/main.js"></script>
</body>
</html>
