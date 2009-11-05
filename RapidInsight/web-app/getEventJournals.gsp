<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Nov 26, 2008
  Time: 4:42:43 PM
--%>

<%
    def componentId = params.componentId
    def isHistorical = params.isHistorical;
    def eventId = params.id;
    def domainObject = isHistorical == "true" ? RsHistoricalEvent.get(id:eventId): RsEvent.get(id:eventId);
    if (domainObject != null) {
        eventId = isHistorical == "true" ? domainObject.activeId:eventId;
        def propertiesUrl = isHistorical == "false"? "createURL('getEventDetails.gsp', {name:'${domainObject.name}'})":"createURL('getHistoricalEventDetails.gsp', {id:'${domainObject.id}'})";
        %>
             <div class="yui-navset yui-navset-top" style="margin-top:5px">
                    <ul class="yui-nav">
                        <li>
                            <a onclick="YAHOO.rapidjs.Components['${componentId}'].show(${propertiesUrl});">
                                <em>Properties</em>
                            </a>
                        </li>
                        <li  class="selected">
                            <a onclick="YAHOO.rapidjs.Components['${componentId}'].show(createURL('getEventJournals.gsp', {id:'${domainObject.id}', isHistorical:'${isHistorical}'}));">
                                <em>Journal</em>
                            </a>
                        </li>
                    </ul>
                    <div style="display:block">
                        <div id="journalTable"></div>
                    </div>
                </div>
       <%
        def journals = RsEventJournal.searchEvery("eventId:${eventId}")
        journals = journals.sort{it.rsTime}
         %>
            <script>
                 YAHOO.util.Event.onDOMReady(function() {
                    var mydata =   [];
                    <g:each var="journal" in="${journals}">
                          var journalDate = new Date();
                          journalDate.setTime(${journal.rsTime.getTime()})
                          mydata[mydata.length] = {date:journalDate, eventname:"${journal.eventName}", details:"${journal.details}"}
                    </g:each>
                    var formatDate = function(el, oRecord, oColumn, oData){
                        el.innerHTML = oData.format("d M H:i:s");
                    }
                    var myColumnDefs = [
                        {key:"date", label:"Date", formatter:formatDate, sortable:true,resizeable:true, width:100},
                        {key:"eventname", label:"Event Name",sortable:true, resizeable:true, width:100},
                        {key:"details", label:"Details",sortable:true, resizeable:true, width:200}
                    ];

                   var myDataSource = new YAHOO.util.DataSource(mydata);
                    myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
                    myDataSource.responseSchema = {
                        fields: ["date","eventname","details"]
                    };

                    new YAHOO.widget.DataTable("journalTable",myColumnDefs, myDataSource, {});
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