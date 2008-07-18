
<%@ page import="connection.SnmpConnection" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>SnmpConnection List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New SnmpConnection</g:link></span>
            <span class="menuButton"><a class="logout" href="${createLinkTo(dir: 'auth/logout')}">Logout</a></span>
        </div>
        <div class="body">
            <h1>SnmpConnection List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        <g:sortableColumn property="id" title="Id" />
                        
                   	        <g:sortableColumn property="name" title="Name" />
                        
                   	        <g:sortableColumn property="host" title="Host" />
                        
                   	        <g:sortableColumn property="port" title="Port" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${snmpConnectionList}" status="i" var="snmpConnection">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${snmpConnection.id}">${snmpConnection.id?.encodeAsHTML()}</g:link></td>
                        
                            <td>${snmpConnection.name?.encodeAsHTML()}</td>
                        
                            <td>${snmpConnection.host?.encodeAsHTML()}</td>
                        
                            <td>${snmpConnection.port?.encodeAsHTML()}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${SnmpConnection.count()}" />
            </div>
        </div>
    </body>
</html>
