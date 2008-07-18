
<%@ page import="datasource.SnmpDatasource" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>SnmpDatasource List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New SnmpDatasource</g:link></span>
            <span class="menuButton"><a class="logout" href="${createLinkTo(dir: 'auth/logout')}">Logout</a></span>
        </div>
        <div class="body">
            <h1>SnmpDatasource List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        <g:sortableColumn property="id" title="Id" />
                        
                   	        <g:sortableColumn property="name" title="Name" />
                        
                   	        <th>Script</th>
                   	    
                   	        <th>Connection</th>
                   	    
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${snmpDatasourceList}" status="i" var="snmpDatasource">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${snmpDatasource.id}">${snmpDatasource.id?.encodeAsHTML()}</g:link></td>
                        
                            <td>${snmpDatasource.name?.encodeAsHTML()}</td>
                        
                            <td>${snmpDatasource.scriptName?.encodeAsHTML()}</td>
                        
                            <td>${snmpDatasource.connection?.encodeAsHTML()}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${SnmpDatasource.count()}" />
            </div>
        </div>
    </body>
</html>
