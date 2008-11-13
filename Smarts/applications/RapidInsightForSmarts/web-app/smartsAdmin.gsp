<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Nov 12, 2008
  Time: 5:56:38 PM
--%>

<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Sep 9, 2008
  Time: 2:30:57 PM
--%>

<%@ page import="com.ifountain.rcmdb.datasource.ListeningAdapterManager; connector.SmartsListeningTopologyConnector" contentType="text/html;charset=UTF-8" %>
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
                    <span style="color:#006DBA;font-size:14px;font-weight:bold;margin:0.8em 0pt 0.3em;">Connection Data</span>
                    <span class="menuButton"><g:link class="create" controller="smartsConnectionTemplate" action="create">New Connection Data</g:link></span>
                    <div class="list">
                        <table>
                            <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>Broker</th>
                                </tr>
                            </thead>
                            <tbody>
                                <g:each in="${connection.SmartsConnectionTemplate.list()}" status="i" var="smartsConnectionTemplate">
                                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                        <td><g:link action="edit" controller="smartsConnectionTemplate" id="${smartsConnectionTemplate.id}">${smartsConnectionTemplate.name?.encodeAsHTML()}</g:link></td>
                                        <td>${smartsConnectionTemplate?.broker?.encodeAsHTML()}</td>
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
                    <span style="color:#006DBA;font-size:14px;font-weight:bold;margin:0.8em 0pt 0.3em;">Smarts Connector List</span>
                    <span class="menuButton"><g:link class="create" controller="smartsConnector" action="create">New Connector</g:link></span>
                    <div class="list">
                        <table>
                            <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>Domain</th>
                                    <th>Connection Configuration Data</th>
                                    <th>Type</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <g:each in="${connector.SmartsConnector.list()}" status="i" var="smartsConnector">
                                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                        <td><g:link action="show" controller="smartsConnector" id="${smartsConnector.id}">${smartsConnector.name?.encodeAsHTML()}</g:link></td>
                                        <td>${smartsConnector?.ds?.connection?.domain?.encodeAsHTML()}</td>
                                        <td><g:link action="show" controller="smartsConnectionTemplate" id="${smartsConnector.connectionTemplate.id}">${smartsConnector.connectionTemplate.name?.encodeAsHTML()}</g:link></td>
                                        <td>${smartsConnector instanceof SmartsListeningTopologyConnector ? "Topology" : "Notification"}</td>
                                        <%
                                            def isSubscribed = ListeningAdapterManager.getInstance().isSubscribed(smartsConnector.ds);
                                            if (isSubscribed) {
                                        %>
                                        <td><g:link action="stopConnector" controller="smartsConnector" id="${smartsConnector.id}" class="stop">Stop</g:link></td>
                                        <%
                                            }
                                            else {
                                        %>
                                        <td><g:link action="startConnector" controller="smartsConnector" id="${smartsConnector.id}" class="start">Start</g:link></td>
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