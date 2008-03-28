
<%@ page import="connection.SmartsConnection" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>SmartsConnection List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New SmartsConnection</g:link></span>
        </div>
        <div class="body">
            <h1>SmartsConnection List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>

                   	        <g:sortableColumn property="name" title="Name" />
                        
                   	        <g:sortableColumn property="connectionClass" title="Connection Class" />
                        
                   	        <g:sortableColumn property="broker" title="Broker" />
                        
                   	        <g:sortableColumn property="domain" title="Domain" />
                        
                   	        <g:sortableColumn property="username" title="Username" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${smartsConnectionList}" status="i" var="smartsConnection">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${smartsConnection.id}">${smartsConnection.name?.encodeAsHTML()}</g:link></td>
                        
                            <td>${smartsConnection.connectionClass?.encodeAsHTML()}</td>
                        
                            <td>${smartsConnection.broker?.encodeAsHTML()}</td>
                        
                            <td>${smartsConnection.domain?.encodeAsHTML()}</td>
                        
                            <td>${smartsConnection.username?.encodeAsHTML()}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${SmartsConnection.count()}" />
            </div>
        </div>
    </body>
</html>
