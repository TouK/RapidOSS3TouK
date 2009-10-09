
<%@ page import="datasource.SingleTableDatabaseDatasource" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="adminLayout" />
        <title>Create SingleTableDatabaseDatasource</title>         
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><g:link class="list" action="list">SingleTableDatabaseDatasource List</g:link></span>
        </div>
        <div class="body">
            <h1>Create SingleTableDatabaseDatasource</h1>
            <g:render template="/common/messages" model="[flash:flash, beans:[singleTableDatabaseDatasource]]"></g:render>
            <g:form action="save" method="post" >
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
                                    <input type="text" class="inputtextfield" id="reconnectInterval" name="reconnectInterval" value="${fieldValue(bean:singleTableDatabaseDatasource,field:'reconnectInterval')}" />sec.
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
