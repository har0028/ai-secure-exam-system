<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Sign In &mdash; AI Secure Exam</title>
    <%@ include file="/common/head.jsp" %>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/auth.css">
</head>
<body>
<div class="auth-shell">
    <div class="glass-dark auth-card">
        <div class="auth-brand">
            <span class="brand-mark"><i class="fa-solid fa-shield-halved"></i></span>
            AI Secure Exam
        </div>
        <h2>Welcome Back</h2>
        <p class="sub">Sign in with your email & password to access your portal.</p>

        <c:if test="${not empty errorMessage}">
            <div class="alert-banner error"><i class="fa-solid fa-circle-exclamation"></i> <span>${errorMessage}</span></div>
        </c:if>
        <c:if test="${not empty successMessage}">
            <div class="alert-banner success"><i class="fa-solid fa-circle-check"></i> <span>${successMessage}</span></div>
        </c:if>

        <div style="background:rgba(255,255,255,0.05); border:1px solid var(--glass-border-dark); border-radius:12px; padding:12px; margin-bottom:20px; font-size:0.82rem; color:var(--color-text-on-dark-muted);">
            <div style="font-size:0.72rem; font-weight:800; text-transform:uppercase; letter-spacing:0.06em; color:var(--color-cyan-light); margin-bottom:8px;">Quick Login Shortcuts</div>
            <div style="display:flex; gap:8px;">
                <button type="button" onclick="fillCreds('admin@aiexam.com', 'Admin@123')" class="btn-outline-light" style="padding:6px 12px; font-size:0.78rem; flex:1;"><i class="fa-solid fa-user-shield"></i> Admin</button>
                <button type="button" onclick="fillCreds('aarav.sharma@student.com', 'Student@123')" class="btn-outline-light" style="padding:6px 12px; font-size:0.78rem; flex:1;"><i class="fa-solid fa-user-graduate"></i> Student</button>
            </div>
        </div>

        <form action="<%=request.getContextPath()%>/login" method="post">
            <div class="auth-field">
                <label for="email">Email Address</label>
                <input type="email" id="email" name="email" class="auth-input" placeholder="you@example.com"
                       value="${email}" required>
            </div>
            <div class="auth-field">
                <label for="password">Password</label>
                <div class="auth-input-wrap">
                    <input type="password" id="password" name="password" class="auth-input" placeholder="Enter your password" required>
                    <button type="button" class="auth-toggle-visibility" data-target="password"><i class="fa-solid fa-eye"></i></button>
                </div>
            </div>
            <div class="auth-row-between">
                <label><input type="checkbox" name="rememberMe"> Remember me</label>
                <a href="<%=request.getContextPath()%>/forgot-password">Forgot password?</a>
            </div>
            <button type="submit" class="btn-gradient btn-block">Sign In to Dashboard <i class="fa-solid fa-arrow-right"></i></button>
        </form>

        <div class="auth-footer-link">
            Don't have an account? <a href="<%=request.getContextPath()%>/register">Register as Student</a>
        </div>
    </div>
</div>
<script src="<%=request.getContextPath()%>/assets/js/main.js"></script>
<script>
function fillCreds(email, pass) {
    document.getElementById('email').value = email;
    document.getElementById('password').value = pass;
}
</script>
</body>
</html>
