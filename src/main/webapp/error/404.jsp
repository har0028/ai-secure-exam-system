<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Page Not Found &mdash; AI Secure Exam</title>
    <%@ include file="/common/head.jsp" %>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/auth.css">
</head>
<body>
<div class="auth-shell">
    <div class="glass-dark auth-card" style="text-align:center;">
        <div style="width:64px;height:64px;border-radius:16px;background:rgba(79,70,229,0.15);
                    display:flex;align-items:center;justify-content:center;margin:0 auto 20px;">
            <i class="fa-solid fa-compass" style="color:#818CF8;font-size:1.6rem;"></i>
        </div>
        <h2>404 &mdash; Page Not Found</h2>
        <p class="sub">The page you're looking for doesn't exist or may have moved.</p>
        <a href="<%=request.getContextPath()%>/" class="btn-gradient btn-block">Back to Home</a>
    </div>
</div>
</body>
</html>
