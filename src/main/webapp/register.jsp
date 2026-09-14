<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Create Account &mdash; AI Secure Exam</title>
    <%@ include file="/common/head.jsp" %>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/auth.css">
</head>
<body>
<div class="auth-shell">
    <div class="glass-dark auth-card" style="max-width:480px;">
        <div class="auth-brand">
            <span class="brand-mark"><i class="fa-solid fa-shield-halved" style="color:#fff;font-size:0.9rem;"></i></span>
            AI Secure Exam
        </div>
        <h2>Create your student account</h2>
        <p class="sub">Register to view available exams and take proctored tests.</p>

        <c:if test="${not empty errorMessage}">
            <div class="alert-banner error"><i class="fa-solid fa-circle-exclamation"></i> <span>${errorMessage}</span></div>
        </c:if>

        <form action="<%=request.getContextPath()%>/register" method="post">
            <div class="auth-field">
                <label for="fullName">Full Name</label>
                <input type="text" id="fullName" name="fullName" class="auth-input" placeholder="Your full name"
                       value="${fullName}" required>
            </div>
            <div class="auth-field">
                <label for="rollNumber">Roll Number</label>
                <input type="text" id="rollNumber" name="rollNumber" class="auth-input" placeholder="e.g. CS2026045"
                       value="${rollNumber}" required>
            </div>
            <div class="auth-field">
                <label for="email">Email Address</label>
                <input type="email" id="email" name="email" class="auth-input" placeholder="you@example.com"
                       value="${email}" required>
            </div>
            <div class="auth-field">
                <label for="password">Password</label>
                <div class="auth-input-wrap">
                    <input type="password" id="password" name="password" class="auth-input"
                           placeholder="Create a password" required data-password-strength="#pwHint">
                    <button type="button" class="auth-toggle-visibility" data-target="password"><i class="fa-solid fa-eye"></i></button>
                </div>
                <p class="password-hint" id="pwHint">Use 8+ characters with uppercase, lowercase, a number, and a special character.</p>
            </div>
            <div class="auth-field">
                <label for="confirmPassword">Confirm Password</label>
                <div class="auth-input-wrap">
                    <input type="password" id="confirmPassword" name="confirmPassword" class="auth-input"
                           placeholder="Re-enter password" required
                           data-confirm-target="password" data-confirm-msg="#confirmMsg">
                    <button type="button" class="auth-toggle-visibility" data-target="confirmPassword"><i class="fa-solid fa-eye"></i></button>
                </div>
                <p class="password-hint" id="confirmMsg"></p>
            </div>
            <button type="submit" class="btn-gradient btn-block">Create Account</button>
        </form>

        <div class="auth-footer-link">
            Already have an account? <a href="<%=request.getContextPath()%>/login">Sign In</a>
        </div>
    </div>
</div>
<script src="<%=request.getContextPath()%>/assets/js/main.js"></script>
</body>
</html>
