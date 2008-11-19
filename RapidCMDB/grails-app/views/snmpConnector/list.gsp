<%@ page import="com.ifountain.rcmdb.datasource.ListeningAdapterManager; connector.SnmpConnector" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout"/>
    <title>SnmpConnector List</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><g:link class="create" action="create">New SnmpConnector</g:link></span>
</div>
<div class="body">
    <h1>SnmpConnector List</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${flash.errors}">
        <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <div class="list">
        <table>
            <thead>
                <tr>

                    <g:sortableColumn property="name" title="Name"/>
                    <th>Host</th>
                    <th>Port</th>
                    <th>Script Name</th>
                    <th>Script File</th>
                    <th>Log Level</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <g:each in="${snmpConnectorList}" status="i" var="snmpConnector">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                        <td><g:link action="show" id="${snmpConnector.id}">${snmpConnector.name?.encodeAsHTML()}</g:link></td>

                        <td>${snmpConnector.connection?.host?.encodeAsHTML()}</td>

                        <td>${snmpConnector.connection?.port?.encodeAsHTML()}</td>
                        <td>${snmpConnector.script?.name?.encodeAsHTML()}</td>
                        <td>${snmpConnector.script?.scriptFile?.encodeAsHTML()}</td>
                        <td>${snmpConnector.script?.logLevel?.encodeAsHTML()}</td>
                        <%
                            def isSubscribed = ListeningAdapterManager.getInstance().isSubscribed(snmpConnector.script.listeningDatasource);
                            if (isSubscribed) {
                        %>
                        <td><g:link action="stopConnector" controller="snmpConnector" id="${snmpConnector.id}" class="stop">Stop</g:link></td>
                        <%
                            }
                            else {
                        %>
                        <td><g:link action="startConnector" controller="snmpConnector" id="${snmpConnector.id}" class="start">Start</g:link></td>
                        <%
                            }
                        %>
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>
    <div class="paginateButtons">
        <g:paginate total="${SnmpConnector.count()}"/>
    </div>
</div>
</body>
</html>
