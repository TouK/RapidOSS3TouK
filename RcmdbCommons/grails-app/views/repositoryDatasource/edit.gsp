<%@ page import="datasource.RepositoryDatasource" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout"/>
    <title>Edit RepositoryDatasource</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><g:link class="list" action="list">RepositoryDatasource List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New RepositoryDatasource</g:link></span>
</div>
<div class="body">
    <h1>Edit RepositoryDatasource</h1>
    <g:render template="/common/messages" model="[flash:flash, beans:[repositoryDatasource]]"></g:render>
    <g:form method="post">
        <input type="hidden" name="id" value="${repositoryDatasource?.id}"/>
        <div class="dialog">
            <table>
                <tbody>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">Name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: repositoryDatasource, field: 'name', 'errors')}">
                            <input type="text" class="inputtextfield" id="name" name="name" value="${fieldValue(bean: repositoryDatasource, field: 'name')}"/>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="connection">Connection:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: repositoryDatasource, field: 'connection', 'errors')}">
                            <g:select class="inputtextfield" optionKey="id" from="${connection.RepositoryConnection.list()}" name="connection.id" value="${repositoryDatasource?.connection?.id}"></g:select>
                        </td>
                    </tr>

                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button"><g:actionSubmit class="save" value="Update"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </div>
    </g:form>
</div>
</body>
</html>
