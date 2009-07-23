<jsec:lacksRole name="Administrator">
    <%
        response.sendRedirect(createLinkTo(dir: 'auth/unauthorized'));
        return;
    %>
</jsec:lacksRole>

<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Nov 12, 2008
  Time: 2:52:00 PM
--%>

<html>
<head>
    <title>RapidInsight Admin UI</title>
    <rui:stylesheet dir="js/yui/assets/skins/sam" file="skin.css"></rui:stylesheet>
    <link rel="stylesheet" href="${createLinkTo(dir: 'css/rapidjs', file: 'yuioverride.css')}"/>
    <link rel="stylesheet" href="${createLinkTo(dir: 'css', file: 'main.css')}"/>
    <link rel="stylesheet" href="${createLinkTo(file: 'rimain.css')}"/> 
    <g:layoutHead/>
</head>
<body onload="${pageProperty(name: 'body.onload')}" class="yui-skin-sam rimain">
<div style="padding-left:10px;padding-top:5px;"><img src="${createLinkTo(dir: 'images', file: 'RapidInsight-white.png')}"><span class="menuButton"><a href="${createLinkTo(dir:'auth/logout?targetUri=/admin.gsp')}" class="logout">Logout</a></span></div>
<br>
<p/>
<p/>
<div class="yui-navset">
    <ul class="yui-nav">
        <%
            String currentUrl = request.uri.toString();
        %>
        <li class="${currentUrl.matches('.*/script/.*') ? "selected" : ""}"><g:link action="list" controller="script"><em>Scripts</em></g:link></li>
        <li class="${currentUrl.indexOf('reload.gsp') > -1 ? "selected" : ""}"><a href="${createLinkTo(file: 'reload.gsp')}"><em>Reload</em></a></li>
        <li class="${currentUrl.matches('.*/rsUser/.*') ? "selected" : ""}"><g:link action="list" controller="rsUser"><em>Users</em></g:link></li>
        <li class="${currentUrl.matches('.*/group/.*') ? "selected" : ""}"><g:link action="list" controller="group"><em>Groups</em></g:link></li>
        <li class="${(currentUrl.indexOf('dbDatasources.gsp') > -1 || currentUrl.matches('.*/.*([dD]atabase).*/.*')) ? "selected" : ""}"><a href="${createLinkTo(file: 'dbDatasources.gsp')}"><em>Database</em></a></li>
        <li class="${currentUrl.matches('.*/snmpConnector/.*') ? "selected" : ""}"><g:link action="list" controller="snmpConnector"><em>SNMP</em></g:link></li>
        <li class="${currentUrl.matches('.*/ldapConnection/.*') ? "selected" : ""}"><g:link action="list" controller="ldapConnection"><em>LDAP</em></g:link></li>
        <li class="${ ( currentUrl.indexOf('notificationChannels.gsp') > -1 || currentUrl.matches('.*/.*[(email)(sms)(aol)(jabber)(sametime)]Connector/.*') )? "selected" : ""}"><a href="${createLinkTo(file: 'notificationChannels.gsp')}"><em>Notification Channels</em></a></li>
        %{--<li class="${(currentUrl.indexOf('datasources.gsp') > -1 || currentUrl.matches('.*/.*^([dD]atabase).*(Datasource|Connection)/.*')) ? "selected" : ""}"><a href="${createLinkTo(file: 'datasources.gsp')}"><em>Datasources</em></a></li>--}%
    </ul>
    <div style="margin:5px 5px 5px;">
        <g:layoutBody/>
    </div>
</div>
</body>
</html>