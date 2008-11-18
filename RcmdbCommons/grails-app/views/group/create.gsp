<%@ page import="auth.Role" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout" />
    <title>Create Group</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">Group List</g:link></span>
</div>
<div class="body">
    <h1>Create Group</h1>
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
    <g:form action="save" method="post" >
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
                    
                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button"><input class="save" type="submit" value="Create"/></span>
        </div>
    </g:form>
</div>
</body>
</html>
