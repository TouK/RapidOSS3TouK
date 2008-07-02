<%@ page import="model.*" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>ModelDatasourceKeyMapping List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
            <span class="menuButton"><a class="logout" href="${createLinkTo(dir: 'auth/logout')}">Logout</a></span>
        </div>
        <div class="body">
            <h1>ModelDatasourceKeyMapping List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        <g:sortableColumn property="property" title="Property" />
                        
                   	        <th>Datasource</th>
                   	    
                   	        <g:sortableColumn property="nameInDatasource" title="Name In Datasource" />
                        
                   	    
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${modelDatasourceKeyMappingList}" status="i" var="modelDatasourceKeyMapping">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${modelDatasourceKeyMapping.id}">${modelDatasourceKeyMapping.property?.encodeAsHTML()}</g:link></td>
                        
                            <td>${modelDatasourceKeyMapping.datasource?.encodeAsHTML()}</td>
                        
                            <td>${modelDatasourceKeyMapping.nameInDatasource?.encodeAsHTML()}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total='${ModelDatasourceKeyMapping.countHits("id:*")}' />
            </div>
        </div>
    </body>
</html>
