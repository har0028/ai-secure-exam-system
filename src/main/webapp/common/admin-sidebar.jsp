<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- common/admin-sidebar.jsp --%>
<aside class="app-sidebar">
    <a href="<%=request.getContextPath()%>/admin/dashboard" class="brand">
        <span class="brand-mark"><i class="fa-solid fa-shield-halved"></i></span>
        <div>
            <div>AI Secure Exam</div>
            <div style="font-size:0.65rem; color:var(--color-cyan-light); font-weight:700; text-transform:uppercase; letter-spacing:0.08em; margin-top:2px;">Admin Workspace</div>
        </div>
    </a>
    <nav class="sidebar-nav">
        <a href="<%=request.getContextPath()%>/admin/dashboard" class="${activePage == 'dashboard' ? 'active' : ''}">
            <i class="fa-solid fa-chart-line"></i> Dashboard
        </a>
        <a href="<%=request.getContextPath()%>/admin/students" class="${activePage == 'students' ? 'active' : ''}">
            <i class="fa-solid fa-user-graduate"></i> Students
        </a>
        <a href="<%=request.getContextPath()%>/admin/exams" class="${activePage == 'exams' ? 'active' : ''}">
            <i class="fa-solid fa-file-pen"></i> Exams
        </a>
        <a href="<%=request.getContextPath()%>/admin/questions" class="${activePage == 'questions' ? 'active' : ''}">
            <i class="fa-solid fa-list-check"></i> Questions
        </a>
        <a href="<%=request.getContextPath()%>/admin/results" class="${activePage == 'results' ? 'active' : ''}">
            <i class="fa-solid fa-clipboard-list"></i> Results
        </a>
        <a href="<%=request.getContextPath()%>/admin/monitoring" class="${activePage == 'monitoring' ? 'active' : ''}">
            <i class="fa-solid fa-eye"></i> Live Monitoring
        </a>
        <a href="<%=request.getContextPath()%>/admin/analytics" class="${activePage == 'analytics' ? 'active' : ''}">
            <i class="fa-solid fa-chart-pie"></i> Analytics
        </a>
        <a href="<%=request.getContextPath()%>/admin/settings" class="${activePage == 'settings' ? 'active' : ''}">
            <i class="fa-solid fa-sliders"></i> Settings
        </a>
    </nav>
    <div class="sidebar-foot">
        <a href="<%=request.getContextPath()%>/logout"><i class="fa-solid fa-arrow-right-from-bracket"></i> Sign Out</a>
    </div>
</aside>
