<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Sep 3, 2008
  Time: 1:22:15 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="java.sql.Timestamp; java.text.SimpleDateFormat; datasource.SmartsNotificationDatasource" contentType="text/html;charset=UTF-8" %>
<%
    def componentId = params.componentId
    def domainObject = RsSmartsNotification.get(id: params.id);
    if (domainObject != null) {


%>
<style>
    .yui-skin-sam .yui-dt-liner { cursor:pointer; }  

</style>
<div class="yui-navset yui-navset-top">
    <ul class="yui-nav">
        <li>
            <a onclick="YAHOO.rapidjs.Components['${componentId}'].show('getEventDetails.gsp?name=' + encodeURIComponent('${domainObject.name}'));">
                <em>Properties</em>
            </a>
        </li>
        <li><a onclick="YAHOO.rapidjs.Components['${componentId}'].show('getAuditLog.gsp?id=' + encodeURIComponent('${domainObject?.id}'));"><em>Audit Log</em></a></li>
        <li class="selected"><a onclick="YAHOO.rapidjs.Components['${componentId}'].show('getCauses.gsp?id=' + encodeURIComponent('${domainObject?.id}'));"><em>Impact</em></a></li>
        <%
            if(domainObject.causedBy.size() > 0){
                %>
                     <li><a onclick="YAHOO.rapidjs.Components['${componentId}'].show('getCausedBy.gsp?id=' + encodeURIComponent('${domainObject?.id}'));"><em>Caused By</em></a></li>
                <%
            }
        %>
    </ul>
    <div style="display:block">
        <div id="causesTable"></div>
    </div>
</div>
<script>
    Event.onDOMReady(function() {
        var mydata = [];
         <g:each var="causeEvent" in="${domainObject.causes}">
              mydata[mydata.length] = {className:"${causeEvent.className}", name:"${causeEvent.instanceName}", event:"${causeEvent.eventName}"}
        </g:each>
        
        var myColumnDefs = [
            {key:"className", sortable:true, resizeable:true, width:200},
            {key:"name", sortable:true, resizeable:true, width:200},
            {key:"event", sortable:true, resizeable:true, width:200}
        ];

       var myDataSource = new YAHOO.util.DataSource(mydata);
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        myDataSource.responseSchema = {
            fields: ["className","name","event"]
        };

        var dataTable = new YAHOO.widget.DataTable("causesTable",myColumnDefs, myDataSource, {});
        dataTable.subscribe("rowDblclickEvent", function(args){
            var record = dataTable.getRecord(args.target);
            var className = record.getData('className')
            var instanceName = record.getData('name');
            var eventName = record.getData('event');
            var url = 'getEventDetails.gsp?className=' + encodeURIComponent(className) + '&instanceName=' + encodeURIComponent(instanceName) + '&eventName=' + encodeURIComponent(eventName);
            YAHOO.rapidjs.Components['${componentId}'].show(url, 'Details of ' + className + ' ' + instanceName + ' ' + eventName);
        });
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