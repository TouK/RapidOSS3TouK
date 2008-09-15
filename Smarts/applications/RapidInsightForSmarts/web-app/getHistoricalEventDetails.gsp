<%@ page import="java.sql.Timestamp; java.text.SimpleDateFormat; com.ifountain.rcmdb.util.RapidCMDBConstants; org.codehaus.groovy.grails.commons.GrailsDomainClass; org.codehaus.groovy.grails.commons.ApplicationHolder" %>
<%
    def notificationName = params.name;
    def domainObject = RsEvent.get(name: notificationName);
    if (domainObject != null) {
        def allProperties = domainObject.getModelProperties();
%>
<div class="yui-navset yui-navset-top">
    <ul class="yui-nav">
        <li class="selected">
            <a onclick="YAHOO.rapidjs.Components['eventDetails'].show('getHistoricalEventDetails.gsp?name=${domainObject.name}');">
                <em>Properties</em>
            </a>
        </li>
    </ul>
    <div style="display:block;margin-top:10px;">
        <%
                SimpleDateFormat format = new SimpleDateFormat("dd MMM HH:mm:ss")
                def firstNotifiedAt = format.format(new Timestamp(domainObject.firstNotifiedAt*1000));
                def lastNotifiedAt = format.format(new Timestamp(domainObject.lastNotifiedAt*1000));
                def lastChangedAt = format.format(new Timestamp(domainObject.lastChangedAt*1000));
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
        <div style="padding:10px;float:right">
            <div style="border:1px solid;padding:3px;">
                <div class="event-details-severity ${severityClass}"></div>
                <div style="padding:3px 20px 20px 20px;">
                    First Notified At: ${firstNotifiedAt}<br>
                    Last Notified At: ${lastNotifiedAt}<br>
                    Last Changed At: ${lastChangedAt}<br>
                    Count: ${domainObject.occurrenceCount}<br>
                </div>
            </div>
        </div>
        <div>
            <table cellspacing="2" cellpadding="2">
                <tbody>

                    <g:each var="property" in="${allProperties}">
                        <%
                            def propertyName = property.name;
                        %>
                        <tr>
                            <td>${propertyName}</td>
                            <%
                                    if (propertyName == "instanceName" || propertyName == "elementName") {
                                        def title = propertyName == "instanceName"? "Details of ${domainObject.className} ${domainObject.instanceName}" : "Details of ${domainObject.elementClassName} ${domainObject.elementName}"
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
