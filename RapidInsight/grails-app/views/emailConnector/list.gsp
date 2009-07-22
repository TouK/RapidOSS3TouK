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
                    <th>Smtp Host</th>
                    <th>Username</th>
                    <th></th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <g:each in="${EmailConnector.list()}" status="i" var="emailConnector">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        <td><g:link action="show" controller="emailConnector" id="${emailConnector.id}">${emailConnector.name?.encodeAsHTML()}</g:link></td>
                        <td>${emailConnector?.ds?.connection?.smtpHost?.encodeAsHTML()}</td>
                        <td>${emailConnector?.ds?.connection?.username?.encodeAsHTML()}</td>
                        <td><g:link action="testConnection" controller="emailConnector" id="${emailConnector.id}" class="testConnection">Test Connection</g:link></td>
                        <td><g:link action="edit" controller="emailConnector" id="${emailConnector.id}" class="edit">Edit</g:link></td>
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>

</body>
</html>