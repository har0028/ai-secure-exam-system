<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- common/student-sidebar.jsp --%>
<aside class="app-sidebar">
    <a href="<%=request.getContextPath()%>/student/dashboard" class="brand">
        <span class="brand-mark"><i class="fa-solid fa-shield-halved"></i></span>
        <div>
            <div>AI Secure Exam</div>
            <div style="font-size:0.65rem; color:var(--color-cyan-light); font-weight:700; text-transform:uppercase; letter-spacing:0.08em; margin-top:2px;">Candidate Portal</div>
        </div>
    </a>
    <nav class="sidebar-nav">
        <a href="<%=request.getContextPath()%>/student/dashboard" class="${activePage == 'dashboard' ? 'active' : ''}">
            <i class="fa-solid fa-house"></i> Dashboard
        </a>
        <a href="<%=request.getContextPath()%>/student/exams" class="${activePage == 'exams' ? 'active' : ''}">
            <i class="fa-solid fa-file-pen"></i> Available Exams
        </a>
        <a href="<%=request.getContextPath()%>/student/results" class="${activePage == 'results' ? 'active' : ''}">
            <i class="fa-solid fa-chart-simple"></i> My Results
        </a>
        <a href="<%=request.getContextPath()%>/student/notifications" class="${activePage == 'notifications' ? 'active' : ''}">
            <i class="fa-solid fa-bell"></i> Notifications
        </a>
        <a href="<%=request.getContextPath()%>/student/profile" class="${activePage == 'profile' ? 'active' : ''}">
            <i class="fa-solid fa-user"></i> My Profile
        </a>
    </nav>
    <div class="sidebar-foot">
        <a href="<%=request.getContextPath()%>/logout"><i class="fa-solid fa-arrow-right-from-bracket"></i> Sign Out</a>
    </div>
</aside>
