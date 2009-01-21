<%@ page import="connector.JiraConnector" %><html>
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
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${jiraConnector}">
        <div class="errors">
            <g:renderErrors bean="${jiraConnector}" as="list"/>
        </div>
    </g:hasErrors>

    <g:hasErrors bean="${jiraConnection}">
        <div class="errors">
            <g:renderErrors bean="${jiraConnection}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
        <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
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
                            <input type="text" class="inputtextfield" id="username" name="username" value="${fieldValue(bean: jiraConnection, field: 'username')}"/>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="userPassword">Password:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: jiraConnection, field: 'userPassword', 'errors')}">
                            <input type="password" class="inputtextfield" id="userPassword" name="userPassword" value="${fieldValue(bean: jiraConnection, field: 'userPassword')}"/>
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
