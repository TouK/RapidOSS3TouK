<%@ page import="connection.HypericConnection; connector.HypericConnector" %>
<%--
  Created by IntelliJ IDEA.
  User: sezgin
  Date: Dec 1, 2008
  Time: 3:05:53 PM
  To change this template use File | Settings | File Templates.
--%>

<html>
<head>
    <meta name="layout" content="adminLayout"/>
</head>
<body>
<div class="nav">
    &nbsp
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
    <div style="margin-top:20px;">
        <table style="width:900;">
            <tr>
                <td>
                    <span style="color:#006DBA;font-size:14px;font-weight:bold;margin:0.8em 0pt 0.3em;">Hyperic Connections</span>
                    <span class="menuButton"><g:link class="create" controller="hypericConnection" action="create">New Hyperic Connection</g:link></span>
                    <div class="list">
                        <table>
                            <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>Base Url</th>
                                </tr>
                            </thead>
                            <tbody>
                                <g:each in="${HypericConnection.list()}" status="i" var="hypericConnection">
                                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                        <td><g:link action="edit" controller="hypericConnection" id="${hypericConnection.id}">${hypericConnection.name?.encodeAsHTML()}</g:link></td>
                                        <td>${hypericConnection?.baseUrl?.encodeAsHTML()}</td>
                                    </tr>
                                </g:each>
                            </tbody>
                        </table>
                    </div>
                </td>
            </tr>
        </table>
    </div>
    <div style="margin-top:20px;">
        <table style="width:900;">
            <tr>
                <td>
                    <span style="color:#006DBA;font-size:14px;font-weight:bold;margin:0.8em 0pt 0.3em;">Hyperic Connector List</span>
                    <span class="menuButton"><g:link class="create" controller="hypericConnector" action="create">New Connector</g:link></span>
                    <div class="list">
                        <table>
                            <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>Connection</th>
                                    <th>Type</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <g:each in="${HypericConnector.list()}" status="i" var="hypericConnector">
                                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                        <td><g:link action="show" controller="hypericConnector" id="${hypericConnector.id}">${hypericConnector.name?.encodeAsHTML()}</g:link></td>
                                        <td><g:link action="show" controller="hypericConnection" id="${hypericConnector.connection.id}">${hypericConnector.connection.name?.encodeAsHTML()}</g:link></td>
                                        <td>${hypericConnector?.type.encodeAsHTML()}</td>
                                        <%
                                            def isEnabled = hypericConnector?.script?.enabled;
                                            if (isEnabled) {
                                        %>
                                        <td><g:link action="stopConnector" controller="hypericConnector" id="${hypericConnector.id}" class="stop">Stop</g:link></td>
                                        <%
                                            }
                                            else {
                                        %>
                                        <td><g:link action="startConnector" controller="hypericConnector" id="${hypericConnector.id}" class="start">Start</g:link></td>
                                        <%
                                            }
                                        %>
                                    </tr>
                                </g:each>
                            </tbody>
                        </table>
                    </div>
                </td>
            </tr>
        </table>
    </div>
</div>

</body>
</html>