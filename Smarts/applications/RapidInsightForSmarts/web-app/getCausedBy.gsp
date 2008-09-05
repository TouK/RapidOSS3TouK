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


%>
<div class="yui-navset yui-navset-top">
    <ul class="yui-nav">
        <li>
            <a onclick="YAHOO.rapidjs.Components['eventDetails'].show('getEventDetails.gsp?className=${domainObject.className}&instanceName=${domainObject.instanceName}&eventName=${domainObject.eventName}');">
                <em>Properties</em>
            </a>
        </li>
        <li><a onclick="YAHOO.rapidjs.Components['eventDetails'].show('getAuditLog.gsp?id=${domainObject?.id}');"><em>Audit Log</em></a></li>
        <%
            if(domainObject.causes.size() > 0){
                %>
                     <li><a onclick="YAHOO.rapidjs.Components['eventDetails'].show('getCauses.gsp?id=${domainObject?.id}');"><em>Impact</em></a></li>
                <%
            }
        %>
        <li class="selected"><a onclick="YAHOO.rapidjs.Components['eventDetails'].show('getCausedBy.gsp?id=${domainObject?.id}');"><em>Caused By</em></a></li>
    </ul>
    <div style="display:block">
        <div id="causedByTable"></div>
    </div>
</div>
<script>
    Event.onDOMReady(function() {
        var mydata = [];
         <g:each var="causeEvent" in="${domainObject.causedBy}">
              mydata[mydata.length] = {class:"${causeEvent.className}", name:"${causeEvent.instanceName}", event:"${causeEvent.eventName}"}
        </g:each>

        var myColumnDefs = [
            {key:"class", sortable:true, resizeable:true, width:200},
            {key:"name", sortable:true, resizeable:true, width:200},
            {key:"event", sortable:true, resizeable:true, width:200}
        ];

       var myDataSource = new YAHOO.util.DataSource(mydata);
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        myDataSource.responseSchema = {
            fields: ["class","name","event"]
        };

        new YAHOO.widget.DataTable("causedByTable",myColumnDefs, myDataSource, {});
    });

</script>
<%
    }
    else {
%>
Event with id ${params.id} does not exist.
<%
    }
%>