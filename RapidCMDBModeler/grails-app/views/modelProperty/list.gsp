<%@ page import="model.*" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>ModelProperty List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'/admin.gsp')}">Home</a></span>
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
                        
                   	        <g:sortableColumn property="name" title="Name" />
                        
                   	        <th>Static Datasource Name</th>
                   	    
                   	        <th>Dynamic Datasource (specified in property)</th>
                   	    
                   	        <g:sortableColumn property="type" title="Type" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${modelPropertyList}" status="i" var="modelProperty">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${modelProperty.id}">${modelProperty.name?.encodeAsHTML()}</g:link></td>
                        
                            <td>${modelProperty.propertyDatasource?.encodeAsHTML()}</td>
                        
                            <td>${modelProperty.propertySpecifyingDatasource?.encodeAsHTML()}</td>
                        
                            <td>${modelProperty.type?.encodeAsHTML()}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total='${ModelProperty.countHits("id:*")}' />
            </div>
        </div>
    </body>
</html>
