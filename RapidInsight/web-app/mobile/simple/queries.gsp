<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Oct 15, 2009
  Time: 9:31:58 AM
--%>
<%
    CONFIG = [:]
%>
<rui:include template="mobile/config.gsp" model="${['CONFIG':CONFIG]}"></rui:include>
<%
    def rootClass = params.filterType == "topology"?"RsTopologyObject":params.filterType == "event" ? CONFIG.EVENT_CLASS.name: CONFIG.HISTORICAL_EVENT_CLASS.name;
%>
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
        <rui:link url="mobile/simple/search.gsp" params="${[rootClass:rootClass]}">Search</rui:link>
    </div>
</div>
<rui:include template="mobile/contents/queries.gsp" model="${binding.variables}"></rui:include>
<div class="footer">
    <g:link controller="auth" action="logout">Logout</g:link>
</div>
</body>
</html>