<html>
<head>
    <title><g:layoutTitle default="Grails"/></title>
    <rui:stylesheet dir="js/yui/assets/skins/sam" file="skin.css"></rui:stylesheet>
    <link rel="stylesheet" href="${createLinkTo(dir: 'css', file: 'main.css')}"/>
    <link rel="stylesheet" href="${createLinkTo(file: 'admin.css')}"/>
     <jsec:isNotLoggedIn>
        <g:javascript>window.location='auth/login?targetUri=/admin.gsp'</g:javascript>
    </jsec:isNotLoggedIn>
    <g:layoutHead/>
    <g:javascript library="application"/>
</head>
<body onload="${pageProperty(name: 'body.onload')}" class="yui-skin-sam admin">
<div style="padding-left:10px;padding-top:5px;"><img src="${createLinkTo(dir:'images',file:'RapidInsight-white.png')}"></div>
<div class="nav"><h1 style="display:inline">RapidInsight For Smarts</h1><span class="menuButton"><a href="${createLinkTo(dir: 'auth/logout?targetUri=/admin.gsp')}" class="logout">Logout</a></span></div>
<p style="margin-left:20px;width:80%">This is the basic administration UI where you can define your connections....</p>
<br>
<p/>
<p/>
<div class="yui-navset">
    <ul class="yui-nav">
        <li><a href="${createLinkTo(file: 'admin.gsp')}"><em>Connectors</em></a></li>
        <li class="selected"><g:link action="list" controller="script"><em>Scripts</em></g:link></li>
        <li><a href="${createLinkTo(file: 'reload.gsp')}"><em>Reload</em></a></li>
        <li><g:link action="list" controller="rsUser"><em>Users</em></g:link></li>
    </ul>
    <div style="margin:20px 15px 10px;">
        <g:layoutBody/>
    </div>
</div>

</body>
</html>