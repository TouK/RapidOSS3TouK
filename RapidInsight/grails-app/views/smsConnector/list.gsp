<%@ page import="connector.SmsConnector" %>

<html>
<head>
<head>
    <meta name="layout" content="adminLayout"/>
</head>
<div class="nav">
    <span class="menuButton"><g:link class="create" action="create">New SmsConnector</g:link></span>
</div>
<body class="body">
<h1>SmsConnector List</h1>
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
                    <th></th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <g:each in="${SmsConnector.list([sort:'name'])}" status="i" var="smsConnector">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        <g:set var="smsConnection" value="${smsConnector?.ds?.connection}"></g:set>
                        <td><g:link action="show" controller="smsConnector" id="${smsConnector.id}">${smsConnector.name?.encodeAsHTML()}</g:link></td>
                        <td>${smsConnection?.host?.encodeAsHTML()}</td>
                        <td>${smsConnection?.port?.encodeAsHTML()}</td>
                        <td><g:link action="testConnection" controller="smsConnector" id="${smsConnector.id}" class="testConnection">Test Connection</g:link></td>
                        <td><g:link action="edit" controller="smsConnector" id="${smsConnector.id}" class="edit">Edit</g:link></td>
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>

</body>
</html>