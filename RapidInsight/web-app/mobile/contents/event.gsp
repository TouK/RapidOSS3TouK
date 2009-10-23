<%@ page import="com.ifountain.rcmdb.mobile.MobileUtils; java.text.SimpleDateFormat" %>
<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Sep 4, 2009
  Time: 3:44:07 PM
--%>
<%
    CONFIG = [:]
%>
<rui:include template="mobile/config.gsp" model="${['CONFIG':CONFIG]}"></rui:include>
<%
    def gspFolder = "simple"
    def severityMapping = CONFIG.SEVERITY_MAPPING;
    def resultPerPage = CONFIG.RESULT_PER_PAGE;
    if(MobileUtils.isIphone(request)){
        gspFolder = "iphone";
        severityMapping = CONFIG.IPHONE_SEVERITY_MAPPING;
        resultPerPage = CONFIG.IPHONE_RESULT_PER_PAGE;
    }
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
    def events = CONFIG.EVENT_CLASS.search(query, params);
    def total = events.total;
    def format = new SimpleDateFormat("d MMM HH:mm:ss");
%>
<div id="eventList">
    <div class="table">
        <table class="itable" width="100%" border="0" cellspacing="0" cellpadding="3">
            <thead>
                <tr>
                    <th>&nbsp;</th>
                    <g:each var="column" in="${CONFIG.EVENT_COLUMNS}">
                        <rui:sortableColumn property="${column.propertyName}" title="${column.title}" url="mobile/${gspFolder}/event.gsp" linkAttrs="${[params:[query:query]]}"/>
                    </g:each>
                </tr>
            </thead>
            <tbody>
                <g:each in="${events.results}" status="i" var="rsEvent">
                    <g:if test="${MobileUtils.isIphone(request)}">
                        <tr class="${(i % 2) == 0 ? 'alt' : 'reg'}" onclick="window.iui.showPageByHref('${rui.createLink(url: 'mobile/' + gspFolder + '/eventDetails.gsp', params: [name: rsEvent.name])}')">
                    </g:if>
                    <g:else>
                         <tr class="${(i % 2) == 0 ? 'alt' : 'reg'}">
                    </g:else>
                        <td width="1%"><img src="${createLinkTo(dir: severityMapping[rsEvent.severity.toString()] ? severityMapping[rsEvent.severity.toString()] : severityMapping['default'])}"/></td>
                        <%def isFirstColProcessed = false;%>
                        <g:each var="column" in="${CONFIG.EVENT_COLUMNS}">
                            <g:if test="${CONFIG.EVENT_DATE_PROPERTIES.contains(column.propertyName)}">
                                 <td>
                                     <g:if test="${MobileUtils.isIphone(request) || isFirstColProcessed == true}">
                                        ${format.format(rsEvent[column.propertyName])?.encodeAsHTML()}
                                     </g:if>
                                     <g:else>
                                         <a href="${rui.createLink(url: 'mobile/' + gspFolder + '/eventDetails.gsp', params: [name: rsEvent.name])}">${format.format(rsEvent[column.propertyName])?.encodeAsHTML()}</a>
                                    </g:else>
                                 </td>
                            </g:if>
                            <g:else>
                                <td>
                                     <g:if test="${MobileUtils.isIphone(request) || isFirstColProcessed == true}">
                                        ${shortenProperty(rsEvent[column.propertyName])?.encodeAsHTML()}
                                     </g:if>
                                     <g:else>
                                         <a href="${rui.createLink(url: 'mobile/' + gspFolder + '/eventDetails.gsp', params: [name: rsEvent.name])}">${shortenProperty(rsEvent[column.propertyName])?.encodeAsHTML()}</a>
                                    </g:else>
                                </td>
                            </g:else>
                            <%isFirstColProcessed = true;%>
                        </g:each>
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>
    <div class="paginateButtons">
        <rui:paginate total="${total}" url="mobile/${gspFolder}/event.gsp" linkAttrs="${[params:[query:query]]}" maxsteps="5" max="${resultPerPage}"/>
    </div>
</div>