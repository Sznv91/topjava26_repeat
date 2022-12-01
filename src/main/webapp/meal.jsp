<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Meal</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<jsp:useBean id="action" scope="request" type="java.lang.String"/>
<c:set var="action_view" scope="request" value="Add"/>
<c:if test="${action == 'update'}">
    <c:set var="action_view" scope="request" value="Update"/>
</c:if>
<h1>${action_view} Meal</h1>
<jsp:useBean id="meal" scope="request" type="ru.javawebinar.topjava.model.Meal"/>
<form method="POST" action='meals' name="frmAddUser">
    <input type="hidden" name="action" value="${action}"/>
    UUID : <input type="text" readonly="readonly" name="uuid"
                  value="<c:out value="${meal.uuid}" />"/> <br/>
    Description : <input
        type="text" name="description" required
        value="<c:out value="${meal.description}" />"/> <br/>
    Calories : <input
        type="number" name="calories"
        value="<c:out value="${meal.calories}" />"/> <br/>
    DateTime : <input
        type="datetime-local" name="dateTime"
        value="<c:out value="${meal.dateTime}" />"/> <br/><br/>
    <input
            type="submit" value="${action_view}"/>
    <button onclick="window.history.back()"
            type="button">Back</button>
</form>
</body>
</html>
