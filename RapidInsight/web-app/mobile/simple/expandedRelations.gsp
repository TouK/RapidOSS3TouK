<%
    def name = params.name
    def object = RsTopologyObject.get(name: name)
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
    <rui:link url="mobile/simple/objectDetails.gsp" params="${[name:name]}">Object Details</rui:link>
    </div>
</div>
<div>
    <g:if test="${!object}">
        <div class="error">
            Object with name: ${name} does not exist
        </div>
    </g:if>
    <g:else>
        <div class="group">${params.relationName} of ${params.name}</div>
        <div>
            <ul style="margin-left: 10px;list-style:none">
                <%
                    objectProperties=object.retrieveVisibleProperties();
                    def sortedRelatedObjects =objectProperties[params.relationName];
                %>
                <g:each var="rObj" in="${sortedRelatedObjects}">
                    <li><rui:link url="mobile/simple/objectDetails.gsp" params="${[name:rObj.name]}">${rObj.className} ${rObj.name}</rui:link></li>
                </g:each>
            </ul>
        </div>
    </g:else>
</div>
</body>
</html>