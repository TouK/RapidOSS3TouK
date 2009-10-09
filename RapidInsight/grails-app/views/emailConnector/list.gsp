<%@ page import="connector.EmailConnector" %>

<html>
<head>
<head>
    <meta name="layout" content="adminLayout"/>
</head>
<div class="nav">
    <span class="menuButton"><g:link class="create" action="create">New EmailConnector</g:link></span>
</div>
<body class="body">
<h1>EmailConnector List</h1>
<g:render template="/common/messages" model="[flash:flash]"></g:render>


    <div class="list">
        <table>
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Smtp Host</th>
                    <th>Smtp Port</th>
                    <th></th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <g:each in="${EmailConnector.list([sort:'name'])}" status="i" var="emailConnector">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        <g:set var="emailConnection" value="${emailConnector?.ds?.connection}"></g:set>
                        <td><g:link action="show" controller="emailConnector" id="${emailConnector.id}">${emailConnector.name?.encodeAsHTML()}</g:link></td>
                        <td>${emailConnection?.smtpHost?.encodeAsHTML()}</td>
                        <td>${emailConnection?.smtpPort?.encodeAsHTML()}</td>
                        <td><g:link action="testConnection" controller="emailConnector" id="${emailConnector.id}" class="testConnection">Test Connection</g:link></td>
                        <td><g:link action="edit" controller="emailConnector" id="${emailConnector.id}" class="edit">Edit</g:link></td>
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>

</body>
</html>