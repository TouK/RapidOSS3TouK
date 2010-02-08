<%
    CONFIG = [:]
%>
<rui:include template="mobile/config.gsp" model="${['CONFIG':CONFIG]}"></rui:include>
<%
    name = params.name
    object = RsTopologyObject.get(name: name)
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
        <a href="${createLinkTo(dir: 'mobile/simple', file: 'home.gsp')}">Home</a>|
    <rui:link url="mobile/simple/queries.gsp" params="${[filterType:'topology', listURI:'mobile/simple/inventory.gsp']}">Queries</rui:link>|
    <rui:link url="mobile/simple/search.gsp" params="${[rootClass:'RsTopologyObject']}">Search</rui:link>
    </div>
    <g:if test="${CONFIG.INVENTORY_ACTIONS.size() > 0}">
        <div class="toolbarLinks">
            <rui:link url="mobile/simple/actions.gsp" params="${[name:params.name, type:'topology']}">Object Actions</rui:link>
        </div>
    </g:if>
</div>
<div>
    <g:if test="${!object}">
        <div class="error">
            Object with name: ${name} does not exist
        </div>
    </g:if>
    <g:else>
        <%objectProperties=object.retrieveVisibleProperties(); %>
        <rui:include template="mobile/contents/objectDetails.gsp" model="${binding.variables}"></rui:include>
    </g:else>
</div>
<div class="footer">
    <g:link controller="auth" action="logout">Logout</g:link>
</div>
</body>
</html>