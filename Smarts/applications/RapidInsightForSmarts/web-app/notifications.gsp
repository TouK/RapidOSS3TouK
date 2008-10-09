<html>
<head>
    <meta name="layout" content="indexLayout" />
</head>
<body>

<rui:treeGrid id="filterTree" url="script/run/queryList?format=xml&type=notification" rootTag="Filters" keyAttribute="id"
     contentPath="Filter" title="Saved Queries" expanded="true">
    <rui:tgColumns>
        <rui:tgColumn attributeName="name" colLabel="Name" width="248" sortBy="true"></rui:tgColumn>
    </rui:tgColumns>
    <rui:tgMenuItems>
        <rui:tgMenuItem id="delete" label="Delete" visible="data.isPublic != 'true' && !(data.name == 'Default' && data.nodeType == 'group')"></rui:tgMenuItem>
        <rui:tgMenuItem id="update" label="Update" visible="data.isPublic != 'true' && !(data.name == 'Default' && data.nodeType == 'group')"></rui:tgMenuItem>
        <rui:tgMenuItem id="copyQuery" label="Copy Query" visible="data.nodeType == 'filter'"></rui:tgMenuItem>
    </rui:tgMenuItems>
    <rui:tgRootImages>
        <rui:tgRootImage visible="data.nodeType == 'group'" expanded='images/rapidjs/component/tools/folder_open.gif' collapsed='images/rapidjs/component/tools/folder.gif'></rui:tgRootImage>
        <rui:tgRootImage visible="data.nodeType == 'filter'" expanded='images/rapidjs/component/tools/filter.png' collapsed='images/rapidjs/component/tools/filter.png'></rui:tgRootImage>
    </rui:tgRootImages>
</rui:treeGrid>

<rui:form id="filterDialog" width="35em" createUrl="script/run/createQuery?queryType=notification"
        editUrl="script/run/editQuery?queryType=notification" saveUrl="searchQuery/save?format=xml&type=notification"
        updateUrl="searchQuery/update?format=xml&type=notification">
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
        updateUrl="searchQueryGroup/update?format=xml&type=notification">
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
<rui:html id="eventDetails" width="850" height="500" iframe="false"></rui:html>
<rui:html id="objectDetails" width="850" height="700" iframe="false"></rui:html>

<div id="right">
	<div id="searchDiv"></div>
</div>
<script type="text/javascript">
	var tree = YAHOO.rapidjs.Components['filterTree'];
    var dialog = YAHOO.rapidjs.Components['filterDialog'];
    dialog.successful = function(){tree.poll()};
    var groupDialog = YAHOO.rapidjs.Components['filterGroupDialog'];
    groupDialog.successful = function(){tree.poll()};
    
    var eventDetailsDialog = YAHOO.rapidjs.Components['eventDetails'];
    var objectDetailsDialog = YAHOO.rapidjs.Components['objectDetails'];
    eventDetailsDialog.hide();
    objectDetailsDialog.hide();
    
    var actionConfig = {url:'searchQuery/delete?format=xml'}
    var deleteQueryAction = new YAHOO.rapidjs.component.action.RequestAction(actionConfig);

    var actionGroupConfig = {url:'searchQueryGroup/delete?format=xml'}
    var deleteQueryGroupAction = new YAHOO.rapidjs.component.action.RequestAction(actionGroupConfig);
    var smartsEventFields = ['className', 'instanceName', 'eventName', 'sourceDomainName','acknowledged','owner', 'lastChangedAt','elementClassName', 'elementName','isRoot', 'severity'];
    var searchConfig = {
        id:'searchGrid',
        url:'search?format=xml&searchIn=RsEvent',
        searchQueryParamName:'query',
        rootTag:'Objects',
        contentPath:'Object',
        keyAttribute:'id',
        totalCountAttribute:'total',
        offsetAttribute:'offset',
        sortOrderAttribute:'sortOrder',
        title:'Events',
        fieldsUrl: 'script/run/getViewFields?format=xml',
        columns:[{attributeName:'className', colLabel:'Class Name', width:100},
            {attributeName:'instanceName', colLabel:'Instance Name', width:100},
            {attributeName:'eventName', colLabel:'Event Name', width:100},
            {attributeName:'sourceDomainName', colLabel:'Source Domain Name', width:100},
            {attributeName:'acknowledged', colLabel:'Acknowledged', width:50},
            {attributeName:'owner', colLabel:'Owner', width:100},
            {attributeName:'lastChangedAt', colLabel:'Last Changed', width:80},
            {attributeName:'elementClassName', colLabel:'Element Class Name', width:100},
            {attributeName:'elementName', colLabel:'Element Name', width:100},
            {attributeName:'isRoot', colLabel:'Is Root', width:50},
            {attributeName:'severity', colLabel:'Severity', width:50, sortBy:true}],
        menuItems:{
            item1 : { id : 'acknowledge', label : 'Acknowledge', visible:'data.acknowledged != "true"' },
            item2 : { id : 'unacknowledge', label : 'Unacknowledge', visible:'data.acknowledged == "true"' },
            item3 : { id : 'takeOwnership', label : 'Take Ownership'},
            item4 : { id : 'releaseOwnership', label : 'Release Ownership'},
            item4 : { id : 'browse', label : 'Browse'},
            item5 : { id : 'eventDetails', label : 'Event Details' }
        },
        images:[
            {exp:'data["severity"] == 1', src:'images/rapidjs/component/searchlist/red.png'},
            {exp:'data["severity"] == 2', src:'images/rapidjs/component/searchlist/orange.png'},
            {exp:'data["severity"] == 3', src:'images/rapidjs/component/searchlist/yellow.png'},
            {exp:'data["severity"] == 4', src:'images/rapidjs/component/searchlist/blue.png'},
            {exp:'data["severity"] == 5', src:'images/rapidjs/component/searchlist/green.png'}
        ],
        renderCellFunction : function(key, value, data){
        	if(key == "lastChangedAt" || key == "lastNotifiedAt" || key == "firstNotifiedAt" || key == "lastClearedAt"){
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
                    {
                    }
                }
            }
            return value;
        }
    }

    var searchGrid = new YAHOO.rapidjs.component.search.SearchGrid(document.getElementById("searchDiv"), searchConfig);
    searchGrid.events["saveQueryClicked"].subscribe(function(query) {
        dialog.show(dialog.CREATE_MODE, null, {query:query, sortProperty:searchGrid.getSortAttribute(), sortOrder: searchGrid.getSortOrder()});
    });
    var acknowledgeConfig = { url: 'script/run/acknowledge?format=xml' };
	var acknowledgeAction = new YAHOO.rapidjs.component.action.MergeAction(acknowledgeConfig);

	var setOwnershipConfig = { url: 'script/run/setOwnership?format=xml' };
	var setOwnershipAction = new YAHOO.rapidjs.component.action.MergeAction(setOwnershipConfig);

    searchGrid.events["rowHeaderMenuClick"].subscribe(function(xmlData, id, parentId) {
    	var notificationName = xmlData.getAttribute("name");

        if( id == "eventDetails"){

            var url = "getEventDetails.gsp?name="+ encodeURIComponent(notificationName);
            eventDetailsDialog.show(url, "Details of " + notificationName);
        }
        else if(id == "browse"){
                var key = 'elementName'
                var value = xmlData.getAttribute(key);
                if(!value || value == ''){
                    key = 'instanceName'
                    value = xmlData.getAttribute(key)
                }
                var url = "getObjectDetails.gsp?name="+ encodeURIComponent(value);
                var title = key == "instanceName"? "Details of " + xmlData.getAttribute("className") + " " + value : "Details of " + xmlData.getAttribute("elementClassName") + " " + value
                objectDetailsDialog.show(url, title);
            }
        else if( id == 'acknowledge' )
            acknowledgeAction.execute({name:notificationName, acknowledged:true}, [searchGrid]);

        else if( id == 'unacknowledge' )
        	acknowledgeAction.execute({name:notificationName, acknowledged:false}, [searchGrid]);
        else if(id == 'takeOwnership')
        	setOwnershipAction.execute({name:notificationName, act:true}, [searchGrid]);
        else if(id == 'releaseOwnership')
        	setOwnershipAction.execute({name:notificationName, act:false}, [searchGrid]);

    }, this, true);
    searchGrid.events["rowDoubleClicked"].subscribe(function(xmlData, event){
    	if(YAHOO.util.Event.getTarget(event).className != 'rcmdb-search-cell-key')
    	{
    		var notificationName = xmlData.getAttribute("name");
            var url = "getEventDetails.gsp?name="+ encodeURIComponent(notificationName);
            eventDetailsDialog.show(url, "Details of " + notificationName);
        }

    }, true, true);
    tree.addToolbarButton({
        className:'r-filterTree-groupAdd',
        scope:this,
        tooltip: 'Add group',
        click:function() {
            groupDialog.show(groupDialog.CREATE_MODE);
        }
    });
    tree.addToolbarButton({
        className:'r-filterTree-queryAdd',
        scope:this,
        tooltip: 'Add query',
        click:function() {
            dialog.show(dialog.CREATE_MODE);
        }
    });
    tree.poll();
    deleteQueryAction.events.success.subscribe(tree.poll, tree, true);

    deleteQueryGroupAction.events.success.subscribe(tree.poll, tree, true);

    tree.events["treeNodeClick"].subscribe(function(data) {
        if (data.getAttribute("nodeType") == "filter")
        {
            searchGrid.setQueryWithView(data.getAttribute("query"), data.getAttribute('viewName'));
        }
    }, this, true);

    tree.events["rowMenuClick"].subscribe(function(data, id, parentId) {
    	if (id == "delete")
        {
            if (data.getAttribute("nodeType") == "filter")
                deleteQueryAction.execute({id:data.getAttribute("id")});
            else if (data.getAttribute("nodeType") == "group")
                deleteQueryGroupAction.execute({id:data.getAttribute("id")});
        }
        else if(id == "update"){
            if (data.getAttribute("nodeType") == "filter")
                dialog.show(dialog.EDIT_MODE, {queryId:data.getAttribute("id")})
            else if(data.getAttribute("nodeType") == "group"){
                groupDialog.show(groupDialog.EDIT_MODE)
                groupDialog.dialog.form.name.value = data.getAttribute("name");
                groupDialog.dialog.form.id.value = data.getAttribute("id")
            }
       }
       else if(id == "copyQuery"){
        		dialog.show(dialog.CREATE_MODE,null,{name:'', group:data.parentNode().getAttribute('name'), viewName:data.getAttribute('viewName'),
        										query:data.getAttribute('query')});

            }
    }, this, true);


    var Dom = YAHOO.util.Dom, Event = YAHOO.util.Event;

    Event.onDOMReady(function() {
        var layout = new YAHOO.widget.Layout({
            units: [
                { position: 'top', body: 'top', resize: false, height:45},
                { position: 'center', body: searchGrid.container.id, resize: false, gutter: '1px' },
                { position: 'left', width: 250, resize: true, body: tree.container.id, scroll: false}
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

        searchGrid.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
        layout.on('resize', function() {
            searchGrid.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
        });
        tree.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
        layout.on('resize', function() {
            tree.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
        });
        window.layout = layout;

    })
</script>

</body>
</html>
