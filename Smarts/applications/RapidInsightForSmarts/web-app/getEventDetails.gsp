<%@ page import="java.sql.Timestamp; java.text.SimpleDateFormat; com.ifountain.rcmdb.util.RapidCMDBConstants; org.codehaus.groovy.grails.commons.GrailsDomainClass; org.codehaus.groovy.grails.commons.ApplicationHolder" %>
<%
    def eventName = params.eventName;
    def instanceName = params.instanceName;
    def className = params.className;
    def allProperties = ["className", "instanceName", "eventName", "sourceDomainName", "acknowledged", "owner","elementClassName", "elementName", 
                        "severity", "eventText", "isRoot", "isProblem", "certainty", "eventType", "category", "impact", "inMaintenance"];
    def domainObject = RsEvent.get(eventName:eventName, instanceName:instanceName, className:className);
    if(domainObject != null){
         %>
           <div class="yui-navset yui-navset-top">
                <ul class="yui-nav">
                    <li class="selected"><a><em>Properties</em></a></li>
                    <li><a onclick="YAHOO.rapidjs.Components['eventDetails'].show('getAuditLog.gsp?id=${domainObject?.id}');"><em>Audit Log</em></a></li>
                </ul>
                <div style="display:block;margin-top:10px;">
                    <%
                        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss")
                        def firstNotifiedAt = format.format(new Timestamp(domainObject.firstNotifiedAt));
                        def lastNotifiedAt = format.format(new Timestamp(domainObject.lastNotifiedAt));
                        def lastChangedAt = format.format(new Timestamp(domainObject.lastChangedAt));
                        def severityStyle;
                        def severity = domainObject.severity;
                        if(severity == 1){
                            severityStyle = "url(images/rapidjs/component/states/red.png) center";
                        }
                        else if(severity ==2){
                            severityStyle = "url(images/rapidjs/component/states/orange.png) center";
                        }
                        else if(severity ==3){
                            severityStyle = "url(images/rapidjs/component/states/yellow.png) center";
                        }
                        else if(severity ==4){
                            severityStyle = "url(images/rapidjs/component/states/blue.png) center";
                        }
                        else{
                           severityStyle = "url(images/rapidjs/component/states/green.png) center";
                        }

                    %>
                    <div style="padding:10px;float:right">
                        <div style="border:1px solid;padding:3px;">
                            <div style="background:${severityStyle};width:16px;height:16px;"></div>
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

                            <g:each var="propertyName" in="${allProperties}">
                                <tr>
                                    <td>${propertyName}</td>
                                    <%
                                        if(propertyName == "instanceName"){
                                        %>
                                           <td><a style="color:#006DBA;text-decoration:underline;cursor:pointer" onclick="YAHOO.rapidjs.Components['objectDetails'].show('getObjectDetails.gsp?name=${domainObject[propertyName]}');">${domainObject[propertyName]}</a></td>
                                        <%
                                        }
                                        else{
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
    else{
        %>
            Event ${className} ${instanceName} ${eventName} does not exist.
        <%
    }
%>
