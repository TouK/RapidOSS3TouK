<%--
  Created by IntelliJ IDEA.
  User: sezgin
  Date: Dec 18, 2008
  Time: 4:58:31 PM
  To change this template use File | Settings | File Templates.
--%>

<%
    def objectName = params.name;
    def domainObject = RsTopologyObject.get(name: objectName)
%>
<g:if test="domainObject">
    <g:set var="events" value="${[]}"></g:set>
    <g:if test="${domainObject instanceof RsComputerSystem}">
        <%
            def links = domainObject.connectedVia.name;
            def query = "elementName:\"${domainObject.name}\"";
            if (links.size() > 0) {
                def queryArray = [];
                links.each {
                    queryArray.add("elementName:\"${it}\"")
                }
                query += " OR ${queryArray.join(' OR ')}";
            }
            events = RsEvent.searchEvery(query);
        %>
    </g:if>
    <g:else>
        <%
            events = RsEvent.searchEvery("elementName:\"${domainObject.name}\"");
        %>
    </g:else>
    <div style="display:block">
        <div id="eventsTable"></div>
    </div>
     <script>
     YAHOO.util.Event.onDOMReady(function() {
             var mydata = [];
              <g:each var="event" in="${events}">
                  <g:if test="${event instanceof RsRiEvent}">
                      var createdAt = new Date();
                      createdAt.setTime(${event.createdAt})
                      mydata[mydata.length] = {identifier:"${event.identifier}", createdAt:createdAt, name:"${event.name}"}
                  </g:if>
             </g:each>
             var formatDate = function(el, oRecord, oColumn, oData){
                el.innerHTML = oData.format("d M H:i:s");
             }
             var myColumnDefs = [
                 {key:"identifier", sortable:true, resizeable:true, width:200},
                 {key:"createdAt", formatter:formatDate, sortable:true, resizeable:true, width:200}
             ];

            var myDataSource = new YAHOO.util.DataSource(mydata);
             myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
             myDataSource.responseSchema = {
                 fields: ["identifier","createdAt","name"]
             };

             var dataTable = new YAHOO.widget.DataTable("eventsTable",myColumnDefs, myDataSource, {});
             dataTable.subscribe("rowDblclickEvent", function(args){
                 var record = dataTable.getRecord(args.target);
                 var name = record.getData('name')
                 var url = createURL('getEventDetails.gsp', {name:name});
                 var eventDetailsDialog = YAHOO.rapidjs.Components['eventDetails'];
                 if(eventDetailsDialog.popupWindow){
                    eventDetailsDialog.popupWindow.show()
                 }
                 eventDetailsDialog.show(url, 'Details of ' + name);
             });
         });

    </script>
</g:if>
<g:else>
    Object with name ${objectName} does not exist
</g:else>