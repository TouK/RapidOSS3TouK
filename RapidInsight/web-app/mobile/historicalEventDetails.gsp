<%@ page import="search.SearchQuery; auth.RsUser; java.text.SimpleDateFormat" %>
<%
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    DATE_PROPERTIES = ["createdAt", "changedAt", "clearedAt", "willExpireAt"]
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    def id = params.eventId
    def event = RsHistoricalEvent.get(id: id)
    def format = new SimpleDateFormat("d MMM HH:mm:ss");
%>

<div id="historicalEventDetails" title="Details of ${event.name}:Details">

    <g:if test="${!event}">
        <div id="messageArea" class="error">
            HistoricalEvent with id: ${id} does not exist
        </div>
    </g:if>
    <g:else>
        <g:set var="props" value="${event.asMap()}"></g:set>
        <table class="itable" width="100%" border="0" cellspacing="0" cellpadding="3">
            <g:each var="propEntry" in="${props}" status="i">
                <g:set var="propertyName" value="${propEntry.key}"></g:set>
                <g:set var="propertyValue" value="${propEntry.value}"></g:set>
                <tr class="${(i % 2) == 0 ? 'alt' : 'reg'}">
                    <td><b>${propertyName}</b></td>
                    <g:if test="${DATE_PROPERTIES.contains(propertyName)}">
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