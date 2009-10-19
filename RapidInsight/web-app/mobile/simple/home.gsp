<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Oct 15, 2009
  Time: 9:31:58 AM
--%>

<html>
<head>
    <title>ROSSMobile</title>
    <link rel="stylesheet" type="text/css" href="${createLinkTo(dir: 'css/mobile', file: 'simple.css')}"/>
</head>
<body>
<div class="toolbar">
    <h1>ROSS</h1>
</div>
<ul class="list">
    <rui:include template="mobile/contents/pages.gsp" model="${binding.variables}"></rui:include>
</ul>
<div class="footer">
    <g:link controller="auth" action="logout">Logout</g:link>
</div>
</body>
</html>