<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Forgot Password &mdash; AI Secure Exam</title>
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
        <h2>Reset your password</h2>
        <p class="sub">Enter your account email and we'll generate a reset link.</p>

        <c:if test="${not empty successMessage}">
            <div class="alert-banner success"><i class="fa-solid fa-circle-check"></i> <span>${successMessage}</span></div>
        </c:if>
        <c:if test="${not empty devResetLink}">
            <div class="alert-banner success">
                <i class="fa-solid fa-flask"></i>
                <span>Development mode &mdash; email delivery isn't wired up yet, so here's the link directly:
                <a href="${devResetLink}" style="color:#A7F3D0; text-decoration:underline;">${devResetLink}</a></span>
            </div>
        </c:if>

        <form action="<%=request.getContextPath()%>/forgot-password" method="post">
            <div class="auth-field">
                <label for="email">Email Address</label>
                <input type="email" id="email" name="email" class="auth-input" placeholder="you@example.com" required>
            </div>
            <button type="submit" class="btn-gradient btn-block">Send Reset Link</button>
        </form>

        <div class="auth-footer-link">
            <a href="<%=request.getContextPath()%>/login">Back to Sign In</a>
        </div>
    </div>
</div>
<script src="<%=request.getContextPath()%>/assets/js/main.js"></script>
</body>
</html>
