
<%@ page import="datasource.ApgDatabaseDatasource" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>ApgDatabaseDatasource List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New ApgDatabaseDatasource</g:link></span>
        </div>
        <div class="body">
            <h1>ApgDatabaseDatasource List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        <g:sortableColumn property="name" title="Name" />
                        
                   	        <th>Connection</th>
                   	    
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${apgDatabaseDatasourceList}" status="i" var="apgDatabaseDatasource">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${apgDatabaseDatasource.id}">${apgDatabaseDatasource.name?.encodeAsHTML()}</g:link></td>
                        
                            <td>${apgDatabaseDatasource.connection?.encodeAsHTML()}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${ApgDatabaseDatasource.count()}" />
            </div>
        </div>
    </body>
</html>
