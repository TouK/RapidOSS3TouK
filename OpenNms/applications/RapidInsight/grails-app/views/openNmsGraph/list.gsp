

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>OpenNmsGraph List</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="create" action="create">New OpenNmsGraph</g:link></span>
    <span class="menuButton"><g:link class="refresh" action="reloadOperations">Reload Operations</g:link></span>
</div>
<div class="body">
    <h1>OpenNmsGraph List</h1>
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
                    
                    <g:sortableColumn property="url" title="url"/>
                    
                    <th>graphOf</th>
                    
                </tr>
            </thead>
            <tbody>
                <g:each in="${openNmsGraphList}" status="i" var="openNmsGraph">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                        <td><g:link action="show" id="${openNmsGraph.id}">${openNmsGraph.id?.encodeAsHTML()}</g:link></td>
                        
                        <td>${openNmsGraph.url?.encodeAsHTML()}</td>
                        
                        <td><g:link action="show" controller="openNmsObject" id="${openNmsGraph.graphOf?.id}">${openNmsGraph.graphOf?.encodeAsHTML()}</g:link></td>
                        
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>
    <div class="paginateButtons">
        <g:paginate total="${OpenNmsGraph.count()}"/>
    </div>
</div>
</body>
</html>
