
<%@ page import="connection.RapidInsightConnection" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="adminLayout" />
        <title>RapidInsightConnection List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New RapidInsightConnection</g:link></span>
        </div>
        <div class="body">
            <h1>RapidInsightConnection List</h1>
            <g:render template="/common/messages" model="[flash:flash]"></g:render>
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
                    <g:each in="${rapidInsightConnectionList}" status="i" var="rapidInsightConnection">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${rapidInsightConnection.id}">${rapidInsightConnection.name?.encodeAsHTML()}</g:link></td>
                        
                            <td>${rapidInsightConnection.baseUrl?.encodeAsHTML()}</td>
                        
                            <td>${rapidInsightConnection.username?.encodeAsHTML()}</td>

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
