<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>RsHistoricalEvent List</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="create" action="create">New RsHistoricalEvent</g:link></span>
    <span class="menuButton"><g:link class="refresh" action="reloadOperations">Reload Operations</g:link></span>
</div>
<div class="body">
    <h1>RsHistoricalEvent List</h1>
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
                <g:each in="${rsHistoricalEventList}" status="i" var="rsHistoricalEvent">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                        <td><g:link action="show" id="${rsHistoricalEvent.id}">${rsHistoricalEvent.id?.encodeAsHTML()}</g:link></td>
                        <td>${rsHistoricalEvent.name?.encodeAsHTML()}</td>
                        <td>${rsHistoricalEvent.active?.encodeAsHTML()}</td>
                        <td>${rsHistoricalEvent.acknowledged?.encodeAsHTML()}</td>
                        <td>${rsHistoricalEvent.firstNotifiedAt?.encodeAsHTML()}</td>
                        <td>${rsHistoricalEvent.lastNotifiedAt?.encodeAsHTML()}</td>
                        <td>${rsHistoricalEvent.lastChangedAt?.encodeAsHTML()}</td>
                        <td>${rsHistoricalEvent.lastClearedAt?.encodeAsHTML()}</td>
                        
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>
    <div class="paginateButtons">
        <g:paginate total="${RsHistoricalEvent.count()}"/>
    </div>
</div>
</body>
</html>
