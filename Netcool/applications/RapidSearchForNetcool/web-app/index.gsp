<%@ page import="model.*" %>
<%@ page import="connection.*" %>
<%@ page import="datasource.*" %>
<%@ page import="script.*" %>
<html>
<head>
    <title>RapidSearch For Netcool Admin UI</title>
    <meta name="layout" content="main"/>
</head>
<body>

<div class="front"><h1>RapidSearch For Netcool UI</h1></div>
<p style="margin-left:20px;width:80%">This is the basic administration UI where you can define your connections, create NetcoolEvent and NetcoolJournal models, import conversion parameters.</p>
<br>
<p/>
<p/>
<br>
<br>
<div class="front">
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