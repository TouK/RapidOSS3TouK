
<%@ page import="connection.EmailConnection" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout"/>
    <title>EmailConnection List</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="create" action="create">New EmailConnection</g:link></span>
</div>
<div class="body">
    <h1>EmailConnection List</h1>
    <g:render template="/common/messages" model="[flash:flash]"></g:render>
    <div class="list">
        <table>
            <thead>
                <tr>

                    <g:sortableColumn property="name" title="Name"/>
                    <g:sortableColumn property="smtpHost" title="Smtp Host"/>
                    <g:sortableColumn property="smtpPort" title="Smtp Port"/>

                </tr>
            </thead>
            <tbody>
                <g:each in="${emailConnectionList}" status="i" var="emailConnection">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                        <td><g:link action="show" id="${emailConnection.id}">${emailConnection.name?.encodeAsHTML()}</g:link></td>
                        <td>${emailConnection.smtpHost?.encodeAsHTML()}</td>
                        <td>${emailConnection.smtpPort?.encodeAsHTML()}</td>
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
