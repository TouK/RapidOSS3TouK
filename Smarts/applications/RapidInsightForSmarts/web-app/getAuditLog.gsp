<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Sep 3, 2008
  Time: 1:22:15 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="datasource.SmartsNotificationDatasource" contentType="text/html;charset=UTF-8" %>
<%
    def componentId = params.componentId
    def domainObject = SmartsNotification.get(id: params.id);
    if (domainObject != null) {
        %>
             <div class="yui-navset yui-navset-top" style="margin-top:5px">
                    <ul class="yui-nav">
                        <li>
                            <a onclick="YAHOO.rapidjs.Components['${componentId}'].show('smartsEventDetails.gsp?name=' + encodeURIComponent('${domainObject.name}'));">
                                <em>Properties</em>
                            </a>
                        </li>
                        <li class="selected"><a onclick="YAHOO.rapidjs.Components['${componentId}'].show('getAuditLog.gsp?id=' + encodeURIComponent('${domainObject?.id}'));"><em>Audit Log</em></a></li>
                         <%
                            if(domainObject.causes.size() > 0){
                                %>
                                    <li><a onclick="YAHOO.rapidjs.Components['${componentId}'].show('getCauses.gsp?id=' + encodeURIComponent('${domainObject?.id}'));"><em>Impact</em></a></li>
                                <%
                            }
                            if(domainObject.causedBy.size() > 0){
                                %>
                                     <li><a onclick="YAHOO.rapidjs.Components['${componentId}'].show('getCausedBy.gsp?id=' + encodeURIComponent('${domainObject?.id}'));"><em>Caused By</em></a></li>
                                <%
                            }
                        %>
                    </ul>
                    <div style="display:block">
                        <div id="auditLogTable"></div>

                    </div>
                </div>
       <%
        def datasource = SmartsNotificationDatasource.get(name:domainObject.rsDatasource)
        if(datasource != null){
            def auditTrail
            try{
                auditTrail =  datasource.getNotification(["ClassName":domainObject.className, "InstanceName":domainObject.instanceName, "EventName":domainObject.eventName], ["AuditTrail"]).AuditTrail;
            }
            catch(e){

            }
            %>

                <%
                   if(auditTrail != null){
                      %>
                        <script>
                             YAHOO.util.Event.onDOMReady(function() {
                                var mydata =   [];
                                <g:each var="audit" in="${auditTrail}">
                                      var auditDate = new Date();
                                      auditDate.setTime(${audit.element1}*1000)
                                      mydata[mydata.length] = {date:auditDate, userid:"${audit.element2}", type:"${audit.element3}", description:"${audit.element4}"}
                                </g:each>
                                var formatDate = function(el, oRecord, oColumn, oData){
                                    el.innerHTML = oData.format("d M H:i:s");
                                }
                                var myColumnDefs = [
                                    {key:"date", formatter:formatDate, sortable:true,resizeable:true, width:100},
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
                        <script>
                           YAHOO.util.Event.onDOMReady(function() {
                              document.getElementById('auditLogTable').innerHTML = 'Cannot connect to smarts datasource ${datasource}'; 
                           })

                        </script>
                    <%
                   }
        }
        else{
            %>
                Could not find SmartsNotification's datasource.
            <%
        }
    }
    else {
        %>
        Event with id ${params.id} does not exist.
        <%
    }
%>