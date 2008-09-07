

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Service List</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="create" action="create">New Service</g:link></span>
    <span class="menuButton"><g:link class="refresh" action="reloadOperations">Reload Operations</g:link></span>
</div>
<div class="body">
    <h1>Service List</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <div class="list">
        <table>
            <thead>
                <tr>
                    
                    <g:sortableColumn property="id" title="id"/>
                    
                    <g:sortableColumn property="resource_name" title="resource_name"/>
                    
                    <th>serviceOf</th>
                    
                    <g:sortableColumn property="status" title="status"/>
                    
                </tr>
            </thead>
            <tbody>
                <g:each in="${serviceList}" status="i" var="service">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                        <td><g:link action="show" id="${service.id}">${service.id?.encodeAsHTML()}</g:link></td>
                        
                        <td>${service.resource_name?.encodeAsHTML()}</td>
                        
                        <td><g:link action="show" controller="server" id="${service.serviceOf?.id}">${service.serviceOf?.encodeAsHTML()}</g:link></td>
                        
                        <td>${service.status?.encodeAsHTML()}</td>
                        
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>
    <div class="paginateButtons">
        <g:paginate total="${Service.count()}"/>
    </div>
</div>
</body>
</html>
