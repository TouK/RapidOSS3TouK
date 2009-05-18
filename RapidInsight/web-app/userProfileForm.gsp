<%@ page import="auth.RsUser" %>
<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Apr 3, 2009
  Time: 5:58:16 PM
--%>
<%
    def rsUser = RsUser.get(username:session.username);
%>
<rui:formRemote method="POST" action="rsUser/changeProfile?format=xml" componentId="${params.componentId}">
    <table>
        <tr><td width="50%"><label>Old Password:</label></td><td width="50%"><input type="password" name="oldPassword" style="width:175px"/></td></tr>
        <tr><td width="50%"><label>New Password:</label></td><td width="50%"><input type="password" name="password1" style="width:175px"/></td></tr>
        <tr><td width="50%"><label>Confirm Password:</label></td><td width="50%"><input type="password" name="password2" style="width:175px"/></td></tr>
        <tr><td width="50%"><label>Email:</label></td><td width="50%"><input type="text" name="email" style="width:175px" value="${rsUser.retrieveEmail()}"/></td></tr>
    </table>
    <input type="hidden" name="username" value="${rsUser.username}">
</rui:formRemote>