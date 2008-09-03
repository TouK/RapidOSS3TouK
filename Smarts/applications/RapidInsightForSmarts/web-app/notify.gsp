<html>
<head>
    <meta name="layout" content="indexLayout" />
    <style>
		.r-filterTree-groupAdd{
			background-image: url( images/rapidjs/component/tools/filter_group.png);
		}
		.r-filterTree-queryAdd{
			background-image: url( images/rapidjs/component/tools/filteradd.png);
		}
		.r-tree-firstCell{
			cursor:pointer;
		}
    </style>
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
        <tr><td width="50%"><label>Sort Property:</label></td><td width="50%"><select name="sortProperty" style="width:175px"/></td></tr>
        <tr><td width="50%"><label>Sort Order:</label></td><td width="50%">
            <select name="sortOrder" style="width:175px"><option value="asc">asc</option><option value="desc">desc</option></select>
        </td></tr>
        </table>
        <input type="hidden" name="id">
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
<div id="passwordDialog">
    <div class="hd">Change Password</div>
    <div class="bd">
    <form method="POST" action="javascript://nothing">
        <table width="100%">
        <tr><td width="50%"><label>Old Password:</label></td><td width="50%"><input type="password" name="oldPassword" style="width:175px"/></td></tr>
        <tr><td width="50%"><label>New Password:</label></td><td width="50%"><input type="password" name="password1" style="width:175px"/></td></tr>
        <tr><td width="50%"><label>Confirm Password:</label></td><td width="50%"><input type="password" name="password2" style="width:175px"/></td></tr>
        </table>
        <input type="hidden" name="username">
    </form>

    </div>
</div>
<div id="left">
    <div id="treeDiv1"></div>
</div>
<div id="top" style="background-color:#BBD4F6;">
    <table style="height:100%" cellspacing="0" cellpadding="0"><tbody><tr>
        <td width="0%" style="padding-left:10px;padding-top:5px;">
            <img src="images/RapidInsight-blue.png">
        </td>
        <td width="100%"></td>
        <td id="serverDownEl" width="0%" style="display:none">
            <img src="images/network-offline.png"/>
        </td>
        <td width="0%">
           <div style="vertical-align:bottom">
               <span id="rsUser" style="font-size:12px;font-weight:bold;color:#083772;text-align:right;margin-bottom:5px;cursor:pointer">${session.username}</span>
               <a href="auth/logout" style="font-size:13px;font-weight:bold;color:#083772;text-align:right;text-decoration:none">Logout</a>
           </div>
        </td>
    </tr>
    <tr>
    	<td width="100%" style="padding-bottom:5px;">
		    <div class="yui-navset">
		    <ul class="yui-nav" style="border-style: none">
		        <li><a href="${createLinkTo(file: 'index.gsp')}"><em>Topology</em></a></li>
		        <li class="selected"><a href="${createLinkTo(file: 'notify.gsp')}"><em>Notifications</em></a></li>
                <li><a href="${createLinkTo(file: 'topology.gsp')}"><em>Map</em></a></li>
            </ul>
		    </div>
		</td>
	</tr>
    </tbody></table>
</div>
<div id="right">
    <div id="searchDiv"></div>
</div>
  <style>
    .dragging, .drag-hint {
      border: 1px solid gray;
      background-color: blue;
      color: white;
      opacity: 0.76;
      filter: "alpha(opacity=76)";
    }
    </style>

<script type="text/javascript">
	function searchListPropertyMenuConditionFunctionGreaterThan(key, value, data)
    {
    	return (key == "severity" && value != '1') || (key != "severity" && searchListPropertyMenuConditionFunction(key, value, data));
    }
    function searchListPropertyMenuConditionFunctionLessThan(key, value, data)
	{
    	return (key == "severity" && value != '5') || (key != "severity" && searchListPropertyMenuConditionFunction(key, value, data));
    }

    function searchListPropertyMenuConditionFunction(key, value, data)
    {
           return YAHOO.lang.isNumber(parseInt(value));
    }

	function searchListHeaderMenuConditionFunctionAcknowledge(data)
    {
        return data.getAttribute("acknowledged") == "false";
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

    var eventDetailsDialog = new YAHOO.rapidjs.component.Html({id:'eventDetails', width:500, height:400, iframe:false});
    var objectDetailsDialog = new YAHOO.rapidjs.component.Html({id:'objectDetails', width:500, height:400, iframe:false});
    eventDetailsDialog.hide();
    objectDetailsDialog.hide();
    var actionConfig = {url:'searchQuery/delete?format=xml'}
    var deleteQueryAction = new YAHOO.rapidjs.component.action.RequestAction(actionConfig);

    var actionGroupConfig = {url:'searchQueryGroup/delete?format=xml'}
    var deleteQueryGroupAction = new YAHOO.rapidjs.component.action.RequestAction(actionGroupConfig);


    var searchConfig = {
        id:'searchList',
        url:'search?format=xml',
        searchQueryParamName:'query',
        defaultFilter:"alias:RsEvent",
        rootTag:'Objects',
        contentPath:'Object',
        keyAttribute:'id',
        totalCountAttribute:'total',
        offsetAttribute:'offset',
        sortOrderAttribute:'sortOrder',
        titleAttribute:"serverserial",
        lineSize:3,
        title:'Events',
        defaultFields:['className', 'instanceName', 'eventName', 'sourceDomainName','acknowledged','owner',
                        'lastChangedAt','elementClassName', 'elementName','isRoot'],

        menuItems:{
            item1 : { id : 'acknowledge', label : 'Acknowledge', condition: searchListHeaderMenuConditionFunctionAcknowledge },
            item2 : { id : 'unacknowledge', label : 'Unacknowledge', condition: searchListHeaderMenuConditionFunctionUnacknowledge },
            item3 : { id : 'takeOwnership', label : 'Take Ownership'},
            item4 : { id : 'releaseOwnership', label : 'Release Ownership'},
            item5 : { id : 'eventDetails', label : 'Event Details' }
        },
        images:[
            {exp:'data["severity"] == 1', src:'images/rapidjs/component/searchlist/red.png'},
            {exp:'data["severity"] == 2', src:'images/rapidjs/component/searchlist/orange.png'},
            {exp:'data["severity"] == 3', src:'images/rapidjs/component/searchlist/yellow.png'},
            {exp:'data["severity"] == 4', src:'images/rapidjs/component/searchlist/blue.png'},
            {exp:'data["severity"] == 5', src:'images/rapidjs/component/searchlist/green.png'}
        ],
        propertyMenuItems:{
            item1 : { id : 'sortAsc', label : 'Sort asc' },
            item2 : { id : 'sortDesc', label : 'Sort desc' },
            item3 : { id : 'greaterThan', label : 'Greater than',  condition: searchListPropertyMenuConditionFunctionGreaterThan},
            item4 : { id : 'lessThan', label : 'Less than' , condition: searchListPropertyMenuConditionFunctionLessThan},
            item5 : { id : 'greaterThanOrEqualTo', label : 'Greater than or equal to',  condition: searchListPropertyMenuConditionFunction},
            item6 : { id : 'lessThanOrEqualTo', label : 'Less than or equal to' , condition: searchListPropertyMenuConditionFunction},
            item7 : { id : 'except', label : 'Except'},
            item8 : { id : 'browse', label : 'Browse', condition:function(key, value, data){return key == "instanceName"}}
        } ,
        saveQueryFunction: function(query) {
            dialog.show(dialog.CREATE_MODE, null, {query:query, sortProperty:searchList.getSortAttribute(), sortOrder: searchList.getSortOrder()});
        }
    }

    var searchList = new YAHOO.rapidjs.component.search.SearchList(document.getElementById("searchDiv"), searchConfig);

    searchList.events["cellMenuClick"].subscribe(function(key, value, xmlData, id) {
			if(	id == "except"){
				 if(searchList.searchInput.value!= "")
                	searchList.appendToQuery("NOT " + key + ": \""+ value + "\"");
                 else
                    searchList.appendToQuery(key + ":[0 TO *] NOT "+ key + ": \""+ value + "\"");
			}
            else if(id == "browse"){
                var url = "getObjectDetails.gsp?name="+value;
                objectDetailsDialog.show(url, "Details of " + xmlData.getAttribute("className") + " " + value);
            }
            else if (id == "sortAsc") {
	            searchList.setSortDirection(key, true);
	        }
	        else if (id == "sortDesc") {
	            searchList.setSortDirection(key, false);
	        }
    }, this, true);
    var acknowledgeConfig = { url: 'script/run/acknowledge?format=xml' };
	var acknowledgeAction = new YAHOO.rapidjs.component.action.MergeAction(acknowledgeConfig);

	var setOwnershipConfig = { url: 'script/run/setOwnership?format=xml' };
	var setOwnershipAction = new YAHOO.rapidjs.component.action.MergeAction(setOwnershipConfig);

    searchList.events["rowHeaderMenuClick"].subscribe(function(xmlData, id, parentId) {
    	var eventName = xmlData.getAttribute("eventName");
		var instanceName = xmlData.getAttribute("instanceName");
		var className = xmlData.getAttribute("className");

        if( id == "eventDetails"){

            var url = "getEventDetails.gsp?eventName="+eventName+"&instanceName="+instanceName+"&className="+className;
            eventDetailsDialog.show(url, "Details of " + className + " " + instanceName + " " + eventName);
        }
        else if( id == 'acknowledge' )
            acknowledgeAction.execute({eventName:eventName, instanceName : instanceName,className : className, acknowledged:true}, [searchList]);

        else if( id == 'unacknowledge' )
        	acknowledgeAction.execute({eventName:eventName, instanceName : instanceName,className : className, acknowledged:false}, [searchList]);
        else if(id == 'takeOwnership')
        	setOwnershipAction.execute({eventName:eventName, instanceName : instanceName,className : className, act:true}, [searchList]);
        else if(id == 'releaseOwnership')
        	setOwnershipAction.execute({eventName:eventName, instanceName : instanceName,className : className, act:false}, [searchList]);

    }, this, true);
    searchList.events["rowDoubleClicked"].subscribe(function(xmlData, event){
    	if(YAHOO.util.Event.getTarget(event).className != 'rcmdb-search-cell-key')
    	{
    		var eventName = xmlData.getAttribute("eventName");
    		var instanceName = xmlData.getAttribute("instanceName");
    		var className = xmlData.getAttribute("className");
            var url = "getEventDetails.gsp?eventName="+eventName+"&instanceName="+instanceName+"&className="+className;
            eventDetailsDialog.show(url, "Details of " + className + " " + instanceName + " " + eventName);
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
        saveUrl:"searchQueryGroup/save?format=xml",
        updateUrl:"searchQueryGroup/update?format=xml",
        successfulyExecuted: function () {
            tree.poll()
        }
    };
    var groupDialog = new YAHOO.rapidjs.component.Form(document.getElementById("filterGroup"), groupDefinitionDialogConfig);
    var treeGridConfig = {
         id:"filterTree",
         url:"script/run/queryList?format=xml",
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
            searchList.setQuery(data.getAttribute("query"), data.getAttribute('sortProperty'), data.getAttribute('sortOrder'));
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
        		dialog.show(dialog.CREATE_MODE,null,{name:'', group:data.parentNode().getAttribute('name'),
        										query:data.getAttribute('query')});

            }
    }, this, true);

    var filterDefinitionDialogConfig = {
        id:"filterDialog",
        width:"35em",
        createUrl:"script/run/createQuery",
        editUrl:"script/run/editQuery",
        saveUrl:"searchQuery/save?format=xml",
        updateUrl:"searchQuery/update?format=xml",
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
                { position: 'top', body: 'top', resize: false, height:60},
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

        searchList.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
        layout.on('resize', function() {
            searchList.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
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
