<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- common/flash-messages.jsp : include at the top of app-content. Reads and
     clears one-time flash messages set by redirect-after-POST servlets. --%>
<c:if test="${not empty sessionScope.flashSuccess}">
    <div class="alert-banner success" style="margin-bottom:20px;">
        <i class="fa-solid fa-circle-check"></i> <span>${sessionScope.flashSuccess}</span>
    </div>
    <c:remove var="flashSuccess" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.flashError}">
    <div class="alert-banner error" style="margin-bottom:20px;">
        <i class="fa-solid fa-circle-exclamation"></i> <span>${sessionScope.flashError}</span>
    </div>
    <c:remove var="flashError" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.flashRowErrors}">
    <div class="alert-banner error" style="margin-bottom:20px; flex-direction:column; align-items:flex-start;">
        <strong style="margin-bottom:6px;"><i class="fa-solid fa-triangle-exclamation"></i> Some rows were skipped:</strong>
        <ul style="margin:0; padding-left:18px;">
            <c:forEach var="rowErr" items="${sessionScope.flashRowErrors}">
                <li>${rowErr}</li>
            </c:forEach>
        </ul>
    </div>
    <c:remove var="flashRowErrors" scope="session"/>
</c:if>
