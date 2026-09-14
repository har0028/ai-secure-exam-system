<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Access Denied &mdash; AI Secure Exam</title>
    <%@ include file="/common/head.jsp" %>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/auth.css">
</head>
<body>
<div class="auth-shell">
    <div class="glass-dark auth-card" style="text-align:center;">
        <div style="width:64px;height:64px;border-radius:16px;background:rgba(244,63,94,0.15);
                    display:flex;align-items:center;justify-content:center;margin:0 auto 20px;">
            <i class="fa-solid fa-lock" style="color:#F43F5E;font-size:1.6rem;"></i>
        </div>
        <h2>403 &mdash; Access Denied</h2>
        <p class="sub">You don't have permission to view this page with your current role.
           If you believe this is a mistake, sign in with the correct account.</p>
        <a href="<%=request.getContextPath()%>/login" class="btn-gradient btn-block">Back to Sign In</a>
    </div>
</div>
</body>
</html>
