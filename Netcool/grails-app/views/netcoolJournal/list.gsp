

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>NetcoolJournal List</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="create" action="create">New NetcoolJournal</g:link></span>
    <span class="menuButton"><g:link class="refresh" action="reloadOperations">Reload Operations</g:link></span>
</div>
<div class="body">
    <h1>NetcoolJournal List</h1>
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
                    
                    <g:sortableColumn property="keyfield" title="keyfield"/>
                    
                    <g:sortableColumn property="servername" title="servername"/>
                    
                </tr>
            </thead>
            <tbody>
                <g:each in="${netcoolJournalList}" status="i" var="netcoolJournal">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                        <td><g:link action="show" id="${netcoolJournal.id}">${netcoolJournal.id?.encodeAsHTML()}</g:link></td>
                        
                        <td>${netcoolJournal.keyfield?.encodeAsHTML()}</td>
                        
                        <td>${netcoolJournal.servername?.encodeAsHTML()}</td>
                        
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>
    <div class="paginateButtons">
        <g:paginate total="${NetcoolJournal.count()}"/>
    </div>
</div>
</body>
</html>
