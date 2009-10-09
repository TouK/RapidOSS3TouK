
<%@ page import="connection.SametimeConnection" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="adminLayout" />
        <title>SametimeConnection List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New SametimeConnection</g:link></span>
        </div>
        <div class="body">
            <h1>SametimeConnection List</h1>
            <g:render template="/common/messages" model="[flash:flash]"></g:render>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        
                        
                   	        <g:sortableColumn property="name" title="Name" />
                        
                   	        <g:sortableColumn property="host" title="Host" />
                        
                   	        <g:sortableColumn property="community" title="Community" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${sametimeConnectionList}" status="i" var="sametimeConnection">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${sametimeConnection.id}">${sametimeConnection.name?.encodeAsHTML()}</g:link></td>
                        
                            <td>${sametimeConnection.host?.encodeAsHTML()}</td>
                        
                            <td>${sametimeConnection.community?.encodeAsHTML()}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${SametimeConnection.count()}" />
            </div>
        </div>
    </body>
</html>
