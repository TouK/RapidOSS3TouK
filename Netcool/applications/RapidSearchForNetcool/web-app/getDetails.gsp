<%@ page import="com.ifountain.rcmdb.util.RapidCMDBConstants; org.codehaus.groovy.grails.commons.GrailsDomainClass; org.codehaus.groovy.grails.commons.ApplicationHolder" %>
<%
    def type = params.type;
    def id = params.id;
    def allProperties = [];
    def domainObject = null;
    GrailsDomainClass domainClass = ApplicationHolder.application.getDomainClass("NetcoolEvent");
    if (domainClass != null && id != null)
    {
        domainClass.getProperties().each{
            if(it.name != RapidCMDBConstants.ERRORS_PROPERTY_NAME && it.name != RapidCMDBConstants.OPERATION_PROPERTY_NAME)
            {
                allProperties += it;
            }
        }
        domainObject = NetcoolEvent.get(id: id);
    }
    if (domainObject != null) {
%>
<div class="yui-navset yui-navset-top" style="margin-top:5px">
    <ul class="yui-nav">
        <li class="selected"><a><em>Event</em></a></li>
        <li><a onclick="window.html.show('getJournals.gsp?type=NetcoolJournal&id=${domainObject?.id}');"><em>Journal</em></a></li>
    </ul>
    <div style="display:block">
        <table>
            <tbody>

                <g:each var="property" in="${allProperties}">
                    <tr>
                        <td>${property.name}</td>
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


