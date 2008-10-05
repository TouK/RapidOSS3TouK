<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Sep 9, 2008
  Time: 2:30:57 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="datasourceLayout"/>
</head>
<body>
<div class="front">
    <table>
        <tr>
            <th width="50%">Connections</th>
            <th>Datasources</th>
        </tr>
        <tr>
            <td><p style="margin-left:20px;width:80%">Define your connections (connection parameters):</p>
                <br>
                <div class="dialog" style="margin-left:20px;width:60%;">
                    <ul style="margin-left:25px;">
                        <li class="controller"><g:link controller="httpConnection">HttpConnection</g:link></li>
                        <li class="controller"><g:link controller="databaseConnection">DatabaseConnection</g:link></li>
                        <li class="controller"><g:link controller="snmpConnection">SnmpConnection</g:link></li>
                        <li class="controller"><g:link controller="ldapConnection">LdapConnection</g:link></li>
                    </ul>
                </div>
            </td>
            <td>
                <p style="margin-left:20px;width:80%">Define your datasources that use these connections:</p>
                <br>
                <div class="dialog" style="margin-left:20px;width:60%;">
                    <ul style="margin-left:25px;">
                        <li class="controller"><g:link controller="httpDatasource">HttpDatasource</g:link></li>
                        <li class="controller"><g:link controller="databaseDatasource">DatabaseDatasource</g:link></li>
                        <li class="controller"><g:link controller="singleTableDatabaseDatasource">SingleTableDatabaseDatasource</g:link></li>
                        <li class="controller"><g:link controller="snmpDatasource">SnmpDatasource</g:link></li>
                    </ul>
                </div>
            </td>
        </tr>
    </table>
</div>

</body>
</html>