<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>RsRiEvent List</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="create" action="create">New RsRiEvent</g:link></span>
    <span class="menuButton"><g:link class="refresh" action="reloadOperations">Reload Operations</g:link></span>
</div>
<div class="body">
    <h1>RsRiEvent List</h1>
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
                    <g:sortableColumn property="active" title="active"/>
                    <g:sortableColumn property="acknowledged" title="acknowledged"/>
                    <g:sortableColumn property="firstNotifiedAt" title="firstNotifiedAt"/>
                    <g:sortableColumn property="lastNotifiedAt" title="lastNotifiedAt"/>
                    <g:sortableColumn property="lastChangedAt" title="lastChangedAt"/>
                    <g:sortableColumn property="lastClearedAt" title="lastClearedAt"/>
                    
                </tr>
            </thead>
            <tbody>
                <g:each in="${rsRiEventList}" status="i" var="rsRiEvent">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                        <td><g:link action="show" id="${rsRiEvent.id}">${rsRiEvent.id?.encodeAsHTML()}</g:link></td>
                        
                        <td>${rsRiEvent.name?.encodeAsHTML()}</td>
                        <td>${rsRiEvent.active?.encodeAsHTML()}</td>
                        <td>${rsRiEvent.acknowledged?.encodeAsHTML()}</td>
                        <td>${rsRiEvent.firstNotifiedAt?.encodeAsHTML()}</td>
                        <td>${rsRiEvent.lastNotifiedAt?.encodeAsHTML()}</td>
                        <td>${rsRiEvent.lastChangedAt?.encodeAsHTML()}</td>
                        <td>${rsRiEvent.lastClearedAt?.encodeAsHTML()}</td>
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>
    <div class="paginateButtons">
        <g:paginate total="${RsRiEvent.count()}"/>
    </div>
</div>
</body>
</html>
