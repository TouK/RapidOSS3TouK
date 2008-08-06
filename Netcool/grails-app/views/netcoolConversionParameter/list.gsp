
<%@ page import="datasource.NetcoolConversionParameter" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>NetcoolConversionParameter List</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
    <span class="menuButton"><g:link class="create" action="create">New NetcoolConversionParameter</g:link></span>
</div>
<div class="body">
    <h1>NetcoolConversionParameter List</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <div class="list">
        <table>
            <thead>
                <tr>
                    
                    <g:sortableColumn property="id" title="id"/>
                    
                    <g:sortableColumn property="keyField" title="keyField"/>
                    
                    <g:sortableColumn property="columnName" title="columnName"/>
                    
                    <g:sortableColumn property="conversion" title="conversion"/>
                    
                    <g:sortableColumn property="value" title="value"/>
                    
                </tr>
            </thead>
            <tbody>
                <g:each in="${netcoolConversionParameterList}" status="i" var="netcoolConversionParameter">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                        <td><g:link action="show" id="${netcoolConversionParameter.id}">${netcoolConversionParameter.id?.encodeAsHTML()}</g:link></td>
                        
                        <td>${netcoolConversionParameter.keyField?.encodeAsHTML()}</td>
                        
                        <td>${netcoolConversionParameter.columnName?.encodeAsHTML()}</td>
                        
                        <td>${netcoolConversionParameter.conversion?.encodeAsHTML()}</td>
                        
                        <td>${netcoolConversionParameter.value?.encodeAsHTML()}</td>
                        
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>
    <div class="paginateButtons">
        <g:paginate total="${NetcoolConversionParameter.countHits('id:[0 TO *]')}"/>
    </div>
</div>
</body>
</html>
