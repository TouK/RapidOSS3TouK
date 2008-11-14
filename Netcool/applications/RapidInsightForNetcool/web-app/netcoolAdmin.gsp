<%@ page import="script.CmdbScript; connector.NetcoolConnector" %>
<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Nov 14, 2008
  Time: 10:39:15 AM
--%>

<html>
<head>
    <meta name="layout" content="adminLayout"/>
</head>
<body>

<div class="nav">
    <span class="menuButton"><g:link class="create" action="create" controller="netcoolConnector">New Connector</g:link></span>
</div>
<g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
</g:if>
<g:hasErrors bean="${flash.errors}">
    <div class="errors">
        <g:renderErrors bean="${flash.errors}"/>
    </div>
</g:hasErrors>
<div class="body">
    <br><br>
    <div class="list">
        <table>
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Host</th>
                    <th>Port</th>
                    <th>Log Level</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>

                <g:each in="${NetcoolConnector.list()}" status="i" var="netcoolConnector">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        <%
                            def netcoolConnection = NetcoolConnection.get(name: NetcoolConnector.getConnectionName(netcoolConnector.name));
                        %>
                        <td><g:link action="edit" controller="netcoolConnector" id="${netcoolConnector.id}">${netcoolConnector.name?.encodeAsHTML()}</g:link></td>

                        <td>${netcoolConnection?.host?.encodeAsHTML()}</td>

                        <td>${netcoolConnection?.port?.encodeAsHTML()}</td>
                        <td>${netcoolConnector.logLevel?.encodeAsHTML()}</td>
                        <%
                            def connScript = CmdbScript.get(name: NetcoolConnector.getScriptName(netcoolConnector.name));
                            if (connScript?.enabled) {
                        %>
                        <td><g:link action="stopConnector" controller="netcoolConnector" id="${netcoolConnector.id}" class="stop">Stop</g:link></td>
                        <%
                            }
                            else {
                        %>
                        <td><g:link action="startConnector" controller="netcoolConnector" id="${netcoolConnector.id}" class="start">Start</g:link></td>
                        <%
                            }
                        %>

                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>
</div>

</body>
</html>