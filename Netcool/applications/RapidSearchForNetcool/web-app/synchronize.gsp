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
        <ul style="margin-left:25px;">
            <li class="controller"><g:link controller="script" action="run" id="NetcoolColumnMapping">Import Netcool Columns</g:link></li>
            <li class="controller"><g:link controller="script" action="run" id="NetcoolConfigurationLoader">Create RapidCMDB Models</g:link></li>
            <li class="controller"><g:link controller="application" action="reload" params="['targetURI':'/synchronize.gsp']">Reload Application</g:link></li>

        </ul>
    </div>
</div>
</body>
</html>