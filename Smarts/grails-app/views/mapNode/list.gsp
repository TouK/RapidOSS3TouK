

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>MapNode List</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="create" action="create">New MapNode</g:link></span>
    <span class="menuButton"><g:link class="refresh" action="reloadOperations">Reload Operations</g:link></span>
</div>
<div class="body">
    <h1>MapNode List</h1>
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
                    
                    <g:sortableColumn property="mapName" title="mapName"/>
                    
                    <g:sortableColumn property="nodeIdentifier" title="nodeIdentifier"/>
                    
                    <g:sortableColumn property="username" title="username"/>
                    
                </tr>
            </thead>
            <tbody>
                <g:each in="${mapNodeList}" status="i" var="mapNode">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                        <td><g:link action="show" id="${mapNode.id}">${mapNode.id?.encodeAsHTML()}</g:link></td>
                        
                        <td>${mapNode.mapName?.encodeAsHTML()}</td>
                        
                        <td>${mapNode.nodeIdentifier?.encodeAsHTML()}</td>
                        
                        <td>${mapNode.username?.encodeAsHTML()}</td>
                        
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>
    <div class="paginateButtons">
        <g:paginate total="${MapNode.count()}"/>
    </div>
</div>
</body>
</html>
