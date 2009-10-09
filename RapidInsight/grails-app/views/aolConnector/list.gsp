<%@ page import="connector.AolConnector" %>

<html>
<head>
<head>
    <meta name="layout" content="adminLayout"/>
</head>
<div class="nav">
    <span class="menuButton"><g:link class="create" action="create">New AolConnector</g:link></span>
</div>
<body class="body">
<h1>AolConnector List</h1>
<g:render template="/common/messages" model="[flash:flash]"></g:render>


    <div class="list">
        <table>
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Host</th>
                    <th>Port</th>
                    <th></th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <g:each in="${AolConnector.list([sort:'name'])}" status="i" var="aolConnector">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        <g:set var="aolConnection" value="${aolConnector?.ds?.connection}"></g:set>
                        <td><g:link action="show" controller="aolConnector" id="${aolConnector.id}">${aolConnector.name?.encodeAsHTML()}</g:link></td>
                        <td>${aolConnection?.host?.encodeAsHTML()}</td>
                        <td>${aolConnection?.port?.encodeAsHTML()}</td>
                        <td><g:link action="testConnection" controller="aolConnector" id="${aolConnector.id}" class="testConnection">Test Connection</g:link></td>
                        <td><g:link action="edit" controller="aolConnector" id="${aolConnector.id}" class="edit">Edit</g:link></td>
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>

</body>
</html>