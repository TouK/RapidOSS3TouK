<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Oct 24, 2008
  Time: 10:25:41 AM
--%>

<html>
<head>
    <meta name="layout" content="smartsLayout"/>
</head>
<body>
<rui:treeGrid id="filterTree" url="script/run/queryList?format=xml&type=notification" rootTag="Filters" pollingInterval="0"
        keyAttribute="id" contentPath="Filter" title="SavedQueries" expanded="true" onNodeClick="setQueryAction">
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

<rui:form id="filterDialog" width="35em" createUrl="script/run/createQuery?queryType=notification" editUrl="script/run/editQuery?queryType=notification"
        saveUrl="searchQuery/save?format=xml&type=notification" updateUrl="searchQuery/update?format=xml&type=notification" onSuccess="refreshQueriesAction">
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
                <input type="hidden" name="sortProperty">
            </form>

        </div>
    </div>
</rui:form>
<rui:form id="filterGroupDialog" width="30em" saveUrl="searchQueryGroup/save?format=xml&type=notification"
        updateUrl="searchQueryGroup/update?format=xml&type=notification" onSuccess="refreshQueriesAction">
    <div>
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

<rui:searchGrid id="eventsGrid" url="search?format=xml&searchIn=RsEvent" queryParameter="query" rootTag="Objects" contentPath="Object"
        keyAttribute="id" totalCountAttribute="total" offsetAttribute="offset" sortOrderAttribute="sortOrder" title="Events List"
        pollingInterval="0" fieldsUrl="script/run/getViewFields?format=xml" onSaveQueryClick="saveQueryAction">
    <rui:sgMenuItems>
        <rui:sgMenuItem id="browseInstance" label="Browse" visible="!params.data.elementName || params.data.elementName == ''" action="browseInstanceAction"></rui:sgMenuItem>
        <rui:sgMenuItem id="browseElement" label="Browse" visible="params.data.elementName && params.data.elementName != ''" action="browseElementAction"></rui:sgMenuItem>
        <rui:sgMenuItem id="eventDetails" label="Event Details" action="eventDetailsAction"></rui:sgMenuItem>
        <rui:sgMenuItem id="acknowledge" label="Acknowledge" visible="params.data.acknowledged != 'true'" action="acknowledgeAction"></rui:sgMenuItem>
        <rui:sgMenuItem id="unacknowledge" label="Unacknowledge" visible="params.data.acknowledged == 'true'" action="unacknowledgeAction"></rui:sgMenuItem>
        <rui:sgMenuItem id="takeOwnership" label="Take Ownership" action="takeOwnAction"></rui:sgMenuItem>
        <rui:sgMenuItem id="releaseOwnership" label="Release Ownership" action="releaseOwnAction"></rui:sgMenuItem>
    </rui:sgMenuItems>
    <rui:sgImages>
        <rui:sgImage visible="params.data.severity == '1'" src="images/rapidjs/component/searchlist/red.png"></rui:sgImage>
        <rui:sgImage visible="params.data.severity == '2'" src="images/rapidjs/component/searchlist/orange.png"></rui:sgImage>
        <rui:sgImage visible="params.data.severity == '3'" src="images/rapidjs/component/searchlist/yellow.png"></rui:sgImage>
        <rui:sgImage visible="params.data.severity == '4'" src="images/rapidjs/component/searchlist/blue.png"></rui:sgImage>
        <rui:sgImage visible="params.data.severity == '5'" src="images/rapidjs/component/searchlist/green.png"></rui:sgImage>
    </rui:sgImages>
    <rui:sgColumns>
        <rui:sgColumn attributeName="acknowledged" colLabel="Ack" width="50"></rui:sgColumn>
        <rui:sgColumn attributeName="owner" colLabel="Owner" width="100"></rui:sgColumn>
        <rui:sgColumn attributeName="elementName" colLabel="Element Name" width="100"></rui:sgColumn>
        <rui:sgColumn attributeName="classDisplayName" colLabel="Class" width="100"></rui:sgColumn>
        <rui:sgColumn attributeName="instanceDisplayName" colLabel="Name" width="100"></rui:sgColumn>
        <rui:sgColumn attributeName="eventName" colLabel="Event" width="100"></rui:sgColumn>
        <rui:sgColumn attributeName="sourceDomainName" colLabel="Source" width="100"></rui:sgColumn>
        <rui:sgColumn attributeName="occurrenceCount" colLabel="Count" width="50"></rui:sgColumn>
        <rui:sgColumn attributeName="lastNotifiedAt" colLabel="Last Notify" width="120"></rui:sgColumn>
        <rui:sgColumn attributeName="lastChangedAt" colLabel="Last Change" width="120"></rui:sgColumn>
    </rui:sgColumns>
</rui:searchGrid>
<rui:html id="objectDetailsmenuHtml" iframe="false"></rui:html>
<rui:popupWindow componentId="objectDetailsmenuHtml" width="850" height="700" x="85" y="50"></rui:popupWindow>
<rui:html id="eventDetails" iframe="false"></rui:html>
<rui:popupWindow componentId="eventDetails" width="850" height="500"></rui:popupWindow>
<rui:action id="eventDetailsAction" type="function" function="show" componentId="eventDetails">
    <rui:functionArg>'getEventDetails.gsp?name=' + params.data.name</rui:functionArg>
    <rui:functionArg>'Details of ' + params.data.name</rui:functionArg>
</rui:action>
<rui:action id="browseInstanceAction" type="function" function="show" componentId="objectDetailsmenuHtml">
    <rui:functionArg>'getObjectDetails.gsp?name=' + params.data.name</rui:functionArg>
    <rui:functionArg>'Details of ' + params.data.className + ' ' + params.data.instanceName</rui:functionArg>
</rui:action>
<rui:action id="browseElementAction" type="function" function="show" componentId="objectDetailsmenuHtml">
    <rui:functionArg>'getObjectDetails.gsp?name=' + params.data.name</rui:functionArg>
    <rui:functionArg>'Details of ' + params.data.elementClassName + ' ' + params.data.elementName</rui:functionArg>
</rui:action>
<rui:action id="acknowledgeAction" type="merge" url="script/run/acknowledge" components="${['eventsGrid']}">
    <rui:requestParam key="name" value="params.data.name"></rui:requestParam>
    <rui:requestParam key="acknowledged" value="true"></rui:requestParam>
</rui:action>
<rui:action id="unacknowledgeAction" type="merge" url="script/run/acknowledge" components="${['eventsGrid']}">
    <rui:requestParam key="name" value="params.data.name"></rui:requestParam>
    <rui:requestParam key="acknowledged" value="false"></rui:requestParam>
</rui:action>
<rui:action id="takeOwnAction" type="merge" url="script/run/setOwnership" components="${['eventsGrid']}">
    <rui:requestParam key="name" value="params.data.name"></rui:requestParam>
    <rui:requestParam key="act" value="true"></rui:requestParam>
</rui:action>
<rui:action id="releaseOwnAction" type="merge" url="script/run/setOwnership" components="${['eventsGrid']}">
    <rui:requestParam key="name" value="params.data.name"></rui:requestParam>
    <rui:requestParam key="act" value="false"></rui:requestParam>
</rui:action>
<rui:action id="deleteQueryAction" type="request" url="searchQuery/delete?format=xml" onSuccess="refreshQueriesAction">
    <rui:requestParam key="id" value="params.data.id"></rui:requestParam>
</rui:action>
<rui:action id="deleteQueryGroupAction" type="request" url="searchQueryGroup/delete?format=xml" onSuccess="refreshQueriesAction">
    <rui:requestParam key="id" value="params.data.id"></rui:requestParam>
</rui:action>
<rui:action id="saveQueryAction" type="function" componentId="filterDialog" function="show">
   <rui:functionArg>YAHOO.rapidjs.component.Form.CREATE_MODE</rui:functionArg>
   <rui:functionArg>{}</rui:functionArg>
   <rui:functionArg>{query:params.query, sortProperty:YAHOO.rapidjs.Components['eventsGrid'].getSortAttribute(), sortProperty:YAHOO.rapidjs.Components['eventsGrid'].getSortOrder()}</rui:functionArg>
</rui:action>
<rui:action id="setQueryAction" type="function" componentId="eventsGrid" function="setQueryWithView" condition="params.data.nodeType == 'filter'">
    <rui:functionArg>params.data.query</rui:functionArg>
    <rui:functionArg>params.data.viewName</rui:functionArg>
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
<rui:action id="refreshQueriesAction" type="function" function="poll" componentId="filterTree"></rui:action>

<script type="text/javascript">
    var eventsGrid = YAHOO.rapidjs.Components['eventsGrid'];
    var filterTree = YAHOO.rapidjs.Components['filterTree'];
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
    eventsGrid.renderCellFunction = function(key, value, data, el){
        if(key == "lastNotifiedAt" || key == "lastChangedAt"){
            if(value == "0" || value == "")
            {
                return "never"
            }
            else
            {
                try
                {
                    var d = new Date();
                    d.setTime(parseFloat(value))
                    return d.format("d M H:i:s");
                }
                catch(e)
                {}
            }
        }
        return value;
     }
    eventsGrid.poll();
    YAHOO.util.Event.onDOMReady(function() {
        var layout = new YAHOO.widget.Layout({
            units: [
                { position: 'top', body: 'top', resize: false, height:45},
                { position: 'center', body: eventsGrid.container.id, resize: false, gutter: '1px' },
                { position: 'left', width: 250, resize: true, body: filterTree.container.id, scroll: false}
            ]
        });

        layout.render();
        var leftUnit = layout.getUnitByPosition('left');
        var centerUnit = layout.getUnitByPosition('center');
        eventsGrid.resize(centerUnit.getSizes().body.w, centerUnit.getSizes().body.h);
        layout.on('resize', function() {
            eventsGrid.resize(centerUnit.getSizes().body.w, centerUnit.getSizes().body.h);
        });
        filterTree.resize(centerUnit.getSizes().body.w, centerUnit.getSizes().body.h);
        layout.on('resize', function() {
            filterTree.resize(centerUnit.getSizes().body.w, centerUnit.getSizes().body.h);
        });
        window.layout = layout;
    })


</script>
</body>
</html>