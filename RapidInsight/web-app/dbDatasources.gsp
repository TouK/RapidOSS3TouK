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
<div class="nav">
   	<span class="menuButton"><a class="home" href="${createLinkTo(dir: 'admin.gsp')}">Home</a></span>
</div>
<g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
</g:if>
<g:hasErrors bean="${flash.errors}">
    <div class="errors">
        <g:renderErrors bean="${flash.errors}"/>
    </div>
</g:hasErrors>
<div class="body">
    <br>
    <div style="margin-top:0px;">
        <table style="width:900;">
            <tr>
                <td>
                    <span style="color:#006DBA;font-size:14px;font-weight:bold;margin:0.8em 0pt 0.3em;">Database Connections</span>
                    <span class="menuButton"><g:link class="create" controller="databaseConnection" action="create">New Database Connection</g:link></span>
                    <div class="list">
                        <table>
                            <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>Driver</th>
                                    <th>Url</th>
                                    <th>Username</th>
                                </tr>
                            </thead>
                            <tbody>
                                <g:each in="${DatabaseConnection.list()}" status="i" var="databaseConnection">
                                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                        <td><g:link action="edit" controller="databaseConnection" id="${databaseConnection.id}">${databaseConnection.name?.encodeAsHTML()}</g:link></td>
                                        <td>${databaseConnection?.driver?.encodeAsHTML()}</td>
                                        <td>${databaseConnection?.url?.encodeAsHTML()}</td>
                                        <td>${databaseConnection?.username?.encodeAsHTML()}</td>
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
        <table style="width:900;">
            <tr>
                <td>
                    <span style="color:#006DBA;font-size:14px;font-weight:bold;margin:0.8em 0pt 0.3em;">Database Datasource List</span>
                    <span class="menuButton"><g:link class="create" controller="databaseDatasource" action="create">New DatabaseDatasource</g:link></span>
                    <div class="list">
                        <table>
                            <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>ReconnectInterval</th>
                                </tr>
                            </thead>
                            <tbody>
                                <g:each in="${DatabaseDatasource.list()}" status="i" var="databaseDatasource">
                                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                        <td><g:link action="show" controller="databaseDatasource" id="${databaseDatasource.id}">${databaseDatasource.name?.encodeAsHTML()}</g:link></td>
                                        <td>${databaseDatasource.reconnectInterval.encodeAsHTML()}</td>
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
        <table style="width:900;">
            <tr>
                <td>
                    <span style="color:#006DBA;font-size:14px;font-weight:bold;margin:0.8em 0pt 0.3em;">Single Table Database Datasource List</span>
                    <span class="menuButton"><g:link class="create" controller="singleTableDatabaseDatasource" action="create">New SingleTableDatabaseDatasource</g:link></span>
                    <div class="list">
                        <table>
                            <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>Table Name</th>
                                    <th>Table Keys</th>
                                    <th>ReconnectInterval</th>
                                </tr>
                            </thead>
                            <tbody>
                                <g:each in="${SingleTableDatabaseDatasource.list()}" status="i" var="singleTableDatabaseDatasource">
                                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                        <td><g:link action="show" controller="singleTableDatabaseDatasource" id="${singleTableDatabaseDatasource.id}">${singleTableDatabaseDatasource.name?.encodeAsHTML()}</g:link></td>
                                        <td>${singleTableDatabaseDatasource.tableName.encodeAsHTML()}</td>
                                        <td>${singleTableDatabaseDatasource.tableKeys.encodeAsHTML()}</td>
                                        <td>${singleTableDatabaseDatasource.reconnectInterval.encodeAsHTML()}</td>
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