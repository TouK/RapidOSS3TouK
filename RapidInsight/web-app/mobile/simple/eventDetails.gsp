<%@ page import="search.SearchQuery; auth.RsUser; java.text.SimpleDateFormat" %>
<%
    CONFIG = [:]
%>
<rui:include template="mobile/config.gsp" model="${['CONFIG':CONFIG]}"></rui:include>
<%
    def name = params.name
    def event = CONFIG.EVENT_CLASS.get(name: name)
    def format = new SimpleDateFormat("d MMM HH:mm:ss");
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
    <rui:link url="mobile/simple/queries.gsp" params="${[filterType:'event', listURI:'mobile/simple/event.gsp']}">Queries</rui:link>|
    <rui:link url="mobile/simple/search.gsp" params="${[rootClass:CONFIG.EVENT_CLASS.name]}">Search</rui:link>
    </div>
    <g:if test="${CONFIG.EVENT_ACTIONS.size() > 0}">
        <div class="toolbarLinks">
            <rui:link url="mobile/simple/actions.gsp" params="${[name:params.name, type:'event']}">Event Actions</rui:link>
        </div>
    </g:if>
</div>
<div>

    <g:if test="${!event}">
        <div class="error">
            Event with name: ${name} does not exist
        </div>
    </g:if>
    <g:else>
        <div class="ri-mobile-tab">
            <ul>
                <li class="selected"><rui:link url="mobile/simple/eventDetails.gsp" params="${[name:event.name]}">Properties</rui:link></li>
                <li><rui:link url="mobile/getJournals.gsp" params="${[eventId:event.id]}">Journal</rui:link></li>
            </ul>
        </div>
        <g:set var="props" value="${event.asMap()}"></g:set>
        <table class="itable" width="100%" border="0" cellspacing="0" cellpadding="3">
            <g:each var="propEntry" in="${props}" status="i">
                <g:set var="propertyName" value="${propEntry.key}"></g:set>
                <g:set var="propertyValue" value="${propEntry.value}"></g:set>
                <tr class="${(i % 2) == 0 ? 'alt' : 'reg'}">
                    <td><b>${propertyName}</b></td>
                    <g:if test="${CONFIG.EVENT_DATE_PROPERTIES.contains(propertyName)}">
                        <%
                            propertyValue = (propertyValue == 0) ? 'never' : format.format(new Date(propertyValue))
                        %>
                    </g:if>
                    <td>${propertyValue}</td>
                </tr>
            </g:each>
        </table>
    </g:else>
</div>
<div class="footer">
    <g:link controller="auth" action="logout">Logout</g:link>
</div>
</body>
</html>