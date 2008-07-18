
<%@ page import="datasource.RapidInsightDatasource" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>RapidInsightDatasource List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New RapidInsightDatasource</g:link></span>
            <span class="menuButton"><a class="logout" href="${createLinkTo(dir: 'auth/logout')}">Logout</a></span>
        </div>
        <div class="body">
            <h1>RapidInsightDatasource List</h1>
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
                    <g:each in="${rapidInsightDatasourceList}" status="i" var="rapidInsightDatasource">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${rapidInsightDatasource.id}">${rapidInsightDatasource.name?.encodeAsHTML()}</g:link></td>
                        
                            <td>${rapidInsightDatasource.connection?.encodeAsHTML()}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${RapidInsightDatasource.count()}" />
            </div>
        </div>
    </body>
</html>
