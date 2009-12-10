<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Dec 9, 2009
  Time: 3:00:24 PM
--%>
<html>
<head>
    <title>ROSSMobile</title>
    <link rel="stylesheet" type="text/css" href="${createLinkTo(dir: 'css/mobile', file: 'simple.css')}"/>
</head>
<body>
<div class="toolbar">
    <img src="${createLinkTo(dir:'images', file:'RapidOSSsmall.png')}"/>
    <div class="toolbarLinks">
        <a href="${createLinkTo(dir:'mobile/simple', file:'home.gsp')}">Home</a>|
        <a href="${createLinkTo(dir:'mobile/simple', file:'notifications.gsp')}">Notifications</a>
    </div>
</div>
<rui:include template="mobile/contents/messageRuleForm.gsp" model="${binding.variables}"></rui:include>
<div class="footer">
    <g:link controller="auth" action="logout">Logout</g:link>
</div>
</body>
</html>