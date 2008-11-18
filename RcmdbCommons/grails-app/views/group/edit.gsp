<%@ page import="auth.Role" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout" />
    <title>Edit Group</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">Group List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New Group</g:link></span>
</div>
<div class="body">
    <h1>Edit Group</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${group}">
        <div class="errors">
            <g:renderErrors bean="${group}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${group?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:group,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:group,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="role">role:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:group,field:'role','errors')}">
                            <g:select optionKey="id" from="${Role.list()}" name="role.id" value="${group?.role?.id}" noSelection="['null':'']"></g:select>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="segmentFilter">segmentFilter:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:group,field:'segmentFilter','errors')}">
                            <input type="text" id="segmentFilter" name="segmentFilter" value="${fieldValue(bean:group,field:'segmentFilter')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="users">users:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:group,field:'users','errors')}">
                            
<ul>
<g:each var="u" in="${group?.users?}">
    <li style="margin-bottom:3px;">
        <g:link controller="User" action="show" id="${u.id}">${u}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':group?.id, 'relationName':'users', 'relatedObjectId':u.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':group?.id, 'relationName':'users']" action="addTo">Add User</g:link>

                        </td>
                    </tr>
                    
                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button"><g:actionSubmit class="save" value="Update"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </div>
    </g:form>
</div>
</body>
</html>
