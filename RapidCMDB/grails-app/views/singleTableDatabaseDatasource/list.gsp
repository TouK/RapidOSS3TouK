
<%@ page import="datasource.SingleTableDatabaseDatasource" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="adminLayout" />
        <title>SingleTableDatabaseDatasource List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New SingleTableDatabaseDatasource</g:link></span>
        </div>
        <div class="body">
            <h1>SingleTableDatabaseDatasource List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        <g:sortableColumn property="name" title="Name" />
                        
                   	        <g:sortableColumn property="tableName" title="Table Name" />
                        
                   	        <g:sortableColumn property="tableKeys" title="Table Keys" />
                        
                   	        <th>Connection</th>
                   	    
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${singleTableDatabaseDatasourceList}" status="i" var="singleTableDatabaseDatasource">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${singleTableDatabaseDatasource.id}">${singleTableDatabaseDatasource.name?.encodeAsHTML()}</g:link></td>
                        
                            <td>${singleTableDatabaseDatasource.tableName?.encodeAsHTML()}</td>
                        
                            <td>${singleTableDatabaseDatasource.tableKeys?.encodeAsHTML()}</td>
                        
                            <td>${singleTableDatabaseDatasource.connection?.encodeAsHTML()}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${SingleTableDatabaseDatasource.count()}" />
            </div>
        </div>
    </body>
</html>
