<%@ page import="connection.DatabaseConnection" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout" />
    <title>Show DatabaseConnection</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><g:link class="list" action="list">DatabaseConnection List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New DatabaseConnection</g:link></span>
</div>
<div class="body">
    <h1>Show DatabaseConnection</h1>
    <g:render template="/common/messages" model="[flash:flash]"></g:render>
    <div class="dialog">
        <table>
            <tbody>

                <tr class="prop">
                    <td valign="top" class="name">Name:</td>

                    <td valign="top" class="value">${databaseConnection?.name}</td>

                </tr>

                 <tr class="prop">
                    <td valign="top" class="name">Max. Active:</td>

                    <td valign="top" class="value">${databaseConnection.maxNumberOfConnections}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Driver:</td>

                    <td valign="top" class="value">${databaseConnection?.driver}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Url:</td>

                    <td valign="top" class="value">${databaseConnection?.url}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Username:</td>

                    <td valign="top" class="value">${databaseConnection?.username}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Min Timeout:</td>

                    <td valign="top" class="value">${databaseConnection?.minTimeout}</td>

                </tr>
                <tr class="prop">
                    <td valign="top" class="name">Max Timeout:</td>

                    <td valign="top" class="value">${databaseConnection?.maxTimeout}</td>

                </tr>

              </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${databaseConnection?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
