
<%@ page import="datasource.SmartsTopologyDatasource" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>SmartsTopologyDatasource List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New SmartsTopologyDatasource</g:link></span>
        </div>
        <div class="body">
            <h1>SmartsTopologyDatasource List</h1>
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
                    <g:each in="${smartsTopologyDatasourceList}" status="i" var="smartsTopologyDatasource">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${smartsTopologyDatasource.id}">${smartsTopologyDatasource.name?.encodeAsHTML()}</g:link></td>
                        
                            <td>${smartsTopologyDatasource.connection?.encodeAsHTML()}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${SmartsTopologyDatasource.count()}" />
            </div>
        </div>
    </body>
</html>
