<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Notifications &mdash; AI Secure Exam</title>
    <%@ include file="/common/head.jsp" %>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/dashboard.css">
</head>
<body>
<div class="app-shell">
    <%@ include file="/common/student-sidebar.jsp" %>
    <div class="app-main">
        <div class="app-topbar">
            <div style="display:flex; align-items:center;">
                <button class="sidebar-toggle icon-btn" aria-label="Toggle Navigation"><i class="fa-solid fa-bars"></i></button>
                <h1>Notifications</h1>
            </div>
        </div>
        <div class="app-content">
            <div class="panel">
                <c:choose>
                    <c:when test="${empty notifications}">
                        <div class="empty-state"><i class="fa-solid fa-bell-slash"></i><p>No notifications yet.</p></div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="n" items="${notifications}">
                            <div class="list-row">
                                <div class="meta">
                                    <div class="dot-icon"><i class="fa-solid fa-bell"></i></div>
                                    <div>
                                        <div class="title">${n.title}</div>
                                        <div class="subtitle">${n.message}</div>
                                    </div>
                                </div>
                                <div class="subtitle text-mono"><fmt:formatDate value="${n.createdAt}" pattern="MMM d"/></div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>
<script src="<%=request.getContextPath()%>/assets/js/main.js"></script>
</body>
</html>
