<%@ page import="model.*" %>
<%@ page import="connection.*" %>
<%@ page import="datasource.*" %>
<%@ page import="script.*" %>
<html>
<head>
    <title>RapidCMDB Admin UI</title>
    <meta name="layout" content="main"/>
    <jsec:isNotLoggedIn>
	  <g:javascript>window.location='auth/login?targetUri=/admin.gsp'</g:javascript>
	</jsec:isNotLoggedIn>
</head>
<body>

<div class="nav"><h1 style="display:inline">RapidCMDB Admin UI</h1></div>
<p style="margin-left:20px;width:80%">This is the basic administration UI where you can define your connections and datasources, model your classes, and maintain them.</p>
<br>
<p/>
<p/>
<br>
<br>
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
                        <li class="controller"><g:link controller="rapidInsightConnection">RapidInsightConnection</g:link></li>
                        <li class="controller"><g:link controller="snmpConnection">SnmpConnection</g:link></li>
                        <li class="controller"><g:link controller="emailConnection">EmailConnection</g:link></li>
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
                        <li class="controller"><g:link controller="rapidInsightDatasource">RapidInsightDatasource</g:link></li>
                        <li class="controller"><g:link controller="emailDatasource">EmailDatasource</g:link></li>
                        <li class="controller"><g:link controller="snmpDatasource">SnmpDatasource</g:link></li>
                    </ul>
                </div>
            </td>
        </tr>
    </table>
</div>
<p/>
<br>
<br>
<p/>


<p/>
<div class="front">
    <table><tr><th>Scripting</th></tr>
        <tr>
            <td><p style="margin-left:20px;width:80%">Define and Run Scripts.</p>
                <br>
                <div class="dialog" style="margin-left:20px;width:60%;">
                    <ul style="margin-left:25px;">
                        <li class="controller"><g:link controller="script">Scripts</g:link></li>
                    </ul>
                </div>
            </td>
    </table></div>
<br>
<br>

<p/>


<p/>
<div class="front">
    <table><tr><th width="50%">User Management</th>  <th>Application Management</th></tr>
        <tr><td><p style="margin-left:20px;">Manage Users and Groups.</p>
            <br>
            <div class="dialog" style="margin-left:20px;">
                <ul style="margin-left:25px;">
                    <li class="controller"><g:link controller="rsUser">Users</g:link></li>
                    <li class="controller"><g:link controller="group">Groups</g:link></li>
                </ul>
            </div>
        </td>
            <td><p style="margin-left:20px;width:80%">Reload Application, Export/Import configuration.</p>
                <br>
                <div class="dialog" style="margin-left:20px;width:60%;">
                    <ul style="margin-left:25px;">
                        <li class="controller"><g:link controller="application">Application Management</g:link></li>
                    </ul>
                </div>
            </td>
    </table></div>
<br>
<br>

</body>
</html>