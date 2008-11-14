<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Nov 14, 2008
  Time: 10:33:48 AM
--%>

<html>
<head>
    <title>RapidInsight For Netcool Admin UI</title>
    <rui:stylesheet dir="js/yui/assets/skins/sam" file="skin.css"></rui:stylesheet>
    <link rel="stylesheet" href="${createLinkTo(dir: 'css/rapidjs', file: 'yuioverride.css')}"/>
    <link rel="stylesheet" href="${createLinkTo(dir: 'css', file: 'main.css')}"/>
    <link rel="stylesheet" href="${createLinkTo(file: 'admin.css')}"/>
    <jsec:isNotLoggedIn>
        <g:javascript>window.location='auth/login?targetUri=/admin.gsp'</g:javascript>
    </jsec:isNotLoggedIn>
    <jsec:lacksRole name="Administrator">
        <meta http-equiv="REFRESH" content="0;url=${createLinkTo(dir: 'auth/unauthorized')}">
    </jsec:lacksRole>
    <g:layoutHead/>
</head>
<body onload="${pageProperty(name: 'body.onload')}" class="yui-skin-sam admin">
<div style="padding-left:10px;padding-top:5px;"><img src="${createLinkTo(dir: 'images', file: 'RapidInsight-white.png')}"></div>
<div class="nav"><h1 style="display:inline">RapidInsight For Smarts</h1><span class="menuButton"><a href="auth/logout?targetUri=/admin.gsp" class="logout">Logout</a></span></div>
<p style="margin-left:20px;width:80%">This is the basic administration UI where you can configure RapidInsight.</p>
<br>
<p/>
<p/>
<div class="yui-navset">
    <ul class="yui-nav">
        <%
            String currentUrl = request.uri.toString();
        %>
        <li class="${(currentUrl.indexOf('netcoolAdmin.gsp') > -1 || currentUrl.matches('.*/netcoolConnector/.*'))? "selected":""}"><a href="${createLinkTo(file: 'netcoolAdmin.gsp')}"><em>Netcool</em></a></li>
        <li class="${currentUrl.indexOf('synchronize.gsp') > -1 ? "selected":""}"><a href="${createLinkTo(file: 'synchronize.gsp')}"><em>Configuration Synch.</em></a></li>
        <li class="${currentUrl.matches('.*/script/.*')? "selected":""}"><g:link action="list" controller="script"><em>Scripts</em></g:link></li>
        <li class="${currentUrl.matches('.*/netcoolConversionParameter/.*')? "selected":""}"><g:link action="list" controller="netcoolConversionParameter"><em>Conversion Parameters</em></g:link></li>
        <li class="${currentUrl.indexOf('reload.gsp') > -1 ? "selected":""}"><a href="${createLinkTo(file: 'reload.gsp')}"><em>Reload</em></a></li>
        <li class="${currentUrl.matches('.*/rsUser/.*')? "selected":""}"><g:link action="list" controller="rsUser"><em>Users</em></g:link></li>
        <li class="${currentUrl.matches('.*/group/.*')? "selected":""}"><g:link action="list" controller="group"><em>Groups</em></g:link></li>
        <li class="${(currentUrl.indexOf('datasources.gsp') > -1 || currentUrl.matches('.*/.*(Datasource|Connection)/.*'))? "selected":""}"><a href="${createLinkTo(file: 'datasources.gsp')}"><em>Datasources</em></a></li>
    </ul>
    <div style="margin:20px 15px 10px;">
        <g:layoutBody/>
    </div>
</div>
</body>
</html>