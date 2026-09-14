<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Server Error &mdash; AI Secure Exam</title>
    <%@ include file="/common/head.jsp" %>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/auth.css">
</head>
<body>
<div class="auth-shell">
    <div class="glass-dark auth-card" style="text-align:center;">
        <div style="width:64px;height:64px;border-radius:16px;background:rgba(245,158,11,0.15);
                    display:flex;align-items:center;justify-content:center;margin:0 auto 20px;">
            <i class="fa-solid fa-triangle-exclamation" style="color:#F59E0B;font-size:1.6rem;"></i>
        </div>
        <h2>500 &mdash; Something Went Wrong</h2>
        <p class="sub">An unexpected error occurred on our end. Please try again, and contact
           the administrator if the problem continues.</p>
        <a href="<%=request.getContextPath()%>/" class="btn-gradient btn-block">Back to Home</a>
    </div>
</div>
</body>
</html>
