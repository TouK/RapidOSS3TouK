<html>
<head>
    <title>RapidInsight For Smarts Admin UI</title>
    <rui:stylesheet dir="js/yui/assets/skins/sam" file="skin.css"></rui:stylesheet>
    <link rel="stylesheet" href="${createLinkTo(dir: 'css', file: 'main.css')}"/>
    <link rel="stylesheet" href="${createLinkTo(file: 'admin.css')}"/>
    <jsec:isNotLoggedIn>
        <g:javascript>window.location='auth/login?targetUri=/admin.gsp'</g:javascript>
    </jsec:isNotLoggedIn>
    <jsec:lacksRole name="Administrator">
    	<meta http-equiv="REFRESH" content="0;url=${createLinkTo(dir: 'auth/unauthorized')}">
    </jsec:lacksRole>
</head>
<body class="yui-skin-sam admin">
<div style="padding-left:10px;padding-top:5px;"><img src="images/RapidInsight-white.png"></div>
<div class="nav"><h1 style="display:inline">RapidInsight For Smarts</h1><span class="menuButton"><a href="auth/logout?targetUri=/admin.gsp" class="logout">Logout</a></span></div>
<p style="margin-left:20px;width:80%">This is the basic administration UI where you can define your connections ........</p>
<br>
<p/>
<p/>
<div class="yui-navset">
    <ul class="yui-nav">
        <li class="selected"><a href="${createLinkTo(file: 'admin.gsp')}"><em>Home</em></a></li>
        <li><g:link action="list" controller="script"><em>Scripts</em></g:link></li>
        <li><a href="${createLinkTo(file: 'reload.gsp')}"><em>Reload</em></a></li>
        <li><g:link action="list" controller="rsUser"><em>Users</em></g:link></li>
    </ul>
    <div style="margin:20px 15px 10px;">
        <div class="nav">
            
        </div>
        <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
        </g:if>
        <g:hasErrors bean="${flash.errors}">
           <div class="errors">
                <g:renderErrors bean="${flash.errors}"/>
            </div>
        </g:hasErrors>
        <div class="body">
        	
        </div>
    </div>
</div>
</body>
</html>