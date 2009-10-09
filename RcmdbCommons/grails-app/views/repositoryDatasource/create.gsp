
<%@ page import="datasource.RepositoryDatasource" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="adminLayout" />
        <title>Create RepositoryDatasource</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><g:link class="list" action="list">RepositoryDatasource List</g:link></span>
        </div>
        <div class="body">
            <h1>Create RepositoryDatasource</h1>
            <g:render template="/common/messages" model="[flash:flash, beans:[repositoryDatasource]]"></g:render>
            <g:form action="save" method="post" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="name">Name:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:repositoryDatasource,field:'name','errors')}">
                                    <input type="text" class="inputtextfield" id="name" name="name" value="${fieldValue(bean:repositoryDatasource,field:'name')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="connection">Connection:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:repositoryDatasource,field:'connection','errors')}">
                                    <g:select class="inputtextfield" optionKey="id" from="${connection.RepositoryConnection.list()}" name="connection.id" value="${repositoryDatasource?.connection?.id}" ></g:select>
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
