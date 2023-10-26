<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.Statement" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.ResultSet" %><%--
  Created by IntelliJ IDEA.
  Task.User: Camilo
  Date: 25/10/2023
  Time: 08:34 p. m.
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>All Users</title>
</head>
<body>
<h1>List of Users</h1>
<form action="/welcome" method="post">
    <table>
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Login</th>
        </tr>
        <%
            try {
                Class.forName("org.postgresql.Driver");
                Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/11-200", "postgres", "andrescamilo4");
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("select * from usuarios");

                while (resultSet.next()) {
        %>
        <tr>
            <td><%= resultSet.getInt("id") %></td>
            <td><%= resultSet.getString("first_name") %></td>
            <td><%= resultSet.getString("login") %></td>
        </tr>
        <%
                }
                resultSet.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        %>
    </table>
</form>
</body>
</html>

