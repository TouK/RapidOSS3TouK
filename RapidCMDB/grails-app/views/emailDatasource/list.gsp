<%@ page import="datasource.EmailDatasource" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout"/>
    <title>EmailDatasource List</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="create" action="create">New EmailDatasource</g:link></span>
    <span class="menuButton"><g:link class="refresh" action="reloadOperations">Reload Operations</g:link></span>
</div>
<div class="body">
    <h1>EmailDatasource List</h1>
    <g:render template="/common/messages" model="[flash:flash]"></g:render>
    <div class="list">
        <table>
            <thead>
                <tr>
                    
                    <g:sortableColumn property="name" title="Name" />
                    
                    <th>connection</th>
                    

                    
                </tr>
            </thead>
            <tbody>
                <g:each in="${emailDatasourceList}" status="i" var="emailDatasource">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                        <td><g:link action="show" id="${emailDatasource.id}">${emailDatasource.name?.encodeAsHTML()}</g:link></td>
                        
                        <td>${emailDatasource.connection?.encodeAsHTML()}</td>
                        

                        
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>
    <div class="paginateButtons">
        <g:paginate total="${EmailDatasource.count()}"/>
    </div>
</div>
</body>
</html>
