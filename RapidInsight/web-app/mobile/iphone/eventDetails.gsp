<%@ page import="search.SearchQuery; auth.RsUser; java.text.SimpleDateFormat" %>
<%
    CONFIG = [:]
%>
<rui:include template="mobile/config.gsp" model="${['CONFIG':CONFIG]}"></rui:include>
<%
    def name = params.name
    def event = CONFIG.EVENT_CLASS.get(name: name)
    def format = new SimpleDateFormat("d MMM HH:mm:ss");
    def actionGroupIdIndex = 0;
%>
<div id="eventDetails">
    <g:if test="${!event}">
        <div class="error">
            Event with name: ${name} does not exist
        </div>
    </g:if>
    <g:else>
        <div class="ri-mobile-tab">
        <ul>
            <li class="selected"><rui:link url="mobile/iphone/eventDetails.gsp" params="${[name:event.name]}" target="_open">Properties</rui:link></li>
            <li><rui:link url="mobile/getJournals.gsp" params="${[eventId:event.id]}" target="_open">Journal</rui:link></li>
        </ul>
        </div>
        <g:render template="/common/messages" model="[flash:flash, beans:[]]"></g:render>

        <rui:include template="mobile/iphone/actionMenu.gsp" model="${[actions:CONFIG.EVENT_ACTIONS, domainObject:event, title:'Event Actions', redirectUrl:'mobile/iphone/eventDetails.gsp']}"></rui:include>
        
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