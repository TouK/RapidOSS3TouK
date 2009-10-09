<%@ page import="connector.JiraConnector;connection.JiraConnection;datasource.JiraDatasource" %><html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout"/>
    <title>Edit JiraConnector</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><g:link class="list" action="list">JiraConnector List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New JiraConnector</g:link></span>
</div>
<div class="body">
    <h1>Edit JiraConnector</h1>
    <g:render template="/common/messages" model="[flash:flash, beans:[jiraConnector,jiraConnection,jiraDatasource]]"></g:render>
    <g:form method="post">
        <input type="hidden" name="id" value="${jiraConnector?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">Name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:jiraConnector,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:jiraConnector,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="username">Username:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: jiraConnection, field: 'username', 'errors')}">
                            <input type="text" class="inputtextfield" id="username" name="username" value="${fieldValue(bean: jiraConnection, field: 'username')}"  autocomplete="off" />
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
                        <td valign="top" class="value ${hasErrors(bean:jiraDatasource,field:'reconnectInterval','errors')}">
                            <input type="text" class="inputtextfield" id="reconnectInterval" name="reconnectInterval" value="${fieldValue(bean:jiraDatasource,field:'reconnectInterval')}" />sec.
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="buttons">
            <g:form style="display:inline">
                <span class="button"><g:actionSubmit class="save" value="Update"/></span>
                <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
            </g:form>
        </div>
    </g:form>
</div>
</body>
</html>
