

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>RsManagementSystem List</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="create" action="create">New RsManagementSystem</g:link></span>
    <span class="menuButton"><g:link class="refresh" action="reloadOperations">Reload Operations</g:link></span>
</div>
<div class="body">
    <h1>RsManagementSystem List</h1>
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
                    
                    <g:sortableColumn property="name" title="name"/>
                    
                    <g:sortableColumn property="lastPolledAt" title="lastPolledAt"/>
                    
                </tr>
            </thead>
            <tbody>
                <g:each in="${rsManagementSystemList}" status="i" var="rsManagementSystem">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                        <td><g:link action="show" id="${rsManagementSystem.id}">${rsManagementSystem.id?.encodeAsHTML()}</g:link></td>
                        
                        <td>${rsManagementSystem.name?.encodeAsHTML()}</td>
                        
                        <td>${rsManagementSystem.lastPolledAt?.encodeAsHTML()}</td>
                        
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>
    <div class="paginateButtons">
        <g:paginate total="${RsManagementSystem.count()}"/>
    </div>
</div>
</body>
</html>
