<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Nov 11, 2008
  Time: 4:01:33 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="com.ifountain.rcmdb.domain.util.DomainClassUtils; com.ifountain.rcmdb.util.RapidCMDBConstants" %>
<%
    def id = params.id;
    def componentId = params.componentId;
    def allProperties = [];
    def domainObject = null;
    if (id != null)
    {
        allProperties = DomainClassUtils.getFilteredProperties("NetcoolHistoricalEvent", ["id"])
        domainObject = NetcoolHistoricalEvent.get(id: id);
    }
    if (domainObject != null) {
%>
<div style="margin-top:5px">
    <div style="display:block" class="netcool-object-details">
        <table width="100%">
            <tbody>
                <g:each var="property" status="i" in="${allProperties}">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        <td style="font-weight:bold">${property.name}</td>
                        <td>${domainObject[property.name]}</td>
                    </tr>
                </g:each>

            </tbody>
        </table>
    </div>
</div>
<%
    }
    else {
%>
No data found
<%
    }
%>