

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>TopoMap List</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="create" action="create">New TopoMap</g:link></span>
    <span class="menuButton"><g:link class="refresh" action="reloadOperations">Reload Operations</g:link></span>
</div>
<div class="body">
    <h1>TopoMap List</h1>
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
                    
                    <g:sortableColumn property="username" title="username"/>
                    
                    <th>group</th>
                    
                </tr>
            </thead>
            <tbody>
                <g:each in="${topoMapList}" status="i" var="topoMap">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                        <td><g:link action="show" id="${topoMap.id}">${topoMap.id?.encodeAsHTML()}</g:link></td>
                        
                        <td>${topoMap.mapName?.encodeAsHTML()}</td>
                        
                        <td>${topoMap.username?.encodeAsHTML()}</td>
                        
                        <td><g:link action="show" controller="mapGroup" id="${topoMap.group?.id}">${topoMap.group?.encodeAsHTML()}</g:link></td>
                        
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>
    <div class="paginateButtons">
        <g:paginate total="${TopoMap.count()}"/>
    </div>
</div>
</body>
</html>
