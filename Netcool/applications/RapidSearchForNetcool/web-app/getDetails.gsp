<%@ page import="org.codehaus.groovy.grails.commons.GrailsDomainClass; org.codehaus.groovy.grails.commons.ApplicationHolder" %>
<%
    def type = params.type;
    def id = params.id;
    def allProperties = [];
    def domainObject = null;
    GrailsDomainClass domainClass = ApplicationHolder.application.getDomainClass("NetcoolEvent");
    if (domainClass != null && id != null)
    {
        allProperties = domainClass.getProperties();
        domainObject = NetcoolEvent.get(id: id);
    }
    if (domainObject != null) {
%>
<div class="yui-navset yui-navset-top">
    <ul class="yui-nav">
        <li class="selected"><a href="#"><em>Event</em></a></li>
        <li><a href="#" onclick="window.html.show('getJournals.gsp?type=NetcoolJournal&id=${domainObject?.id}');"><em>Journal</em></a></li>
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


