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
    def searchClass = params.searchIn ? params.searchIn : "RsTopologyObject";
    def domainClass = grailsApplication.getDomainClass(searchClass);
    def objects = domainClass.clazz.search(query, params);
    def total = objects.total;
    def format = new SimpleDateFormat("d MMM HH:mm:ss");
%>
<div id="inventoryList">
    <div class="table">
        <table class="itable" height="100%" width="100%" border="0" cellspacing="0" cellpadding="3">
            <thead>
                <tr>
                    <g:each var="column" in="${CONFIG.INVENTORY_COLUMNS}">
                        <rui:sortableColumn property="${column.propertyName}" title="${column.title}" url="mobile/inventory.gsp" linkAttrs="${[params:[query:query]]}"/>
                    </g:each>
                </tr>
            </thead>
            <tbody>
                <g:each in="${objects.results}" status="i" var="obj">
                    <tr class="${(i % 2) == 0 ? 'alt' : 'reg'}" onclick="window.iui.showPageByHref('${rui.createLink(url: 'mobile/objectDetails.gsp', params: [name: obj.name])}')">
                        <g:each var="column" in="${CONFIG.INVENTORY_COLUMNS}">
                            <g:if test="${CONFIG.INVENTORY_DATE_PROPERTIES.contains(column.propertyName)}">
                                 <td>${format.format(obj[column.propertyName])?.encodeAsHTML()}</td>
                            </g:if>
                            <g:else>
                                 <td>${shortenProperty(obj[column.propertyName])?.encodeAsHTML()}</td>
                            </g:else>
                        </g:each>
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>
    <div class="paginateButtons">
        <rui:paginate total="${total}" url="mobile/inventory.gsp" linkAttrs="${[params:[query:query]]}" maxsteps="5" max="100"/>
    </div>
</div>