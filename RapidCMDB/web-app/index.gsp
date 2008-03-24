<%@ page import="model.*" %>
<%@ page import="connection.*" %>
<%@ page import="datasource.*" %>
<%@ page import="script.*" %>
<html>
    <head>
        <title>RapidCMDB Admin UI</title>
		<meta name="layout" content="main" />
    </head>
    <body>


        <h1 style="margin-left:20px;">RapidCMDB Admin UI</h1>
        <p style="margin-left:20px;width:80%">This is the basic administration UI where you can define your connections and datasources, model your managed objects, and maintain them.</p>
<p/>
<p/>
        <h2 style="margin-left:20px;">Connections</h2>
        <p style="margin-left:20px;width:80%">Define your connections (connection parameters):</p>
        <div class="dialog" style="margin-left:20px;width:60%;">
            <ul>
                <li class="controller"><g:link controller="netcoolConnection">NetcoolConnection</g:link></li>
                <li class="controller"><g:link controller="smartsConnection">SmartsConnection</g:link></li>
                <li class="controller"><g:link controller="httpConnection">HttpConnection</g:link></li>
                <li class="controller"><g:link controller="databaseConnection">DatabaseConnection</g:link></li>
                <li class="controller"><g:link controller="rapidInsightConnection">RapidInsightConnection</g:link></li>
            </ul>
        </div>
<p/>
<p/>
        <h2 style="margin-left:20px;">Datasources</h2>
        <p style="margin-left:20px;width:80%">Define your datasources that use these connections:</p>
        <div class="dialog" style="margin-left:20px;width:60%;">
            <ul>
                <li class="controller"><g:link controller="netcoolDatasource">NetcoolDatasource</g:link></li>
                <li class="controller"><g:link controller="smartsTopologyDatasource">SmartsTopologyDatasource</g:link></li>
                <li class="controller"><g:link controller="smartsNotificationDatasource">SmartsNotificationDatasource</g:link></li>
                <li class="controller"><g:link controller="httpDatasource">HttpDatasource</g:link></li>
                <li class="controller"><g:link controller="databaseDatasource">DatabaseDatasource</g:link></li>
                <li class="controller"><g:link controller="singleTableDatabaseDatasource">SingleTableDatabaseDatasource</g:link></li>
                <li class="controller"><g:link controller="rapidInsightDatasource">RapidInsightDatasource</g:link></li>
            </ul>
        </div>
<p/>
<p/>
        <h2 style="margin-left:20px;">Modeling</h2>
        <p style="margin-left:20px;width:80%">Model your Managed Objects.</p>
        <div class="dialog" style="margin-left:20px;width:60%;">
            <ol>
                <li class="controller"><g:link controller="model">Model Managed Objects</g:link></li>
                <li class="controller"><g:link controller="modelRelation">Define Relations</g:link></li>
            </ol>
        </div>

        <h2 style="margin-left:20px;">Scripting</h2>
        <p style="margin-left:20px;width:80%">Define and Run Scripts.</p>
        <div class="dialog" style="margin-left:20px;width:60%;">
            <ul>
                <li class="controller"><g:link controller="script">OnDemand Scripts</g:link></li>
            </ul>
        </div>

<!--
        <h2 style="margin-left:20px;">Modeled Managed Objects</h2>
        <p style="margin-left:20px;width:80%">This is the basic administration UI where you can define your connections and datasources, model your managed objects, and maintain them.</p>
        <div class="dialog" style="margin-left:20px;width:60%;">
            <ul>
              <g:each var="c" in="${grailsApplication.controllerClasses}">
                    <li class="controller"><g:link controller="${c.logicalPropertyName}">${c.fullName}</g:link></li>
              </g:each>
            </ul>
        </div>
-->


    </body>
</html>