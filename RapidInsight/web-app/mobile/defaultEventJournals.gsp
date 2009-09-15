<%@ page import="java.text.SimpleDateFormat" %>
<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Nov 26, 2008
  Time: 4:42:43 PM
--%>
<%
    CONFIG = [:]
%>
<rui:include template="mobile/config.gsp" model="${['CONFIG':CONFIG]}"></rui:include>
<%
    def isHistorical = params.isHistorical;
    def eventId = params.eventId;
    def domainObject = isHistorical == "true" ? CONFIG.HISTORICAL_EVENT_CLASS.get(id: eventId) : CONFIG.EVENT_CLASS.get(id: eventId);
%>
<div id="journals">
    <g:if test="${domainObject}">
        <%
            def format = new SimpleDateFormat("d MMM HH:mm:ss");
            eventId = isHistorical == "true" ? domainObject.activeId : eventId;
            def journals = RsEventJournal.searchEvery("eventId:${eventId}", params)
            def propertiesUrl = isHistorical == "true" ? "mobile/historicalEventDetails.gsp" : "mobile/eventDetails.gsp";
            def propertiesParams = isHistorical == "true" ? [eventId: domainObject.id] : [name: domainObject.name];
        %>
        <div class="ri-mobile-tab">
            <ul>
                <li><rui:link url="${propertiesUrl}" params="${propertiesParams}" target="_open">Properties</rui:link></li>
                <li class="selected"><rui:link url="mobile/getJournals.gsp" params="${[eventId:params.eventId]}" target="_open">Journal</rui:link></li>
            </ul>
        </div>
        <div class="table">
            <table class="itable" height="100%" width="100%" border="0" cellspacing="0" cellpadding="3">
                <thead>
                    <tr>
                        <rui:sortableColumn property="rsTime" title="Date" url="mobile/getJournals.gsp" linkAttrs="${[params:[isHistorical:params.isHistorical, eventId:params.eventId]]}"/>
                        <rui:sortableColumn property="eventName" title="Event" url="mobile/getJournals.gsp" linkAttrs="${[params:[isHistorical:params.isHistorical, eventId:params.eventId]]}"/>
                        <rui:sortableColumn property="details" title="Details" url="mobile/getJournals.gsp" linkAttrs="${[params:[isHistorical:params.isHistorical, eventId:params.eventId]]}"/>
                    </tr>
                </thead>
                <tbody>
                    <g:each in="${journals}" status="i" var="journal">
                        <tr class="${(i % 2) == 0 ? 'alt' : 'reg'}">
                            <td>${format.format(journal.rsTime.getTime()).encodeAsHTML()}</td>
                            <td>${journal.eventName?.encodeAsHTML()}</td>
                            <td>${journal.details?.encodeAsHTML()}</td>
                        </tr>
                    </g:each>
                </tbody>
            </table>
        </div>
    </g:if>
    <g:else>
        <div class="error">
            Event with id: ${eventId} does not exist
        </div>
    </g:else>
</div>