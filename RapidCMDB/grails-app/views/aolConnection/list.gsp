
<%@ page import="connection.AolConnection" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="adminLayout" />
        <title>AolConnection List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New AolConnection</g:link></span>
        </div>
        <div class="body">
            <h1>AolConnection List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        <g:sortableColumn property="name" title="Name" />
                        
                   	        <g:sortableColumn property="host" title="Host" />
                        
                   	        <g:sortableColumn property="port" title="Port" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${aolConnectionList}" status="i" var="aolConnection">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${aolConnection.id}">${aolConnection.name?.encodeAsHTML()}</g:link></td>
                        
                            <td>${aolConnection.host?.encodeAsHTML()}</td>
                        
                            <td>${aolConnection.port?.encodeAsHTML()}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${AolConnection.count()}" />
            </div>
        </div>
    </body>
</html>
