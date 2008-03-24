<%@ page import="model.*" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>ModelProperty List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New ModelProperty</g:link></span>
        </div>
        <div class="body">
            <h1>ModelProperty List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        <g:sortableColumn property="id" title="Id" />
                        
                   	        <g:sortableColumn property="name" title="Name" />
                        
                   	        <th>Datasource Name</th>
                   	    
                   	        <th>Property Specifying Datasource</th>
                   	    
                   	        <g:sortableColumn property="type" title="Type" />
                        
                   	        <g:sortableColumn property="blank" title="Blank" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${modelPropertyList}" status="i" var="modelProperty">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${modelProperty.id}">${modelProperty.id?.encodeAsHTML()}</g:link></td>
                        
                            <td>${modelProperty.name?.encodeAsHTML()}</td>
                        
                            <td>${modelProperty.modelDatasourceId?.encodeAsHTML()}</td>
                        
                            <td>${modelProperty.propertySpecifyingDatasource?.encodeAsHTML()}</td>
                        
                            <td>${modelProperty.type?.encodeAsHTML()}</td>
                        
                            <td>${modelProperty.blank?.encodeAsHTML()}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${ModelProperty.count()}" />
            </div>
        </div>
    </body>
</html>
