<%@ page import="com.ifountain.rcmdb.mobile.MobileUtils; search.SearchQuery; auth.RsUser; java.text.SimpleDateFormat" %>
<%
    CONFIG = [:]
%>
<rui:include template="mobile/config.gsp" model="${['CONFIG':CONFIG]}"></rui:include>
<%
     def gspFolder = "simple"
    if(MobileUtils.isIphone(request)){
        gspFolder = "iphone";
    }
    def id = params.eventId
    def event = CONFIG.HISTORICAL_EVENT_CLASS.get(id: id)
    def format = new SimpleDateFormat("d MMM HH:mm:ss");
%>

<div id="historicalEventDetails">

    <g:if test="${!event}">
        <div id="messageArea" class="error">
            HistoricalEvent with id: ${id} does not exist
        </div>
    </g:if>
    <g:else>
        <div class="ri-mobile-tab">
        <ul>
            <li class="selected"><rui:link url="mobile/${gspFolder}/historicalEventDetails.gsp" params="${[eventId:event.id]}">Properties</rui:link></li>
            <li><rui:link url="mobile/getJournals.gsp" params="${[eventId:event.id, isHistorical:'true']}">Journal</rui:link></li>
        </ul>
        </div>
        <g:set var="props" value="${event.asMap()}"></g:set>
        <table class="itable" width="100%" border="0" cellspacing="0" cellpadding="3">
            <g:each var="propEntry" in="${props}" status="i">
                <g:set var="propertyName" value="${propEntry.key}"></g:set>
                <g:set var="propertyValue" value="${propEntry.value}"></g:set>
                <tr class="${(i % 2) == 0 ? 'alt' : 'reg'}">
                    <td><b>${propertyName}</b></td>
                    <g:if test="${CONFIG.HISTORICAL_EVENT_DATE_PROPERTIES.contains(propertyName)}">
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