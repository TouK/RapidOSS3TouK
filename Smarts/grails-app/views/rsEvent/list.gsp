<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>RsEvent List</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="create" action="create">New RsEvent</g:link></span>
    <span class="menuButton"><g:link class="refresh" action="reloadOperations">Reload Operations</g:link></span>
</div>
<div class="body">
    <h1>RsEvent List</h1>
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
                    <g:sortableColumn property="lastChangedAt" title="lastChangedAt"/>
                    <g:sortableColumn property="lastClearedAt" title="lastClearedAt"/>
                    
                </tr>
            </thead>
            <tbody>
                <g:each in="${rsEventList}" status="i" var="rsEvent">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                        <td><g:link action="show" id="${rsEvent.id}">${rsEvent.id?.encodeAsHTML()}</g:link></td>
                        
                        <td>${rsEvent.name?.encodeAsHTML()}</td>
                        <td>${rsEvent.active?.encodeAsHTML()}</td>
                        <td>${rsEvent.acknowledged?.encodeAsHTML()}</td>
                        <td>${rsEvent.firstNotifiedAt?.encodeAsHTML()}</td>
                        <td>${rsEvent.lastChangedAt?.encodeAsHTML()}</td>
                        <td>${rsEvent.lastClearedAt?.encodeAsHTML()}</td>
                        
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>
    <div class="paginateButtons">
        <g:paginate total="${RsEvent.count()}"/>
    </div>
</div>
</body>
</html>
