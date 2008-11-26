<%@ page import="auth.Group" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout" />
    <title>Group List</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
    <span class="menuButton"><g:link class="create" action="create">New Group</g:link></span>
</div>
<div class="body">
    <h1>Group List</h1>
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
                    
                    <g:sortableColumn property="name" title="name"/>
                    
                    <th>role</th>
                    
                    <g:sortableColumn property="segmentFilter" title="segmentFilter"/>
                    
                </tr>
            </thead>
            <tbody>
                <g:each in="${groupList}" status="i" var="group">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                        <td><g:link action="edit" id="${group.id}">${group.id?.encodeAsHTML()}</g:link></td>
                        
                        <td>${group.name?.encodeAsHTML()}</td>
                        
                        <td>${group.role?.encodeAsHTML()}</td>
                        
                        <td>${group.segmentFilter?.encodeAsHTML()}</td>
                        
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>
    <div class="paginateButtons">
        <g:paginate total="${Group.count()}"/>
    </div>
</div>
</body>
</html>
