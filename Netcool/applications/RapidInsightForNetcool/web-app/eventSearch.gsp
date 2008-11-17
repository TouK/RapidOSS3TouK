<html>
<head>
     <meta name="layout" content="indexLayout"/>
</head>
<body>
<rui:form id="filterDialog" width="35em" createUrl="script/run/createQuery?queryType=event" editUrl="script/run/editQuery?queryType=event"
        saveUrl="searchQuery/save?format=xml&type=event" updateUrl="searchQuery/update?format=xml&type=event" onSuccess="refreshQueriesAction">
    <div>
        <div class="hd">Save query</div>
        <div class="bd">
        <form method="POST" action="javascript://nothing">
            <table>
            <tr><td width="50%"><label>Group Name:</label></td><td width="50%"><select name="group" style="width:175px"/></td></tr>
            <tr><td width="50%"><label>Query Name:</label></td><td width="50%"><input type="textbox" name="name" style="width:175px"/></td></tr>
            <tr><td width="50%"><label>Query:</label></td><td width="50%"><input type="textbox" name="query" style="width:175px"/></td></tr>
            <tr><td width="50%"><label>Sort Property:</label></td><td width="50%"><select name="sortProperty" style="width:175px"/></td></tr>
            <tr><td width="50%"><label>Sort Order:</label></td><td width="50%">
                <select name="sortOrder" style="width:175px"><option value="asc">asc</option><option value="desc">desc</option></select>
            </td></tr>
            </table>
            <input type="hidden" name="id">
        </form>

        </div>
    </div>
</rui:form>
<rui:form id="filterGroupDialog" width="30em" saveUrl="searchQueryGroup/save?format=xml&type=event"
        updateUrl="searchQueryGroup/update?format=xml&type=event" onSuccess="refreshQueriesAction">
    <div >
        <div class="hd">Save group</div>
        <div class="bd">
        <form method="POST" action="javascript://nothing">
            <table>
            <tr><td width="50%"><label>Group Name:</label></td><td width="50%"><input type="textbox" name="name" style="width:175px"/></td></tr>
            </table>
            <input type="hidden" name="id">
        </form>
        </div>
    </div>
</rui:form>
<rui:treeGrid id="filterTree" url="script/run/queryList?format=xml&type=event" rootTag="Filters" pollingInterval="0"
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
<%
    def defaultFields = ['node', 'owner', 'ownergid', 'acknowledged','agent','manager', 'summary','tally','severity','state','tasklist','lastoccurrence','changedAt','alertgroup','alertkey'];
%>
<rui:searchList id="searchList" url="search?format=xml&searchIn=NetcoolEvent" rootTag="Objects" contentPath="Object" keyAttribute="id"
    lineSize="3" title="Netcool Events" queryParameter="query" totalCountAttribute="total" offsetAttribute="offset" sortOrderAttribute="sortOrder"
    pollingInterval="0" defaultFields="${defaultFields}" onSaveQueryClick="saveQueryAction">
    <rui:slMenuItems>
        <rui:slMenuItem id="eventDetails" label="Event Details" action="eventDetailsAction"></rui:slMenuItem>
        <rui:slMenuItem id="acknowledge" label="Acknowledge" action="acknowledgeAction" visible="params.data.acknowledged == 'No'"></rui:slMenuItem>
        <rui:slMenuItem id="deacknowledge" label="Deacknowledge" action="deacknowledgeAction" visible="params.data.acknowledged == 'Yes'"></rui:slMenuItem>
        <rui:slMenuItem id="takeOwnership" label="Take Ownership" action="takeOwnAction"></rui:slMenuItem>
        <rui:slMenuItem id="addTaskToList" label="Add Task To List" action="addTaskAction" visible="params.data.tasklist == '0'"></rui:slMenuItem>
        <rui:slMenuItem id="removeTaskFromList" label="Remove Task From List" action="removeTaskAction" visible="params.data.tasklist == '1'"></rui:slMenuItem>
        <rui:slMenuItem id="suppressEscalate" label="Suppress/Escalate">
               <rui:slSubmenuItems>
                    <rui:slMenuItem id="0" label="Normal" action="suppressEscalateAction" visible="params.data.state != '0'"></rui:slMenuItem>
                    <rui:slMenuItem id="1" label="Escalated" action="suppressEscalateAction" visible="params.data.state != '1'"></rui:slMenuItem>
                    <rui:slMenuItem id="2" label="Escalated-Level 2" action="suppressEscalateAction" visible="params.data.state != '2'"></rui:slMenuItem>
                    <rui:slMenuItem id="3" label="Escalated-Level 3" action="suppressEscalateAction" visible="params.data.state != '3'"></rui:slMenuItem>
                    <rui:slMenuItem id="4" label="Suppressed" action="suppressEscalateAction" visible="params.data.state != '4'"></rui:slMenuItem>
                    <rui:slMenuItem id="5" label="Hidden" action="suppressEscalateAction" visible="params.data.state != '5'"></rui:slMenuItem>
                    <rui:slMenuItem id="6" label="Maintenance" action="suppressEscalateAction" visible="params.data.state != '6'"></rui:slMenuItem>
               </rui:slSubmenuItems>
        </rui:slMenuItem>
        <rui:slMenuItem id="severity" label="Chanage Severity">
               <rui:slSubmenuItems>
                    <rui:slMenuItem id="5" label="Critical" action="severityAction" visible="params.data.severity != '5'"></rui:slMenuItem>
                    <rui:slMenuItem id="4" label="Major" action="severityAction" visible="params.data.severity != '4'"></rui:slMenuItem>
                    <rui:slMenuItem id="3" label="Minor" action="severityAction" visible="params.data.severity != '3'"></rui:slMenuItem>
                    <rui:slMenuItem id="2" label="Warning" action="severityAction" visible="params.data.severity != '2'"></rui:slMenuItem>
                    <rui:slMenuItem id="1" label="Indeterminate" action="severityAction" visible="params.data.severity != '1'"></rui:slMenuItem>
                    <rui:slMenuItem id="0" label="Clear" action="severityAction" visible="params.data.severity != '0'"></rui:slMenuItem>
               </rui:slSubmenuItems>
        </rui:slMenuItem>
    </rui:slMenuItems>
    <rui:slPropertyMenuItems>
        <rui:slMenuItem id="sortAsc" label="Sort asc" action="sortAscAction"></rui:slMenuItem>
        <rui:slMenuItem id="sortDesc" label="Sort desc" action="sortDescAction"></rui:slMenuItem>
        <rui:slMenuItem id="greaterThan" label="Greater than" action="greaterThanAction"
                visible="(params.key == 'severity' && params.value != '5') || (params.key == 'suppressescl' && params.value != '6') || YAHOO.lang.isNumber(parseInt(params.value))"></rui:slMenuItem>
        <rui:slMenuItem id="lessThan" label="Less than" action="lessThanAction"
                visible="(params.key == 'severity' && params.value != '0') || (params.key == 'suppressescl' && params.value != '0') || YAHOO.lang.isNumber(parseInt(params.value))"></rui:slMenuItem>
        <rui:slMenuItem id="greaterThanOrEqualTo" label="Greater than or equal to" action="greaterThanEqualAction" visible="YAHOO.lang.isNumber(parseInt(params.value))"></rui:slMenuItem>
        <rui:slMenuItem id="lessThanOrEqualTo" label="Less than or equal to" action="lessThanEqualAction" visible="YAHOO.lang.isNumber(parseInt(params.value))"></rui:slMenuItem>
        <rui:slMenuItem id="except" label="Except" action="exceptAction"></rui:slMenuItem>
    </rui:slPropertyMenuItems>
     <rui:slImages>
        <rui:slImage visible="params.data.severity == '5'" src="images/rapidjs/component/searchlist/red.png"></rui:slImage>
        <rui:slImage visible="params.data.severity == '4'" src="images/rapidjs/component/searchlist/orange.png"></rui:slImage>
        <rui:slImage visible="params.data.severity == '3'" src="images/rapidjs/component/searchlist/yellow.png"></rui:slImage>
        <rui:slImage visible="params.data.severity == '2'" src="images/rapidjs/component/searchlist/blue.png"></rui:slImage>
        <rui:slImage visible="params.data.severity == '1'" src="images/rapidjs/component/searchlist/purple.png"></rui:slImage>
        <rui:slImage visible="params.data.severity == '0'" src="images/rapidjs/component/searchlist/green.png"></rui:slImage>
    </rui:slImages>
</rui:searchList>
<rui:html id="eventDetails" iframe="false"></rui:html>
<rui:popupWindow componentId="eventDetails" width="500" height="400"></rui:popupWindow>


<rui:action id="eventDetailsAction"  type="function" function="show" componentId="eventDetails">
    <rui:functionArg>'getDetails.gsp?id=' + params.data.id</rui:functionArg>
    <rui:functionArg>'Details of ' + params.data.identifier</rui:functionArg>
</rui:action>
<rui:action id="acknowledgeAction" type="merge" url="script/run/acknowledge?format=xml" components="${['searchList']}">
    <rui:requestParam key="name" value="params.data.name"></rui:requestParam>
    <rui:requestParam key="acknowledged" value="'true'"></rui:requestParam>
</rui:action>
<rui:action id="deacknowledgeAction" type="merge" url="script/run/acknowledge?format=xml" components="${['searchList']}">
    <rui:requestParam key="name" value="params.data.name"></rui:requestParam>
    <rui:requestParam key="acknowledged" value="'false'"></rui:requestParam>
</rui:action>
<rui:action id="takeOwnAction" type="merge" url="script/run/takeOwnership?format=xml" components="${['searchList']}">
    <rui:requestParam key="name" value="params.data.name"></rui:requestParam>
</rui:action>
<rui:action id="addTaskAction" type="merge" url="script/run/taskList?format=xml" components="${['searchList']}">
    <rui:requestParam key="name" value="params.data.name"></rui:requestParam>
    <rui:requestParam key="taskList" value="'true'"></rui:requestParam>
</rui:action>
<rui:action id="removeTaskAction" type="merge" url="script/run/taskList?format=xml" components="${['searchList']}">
    <rui:requestParam key="name" value="params.data.name"></rui:requestParam>
    <rui:requestParam key="taskList" value="'false'"></rui:requestParam>
</rui:action>
<rui:action id="suppressEscalateAction" type="merge" url="script/run/suppress?format=xml" components="${['searchList']}">
    <rui:requestParam key="name" value="params.data.name"></rui:requestParam>
    <rui:requestParam key="suppressescl" value="params.menuId"></rui:requestParam>
</rui:action>
<rui:action id="severityAction" type="merge" url="script/run/severity?format=xml" components="${['searchList']}">
    <rui:requestParam key="name" value="params.data.name"></rui:requestParam>
    <rui:requestParam key="severity" value="params.menuId"></rui:requestParam>
</rui:action>
<rui:action id="greaterThanAction" type="function" componentId="searchList" function="appendToQuery">
    <rui:functionArg>params.key + ':{' + params.value + ' TO *}'</rui:functionArg>
</rui:action>
<rui:action id="lessThanAction" type="function" componentId="searchList" function="appendToQuery">
    <rui:functionArg>params.key + ':{* TO ' + params.value + '}'</rui:functionArg>
</rui:action>
<rui:action id="greaterThanEqualAction" type="function" componentId="searchList" function="appendToQuery">
    <rui:functionArg>params.key + ':[' + params.value + ' TO *]'</rui:functionArg>
</rui:action>
<rui:action id="lessThanEqualAction" type="function" componentId="searchList" function="appendToQuery">
    <rui:functionArg>params.key + ':[* TO ' + params.value + ']'</rui:functionArg>
</rui:action>
<rui:action id="sortAscAction" type="function" componentId="searchList" function="sort">
    <rui:functionArg>params.key</rui:functionArg>
    <rui:functionArg>'asc'</rui:functionArg>
</rui:action>
<rui:action id="sortDescAction" type="function" componentId="searchList" function="sort">
    <rui:functionArg>params.key</rui:functionArg>
    <rui:functionArg>'desc'</rui:functionArg>
</rui:action>
<rui:action id="exceptAction" type="function" componentId="searchList" function="appendExceptQuery">
    <rui:functionArg>params.key</rui:functionArg>
    <rui:functionArg>params.value</rui:functionArg>
</rui:action>
<rui:action id="saveQueryAction" type="function" componentId="filterDialog" function="show">
    <rui:functionArg>YAHOO.rapidjs.component.Form.CREATE_MODE</rui:functionArg>
    <rui:functionArg>{}</rui:functionArg>
    <rui:functionArg>{query:params.query, sortProperty:YAHOO.rapidjs.Components['searchList'].getSortAttribute(), sortProperty:YAHOO.rapidjs.Components['searchList'].getSortOrder()}</rui:functionArg>
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
<rui:action id="setQueryAction" type="function" componentId="searchList" function="setQuery" condition="params.data.nodeType == 'filter'">
    <rui:functionArg>params.data.query</rui:functionArg>
    <rui:functionArg>params.data.sortProperty</rui:functionArg>
    <rui:functionArg>params.data.sortOrder</rui:functionArg>
</rui:action>
<rui:action id="refreshQueriesAction" type="function" function="poll" componentId="filterTree"></rui:action>
<script type="text/javascript">
    var filterTree = YAHOO.rapidjs.Components['filterTree'];
    var searchList = YAHOO.rapidjs.Components['searchList'];
    searchList.renderCellFunction = function(key, value, data){
        if(key == "lastoccurence" || key == "changedAt"){
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
   searchList.poll();
    var Dom = YAHOO.util.Dom, Event = YAHOO.util.Event;
    Event.onDOMReady(function() {
        var layout = new YAHOO.widget.Layout({
            units: [
                { position: 'top', body: 'top', resize: false, height:40},
                { position: 'center', body: searchList.container.id, resize: false, gutter: '1px' },
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

        searchList.resize(layout.getUnitByPosition('center').getSizes().body.w, layout.getUnitByPosition('center').getSizes().body.h);
        layout.on('resize', function() {
            searchList.resize(layout.getUnitByPosition('center').getSizes().body.w, layout.getUnitByPosition('center').getSizes().body.h);
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