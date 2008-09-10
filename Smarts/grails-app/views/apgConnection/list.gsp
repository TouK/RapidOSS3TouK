
<%@ page import="connection.ApgConnection" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>ApgConnection List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New ApgConnection</g:link></span>
        </div>
        <div class="body">
            <h1>ApgConnection List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        <g:sortableColumn property="name" title="Name" />

                   	        <g:sortableColumn property="wsdlBaseUrl" title="Wsdl Base Url" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${apgConnectionList}" status="i" var="apgConnection">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${apgConnection.id}">${apgConnection.name?.encodeAsHTML()}</g:link></td>
                        
                            <td>${apgConnection.wsdlBaseUrl?.encodeAsHTML()}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${ApgConnection.count()}" />
            </div>
        </div>
    </body>
</html>
