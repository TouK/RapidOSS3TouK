
<%@ page import="auth.RsUser" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="adminLayout" />
        <title>Create User</title>         
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><g:link class="list" action="list">User List</g:link></span>
        </div>
        <div class="body">
            <h1>Create User</h1>
            <%
                def errorBeans=[rsUser];
                errorBeans.addAll(userChannels);
            %>
            <g:render template="/common/messages" model="[flash:flash, beans:errorBeans]"></g:render>
            <g:form action="save" method="post" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="username">Username:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:rsUser,field:'username','errors')}">
                                    <input type="text" id="username" name="username" value="${fieldValue(bean:rsUser,field:'username')}" autocomplete="off" />
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="password1">Password:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:rsUser,field:'passwordHash','errors')}" >
                                    <input type="password" id="password1" name="password1" value="" autocomplete="off" />
                                </td>
                            </tr> 
                            
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="password2">Confirm Password:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:rsUser,field:'passwordHash','errors')}">
                                    <input type="password" id="password2" name="password2" value="" autocomplete="off" />
                                </td>
                            </tr>


                            <g:each in="${userChannels}" var="userChannelInfo">
                                <tr class="prop">
                                    <td valign="top" class="name">
                                        <label for="${userChannelInfo.type}">${userChannelInfo.type}:</label>
                                    </td>
                                    <td valign="top" class="value ${hasErrors(bean: userChannelInfo, field: 'destination', 'errors')}">
                                        <input type="text" class="inputtextfield" id="${userChannelInfo.type}" name="${userChannelInfo.type}" value="${fieldValue(bean: userChannelInfo, field: 'destination')}"/>
                                    </td>
                                </tr>
                            </g:each>
                            

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
                <div class="buttons">
                    <span class="button"><input class="save" type="submit" value="Create" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
