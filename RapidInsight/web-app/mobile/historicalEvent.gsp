<%@ page import="java.text.SimpleDateFormat" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: Sep 4, 2009
  Time: 3:44:07 PM
  To change this template use File | Settings | File Templates.
--%>
<%
    //////////////////////////// SEVERITY MAPPING //////////////////////
    SEVERITY_MAPPING = [
                "0":"images/mobile/states/green.png",
                "1":"images/mobile/states/purple.png",
                "2":"images/mobile/states/blue.png",
                "3":"images/mobile/states/yellow.png",
                "4":"images/mobile/states/orange.png",
                "5":"images/mobile/states/red.png"
    ]

    ///////////////////////////////////////////////////////////////////////
    def shortenProperty = {propValue ->
        def sProp = propValue.toString();
        if(sProp.length() > 15){
            sProp = "${sProp.substring(0, 12)}.."
        }

        return sProp;
    }
    def query = params.query ? params.query : "alias:*"
    if(params.max == null){
    	params.max = 100
    }
    def events = RsHistoricalEvent.search(query, params);
    def total = events.total;
    def format = new SimpleDateFormat("d MMM HH:mm:ss");
%>
<div title="Historical Events" id="historicalEventList">
<div class="table">
    <table class="itable" height="100%" width="100%" border="0" cellspacing="0" cellpadding="3">
        <thead>
            <tr>
                <th></th>
                <rui:sortableColumn property="name" title="Name" url="mobile/historicalEvent.gsp" linkAttrs="${[params:[query:query]]}"/>

                <rui:sortableColumn property="acknowledged" title="Ack" url="mobile/historicalEvent.gsp" linkAttrs="${[params:[query:query]]}"/>

                <rui:sortableColumn property="owner" title="Owner" url="mobile/historicalEvent.gsp" linkAttrs="${[params:[query:query]]}"/>
                <rui:sortableColumn property="source" title="Source" url="mobile/historicalEvent.gsp" linkAttrs="${[params:[query:query]]}"/>

            </tr>
        </thead>
        <tbody>
            <g:each in="${events.results}" status="i" var="rsEvent">
                <tr class="${(i % 2) == 0 ? 'alt' : 'reg'}" onclick="window.iui.showPageByHref('${rui.createLink(url:'mobile/historicalEventDetails.gsp', params:[eventId:rsEvent.id])}')">
                    <td width="1%"><img src="${createLinkTo(dir:SEVERITY_MAPPING[rsEvent.severity.toString()] ? SEVERITY_MAPPING[rsEvent.severity.toString()] : SEVERITY_MAPPING["0"])}" height="25px" width="19px"/></td>
                    <td>${shortenProperty(rsEvent.name)?.encodeAsHTML()}</td>
                    <td>${rsEvent.acknowledged.encodeAsHTML()}</td>
                    <td>${shortenProperty(rsEvent.owner)?.encodeAsHTML()}</td>
                    <td>${shortenProperty(rsEvent.source)?.encodeAsHTML()}</td>
                </tr>
            </g:each>
        </tbody>
    </table>
</div>
<div class="paginateButtons">
    <rui:paginate total="${total}" url="mobile/historicalEvent.gsp" linkAttrs="${[params:[query:query]]}" maxsteps="5" max="100"/>
</div>
</div>