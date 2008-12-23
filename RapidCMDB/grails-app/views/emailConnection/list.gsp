
<%@ page import="connection.EmailConnection" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>EmailConnection List</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="create" action="create">New EmailConnection</g:link></span>
</div>
<div class="body">
    <h1>EmailConnection List</h1>
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
                    <g:sortableColumn property="smtpHost" title="Smtp Host"/>

                </tr>
            </thead>
            <tbody>
                <g:each in="${emailConnectionList}" status="i" var="emailConnection">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                        <td><g:link action="show" id="${emailConnection.id}">${emailConnection.name?.encodeAsHTML()}</g:link></td>
                        <td>${emailConnection.smtpHost?.encodeAsHTML()}</td>
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>
    <div class="paginateButtons">
        <g:paginate total="${EmailConnection.count()}"/>
    </div>
</div>
</body>
</html>
