<%@ page import="connector.JabberConnector" %>

<html>
<head>
<head>
    <meta name="layout" content="adminLayout"/>
</head>
<div class="nav">
    <span class="menuButton"><g:link class="create" action="create">New JabberConnector</g:link></span>
</div>
<body class="body">
<h1>JabberConnector List</h1>
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
                    <th>Name</th>
                    <th>Host</th>
                    <th>Port</th>
                    <th>Service Name</th>
                    <th></th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <g:each in="${JabberConnector.list()}" status="i" var="jabberConnector">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        <g:set var="jabberConnection" value="${jabberConnector?.ds?.connection}"></g:set>
                        <td><g:link action="show" controller="jabberConnector" id="${jabberConnector.id}">${jabberConnector.name?.encodeAsHTML()}</g:link></td>
                        <td>${jabberConnection?.host?.encodeAsHTML()}</td>
                        <td>${jabberConnection?.port?.encodeAsHTML()}</td>
                        <td>${jabberConnection?.serviceName?.encodeAsHTML()}</td>
                        <td><g:link action="testConnection" controller="jabberConnector" id="${jabberConnector.id}" class="testConnection">Test Connection</g:link></td>
                        <td><g:link action="edit" controller="jabberConnector" id="${jabberConnector.id}" class="edit">Edit</g:link></td>
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>

</body>
</html>