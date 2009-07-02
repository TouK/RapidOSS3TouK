<%@ page import="auth.Role" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout" />
    <title>Edit Group</title>
</head>
<body>
<div class="nav">
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
                        <td valign="top" class="name" colspan="2">
                            Users:
                        </td>
                    </tr>
                    <tr>
                        <td valign="top" class="name" colspan="2">
                            <g:render template="/common/listToList" model="[id:'users', inputName:'users.id', valueProperty:'id', displayProperty:'username', fromListTitle:'Available Users', toListTitle:'Group Users', fromListContent:availableUsers, toListContent:group?.users]"></g:render>
                        </td>
                    </tr>
                    
                </tbody>
            </table>
        </div>
        <div style="margin-top:20px;">
            <%
                def currentUrl = "/group/edit/${group.id}"
            %>
        <table>
            <tr>
                <td>
                    <span style="color:#006DBA;font-size:14px;font-weight:bold;margin:0.8em 0pt 0.3em;">SegmentFilter List</span>
                    <span class="menuButton"><g:link class="create" controller="segmentFilter" params="${['groupId':group?.id, 'targetURI':currentUrl]}" action="create">New SegmentFilter</g:link></span>
                    <div class="list">
                        <table><br>
                            <thead>
                                <tr>
                                    <th>Class Name</th>
                                    <th>Filter</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <g:each in="${group.filters}" status="i" var="segmentFilter">
                                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                        <td><g:link action="show" controller="segmentFilter" id="${segmentFilter.id}" params="${[groupId:group.id, targetURI:currentUrl]}">${segmentFilter.className?.encodeAsHTML()}</g:link></td>
                                        <td>${segmentFilter.filter.encodeAsHTML()}</td>
                                        <td><g:link action="edit" controller="segmentFilter" id="${segmentFilter.id}" class="edit" params="${[groupId:group.id, targetURI:currentUrl]}">Edit</g:link></td>
                                    </tr>
                                </g:each>
                            </tbody>
                        </table>
                    </div>
                </td>
            </tr>
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
