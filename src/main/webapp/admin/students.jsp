<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Students &mdash; AI Secure Exam</title>
    <%@ include file="/common/head.jsp" %>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/dashboard.css">
</head>
<body>
<div class="app-shell">
    <%@ include file="/common/admin-sidebar.jsp" %>

    <div class="app-main">
        <div class="app-topbar">
            <div style="display:flex; align-items:center;">
                <button class="sidebar-toggle icon-btn" aria-label="Toggle Navigation"><i class="fa-solid fa-bars"></i></button>
                <h1>Students</h1>
            </div>
            <div class="top-actions">
                <a href="<%=request.getContextPath()%>/admin/students/create" class="btn-primary-solid">
                    <i class="fa-solid fa-user-plus"></i> Add Student
                </a>
            </div>
        </div>

        <div class="app-content">
            <%@ include file="/common/flash-messages.jsp" %>
            <c:if test="${not empty errorMessage}">
                <div class="alert-banner error">${errorMessage}</div>
            </c:if>

            <div class="panel">
                <div class="table-toolbar">
                    <form class="search-box" method="get" action="<%=request.getContextPath()%>/admin/students">
                        <i class="fa-solid fa-magnifying-glass"></i>
                        <input type="text" name="q" placeholder="Search by name, email, or roll number"
                               value="${searchKeyword}">
                    </form>
                    <span style="font-size:0.82rem; color:var(--color-text-muted);">
                        ${students.size()} student(s)
                    </span>
                </div>

                <c:choose>
                    <c:when test="${empty students}">
                        <div class="empty-state">
                            <i class="fa-solid fa-user-graduate"></i>
                            <p>No students found. Try a different search or add a new student.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>Roll Number</th>
                                    <th>Course</th>
                                    <th>Risk Level</th>
                                    <th>Joined</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="s" items="${students}">
                                    <tr>
                                        <td>
                                            <div class="cell-title">${s.fullName}</div>
                                            <div class="cell-subtitle">${s.email}</div>
                                        </td>
                                        <td class="text-mono">${s.rollNumber}</td>
                                        <td>${not empty s.course ? s.course : '&mdash;'}</td>
                                        <td>
                                            <span class="badge-risk ${s.riskLevel != null ? s.riskLevel.toLowerCase() : 'low'}">
                                                ${s.riskLevel != null ? s.riskLevel : 'LOW'}
                                            </span>
                                        </td>
                                        <td class="cell-subtitle"><fmt:formatDate value="${s.createdAt}" pattern="MMM d, yyyy"/></td>
                                        <td>
                                            <div class="cell-actions">
                                                <a class="btn-sm btn-light" href="<%=request.getContextPath()%>/admin/students/edit?id=${s.studentId}">
                                                    <i class="fa-solid fa-pen"></i> Edit
                                                </a>
                                                <form method="post" action="<%=request.getContextPath()%>/admin/students/delete"
                                                      onsubmit="return confirm('Delete this student? This also removes their exam attempts and results.');" style="display:inline;">
                                                    <input type="hidden" name="id" value="${s.studentId}">
                                                    <button type="submit" class="btn-sm btn-danger">
                                                        <i class="fa-solid fa-trash"></i> Delete
                                                    </button>
                                                </form>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>
<script src="<%=request.getContextPath()%>/assets/js/main.js"></script>
</body>
</html>
