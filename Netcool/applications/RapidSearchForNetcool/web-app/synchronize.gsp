<%@ page import="script.CmdbScript; connection.NetcoolConnection" %><html>
<head>
    <title>RapidInsight For Netcool Admin UI</title>
    <rui:stylesheet dir="js/yui/assets/skins/sam" file="skin.css"></rui:stylesheet>
    <link rel="stylesheet" href="${createLinkTo(dir: 'css', file: 'main.css')}"/>
    <link rel="stylesheet" href="${createLinkTo(file: 'admin.css')}"/>
    <jsec:isNotLoggedIn>
        <g:javascript>window.location='auth/login?targetUri=/admin.gsp'</g:javascript>
    </jsec:isNotLoggedIn>
</head>
<body class="yui-skin-sam admin">
<div style="padding-left:10px;padding-top:5px;"><img src="images/RapidInsight-white.png"></div>
<div class="nav"><h1 style="display:inline">RapidInsight For Netcool</h1><span class="menuButton"><a href="auth/logout?targetUri=/admin.gsp" class="logout">Logout</a></span></div>
<p style="margin-left:20px;width:80%">This is the basic administration UI where you can define your connections, create NetcoolEvent and NetcoolJournal models, import conversion parameters.</p>
<br>
<p/>
<p/>
<div class="yui-navset">
    <ul class="yui-nav">
        <li><a href="${createLinkTo(file: 'admin.gsp')}"><em>Connectors</em></a></li>
        <li class="selected"><a href="${createLinkTo(file: 'synchronize.gsp')}"><em>Configuration Sync.</em></a></li>
        <li><g:link action="list" controller="script"><em>Scripts</em></g:link></li>
        <li><g:link action="list" controller="netcoolConversionParameter"><em>Conversion Parameters</em></g:link></li>
        <li><a href="${createLinkTo(file: 'reload.gsp')}"><em>Reload</em></a></li>
        <li><g:link action="list" controller="rsUser"><em>Users</em></g:link></li>
    </ul>
    <div style="margin:20px 15px 10px;">
        <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
        </g:if>
        <g:hasErrors bean="${flash.errors}">
           <div class="errors">
                <g:renderErrors bean="${flash.errors}"/>
            </div>
        </g:hasErrors>
        <p></p>
        RapidInsight for Netcool comes with a default model for NetcoolEvent and NetcoolJournal that defines the properties of each event object. 
        Netcool utilizes conversion tables to use different values while storing and displaying values for some properties. 
        RapidInsight for Netcool allows you to synchronize the configuration data (such as the properties and their conversions) with your Netcool server.
        <p></p>
        <ol style="margin-left:25px;">
            <li class="controller"><g:link controller="script" action="run" id="NetcoolColumnMapping">Import Netcool Columns</g:link></li>
            <p></p>
            Retrieves the column (property) information for the events and journals from the Netcool server. 
            When this action is executed, the columns for the event and journal tables will be retrieved and saved into the RS_HOME/RapidSuite/grails-app/conf/NetcoolFieldConfiguration.xml file. 
            This will be used as an input by the next step, event model creation. The XML file shows all the properties supported by your Netcool server. 
            You can remove properties from this file before modifying the model. 
            <p></p> 
            <li class="controller"><g:link controller="script" action="run" id="NetcoolConfigurationLoader">Create Event and Journal Models</g:link></li>
            <p></p>
            Overwrites the default event model with the new one. This process uses the edited XML file (RS_HOME/RapidSuite/grails-app/conf/NetcoolFieldConfiguration.xm) 
            as the input. RapidInsight for Netcool will generate all necessary files to update the Event and Journal models.
            <p></p>
            <li class="controller"><g:link controller="application" action="reload" params="['targetURI':'/synchronize.gsp']">Reload Application</g:link></li>
			<p></p>
			Restarts the RapidInsight for Netcool application to deploy the new model files. As the final step, reload the application for the model changes to take effect.
			<p></p>
        </ol>
    </div>
</div>
</body>
</html>