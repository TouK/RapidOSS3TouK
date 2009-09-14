<%@ page import="java.text.SimpleDateFormat" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: Sep 4, 2009
  Time: 3:44:07 PM
  To change this template use File | Settings | File Templates.
--%>
<%
    CONFIG = [:]
%>
<rui:include template="mobile/config.gsp" model="${['CONFIG':CONFIG]}"></rui:include>
<%
    def shortenProperty = {propValue ->
        def sProp = propValue.toString();
        if (sProp.length() > 15) {
            sProp = "${sProp.substring(0, 12)}.."
        }

        return sProp;
    }
    def query = params.query ? params.query : "alias:*"
    if (params.max == null) {
        params.max = 100
    }
    def events = CONFIG.HISTORICAL_EVENT_CLASS.search(query, params);
    def total = events.total;
    def format = new SimpleDateFormat("d MMM HH:mm:ss");
%>
<div title="Historical Events" id="historicalEventList">
    <div class="table">
        <table class="itable" height="100%" width="100%" border="0" cellspacing="0" cellpadding="3">
            <thead>
                <tr>
                    <th></th>
                    <g:each var="column" in="${CONFIG.HISTORICAL_EVENT_COLUMNS}">
                        <rui:sortableColumn property="${column.propertyName}" title="${column.title}" url="mobile/historicalEvent.gsp" linkAttrs="${[params:[query:query]]}"/>
                    </g:each>
                </tr>
            </thead>
            <tbody>
                <g:each in="${events.results}" status="i" var="rsEvent">
                    <tr class="${(i % 2) == 0 ? 'alt' : 'reg'}" onclick="window.iui.showPageByHref('${rui.createLink(url: 'mobile/historicalEventDetails.gsp', params: [eventId: rsEvent.id])}')">
                        <td width="1%"><img src="${createLinkTo(dir: CONFIG.SEVERITY_MAPPING[rsEvent.severity.toString()] ? CONFIG.SEVERITY_MAPPING[rsEvent.severity.toString()] : CONFIG.SEVERITY_MAPPING['default'])}" height="25px" width="19px"/></td>
                        <g:each var="column" in="${CONFIG.HISTORICAL_EVENT_COLUMNS}">
                            <g:if test="${CONFIG.HISTORICAL_EVENT_DATE_PROPERTIES.contains(column.propertyName)}">
                                <td>${format.format(rsEvent[column.propertyName])?.encodeAsHTML()}</td>
                            </g:if>
                            <g:else>
                                <td>${shortenProperty(rsEvent[column.propertyName])?.encodeAsHTML()}</td>
                            </g:else>
                        </g:each>
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>
    <div class="paginateButtons">
        <rui:paginate total="${total}" url="mobile/historicalEvent.gsp" linkAttrs="${[params:[query:query]]}" maxsteps="5" max="100"/>
    </div>
</div>