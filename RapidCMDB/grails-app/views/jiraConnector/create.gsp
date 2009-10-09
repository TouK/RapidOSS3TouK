<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout" />
    <title>Create JiraConnector</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><g:link class="list" action="list">JiraConnector List</g:link></span>
</div>
<div class="body">
    <h1>Create JiraConnector</h1>
    <g:render template="/common/messages" model="[flash:flash, beans:[jiraConnector,jiraConnection,jiraDatasource]]"></g:render>    
    <g:form action="save" method="post">
        <div class="dialog">
            <table>
                <tbody>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">Name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: jiraConnector, field: 'name', 'errors')}">
                            <input type="text" id="name" class="inputtextfield" name="name" value="${fieldValue(bean: jiraConnector, field: 'name')}"/>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="username">Username:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: jiraConnection, field: 'username', 'errors')}">
                            <input type="text" class="inputtextfield" id="username" name="username" value="${fieldValue(bean: jiraConnection, field: 'username')}" autocomplete="off" />
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="userPassword">Password:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: jiraConnection, field: 'userPassword', 'errors')}">
                            <input type="text" class="inputtextfield" id="userPassword" name="userPassword" value="${fieldValue(bean: jiraConnection, field: 'userPassword')}" autocomplete="off" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="reconnectInterval">Reconnect Interval:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(jiraConnection,field:'reconnectInterval','errors')}">
                            <input type="text" class="inputtextfield" id="reconnectInterval" name="reconnectInterval" value="${fieldValue(jiraConnection,field:'reconnectInterval')}" />sec.
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
