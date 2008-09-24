<html>
<head>
    <meta name="layout" content="indexLayout" />
</head>
<body>
<div id="filterDialog">
    <div class="hd">Save query</div>
    <div class="bd">
    <form method="POST" action="javascript://nothing">
        <table width="100%">
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
<div id="filterGroup">
    <div class="hd">Save group</div>
    <div class="bd">
    <form method="POST" action="javascript://nothing">
        <table width="100%">
        <tr><td width="50%"><label>Group Name:</label></td><td width="50%"><input type="textbox" name="name" style="width:175px"/></td></tr>
        </table>
        <input type="hidden" name="id">
    </form>

    </div>
</div>
<div id="left">
    <div id="treeDiv1"></div>
</div>
<div id="right">
    <div id="searchDiv"></div>
</div>

<script type="text/javascript">
	function searchListPropertyMenuConditionFunctionGreaterThan(key, value, data)
    {
    	return (key == "severity" && value != '1') || (key != "severity" && propertyMenuIsNumberCondition(key, value, data));
    }
    function searchListPropertyMenuConditionFunctionLessThan(key, value, data)
	{
    	return (key == "severity" && value != '5') || (key != "severity" && propertyMenuIsNumberCondition(key, value, data));
    }

    function propertyMenuIsNumberCondition(key, value, data)
    {
           return YAHOO.lang.isNumber(parseInt(value));
    }

	function searchListHeaderMenuConditionFunctionAcknowledge(data)
    {
        return data.getAttribute("acknowledged") != "true";
    }

    function searchListHeaderMenuConditionFunctionUnacknowledge(data)
    {
        return data.getAttribute("acknowledged") == "true";
    }

    YAHOO.rapidjs.ErrorManager.serverDownEvent.subscribe(function(){
        YAHOO.util.Dom.setStyle(document.getElementById('serverDownEl'), 'display', '');
    }, this, true);
    YAHOO.rapidjs.ErrorManager.serverUpEvent.subscribe(function(){
        YAHOO.util.Dom.setStyle(document.getElementById('serverDownEl'), 'display', 'none');
    }, this, true);

    var eventDetailsDialog = new YAHOO.rapidjs.component.Html({id:'eventDetails', width:850, height:500, iframe:false});
    var objectDetailsDialog = new YAHOO.rapidjs.component.Html({id:'objectDetails', width:850, height:700, iframe:false});
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
            item1 : { id : 'acknowledge', label : 'Acknowledge', condition: searchListHeaderMenuConditionFunctionAcknowledge },
            item2 : { id : 'unacknowledge', label : 'Unacknowledge', condition: searchListHeaderMenuConditionFunctionUnacknowledge },
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
        	if(key == "lastChangedAt" || key == "lastNotifiedAt" || key == "lastCreatedAt" || key == "firstNotifiedAt" || key == "lastClearedAt"){
                if(value == "0" || value == "")
                {
                    return "never"
                }
                else
                {
                    try
                    {
                        var d = new Date();
                        d.setTime(parseFloat(value)*1000)
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

    function treeNodesUpdateDeleteConditionFunction(data)
    {
    	return data.getAttribute("isPublic") != "true" && !(data.getAttribute("nodeType") == "group" && data.getAttribute("name") == "Default");
    }
    function treeNodesCopyConditionFunction(data)
    {
    	return data.getAttribute("nodeType") == "filter";
    }

    var groupDefinitionDialogConfig = {
        id:"filterGroupDialog",
        width:"30em",
        saveUrl:"searchQueryGroup/save?format=xml&type=notification",
        updateUrl:"searchQueryGroup/update?format=xml&type=notification",
        successfulyExecuted: function () {
            tree.poll()
        }
    };
    var groupDialog = new YAHOO.rapidjs.component.Form(document.getElementById("filterGroup"), groupDefinitionDialogConfig);
    var treeGridConfig = {
         id:"filterTree",
         url:"script/run/queryList?format=xml&type=notification",
         rootTag:"Filters",
         nodeId:"id",
         contentPath:"Filter",
         title:'Saved Queries',
         mouseOverCursor: 'pointer',
         columns: [
            {attributeName:'name', colLabel:'Name', width:248, sortBy:true}
         ],
        menuItems:{
            Delete : { id: 'delete', label : 'Delete',  condition : treeNodesUpdateDeleteConditionFunction },
            Update : { id: 'update', label : 'Update',  condition : treeNodesUpdateDeleteConditionFunction },
            CopyQuery : { id: 'copyQuery', label : 'Copy Query',  condition : treeNodesCopyConditionFunction }
        },
        rootImages :[
			{visible:'data["nodeType"] == "group"', expanded:'images/rapidjs/component/tools/folder_open.gif', collapsed:'images/rapidjs/component/tools/folder.gif'},
			{visible:'data["nodeType"] == "filter"', expanded:'images/rapidjs/component/tools/filter.png', collapsed:'images/rapidjs/component/tools/filter.png'}
		]
      };
    var tree = new YAHOO.rapidjs.component.TreeGrid(document.getElementById("treeDiv1"), treeGridConfig);
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

    var filterDefinitionDialogConfig = {
        id:"filterDialog",
        width:"35em",
        createUrl:"script/run/createQuery?queryType=notification",
        editUrl:"script/run/editQuery?queryType=notification",
        saveUrl:"searchQuery/save?format=xml&type=notification",
        updateUrl:"searchQuery/update?format=xml&type=notification",
        successfulyExecuted: function () {
            tree.poll()
        }
    };
    var dialog = new YAHOO.rapidjs.component.Form(document.getElementById("filterDialog"), filterDefinitionDialogConfig);
     var changePassDialogConfig = {
        id:"changePassDialog",
        width:"35em",
        saveUrl:"rsUser/changePassword?format=xml",
        successfulyExecuted: function () {}
    };
    var changePassDialog = new YAHOO.rapidjs.component.Form(document.getElementById("passwordDialog"), changePassDialogConfig);
    var Dom = YAHOO.util.Dom, Event = YAHOO.util.Event;
    Event.addListener(document.getElementById('rsUser'), 'click', function(){
         changePassDialog.show(dialog.CREATE_MODE);
         changePassDialog.dialog.form.username.value = "${session.username}";
    },this, true)

    Event.onDOMReady(function() {
        var layout = new YAHOO.widget.Layout({
            units: [
                { position: 'top', body: 'top', resize: false, height:45},
                { position: 'center', body: 'right', resize: false, gutter: '1px' },
                { position: 'left', width: 250, resize: true, body: 'left', scroll: false}
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
