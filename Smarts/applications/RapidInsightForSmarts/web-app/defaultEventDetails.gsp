<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Oct 22, 2008
  Time: 2:34:40 PM
--%>

<%@ page import="com.ifountain.rcmdb.domain.util.DomainClassUtils; java.sql.Timestamp; java.text.SimpleDateFormat; com.ifountain.rcmdb.util.RapidCMDBConstants; org.codehaus.groovy.grails.commons.GrailsDomainClass; org.codehaus.groovy.grails.commons.ApplicationHolder" %>
<%
    def componentId = params.componentId
    def notificationName = params.name;
    def allProperties;
    def relations;
    def domainObject = RsEvent.get(name: notificationName);
    if (domainObject != null) {
        allProperties = domainObject.getPropertiesList();
        relations = DomainClassUtils.getRelations(domainObject.getClass().getName());
        def excludedProps = ["id", "rsDatasource", "firstNotifiedAt", "lastNotifiedAt", "lastChangedAt", "lastClearedAt"]
        def filteredProps = allProperties.findAll {!excludedProps.contains(it.name) && !relations.containsKey(it.name)}
%>
<div class="yui-navset yui-navset-top" style="margin-top:5px">
    <ul class="yui-nav">
        <li class="selected">
            <a onclick="YAHOO.rapidjs.Components['${componentId}'].show('getEventDetails.gsp?name=' + encodeURIComponent('${domainObject.name}'));">
                <em>Properties</em>
            </a>
        </li>
    </ul>
    <div style="display:block;margin-top:10px;">
        <%
                SimpleDateFormat format = new SimpleDateFormat("dd MMM HH:mm:ss")
                def firstNotifiedAt = format.format(new Timestamp(domainObject.firstNotifiedAt));
                def lastNotifiedAt = format.format(new Timestamp(domainObject.lastNotifiedAt));
                def lastChangedAt = format.format(new Timestamp(domainObject.lastChangedAt));
                def lastClearedAt = format.format(new Timestamp(domainObject.lastClearedAt));
                def severityClass;
                def severity = domainObject.severity;
                if (severity == 1) {
                    severityClass = "event-details-severity-critical";
                }
                else if (severity == 2) {
                    severityClass = "event-details-severity-major";
                }
                else if (severity == 3) {
                    severityClass = "event-details-severity-minor";
                }
                else if (severity == 4) {
                    severityClass = "event-details-severity-unknown";
                }
                else {
                    severityClass = "event-details-severity-normal";
                }

        %>
        <table>
            <tr>
                <td width="100%">
                    <div class="smarts-object-details" style="width:100%">
                        <table cellspacing="2" cellpadding="2" width="100%">
                            <tbody>
                                <g:each var="prop" status="i" in="${filteredProps}">
                                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                        <td style="font-weight:bold">${prop.name}</td>
                                        <td>${domainObject[prop.name]}</td>
                                    </tr>
                                </g:each>
                            </tbody>
                        </table>

                    </div>
                </td>
                <td width="0%" style="vertical-align:top;">
                    <div style="padding:0px 10px;">
                        <div style="background-color:#EDF5FF;padding:3px;border:#2647A0 1px solid">
                            <div class="event-details-severity ${severityClass}"></div>
                            <div style="padding:3px 20px 20px 20px;">
                                <div style="width:190px;">
                                    <b>First Notified At:</b> ${firstNotifiedAt}<br>
                                    <b>Last Notified At:</b> ${lastNotifiedAt}<br>
                                    <b>Last Changed At:</b> ${lastChangedAt}<br>
                                    <b>Last Cleared At:</b> ${lastClearedAt}<br>
                                </div>
                            </div>
                        </div>
                    </div>
                </td>
            </tr>
        </table>
    </div>
</div>
<%
    }
    else {
%>
Event ${notificationName} does not exist.
<%
    }
%>
