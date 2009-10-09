
<%@ page import="connection.JabberConnection" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="adminLayout" />
        <title>JabberConnection List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New JabberConnection</g:link></span>
        </div>
        <div class="body">
            <h1>JabberConnection List</h1>
            <g:render template="/common/messages" model="[flash:flash]"></g:render>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        
                        
                   	        <g:sortableColumn property="name" title="Name" />
                        
                   	        <g:sortableColumn property="host" title="Host" />
                   	        <g:sortableColumn property="port" title="Port" />

                   	        <g:sortableColumn property="serviceName" title="Service Name" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${jabberConnectionList}" status="i" var="jabberConnection">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${jabberConnection.id}">${jabberConnection.name?.encodeAsHTML()}</g:link></td>
                        
                            <td>${jabberConnection.host?.encodeAsHTML()}</td>
                            <td>${jabberConnection.port?.encodeAsHTML()}</td>

                            <td>${jabberConnection.serviceName?.encodeAsHTML()}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${JabberConnection.count()}" />
            </div>
        </div>
    </body>
</html>
