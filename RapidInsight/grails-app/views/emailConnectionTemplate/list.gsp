<%@ page import="connection.EmailConnectionTemplate" %>

<html>
<head>
<head>
    <meta name="layout" content="adminLayout"/>
</head>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir: 'admin.gsp')}">Home</a></span>
    <span class="menuButton"><g:link class="create" action="create">New EmailConnectionTemplate</g:link></span>
</div>
<body class="body">
<h1>EmailConnectionTemplate List</h1>
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
                </tr>
            </thead>
            <tbody>
                <g:each in="${EmailConnectionTemplate.list()}" status="i" var="emailConnectionTemplate">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        <td><g:link action="edit" controller="emailConnectionTemplate" id="${emailConnectionTemplate.id}">${emailConnectionTemplate.name?.encodeAsHTML()}</g:link></td>
                        <td>${emailConnectionTemplate?.emailConnection?.smtpHost?.encodeAsHTML()}</td>
                        <td>${emailConnectionTemplate?.emailConnection?.username?.encodeAsHTML()}</td>
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>

</body>
</html>