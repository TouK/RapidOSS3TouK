<%@ page import="auth.Role" %>
<jsec:lacksRole name="${Role.ADMINISTRATOR}">
     <%
         response.sendRedirect("/RapidSuite/auth/unauthorized");
     %>
</jsec:lacksRole>