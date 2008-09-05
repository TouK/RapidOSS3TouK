<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Sep 3, 2008
  Time: 1:22:15 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="datasource.SmartsNotificationDatasource" contentType="text/html;charset=UTF-8" %>
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
                            <a onclick="YAHOO.rapidjs.Components['eventDetails'].show('getEventDetails.gsp?className=${domainObject.className}&instanceName=${domainObject.instanceName}&eventName=${domainObject.eventName}');">
                                <em>Properties</em>
                            </a>
                        </li>
                        <li class="selected"><a onclick="YAHOO.rapidjs.Components['eventDetails'].show('getAuditLog.gsp?id=${domainObject?.id}');"><em>Audit Log</em></a></li>
                         <%
                            if(domainObject.causes.size() > 0){
                                %>
                                    <li><a onclick="YAHOO.rapidjs.Components['eventDetails'].show('getCauses.gsp?id=${domainObject?.id}');"><em>Impact</em></a></li>
                                <%
                            }
                            if(domainObject.causedBy.size() > 0){
                                %>
                                     <li><a onclick="YAHOO.rapidjs.Components['eventDetails'].show('getCausedBy.gsp?id=${domainObject?.id}');"><em>Caused By</em></a></li>
                                <%
                            }
                        %>
                    </ul>
                    <div style="display:block">
                        <div id="auditLogTable"></div>

                    </div>
                </div>
                <script>
                     Event.onDOMReady(function() {
                        var mydata =   [];
                        <g:each var="audit" in="${auditTrail}">
                              var auditDate = new Date();
                              auditDate.setTime(${audit.element1})
                              mydata[mydata.length] = {date:auditDate, userid:"${audit.element2}", type:"${audit.element3}", description:"${audit.element4}"}
                        </g:each>
                        var myColumnDefs = [
                            {key:"date", formatter:YAHOO.widget.DataTable.formatDate, sortable:true,resizeable:true, width:100},
                            {key:"userid", sortable:true, resizeable:true, width:100},
                            {key:"type", sortable:true, resizeable:true, width:100},
                            {key:"description", sortable:true, resizeable:true, width:200}
                        ];

                       var myDataSource = new YAHOO.util.DataSource(mydata);
                        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
                        myDataSource.responseSchema = {
                            fields: ["date","userid","type","description"]
                        };

                        new YAHOO.widget.DataTable("auditLogTable",myColumnDefs, myDataSource, {});
                    });
                </script>
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