<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="pluginResources" scope="request" type="java.lang.String"/>
<jsp:useBean id="report" scope="request"
             type="jetbrains.buildServer.report.paramsUsage.report.view.ParametersUsageReportView"/>

<table class="highlightable settings userProfileTable">
    <thead>
    <tr>
        <th>Parameter</th>
        <th>Defined In</th>
        <th>Used In</th>
    </tr>
    </thead>
    <tbody>

    <c:forEach items="${report.parameters}" var="parameter">
        <tr>
            <td><c:out value="${parameter.name}"/></td>
            <td>
                <c:if test="${not empty parameter.definedAt }">
                    <ul style="list-style: none; padding:0; margin: 0">
                        <c:forEach items="${parameter.definedAt}" var="link">
                            <li>
                                <a href="${link.linkHref}">${link.linkText}</a>
                            </li>
                        </c:forEach>
                    </ul>
                </c:if>
            </td>
            <td>
                <c:if test="${not empty parameter.usedAt }">
                    <ul style="list-style: none; margin: 0; padding: 0">
                        <c:forEach items="${parameter.usedAt}" var="link">
                            <li>
                                <a href="${link.linkHref}">${link.linkText}</a>
                            </li>
                        </c:forEach>
                    </ul>
                </c:if>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>