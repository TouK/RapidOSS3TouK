

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Platform List</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="create" action="create">New Platform</g:link></span>
    <span class="menuButton"><g:link class="refresh" action="reloadOperations">Reload Operations</g:link></span>
</div>
<div class="body">
    <h1>Platform List</h1>
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
                    
                    <g:sortableColumn property="resource_name" title="resource_name"/>
                    
                    <g:sortableColumn property="status" title="status"/>
                    
                </tr>
            </thead>
            <tbody>
                <g:each in="${platformList}" status="i" var="platform">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                        <td><g:link action="show" id="${platform.id}">${platform.id?.encodeAsHTML()}</g:link></td>
                        
                        <td>${platform.resource_name?.encodeAsHTML()}</td>
                        
                        <td>${platform.status?.encodeAsHTML()}</td>
                        
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>
    <div class="paginateButtons">
        <g:paginate total="${Platform.countHits('id:[0 TO *]')}"/>
    </div>
</div>
</body>
</html>
