
<%@ page import="datasource.NetcoolColumn" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>NetcoolColumn List</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="create" action="create">New NetcoolColumn</g:link></span>
    <span class="menuButton"><g:link class="refresh" action="reloadOperations">Reload Operations</g:link></span>
</div>
<div class="body">
    <h1>NetcoolColumn List</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <div class="list">
        <table>
            <thead>
                <tr>
                    
                    <g:sortableColumn property="id" title="id"/>
                    
                    <g:sortableColumn property="netcoolName" title="netcoolName"/>
                    
                    <g:sortableColumn property="isDeleteMarker" title="isDeleteMarker"/>
                    
                    <g:sortableColumn property="localName" title="localName"/>
                    
                    <g:sortableColumn property="type" title="type"/>
                    
                </tr>
            </thead>
            <tbody>
                <g:each in="${netcoolColumnList}" status="i" var="netcoolColumn">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                        <td><g:link action="show" id="${netcoolColumn.id}">${netcoolColumn.id?.encodeAsHTML()}</g:link></td>
                        
                        <td>${netcoolColumn.netcoolName?.encodeAsHTML()}</td>
                        
                        <td>${netcoolColumn.isDeleteMarker?.encodeAsHTML()}</td>
                        
                        <td>${netcoolColumn.localName?.encodeAsHTML()}</td>
                        
                        <td>${netcoolColumn.type?.encodeAsHTML()}</td>
                        
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>
    <div class="paginateButtons">
        <g:paginate total="${NetcoolColumn.countHits('id:[0 TO *]')}"/>
    </div>
</div>
</body>
</html>
