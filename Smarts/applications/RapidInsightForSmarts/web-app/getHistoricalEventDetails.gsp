<%@ page import="java.sql.Timestamp; java.text.SimpleDateFormat; com.ifountain.rcmdb.util.RapidCMDBConstants; org.codehaus.groovy.grails.commons.GrailsDomainClass; org.codehaus.groovy.grails.commons.ApplicationHolder" %>
<%
    def notificationId = params.id;
    def domainObject = RsHistoricalEvent.get(id: notificationId);
    if (domainObject != null) {
        def allProperties = domainObject.getModelProperties();
%>
<div class="yui-navset yui-navset-top">
    <ul class="yui-nav">
        <li class="selected">
            <a onclick="YAHOO.rapidjs.Components['eventDetails'].show('getHistoricalEventDetails.gsp?id=${domainObject.id}');">
                <em>Properties</em>
            </a>
        </li>
    </ul>
    <div style="display:block;margin-top:10px;">
        <%
                SimpleDateFormat format = new SimpleDateFormat("dd MMM HH:mm:ss")
                def firstNotifiedAt = format.format(new Timestamp(domainObject.firstNotifiedAt * 1000));
                def lastNotifiedAt = format.format(new Timestamp(domainObject.lastNotifiedAt * 1000));
                def lastChangedAt = format.format(new Timestamp(domainObject.lastChangedAt * 1000));
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

                                <g:each var="property" status="i" in="${allProperties}">
                                    <%
                                            def propertyName = property.name;
                                    %>
                                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                        <td style="font-weight:bold">${propertyName}</td>
                                        <%
                                                if (propertyName == "instanceName" || propertyName == "elementName") {
                                                    def title = propertyName == "instanceName" ? "Details of ${domainObject.className} ${domainObject.instanceName}" : "Details of ${domainObject.elementClassName} ${domainObject.elementName}"
                                        %>
                                        <td><a style="color:#006DBA;text-decoration:underline;cursor:pointer" onclick="YAHOO.rapidjs.Components['objectDetails'].show('getObjectDetails.gsp?name=${domainObject[propertyName]}', '${title}');">${domainObject[propertyName]}</a></td>
                                        <%
                                            }
                                            else {
                                        %>
                                        <td>${domainObject[propertyName]}</td>
                                        <%
                                                }
                                        %>

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
                            <div style="padding:3px 20px 20px 20px;width:190px;">
                                <b>First Notified At:</b> ${firstNotifiedAt}<br>
                                <b>Last Notified At:</b> ${lastNotifiedAt}<br>
                                <b>Last Changed At:</b> ${lastChangedAt}<br>
                                <b>Count:</b> ${domainObject.occurrenceCount}<br>
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
Event ${className} ${instanceName} ${eventName} does not exist.
<%
    }
%>
