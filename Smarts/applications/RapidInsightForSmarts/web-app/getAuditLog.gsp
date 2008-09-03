<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Sep 3, 2008
  Time: 1:22:15 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="java.sql.Timestamp; java.text.SimpleDateFormat; datasource.SmartsNotificationDatasource" contentType="text/html;charset=UTF-8" %>
<%

    def domainObject = RsEvent.get(id: params.id);
    if (domainObject != null) {
        def datasource = SmartsNotificationDatasource.get(name:domainObject.rsDatasource)
        if(datasource != null){
            def auditTrail =  datasource.getNotification(["ClassName":domainObject.className, "InstanceName":domainObject.instanceName, "EventName":domainObject.eventName], ["AuditTrail"]).AuditTrail;
            %>
                <div class="yui-navset yui-navset-top">
                    <ul class="yui-nav">
                        <li>
                            <a href="#" onclick="YAHOO.rapidjs.Components['eventDetails'].show('getEventDetails.gsp?className=${domainObject.className}&instanceName=${domainObject.instanceName}&eventName=${domainObject.eventName}');">
                                <em>Properties</em>
                            </a>
                        </li>
                        <li class="selected"><a href="#"><em>Audit Log</em></a></li>
                    </ul>
                    <div style="display:block">
                        <table cellspacing="2" cellpadding="2">
                            <thead>
                                <td>Time</td><td>Userid</td><td>Type</td><td>Description</td>
                            </thead>
                            <tbody>
                                <g:each var="audit" in="${auditTrail}">
                                    <%
                                        def userid = audit.element2;
                                        def time = audit.element1;
                                        def type = audit.element3;
                                        def description = audit.element4;
                                        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss")
                                        def date = format.format(new Timestamp(time));
                                    %>
                                    <tr>
                                        <td>${date}</td><td>${userid}</td><td>${type}</td><td>${description}</td>
                                    </tr>
                                </g:each>

                            </tbody>
                        </table>
                    </div>
                </div>
            <%
        }
        else{
            %>
                Could not find RsEvent's datasource;
            <%
        }
    }
    else {
        %>
        Event with id ${params.id} does not exist.
        <%
    }
%>