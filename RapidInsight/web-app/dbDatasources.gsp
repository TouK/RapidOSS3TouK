<%@ page import="datasource.SingleTableDatabaseDatasource; datasource.DatabaseDatasource; connection.DatabaseConnection" %>
<%--
  Created by IntelliJ IDEA.
  User: sezgin
  Date: Nov 18, 2008
  Time: 10:56:08 AM
  To change this template use File | Settings | File Templates.
--%>

<html>
<head>
    <meta name="layout" content="adminLayout"/>
</head>
<body>
<g:render template="/common/messages" model="[flash:flash, beans:[]]"></g:render>
<div class="body">
    <br>
    <div style="margin-top:0px;">
        <table>
            <tr>
              <td>
                <div>
                  <span style="color:#006DBA;font-size:14px;font-weight:bold;margin:0.8em 0pt 0.3em;">Database Connections</span>
                  <span class="menuButton"><g:link class="create" controller="databaseConnection" action="create">New Database Connection</g:link></span>
                </div>
              </td>
            </tr>
            <tr>
                <td>
                    <div class="list">
                        <table>
                            <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>Driver</th>
                                    <th>Url</th>
                                    <th>Username</th>
                                    <th></th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <g:each in="${DatabaseConnection.list([sort:'name'])}" status="i" var="databaseConnection">
                                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                        <td><g:link action="show" controller="databaseConnection" id="${databaseConnection.id}">${databaseConnection.name?.encodeAsHTML()}</g:link></td>
                                        <td>${databaseConnection?.driver?.encodeAsHTML()}</td>
                                        <td>${databaseConnection?.url?.encodeAsHTML()}</td>
                                        <td>${databaseConnection?.username?.encodeAsHTML()}</td>
                                        <td><g:link action="test" controller="databaseConnection" id="${databaseConnection.id}" class="testConnection">Test Connection</g:link></td>
                                        <td><g:link action="edit" controller="databaseConnection" id="${databaseConnection.id}" class="edit">Edit</g:link></td>
                                    </tr>
                                </g:each>
                            </tbody>
                        </table>
                    </div>
                </td>
            </tr>
        </table>
    </div>
    <div style="margin-top:20px;">
        <table>
            <tr>
              <td>
                <span style="color:#006DBA;font-size:14px;font-weight:bold;margin:0.8em 0pt 0.3em;">Database Datasource List</span>
                    <span class="menuButton"><g:link class="create" controller="databaseDatasource" action="create">New DatabaseDatasource</g:link></span>                
              </td>
            </tr>
            <tr>
                <td>
                    <div class="list">
                        <table>
                            <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>ReconnectInterval</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <g:each in="${DatabaseDatasource.list([sort:'name'])}" status="i" var="databaseDatasource">
                                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                        <td><g:link action="show" controller="databaseDatasource" id="${databaseDatasource.id}">${databaseDatasource.name?.encodeAsHTML()}</g:link></td>
                                        <td>${databaseDatasource.reconnectInterval.encodeAsHTML()}</td>
                                        <td><g:link action="edit" controller="databaseDatasource" id="${databaseDatasource.id}" class="edit">Edit</g:link></td>
                                    </tr>
                                </g:each>
                            </tbody>
                        </table>
                    </div>
                </td>
            </tr>
        </table>
    </div>
    <div style="margin-top:20px;">
        <table>
            <tr>
              <td>
                <span style="color:#006DBA;font-size:14px;font-weight:bold;margin:0.8em 0pt 0.3em;">Single Table Database Datasource List</span>
                    <span class="menuButton"><g:link class="create" controller="singleTableDatabaseDatasource" action="create">New SingleTableDatabaseDatasource</g:link></span>                
              </td>
            </tr>
            <tr>
                <td>
                    <div class="list">
                        <table>
                            <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>Table Name</th>
                                    <th>Table Keys</th>
                                    <th>ReconnectInterval</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <g:each in="${SingleTableDatabaseDatasource.list([sort:'name'])}" status="i" var="singleTableDatabaseDatasource">
                                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                        <td><g:link action="show" controller="singleTableDatabaseDatasource" id="${singleTableDatabaseDatasource.id}">${singleTableDatabaseDatasource.name?.encodeAsHTML()}</g:link></td>
                                        <td>${singleTableDatabaseDatasource.tableName.encodeAsHTML()}</td>
                                        <td>${singleTableDatabaseDatasource.tableKeys.encodeAsHTML()}</td>
                                        <td>${singleTableDatabaseDatasource.reconnectInterval.encodeAsHTML()}</td>
                                        <td><g:link action="edit" controller="singleTableDatabaseDatasource" id="${singleTableDatabaseDatasource.id}" class="edit">Edit</g:link></td>
                                    </tr>
                                </g:each>
                            </tbody>
                        </table>
                    </div>
                </td>
            </tr>
        </table>
    </div>
</div>

</body>
</html>