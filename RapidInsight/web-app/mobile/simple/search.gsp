<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Oct 13, 2009
  Time: 3:48:42 PM
--%>
<%
    CONFIG = [:]
%>
<rui:include template="mobile/config.gsp" model="${['CONFIG':CONFIG]}"></rui:include>
<%
    def rootClass = params.rootClass;
    def title = "Event Search";
    def action = "event.gsp"
    def domainClasses = [];
    if (rootClass == 'RsTopologyObject') {
        title = "Inventory Search"
        action = "inventory.gsp";
        def domainClass = grailsApplication.getDomainClass(rootClass);
        domainClasses.add(domainClass);
        domainClasses.addAll(domainClass.getSubClasses());
        domainClasses = domainClasses.sort {it.fullName}
    }
    else if (rootClass == CONFIG.HISTORICAL_EVENT_CLASS.name) {
        title = "H.Event Search"
        action = "historicalEvent.gsp";
    }
%>
<html>
<head>
    <title>ROSSMobile</title>
    <link rel="stylesheet" type="text/css" href="${createLinkTo(dir: 'css/mobile', file: 'simple.css')}"/>
</head>
<body>
<div class="toolbar">
    <h1 id="pageTitle">${title}</h1>
    <div class="toolbarLinks">
        <a href="${createLinkTo(dir: 'mobile/simple', file: 'home.gsp')}">Home</a>
    </div>
</div>
<div class="form-wrp">
    <form title="Search" action="${action}">
        <div class="row"><label>Query:</label></div>
        <div class="row"><input type="text" name="query" style="width:90%"/></div>
        <g:if test="${rootClass == 'RsTopologyObject'}">
            <div class="row"><label>In:</label></div>
            <div class="row"><g:select name="searchIn" from="${domainClasses.fullName}" style="width:90%"></g:select></div>
        </g:if>
        <div class="row"><input id="searchButton" type="submit" value="Search"/></div>
    </form>
</div>
</body>
</html>