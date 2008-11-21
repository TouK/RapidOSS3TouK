
<%@ page import="connection.HypericConnection" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="adminLayout" />
        <title>HypericConnection List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New HypericConnection</g:link></span>
        </div>
        <div class="body">
            <h1>HypericConnection List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        <g:sortableColumn property="name" title="Name" />
                        
                   	        <g:sortableColumn property="baseUrl" title="Base Url" />
                        
                   	        <g:sortableColumn property="username" title="Username" />
                        
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${hypericConnectionList}" status="i" var="hypericConnection">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${hypericConnection.id}">${hypericConnection.name?.encodeAsHTML()}</g:link></td>
                        
                            <td>${hypericConnection.baseUrl?.encodeAsHTML()}</td>
                        
                            <td>${hypericConnection.username?.encodeAsHTML()}</td>

                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${HypericConnection.count()}" />
            </div>
        </div>
    </body>
</html>
