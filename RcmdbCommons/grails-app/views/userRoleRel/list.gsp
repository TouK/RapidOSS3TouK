
<%@ page import="auth.UserRoleRel" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>UserRoleRel List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New UserRoleRel</g:link></span>
        </div>
        <div class="body">
            <h1>UserRoleRel List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        <g:sortableColumn property="id" title="Id" />
                        
                   	        <th>Role</th>
                   	    
                   	        <th>User</th>
                   	    
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${userRoleRelList}" status="i" var="userRoleRel">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${userRoleRel.id}">${userRoleRel.id?.encodeAsHTML()}</g:link></td>
                        
                            <td>${userRoleRel.role?.encodeAsHTML()}</td>
                        
                            <td>${userRoleRel.rsUser?.encodeAsHTML()}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${UserRoleRel.count()}" />
            </div>
        </div>
    </body>
</html>
