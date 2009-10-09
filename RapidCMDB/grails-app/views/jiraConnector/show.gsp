<%@ page import="connector.JiraConnector;connection.JiraConnection;datasource.JiraDatasource" %><html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout"/>
    <title>Show JiraConnector</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><g:link class="list" action="list">JiraConnector List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New JiraConnector</g:link></span>
</div>
<div class="body">
    <h1>Show JiraConnector</h1>
    <g:render template="/common/messages" model="[flash:flash]"></g:render>
    <div class="dialog">
        <table>
            <tbody>

                <tr class="prop">
                    <td valign="top" class="name">Name:</td>
                    <td valign="top" class="value">${jiraConnector.name}</td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name">Username:</td>
                    <td valign="top" class="value">${jiraConnection?.username}</td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name">Password:</td>
                    <td valign="top" class="value">${jiraConnection?.userPassword}</td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name">Reconnect Interval:</td>
                    <td valign="top" class="value">${jiraDatasource?.reconnectInterval} sec.</td>
                </tr>
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form style="display:inline">
            <input type="hidden" name="id" value="${jiraConnector?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
