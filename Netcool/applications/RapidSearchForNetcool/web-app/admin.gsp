<%@ page import="model.*" %>
<%@ page import="connection.*" %>
<%@ page import="datasource.*" %>
<%@ page import="script.*" %>
<html>
<head>
    <title>RapidSearch For Netcool Admin UI</title>
    <meta name="layout" content="main"/>
    <jsec:isNotLoggedIn>
	  <g:javascript>window.location='auth/login?targetUri=/admin.gsp'</g:javascript>
	</jsec:isNotLoggedIn>
</head>
<body>

<div class="nav"><h1 style="display:inline">RapidSearch For Netcool UI</h1><span class="menuButton"><a href="auth/logout?targetUri=/admin.gsp" class="logout">Logout</a></span></div>
<p style="margin-left:20px;width:80%">This is the basic administration UI where you can define your connections, create NetcoolEvent and NetcoolJournal models, import conversion parameters.</p>
<br>
<p/>
<p/>
<br>
<br>
<div class="front">
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors>
        <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <table>
        <tr>
            <th width="50%">Connections</th>
            <th>ModelOperations</th>
        </tr>
        <tr>
            <td><p style="margin-left:20px;width:80%">Define your Netcool connections (connection parameters):</p>
                <br>
                <div class="dialog" style="margin-left:20px;width:60%;">
                    <ul style="margin-left:25px;">
                        <li class="controller"><g:link controller="netcoolConnection">Netcool Connection</g:link></li>
                        <li class="controller"><g:link controller="netcoolConnection" action="startConnectors">Start Connectors</g:link></li>
                        <li class="controller"><g:link controller="netcoolConnection" action="stopConnectors">Stop Connectors</g:link></li>
                    </ul>
                </div>
            </td>
            <td>
                <p style="margin-left:20px;width:80%">Import Netcool configuration into RapidCMDB and Reload Application:</p>
                <br>
                <div class="dialog" style="margin-left:20px;width:60%;">
                    <ul style="margin-left:25px;">
                        <li class="controller"><g:link controller="script" action="run" id="NetcoolColumnMapping">Import Netcool Columns</g:link></li>
                        <li class="controller"><g:link controller="script" action="run" id="NetcoolConfigurationLoader">Create RapidCMDB Models</g:link></li>
                        <li class="controller"><g:link controller="application" action="reload">Reload Application</g:link></li>

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



</body>
</html>