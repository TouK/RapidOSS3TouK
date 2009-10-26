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
    def type = params.type;
    def actionConfig = CONFIG.EVENT_ACTIONS;
    def listURI = "mobile/simple/event.gsp"
    def detailsUrl = "mobile/simple/eventDetails.gsp";
    def detailsLink = "Event";
    def domainObject;
    if (type == 'topology') {
        actionConfig = CONFIG.INVENTORY_ACTIONS;
        listURI = "mobile/simple/inventory.gsp"
        detailsUrl = "mobile/simple/objectDetails.gsp";
        detailsLink = "Object";
        domainObject = RsTopologyObject.get(name: params.name);
    }
    else {
        domainObject = CONFIG.EVENT_CLASS.get(name: params.name);
    }
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
    <rui:link url="mobile/simple/queries.gsp" params="${[filterType:type, listURI:listURI]}">Queries</rui:link>|
    <rui:link url="${detailsUrl}" params="${[name:params.name]}">${detailsLink} Details</rui:link>
    </div>
</div>
<g:render template="/common/messages" model="[flash:flash, beans:[]]"></g:render>
<g:if test="${domainObject}">
    <ul class="list">
        <g:each var="actionConf" in="${actionConfig}">
            <g:if test="${actionConf.type && actionConf.type == 'group'}">
                <li>
                    <div>
                        <div class="group">${actionConf.title.encodeAsHTML()}</div>
                    </div>
                    <div>
                        <ul>
                            <g:each var="subActionConf" in="${actionConf.actions}">
                                <g:if test="${!subActionConf.visible || subActionConf.visible(domainObject)}">
                                    <%
                                        def subScriptParams = subActionConf.parameters ? subActionConf.parameters(domainObject) : [:]
                                        subScriptParams["scriptName"] = subActionConf.scriptName;
                                        subScriptParams["redirectUrl"] = rui.createLink(url:"mobile/simple/actions.gsp", params:[type:type, name:params.name]);
                                    %>
                                    <li><rui:link url="mobile/scriptExecuter.gsp" params="${subScriptParams}">${subActionConf.title.encodeAsHTML()}</rui:link></li>
                                </g:if>
                            </g:each>
                        </ul>
                    </div>
                </li>
            </g:if>
            <g:else>
                <g:if test="${!actionConf.visible || actionConf.visible(domainObject)}">
                    <%
                        def scriptParams = actionConf.parameters ? actionConf.parameters(domainObject) : [:]
                        scriptParams["scriptName"] = actionConf.scriptName;
                        scriptParams["redirectUrl"] = rui.createLink(url:"mobile/simple/actions.gsp", params:[type:type, name:params.name]);
                    %>
                    <li><rui:link url="mobile/scriptExecuter.gsp" params="${scriptParams}">${actionConf.title.encodeAsHTML()}</rui:link></li>
                </g:if>
            </g:else>
        </g:each>
    </ul>
</g:if>
<g:else>
    <div class="error">
        ${detailsLink} with name: ${params.name} does not exist
    </div>
</g:else>
<div class="footer">
    <g:link controller="auth" action="logout">Logout</g:link>
</div>
</body>
</html>