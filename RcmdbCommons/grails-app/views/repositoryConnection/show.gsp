<%@ page import="connection.RepositoryConnection" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout" />
    <title>Show RepositoryConnection</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><g:link class="list" action="list">RepositoryConnection List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New RepositoryConnection</g:link></span>
</div>
<div class="body">
    <h1>Show RepositoryConnection</h1>
    <g:render template="/common/messages" model="[flash:flash]"></g:render>
    <div class="dialog">
        <table>
            <tbody>

                <tr class="prop">
                    <td valign="top" class="name">Name:</td>

                    <td valign="top" class="value">${repositoryConnection?.name}</td>

                </tr>

                 <tr class="prop">
                    <td valign="top" class="name">Max. Active:</td>

                    <td valign="top" class="value">${repositoryConnection.maxNumberOfConnections}</td>

                </tr>

              </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${repositoryConnection?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
