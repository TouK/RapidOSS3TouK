

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>RsService List</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="create" action="create">New RsService</g:link></span>
    <span class="menuButton"><g:link class="refresh" action="reloadOperations">Reload Operations</g:link></span>
</div>
<div class="body">
    <h1>RsService List</h1>
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
                    
                    <g:sortableColumn property="interval" title="interval"/>
                    
                    <g:sortableColumn property="lastChangedAt" title="lastChangedAt"/>
                    
                    <g:sortableColumn property="status" title="status"/>
                    
                </tr>
            </thead>
            <tbody>
                <g:each in="${rsServiceList}" status="i" var="rsService">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                        <td><g:link action="show" id="${rsService.id}">${rsService.id?.encodeAsHTML()}</g:link></td>
                        
                        <td>${rsService.name?.encodeAsHTML()}</td>
                        
                        <td>${rsService.interval?.encodeAsHTML()}</td>
                        
                        <td>${rsService.lastChangedAt?.encodeAsHTML()}</td>
                        
                        <td>${rsService.status?.encodeAsHTML()}</td>
                        
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>
    <div class="paginateButtons">
        <g:paginate total="${RsService.count()}"/>
    </div>
</div>
</body>
</html>
