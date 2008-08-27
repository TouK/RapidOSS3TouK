

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>RsNotification List</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="create" action="create">New RsNotification</g:link></span>
    <span class="menuButton"><g:link class="refresh" action="reloadOperations">Reload Operations</g:link></span>
</div>
<div class="body">
    <h1>RsNotification List</h1>
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
                    
                    <g:sortableColumn property="className" title="className"/>
                    
                    <g:sortableColumn property="eventName" title="eventName"/>
                    
                    <g:sortableColumn property="instanceName" title="instanceName"/>
                    
                </tr>
            </thead>
            <tbody>
                <g:each in="${rsNotificationList}" status="i" var="rsNotification">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                        <td><g:link action="show" id="${rsNotification.id}">${rsNotification.id?.encodeAsHTML()}</g:link></td>
                        
                        <td>${rsNotification.className?.encodeAsHTML()}</td>
                        
                        <td>${rsNotification.eventName?.encodeAsHTML()}</td>
                        
                        <td>${rsNotification.instanceName?.encodeAsHTML()}</td>
                        
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>
    <div class="paginateButtons">
        <g:paginate total="${RsNotification.countHits('id:[0 TO *]')}"/>
    </div>
</div>
</body>
</html>
