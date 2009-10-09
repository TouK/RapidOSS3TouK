
<%@ page import="datasource.SingleTableDatabaseDatasource" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="adminLayout" />
        <title>Edit SingleTableDatabaseDatasource</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><g:link class="list" action="list">SingleTableDatabaseDatasource List</g:link></span>
            <span class="menuButton"><g:link class="create" action="create">New SingleTableDatabaseDatasource</g:link></span>
        </div>
        <div class="body">
            <h1>Edit SingleTableDatabaseDatasource</h1>
            <g:render template="/common/messages" model="[flash:flash, beans:[singleTableDatabaseDatasource]]"></g:render>
            <g:form method="post" >
                <input type="hidden" name="id" value="${singleTableDatabaseDatasource?.id}" />
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="name">Name:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:singleTableDatabaseDatasource,field:'name','errors')}">
                                    <input type="text" class="inputtextfield" id="name" name="name" value="${fieldValue(bean:singleTableDatabaseDatasource,field:'name')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="tableName">Table Name:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:singleTableDatabaseDatasource,field:'tableName','errors')}">
                                    <input type="text" class="inputtextfield" id="tableName" name="tableName" value="${fieldValue(bean:singleTableDatabaseDatasource,field:'tableName')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="tableKeys">Table Keys:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:singleTableDatabaseDatasource,field:'tableKeys','errors')}">
                                    <input type="text" class="inputtextfield" id="tableKeys" name="tableKeys" value="${fieldValue(bean:singleTableDatabaseDatasource,field:'tableKeys')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="connection">Connection:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:singleTableDatabaseDatasource,field:'connection','errors')}">
                                    <g:select class="inputtextfield" optionKey="id" from="${connection.DatabaseConnection.list()}" name="connection.id" value="${singleTableDatabaseDatasource?.connection?.id}" ></g:select>
                                </td>
                            </tr>
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="reconnectInterval">Reconnect Interval:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:singleTableDatabaseDatasource,field:'reconnectInterval','errors')}">
                                    <input type="text" class="inputtextfield" id="reconnectInterval" name="reconnectInterval" value="${fieldValue(bean:singleTableDatabaseDatasource,field:'reconnectInterval')}" /> sec.
                                </td>
                            </tr> 
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:actionSubmit class="save" value="Update" /></span>
                    <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
