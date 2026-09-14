<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Analytics &mdash; AI Secure Exam</title>
    <%@ include file="/common/head.jsp" %>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/dashboard.css">
</head>
<body>
<div class="app-shell">
    <%@ include file="/common/admin-sidebar.jsp" %>
    <div class="app-main">
        <div class="app-topbar"><h1>Analytics</h1></div>
        <div class="app-content">

            <!-- Summary stats -->
            <div class="stat-grid" style="margin-bottom:24px;">
                <div class="card-stat">
                    <div class="stat-icon"><i class="fa-solid fa-user-graduate"></i></div>
                    <div class="stat-label">Total Students</div>
                    <div class="stat-value">${totalStudents}</div>
                </div>
                <div class="card-stat">
                    <div class="stat-icon"><i class="fa-solid fa-file-pen"></i></div>
                    <div class="stat-label">Total Exams</div>
                    <div class="stat-value">${totalExams}</div>
                </div>
                <div class="card-stat">
                    <div class="stat-icon"><i class="fa-solid fa-clipboard-list"></i></div>
                    <div class="stat-label">Total Attempts</div>
                    <div class="stat-value">${totalAttempts}</div>
                </div>
                <div class="card-stat">
                    <div class="stat-icon"><i class="fa-solid fa-chart-line"></i></div>
                    <div class="stat-label">Avg. Score</div>
                    <div class="stat-value text-mono">${avgPercentage}%</div>
                </div>
                <div class="card-stat">
                    <div class="stat-icon" style="background:rgba(16,185,129,0.1); color:var(--color-risk-low);"><i class="fa-solid fa-circle-check"></i></div>
                    <div class="stat-label">Passed</div>
                    <div class="stat-value" style="color:var(--color-risk-low);">${passCount}</div>
                </div>
                <div class="card-stat">
                    <div class="stat-icon" style="background:rgba(244,63,94,0.1); color:var(--color-risk-critical);"><i class="fa-solid fa-circle-xmark"></i></div>
                    <div class="stat-label">Failed</div>
                    <div class="stat-value" style="color:var(--color-risk-critical);">${failCount}</div>
                </div>
                <div class="card-stat">
                    <div class="stat-icon" style="background:rgba(249,115,22,0.1); color:var(--color-risk-high);"><i class="fa-solid fa-triangle-exclamation"></i></div>
                    <div class="stat-label">Violations Logged</div>
                    <div class="stat-value">${totalViolations}</div>
                </div>
                <div class="card-stat">
                    <div class="stat-icon"><i class="fa-solid fa-check-double"></i></div>
                    <div class="stat-label">Total Results</div>
                    <div class="stat-value">${totalResults}</div>
                </div>
            </div>

            <div class="dash-grid-2">
                <!-- Pass vs Fail Doughnut -->
                <div class="panel">
                    <div class="panel-head"><h3>Pass / Fail Distribution</h3></div>
                    <div style="position:relative; width:260px; height:260px; margin:0 auto;">
                        <canvas id="passFailChart"></canvas>
                    </div>
                    <div style="display:flex; justify-content:center; gap:24px; margin-top:16px;">
                        <div style="display:flex; align-items:center; gap:8px; font-size:0.84rem;">
                            <span style="width:12px; height:12px; border-radius:3px; background:var(--color-risk-low);"></span> Passed (${passCount})
                        </div>
                        <div style="display:flex; align-items:center; gap:8px; font-size:0.84rem;">
                            <span style="width:12px; height:12px; border-radius:3px; background:var(--color-risk-critical);"></span> Failed (${failCount})
                        </div>
                    </div>
                </div>

                <!-- Violations Bar Chart -->
                <div class="panel">
                    <div class="panel-head"><h3>Violations by Type</h3></div>
                    <c:choose>
                        <c:when test="${violationLabelsJson == '[]'}">
                            <div class="empty-state">
                                <i class="fa-solid fa-shield-check" style="color:var(--color-risk-low);"></i>
                                <p>No violation events recorded yet.</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <canvas id="violationChart" style="max-height:240px;"></canvas>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="<%=request.getContextPath()%>/assets/js/main.js"></script>
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
<script>
(function() {
    // Pass/Fail donut
    var pfCtx = document.getElementById('passFailChart');
    if (pfCtx) {
        new Chart(pfCtx, {
            type: 'doughnut',
            data: {
                labels: ['Passed', 'Failed'],
                datasets: [{
                    data: [${passCount}, ${failCount}],
                    backgroundColor: ['#10B981', '#F43F5E'],
                    borderWidth: 0,
                    hoverOffset: 6
                }]
            },
            options: {
                cutout: '65%',
                plugins: { legend: { display: false } }
            }
        });
    }

    // Violations bar
    var vCtx = document.getElementById('violationChart');
    if (vCtx) {
        var labels = ${violationLabelsJson};
        var data   = ${violationDataJson};
        new Chart(vCtx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Violation Events',
                    data: data,
                    backgroundColor: 'rgba(79,70,229,0.7)',
                    borderRadius: 6
                }]
            },
            options: {
                indexAxis: 'y',
                plugins: { legend: { display: false } },
                scales: {
                    x: { beginAtZero: true, ticks: { precision: 0 } }
                }
            }
        });
    }
})();
</script>
</body>
</html>
