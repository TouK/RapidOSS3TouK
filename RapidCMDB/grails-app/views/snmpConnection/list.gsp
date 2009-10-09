
<%@ page import="connection.SnmpConnection" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="adminLayout" />
        <title>SnmpConnection List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New SnmpConnection</g:link></span>
        </div>
        <div class="body">
            <h1>SnmpConnection List</h1>
            <g:render template="/common/messages" model="[flash:flash]"></g:render>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        
                        
                   	        <g:sortableColumn property="name" title="Name" />
                        
                   	        <g:sortableColumn property="host" title="Host" />
                        
                   	        <g:sortableColumn property="port" title="Port" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${snmpConnectionList}" status="i" var="snmpConnection">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${snmpConnection.id}">${snmpConnection.name?.encodeAsHTML()}</g:link></td>
                        
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
