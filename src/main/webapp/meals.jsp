<%@ page import="ru.javawebinar.topjava.model.MealTo" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<style>
    table, td, th {
        border: 2px solid black;
    }

    table {
        border-collapse: collapse;
    }

    th {
        font-weight: bold;
        padding: 5px;
    }

    td {
        padding: 5px;
    }
</style>
<head>
    <title>Meals</title>
</head>
<body>
<%--<% List<MealTo> mealToList = (List<MealTo>) request.getAttribute("meals");%>
<% for (MealTo mealTo: mealToList) {%>
    <%=mealTo.toString()%>
    <hr>
<%};%>--%>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>
<p><a href="meals?action=create">Add Meal</a></p>
<br>
<jsp:useBean id="formatter" scope="request" type="java.time.format.DateTimeFormatter"/>
<jsp:useBean id="mealToList" scope="request" type="java.util.List<ru.javawebinar.topjava.model.MealTo>"/>
<table>
    <tr>
        <th>Date</th>
        <th>Description</th>
        <th>Calories</th>
        <td></td>
        <td></td>
    </tr>
    <c:forEach var="mealTo" items="${mealToList}">
        <tr>
            <c:set var="td_color" scope="session" value="#008000FF"/>
            <c:if test="${mealTo.excess == true}">
                <c:set var="td_color" scope="session" value="#FF0000"/>
            </c:if>
            <td style="color:${td_color};"><c:out value="${mealTo.dateTime.format(formatter)}"/></td>
            <td style="color:${td_color};"><c:out value="${mealTo.description}"/></td>
            <td style="color:${td_color};"><c:out value="${mealTo.calories}"/></td>
            <td><a href="meals?action=update&uuid=${mealTo.uuid}">Update</a></td>
            <td><a href="meals?action=delete&uuid=${mealTo.uuid}">Delete</a></td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
