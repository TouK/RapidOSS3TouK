<%@ page import="connection.NetcoolConnection" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main" />
    <title>NetcoolConnection List</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir: 'admin.gsp')}">Home</a></span>
    <span class="menuButton"><g:link class="create" action="create">New NetcoolConnection</g:link></span>
</div>
<div class="body">
    <h1>NetcoolConnection List</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <div class="list">
        <table>
            <thead>
                <tr>

                    <g:sortableColumn property="id" title="Id"/>

                    <g:sortableColumn property="name" title="Name"/>

                    <g:sortableColumn property="host" title="Host"/>

                    <g:sortableColumn property="port" title="Port"/>

                    <g:sortableColumn property="username" title="Username"/>

                </tr>
            </thead>
            <tbody>
                <g:each in="${netcoolConnectionList}" status="i" var="netcoolConnection">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                        <td><g:link action="show" id="${netcoolConnection.id}">${netcoolConnection.id?.encodeAsHTML()}</g:link></td>

                        <td>${netcoolConnection.name?.encodeAsHTML()}</td>

                        <td>${netcoolConnection.host?.encodeAsHTML()}</td>

                        <td>${netcoolConnection.port?.encodeAsHTML()}</td>

                        <td>${netcoolConnection.username?.encodeAsHTML()}</td>

                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>
    <div class="paginateButtons">
        <g:paginate total="${NetcoolConnection.count()}"/>
    </div>
</div>
</body>
</html>
