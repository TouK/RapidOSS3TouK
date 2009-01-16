<%@ page import="auth.Group" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout" />
    <title>Group List</title>
</head>
<body>
<div class="nav">
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
                    <th></th>
                    
                </tr>
            </thead>
            <tbody>
                <g:each in="${groupList}" status="i" var="group">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                        <td><g:link action="show" id="${group.id}">${group.id?.encodeAsHTML()}</g:link></td>
                        
                        <td>${group.name?.encodeAsHTML()}</td>
                        
                        <td>${group.role?.encodeAsHTML()}</td>
                        
                        <td>${group.segmentFilter?.encodeAsHTML()}</td>

                         <td><g:link action="edit" id="${group.id}" class="edit">Edit</g:link></td>
                        
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
