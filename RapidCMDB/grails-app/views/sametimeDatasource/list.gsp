
<%@ page import="datasource.SametimeDatasource" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="adminLayout" />
        <title>SametimeDatasource List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New SametimeDatasource</g:link></span>
        </div>
        <div class="body">
            <h1>SametimeDatasource List</h1>
            <g:render template="/common/messages" model="[flash:flash]"></g:render>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                   	        <g:sortableColumn property="name" title="Name" />
                   	        <th>Connection</th>
                   	    
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${sametimeDatasourceList}" status="i" var="sametimeDatasource">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${sametimeDatasource.id}">${sametimeDatasource.name?.encodeAsHTML()}</g:link></td>
                            <td>${sametimeDatasource.connection?.encodeAsHTML()}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${SametimeDatasource.count()}" />
            </div>
        </div>
    </body>
</html>
