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
    <%
        def errorBeans=[rsUser];
        errorBeans.addAll(userChannels);
    %>
    <g:render template="/common/messages" model="[flash:flash, beans:errorBeans]"></g:render>
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

                    <g:each in="${userChannels}" var="userChannelInfo">
                                <tr class="prop">
                                    <td valign="top" class="name">
                                        <label for="${userChannelInfo.type}">${userChannelInfo.type}:</label>
                                    </td>
                                    <td valign="top" class="value ${hasErrors(bean: userChannelInfo, field: 'destination', 'errors')}">
                                        <input type="text"  class="inputtextfield" id="${userChannelInfo.type}" name="${userChannelInfo.type}" value="${fieldValue(bean: userChannelInfo, field: 'destination')}"/>
                                    </td>
                                </tr>
                    </g:each>
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="defaultDestination">Default Destination:</label>
                        </td>
                        <td valign="top">
                            <g:select class="inputtextfield" optionKey="type" optionValue="type" from="${userChannels}" name="defaultDestination" value="${defaultDestination}" noSelection="['':'']"></g:select>
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
