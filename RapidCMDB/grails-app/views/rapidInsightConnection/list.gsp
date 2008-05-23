
<%@ page import="connection.RapidInsightConnection" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>RapidInsightConnection List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New RapidInsightConnection</g:link></span>
            <span class="menuButton"><a class="logout" href="${createLinkTo(dir: 'auth/logout')}">Logout</a></span>
        </div>
        <div class="body">
            <h1>RapidInsightConnection List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        <g:sortableColumn property="name" title="Name" />
                        
                   	        <g:sortableColumn property="connectionClass" title="Connection Class" />
                        
                   	        <g:sortableColumn property="baseUrl" title="Base Url" />
                        
                   	        <g:sortableColumn property="username" title="Username" />
                        
                   	        <g:sortableColumn property="password" title="Password" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${rapidInsightConnectionList}" status="i" var="rapidInsightConnection">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${rapidInsightConnection.id}">${rapidInsightConnection.name?.encodeAsHTML()}</g:link></td>
                        
                            <td>${rapidInsightConnection.connectionClass?.encodeAsHTML()}</td>
                        
                            <td>${rapidInsightConnection.baseUrl?.encodeAsHTML()}</td>
                        
                            <td>${rapidInsightConnection.username?.encodeAsHTML()}</td>
                        
                            <td>${rapidInsightConnection.password?.encodeAsHTML()}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${RapidInsightConnection.count()}" />
            </div>
        </div>
    </body>
</html>
