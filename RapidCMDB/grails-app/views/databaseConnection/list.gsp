
<%@ page import="connection.DatabaseConnection" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>DatabaseConnection List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New DatabaseConnection</g:link></span>
        </div>
        <div class="body">
            <h1>DatabaseConnection List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        <g:sortableColumn property="name" title="Name" />
                        
                   	        <g:sortableColumn property="connectionClass" title="Connection Class" />
                        
                   	        <g:sortableColumn property="driver" title="Driver" />
                        
                   	        <g:sortableColumn property="url" title="Url" />
                        
                   	        <g:sortableColumn property="username" title="Username" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${databaseConnectionList}" status="i" var="databaseConnection">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${databaseConnection.id}">${databaseConnection.name?.encodeAsHTML()}</g:link></td>

                            <td>${databaseConnection.connectionClass?.encodeAsHTML()}</td>
                        
                            <td>${databaseConnection.driver?.encodeAsHTML()}</td>
                        
                            <td>${databaseConnection.url?.encodeAsHTML()}</td>
                        
                            <td>${databaseConnection.username?.encodeAsHTML()}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${DatabaseConnection.count()}" />
            </div>
        </div>
    </body>
</html>
