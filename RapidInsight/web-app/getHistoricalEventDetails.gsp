<%@ page import="java.sql.Timestamp; java.text.SimpleDateFormat; com.ifountain.rcmdb.util.RapidCMDBConstants; org.codehaus.groovy.grails.commons.GrailsDomainClass; org.codehaus.groovy.grails.commons.ApplicationHolder" %>
<%
    def componentId = params.componentId
    def notificationId = params.id;
    def domainObject = RsHistoricalEvent.get(id: notificationId);
    def excludedProps = ["createdAt", "changedAt", "clearedAt"]
    if (domainObject != null) {
        def propList = domainObject.getPropertiesList();
        def allProperties = propList.findAll{!excludedProps.contains(it.name)}
%>
<div class="yui-navset yui-navset-top" style="margin-top:5px">
    <ul class="yui-nav">
        <li class="selected">
            <a onclick="YAHOO.rapidjs.Components['${componentId}'].show('getHistoricalEventDetails.gsp?id=' + encodeURIComponent('${domainObject.id}'));">
                <em>Properties</em>
            </a>
        </li>
        <li>
            <a onclick="YAHOO.rapidjs.Components['${componentId}'].show('getEventJournals.gsp?id=' + encodeURIComponent('${domainObject.id}') + '&isHistorical=true');">
                <em>Journal</em>
            </a>
        </li>
    </ul>
    <div style="display:block;margin-top:10px;">
        <%
                SimpleDateFormat format = new SimpleDateFormat("dd MMM HH:mm:ss")
                def createdAt = domainObject.createdAt == 0 ? "never" : format.format(new Timestamp(domainObject.createdAt));
                def changedAt = domainObject.changedAt == 0 ? "never" : format.format(new Timestamp(domainObject.changedAt));
                def clearedAt = domainObject.clearedAt == 0 ? "never" : format.format(new Timestamp(domainObject.clearedAt));
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
                    <div class="ri-object-details" style="width:100%">
                        <table cellspacing="2" cellpadding="2" width="100%">
                            <tbody>

                                <g:each var="property" status="i" in="${allProperties}">
                                    <%
                                            def propertyName = property.name;
                                    %>
                                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                        <td style="font-weight:bold">${propertyName}</td>
                                        <td>${domainObject[propertyName]}</td>
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
                                    <b>Created At:</b> ${createdAt}<br>
                                    <b>Changed At:</b> ${changedAt}<br>
                                    <b>Cleared At:</b> ${clearedAt}<br>
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
Event with id ${notificationId} does not exist.
<%
    }
%>
