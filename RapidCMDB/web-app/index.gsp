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


        <div class="front"><h1 >RapidCMDB Admin UI</h1></div>
        <p style="margin-left:20px;width:80%">This is the basic administration UI where you can define your connections and datasources, model your classes, and maintain them.</p>
<br>
        <p/>
<p/>
<br>
<br>
      <div class="front">
      <table>
         <tr>
           <th width="50%" >  Connections</th>
           <th>Datasources</th>
         </tr>
         <tr>
           <td> <p style="margin-left:20px;width:80%">Define your connections (connection parameters):</p>
       <br>
        <div class="dialog" style="margin-left:20px;width:60%;">
            <ul style="margin-left:25px;">
                <li class="controller"><g:link controller="httpConnection">HttpConnection</g:link></li>
                <li class="controller"><g:link controller="databaseConnection">DatabaseConnection</g:link></li>
                <li class="controller"><g:link controller="rapidInsightConnection">RapidInsightConnection</g:link></li>
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
        <table ><tr><th width="50%">Modeling</th>  <th >Scripting</th></tr>
        <tr><td><p style="margin-left:20px;">Model your Managed Classes.</p>
        <br>
        <div class="dialog" style="margin-left:20px;">
            <ul  style="margin-left:25px;">
                <li class="controller"><g:link controller="model">Model Managed Classes</g:link></li>
            </ul>
        </div>
         </td>
         <td><p style="margin-left:20px;width:80%">Define and Run Scripts.</p>
         <br>
        <div class="dialog" style="margin-left:20px;width:60%;">
            <ul style="margin-left:25px;">
                <li class="controller"><g:link controller="script">OnDemand Scripts</g:link></li>
            </ul>
        </div>
        </td>
         </table>  </div>
         <br>
         <br>
         

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