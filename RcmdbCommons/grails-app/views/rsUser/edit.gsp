<%@ page import="auth.RsUser" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout"/>
    <title>Edit User</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><g:link class="list" action="list">User List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New User</g:link></span>
</div>
<div class="body">
    <h1>Edit User</h1>
    <g:render template="/common/messages" model="[flash:flash, beans:[rsUser,emailInformation]]"></g:render>
    <g:form method="post">
        <input type="hidden" name="id" value="${rsUser?.id}"/>
        <div class="dialog">
            <table>
                <tbody>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="username">Username:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: rsUser, field: 'username', 'errors')}">
                            <input type="text" id="username" name="username" value="${fieldValue(bean: rsUser, field: 'username')}" autocomplete="off" />
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="password1">Password:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: rsUser, field: 'passwordHash', 'errors')}">
                            <input type="password" id="password1" name="password1" value="" autocomplete="off" />
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="password2">Confirm Password:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: rsUser, field: 'passwordHash', 'errors')}">
                            <input type="password" id="password2" name="password2" value="" autocomplete="off" />
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="email">Email:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: rsUser, field: 'email', 'errors')}">
                            <input type="text" id="email" name="email" value="${fieldValue(bean: emailInformation, field: 'destination')}"/>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name" colspan="2">
                            Groups:
                        </td>
                    </tr>
                    <tr>
                        <td valign="top" class="name" colspan="2">
                            <g:render template="/common/listToList" model="[id:'groups', inputName:'groups.id', valueProperty:'id', displayProperty:'name', fromListTitle:'Available Groups', toListTitle:'User Groups', fromListContent:availableGroups, toListContent:userGroups]"></g:render>
                        </td>
                    </tr>
                    
                </tbody>
            </table>
        </div>
        <div class="buttons" style="margin-top:20px;">
            <span class="button"><g:actionSubmit class="save" value="Update"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </div>
    </g:form>
</div>
</body>
</html>
