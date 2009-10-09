
<%@ page import="connection.HttpConnection" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="adminLayout" />
        <title>HttpConnection List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New HttpConnection</g:link></span>
        </div>
        <div class="body">
            <h1>HttpConnection List</h1>
            <g:render template="/common/messages" model="[flash:flash]"></g:render>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        <g:sortableColumn property="name" title="Name" />
                        
                   	        <g:sortableColumn property="baseUrl" title="Base Url" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${httpConnectionList}" status="i" var="httpConnection">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${httpConnection.id}">${httpConnection.name?.encodeAsHTML()}</g:link></td>
                        
                            <td>${httpConnection.baseUrl?.encodeAsHTML()}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${HttpConnection.count()}" />
            </div>
        </div>
    </body>
</html>
