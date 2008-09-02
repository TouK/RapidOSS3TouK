

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>EdgeNode List</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="create" action="create">New EdgeNode</g:link></span>
    <span class="menuButton"><g:link class="refresh" action="reloadOperations">Reload Operations</g:link></span>
</div>
<div class="body">
    <h1>EdgeNode List</h1>
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
                    
                    <g:sortableColumn property="from" title="from"/>
                    
                    <g:sortableColumn property="mapName" title="mapName"/>
                    
                    <g:sortableColumn property="to" title="to"/>
                    
                    <g:sortableColumn property="username" title="username"/>
                    
                </tr>
            </thead>
            <tbody>
                <g:each in="${edgeNodeList}" status="i" var="edgeNode">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                        <td><g:link action="show" id="${edgeNode.id}">${edgeNode.id?.encodeAsHTML()}</g:link></td>
                        
                        <td>${edgeNode.from?.encodeAsHTML()}</td>
                        
                        <td>${edgeNode.mapName?.encodeAsHTML()}</td>
                        
                        <td>${edgeNode.to?.encodeAsHTML()}</td>
                        
                        <td>${edgeNode.username?.encodeAsHTML()}</td>
                        
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>
    <div class="paginateButtons">
        <g:paginate total="${EdgeNode.countHits('id:[0 TO *]')}"/>
    </div>
</div>
</body>
</html>
