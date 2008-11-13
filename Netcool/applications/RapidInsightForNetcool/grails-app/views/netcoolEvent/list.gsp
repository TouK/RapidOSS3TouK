

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>NetcoolEvent List</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="create" action="create">New NetcoolEvent</g:link></span>
    <span class="menuButton"><g:link class="refresh" action="reloadOperations">Reload Operations</g:link></span>
</div>
<div class="body">
    <h1>NetcoolEvent List</h1>
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
                    
                    <g:sortableColumn property="servername" title="servername"/>
                    
                    <g:sortableColumn property="serverserial" title="serverserial"/>
                    
                </tr>
            </thead>
            <tbody>
                <g:each in="${netcoolEventList}" status="i" var="netcoolEvent">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                        <td><g:link action="show" id="${netcoolEvent.id}">${netcoolEvent.id?.encodeAsHTML()}</g:link></td>
                        
                        <td>${netcoolEvent.servername?.encodeAsHTML()}</td>
                        
                        <td>${netcoolEvent.serverserial?.encodeAsHTML()}</td>
                        
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>
    <div class="paginateButtons">
        <g:paginate total="${NetcoolEvent.count()}"/>
    </div>
</div>
</body>
</html>
