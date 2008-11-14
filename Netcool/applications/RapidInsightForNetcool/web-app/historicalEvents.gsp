<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Nov 11, 2008
  Time: 4:02:52 PM
  To change this template use File | Settings | File Templates.
--%>

<html>
<head>
     <meta name="layout" content="indexLayout"/>
</head>
<body>
<rui:form id="filterDialog" width="35em" createUrl="script/run/createQuery?queryType=historicalEvent" editUrl="script/run/editQuery?queryType=historicalEvent"
        saveUrl="searchQuery/save?format=xml&type=historicalEvent" updateUrl="searchQuery/update?format=xml&type=historicalEvent" onSuccess="refreshQueriesAction">
    <div>
        <div class="hd">Save query</div>
        <div class="bd">
        <form method="POST" action="javascript://nothing">
            <table>
            <tr><td width="50%"><label>Group Name:</label></td><td width="50%"><select name="group" style="width:175px"/></td></tr>
            <tr><td width="50%"><label>Query Name:</label></td><td width="50%"><input type="textbox" name="name" style="width:175px"/></td></tr>
            <tr><td width="50%"><label>Query:</label></td><td width="50%"><input type="textbox" name="query" style="width:175px"/></td></tr>
            <tr><td width="50%"><label>View Name:</label></td><td width="50%"><select name="viewName" style="width:175px"/></td></tr>
            </table>
            <input type="hidden" name="id">
        </form>

        </div>
    </div>
</rui:form>
<rui:form id="filterGroupDialog" width="30em" saveUrl="searchQueryGroup/save?format=xml&type=historicalEvent"
        updateUrl="searchQueryGroup/update?format=xml&type=historicalEvent" onSuccess="refreshQueriesAction">
    <div >
        <div class="hd">Save group</div>
        <div class="bd">
        <form method="POST" action="javascript://nothing">
            <table>
            <tr><td width="50%"><label>Group Name:</label></td><td width="50%"><input type="textbox" name="name" style="width:175px"/></td></tr>
            </table>
            <input type="hidden" name="sortProperty">
            <input type="hidden" name="id">
        </form>
        </div>
    </div>
</rui:form>
<rui:treeGrid id="filterTree" url="script/run/queryList?format=xml&type=historicalEvent" rootTag="Filters" pollingInterval="0"
        keyAttribute="id" contentPath="Filter" title="Saved Queries" expanded="true" onNodeClick="setQueryAction">
    <rui:tgColumns>
        <rui:tgColumn attributeName="name" colLabel="Name" width="248" sortBy="true"></rui:tgColumn>
    </rui:tgColumns>
    <rui:tgMenuItems>
        <rui:tgMenuItem id="deleteQuery" label="Delete" visible="params.data.isPublic != 'true' && params.data.nodeType == 'filter'" action="deleteQueryAction"></rui:tgMenuItem>
        <rui:tgMenuItem id="deleteQueryGroup" label="Delete" visible="params.data.isPublic != 'true' && params.data.name != 'My Queries' && params.data.nodeType == 'group'" action="deleteQueryGroupAction"></rui:tgMenuItem>
        <rui:tgMenuItem id="queryUpdate" label="Update" visible="params.data.nodeType == 'filter' && params.data.isPublic != 'true'" action="queryUpdateAction"></rui:tgMenuItem>
        <rui:tgMenuItem id="queryGroupUpdate" label="Update" visible="params.data.isPublic != 'true' && params.data.name != 'My Queries' && params.data.nodeType == 'group'" action="queryGroupUpdateAction"></rui:tgMenuItem>
        <rui:tgMenuItem id="copyQuery" label="Copy Query" visible="params.data.nodeType == 'filter'" action="copyQueryAction"></rui:tgMenuItem>
    </rui:tgMenuItems>
    <rui:tgRootImages>
        <rui:tgRootImage visible="params.data.nodeType == 'group'" expanded="images/rapidjs/component/tools/folder_open.gif" collapsed="images/rapidjs/component/tools/folder.gif"></rui:tgRootImage>
        <rui:tgRootImage visible="params.data.nodeType == 'filter'" expanded="images/rapidjs/component/tools/filter.png" collapsed="images/rapidjs/component/tools/filter.png"></rui:tgRootImage>
    </rui:tgRootImages>
</rui:treeGrid>

<rui:searchGrid id="searchGrid" url="search?format=xml&searchIn=NetcoolHistoricalEvent" rootTag="Objects" contentPath="Object" keyAttribute="id"
    title="Netcool Historical Events" queryParameter="query" totalCountAttribute="total" offsetAttribute="offset" sortOrderAttribute="sortOrder"
    pollingInterval="0" onSaveQueryClick="saveQueryAction" fieldsUrl="script/run/getViewFields?format=xml">
    <rui:sgColumns>
        <rui:sgColumn attributeName="node" colLabel="Node" width="100"></rui:sgColumn>
        <rui:sgColumn attributeName="owner" colLabel="Owner" width="50"></rui:sgColumn>
        <rui:sgColumn attributeName="ownergid" colLabel="Group" width="50"></rui:sgColumn>
        <rui:sgColumn attributeName="acknowledged" colLabel="Ack" width="50"></rui:sgColumn>
        <rui:sgColumn attributeName="manager" colLabel="Manager" width="100"></rui:sgColumn>
        <rui:sgColumn attributeName="summary" colLabel="Summary" width="250"></rui:sgColumn>
        <rui:sgColumn attributeName="tally" colLabel="Count" width="50"></rui:sgColumn>
        <rui:sgColumn attributeName="state" colLabel="Suppr/Escl" width="100"></rui:sgColumn>
        <rui:sgColumn attributeName="tasklist" colLabel="TaskList" width="100"></rui:sgColumn>
        <rui:sgColumn attributeName="lastNotifiedAt" colLabel="Last Occurrence" width="100"></rui:sgColumn>
        <rui:sgColumn attributeName="lastChangedAt" colLabel="State Change" width="100"></rui:sgColumn>
        <rui:sgColumn attributeName="alertgroup" colLabel="Alert Group" width="100"></rui:sgColumn>
        <rui:sgColumn attributeName="alertkey" colLabel="Alert Key" width="100"></rui:sgColumn>
    </rui:sgColumns>
    <rui:sgMenuItems>
        <rui:sgMenuItem id="eventDetails" label="Event Details" action="eventDetailsAction"></rui:sgMenuItem>
    </rui:sgMenuItems>
     <rui:sgImages>
        <rui:sgImage visible="params.data.severity == '5'" src="images/rapidjs/component/searchlist/red.png"></rui:sgImage>
        <rui:sgImage visible="params.data.severity == '4'" src="images/rapidjs/component/searchlist/orange.png"></rui:sgImage>
        <rui:sgImage visible="params.data.severity == '3'" src="images/rapidjs/component/searchlist/yellow.png"></rui:sgImage>
        <rui:sgImage visible="params.data.severity == '2'" src="images/rapidjs/component/searchlist/blue.png"></rui:sgImage>
        <rui:sgImage visible="params.data.severity == '1'" src="images/rapidjs/component/searchlist/purple.png"></rui:sgImage>
        <rui:sgImage visible="params.data.severity == '0'" src="images/rapidjs/component/searchlist/green.png"></rui:sgImage>
    </rui:sgImages>

</rui:searchGrid>
<rui:html id="historicalEventDetails" iframe="false"></rui:html>
<rui:popupWindow componentId="historicalEventDetails" width="500" height="400"></rui:popupWindow>


<rui:action id="eventDetailsAction"  type="function" function="show" componentId="historicalEventDetails">
    <rui:functionArg>'getHistoricalEventDetails.gsp?id=' + params.data.id</rui:functionArg>
    <rui:functionArg>'Details of ' + params.data.identifier</rui:functionArg>
</rui:action>
<rui:action id="saveQueryAction" type="function" componentId="filterDialog" function="show">
    <rui:functionArg>YAHOO.rapidjs.component.Form.CREATE_MODE</rui:functionArg>
    <rui:functionArg>{}</rui:functionArg>
    <rui:functionArg>{query:params.query, sortProperty:YAHOO.rapidjs.Components['searchGrid'].getSortAttribute(), sortProperty:YAHOO.rapidjs.Components['searchGrid'].getSortOrder()}</rui:functionArg>
</rui:action>

<rui:action id="deleteQueryAction" type="request" url="searchQuery/delete?format=xml" onSuccess="refreshQueriesAction">
    <rui:requestParam key="id" value="params.data.id"></rui:requestParam>
</rui:action>
<rui:action id="deleteQueryGroupAction" type="request" url="searchQueryGroup/delete?format=xml" onSuccess="refreshQueriesAction">
    <rui:requestParam key="id" value="params.data.id"></rui:requestParam>
</rui:action>
<rui:action id="queryUpdateAction" type="function" componentId="filterDialog" function="show">
    <rui:functionArg>YAHOO.rapidjs.component.Form.EDIT_MODE</rui:functionArg>
    <rui:functionArg>{queryId:params.data.id}</rui:functionArg>
</rui:action>
<rui:action id="queryGroupUpdateAction" type="function" componentId="filterGroupDialog" function="show">
    <rui:functionArg>YAHOO.rapidjs.component.Form.EDIT_MODE</rui:functionArg>
    <rui:functionArg>{}</rui:functionArg>
    <rui:functionArg>{name:params.data.name, id:params.data.id}</rui:functionArg>
</rui:action>
<rui:action id="copyQueryAction" type="function" componentId="filterDialog" function="show">
    <rui:functionArg>YAHOO.rapidjs.component.Form.CREATE_MODE</rui:functionArg>
    <rui:functionArg>{}</rui:functionArg>
    <rui:functionArg>{name:'', query:params.data.query, group:params.data.group, viewName:params.data.viewName}</rui:functionArg>
</rui:action>
<rui:action id="setQueryAction" type="function" componentId="searchGrid" function="setQueryWithView" condition="params.data.nodeType == 'filter'">
    <rui:functionArg>params.data.query</rui:functionArg>
    <rui:functionArg>params.data.viewName</rui:functionArg>
</rui:action>
<rui:action id="refreshQueriesAction" type="function" function="poll" componentId="filterTree"></rui:action>
<script type="text/javascript">
    var filterTree = YAHOO.rapidjs.Components['filterTree'];
    var searchGrid = YAHOO.rapidjs.Components['searchGrid'];
    searchGrid.renderCellFunction = function(key, value, data){
        if(key == "lastNotifiedAt" || key == "lastChangedAt"){
            var d = new Date();
            d.setTime(parseFloat(value))
            return d.format("d/m/Y H:i:s");
        }
        else if(key == "severity")
        {
                switch(value)
                {
                    case '5' : return "Critical";
                    case '4' : return "Major";
                    case '3' : return "Minor";
                    case '2' : return "Warning";
                    case '1' : return "Indeterminate";
                    case '0' : return "Clear";
                    default  : return "";
                }

        }
        else if(key == "state")
        {
                switch(value)
                {
                    case '6' : return "Maintenance";
                    case '5' : return "Hidden";
                    case '4' : return "Suppressed";
                    case '3' : return "Escalated-Level_3";
                    case '2' : return "Escalated-Level_2";
                    case '1' : return "Escalated";
                    case '0' : return "Normal";
                    default  : return "";
                }
        }
        return value;
    }
    filterTree.addToolbarButton({
        className:'r-filterTree-groupAdd',
        scope:this,
        tooltip: 'Add group',
        click:function() {
            YAHOO.rapidjs.Components['filterGroupDialog'].show(YAHOO.rapidjs.component.Form.CREATE_MODE);
        }
    });
    filterTree.addToolbarButton({
        className:'r-filterTree-queryAdd',
        scope:this,
        tooltip: 'Add query',
        click:function() {
            YAHOO.rapidjs.Components['filterDialog'].show(YAHOO.rapidjs.component.Form.CREATE_MODE);
        }
    });
    filterTree.poll();
    searchGrid.poll();
    var Dom = YAHOO.util.Dom, Event = YAHOO.util.Event;
    Event.onDOMReady(function() {
        var layout = new YAHOO.widget.Layout({
            units: [
                { position: 'top', body: 'top', resize: false, height:40},
                { position: 'center', body: searchGrid.container.id, resize: false, gutter: '1px' },
                { position: 'left', width: 250, resize: true, body: filterTree.container.id, scroll: false}
            ]
        });
        layout.on('render', function(){
        	var topUnit = layout.getUnitByPosition('top');
        	YAHOO.util.Dom.setStyle(topUnit.get('wrap'), 'background-color', '#BBD4F6')
            var header = topUnit.body;
            YAHOO.util.Dom.setStyle(header, 'border', 'none');
            var left = layout.getUnitByPosition('left').body;
            YAHOO.util.Dom.setStyle(left, 'top', '1px');
        });
        layout.render();
        var layoutLeft = layout.getUnitByPosition('left');
        layoutLeft.on('resize', function(){
            YAHOO.util.Dom.setStyle(layoutLeft.body, 'top', '1px');
        });

        searchGrid.resize(layout.getUnitByPosition('center').getSizes().body.w, layout.getUnitByPosition('center').getSizes().body.h);
        layout.on('resize', function() {
            searchGrid.resize(layout.getUnitByPosition('center').getSizes().body.w, layout.getUnitByPosition('center').getSizes().body.h);
        });
        filterTree.resize(layout.getUnitByPosition('center').getSizes().body.w, layout.getUnitByPosition('center').getSizes().body.h);
        layout.on('resize', function() {
            filterTree.resize(layout.getUnitByPosition('center').getSizes().body.w, layout.getUnitByPosition('center').getSizes().body.h);
        });
        window.layout = layout;

    })
</script>

</body>
</html>