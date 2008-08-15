<html>
<head>
	<title>iFountain - RapidInsight for Netcool</title>
    <script type="text/javascript" src="js/yui/utilities/utilities.js"></script>
    <script type="text/javascript" src="js/yui/resize/resize-beta-min.js"></script>
    <script type="text/javascript" src="js/yui/layout/layout-beta-min.js"></script>
    <script type="text/javascript" src="js/ext/ext.js"></script>

    <script type="text/javascript" src="js/rapidjs/component/ComponentContainer.js"></script>
    <script type="text/javascript" src="js/rapidjs/RapidUtil.js"></script>
    <script type="text/javascript" src="js/rapidjs/data/NodeFactory.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/RapidElement.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/PollingComponentContainer.js"></script>
    <script type="text/javascript" src="js/yui/container/container-min.js"></script>
    <script type="text/javascript" src="js/yui/button/button-min.js"></script>
    <script type="text/javascript" src="js/rapidjs/data/RapidXmlDocument.js"></script>

    <script type="text/javascript" src="js/rapidjs/SelectUtils.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/form/Form.js"></script>

    <script type="text/javascript" src="js/rapidjs/component/simplewidgets/Button.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/tools/BasicTool.js"></script>
    <script type="text/javascript" src="js/yui/container/container_core-min.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/tools/SettingsTool.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/dialog/Dialog.js"></script>
    <script type="text/javascript" src="js/yui/menu/menu-min.js"></script>

    <script type="text/javascript" src="js/rapidjs/component/search/SearchNode.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/tools/ButtonToolBar.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/tools/SearchListSettingsTool.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/tools/LoadingTool.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/tools/ErrorTool.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/search/SearchList.js"></script>

    <script type="text/javascript" src="js/rapidjs/component/treegrid/split.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/tools/Tooltip.js"></script>

    <script type="text/javascript" src="js/rapidjs/component/treegrid/TreeNode.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/treegrid/TreeHeaderCell.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/treegrid/TreeGridView.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/treegrid/TreeGrid.js"></script>

    <script type="text/javascript" src="js/rapidjs/component/action/Action.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/html/Html.js"></script>

    <link rel="stylesheet" type="text/css" href="js/yui/assets/skins/sam/menu.css" />
    <link rel="stylesheet" type="text/css" href="js/yui/assets/skins/sam/skin.css" />
    <link rel="stylesheet" type="text/css" href="js/yui/button/assets/skins/sam/button.css" />
    <link rel="stylesheet" type="text/css" href="js/yui/container/assets/skins/sam/container.css" />
    <link rel="stylesheet" type="text/css" href="css/rapidjs/common.css" />
	<link rel="stylesheet" type="text/css" href="css/rapidjs/dialog.css" />
	<link rel="stylesheet" type="text/css" href="css/rapidjs/form.css" />
	<link rel="stylesheet" type="text/css" href="css/rapidjs/mgrid.css" />
	<link rel="stylesheet" type="text/css" href="css/rapidjs/overlay.css" />

	<link rel="stylesheet" type="text/css" href="css/rapidjs/ryuitree.css" />
	<link rel="stylesheet" type="text/css" href="css/rapidjs/searchlist.css" />
	<link rel="stylesheet" type="text/css" href="css/rapidjs/simplewidgets/button.css" />
	<link rel="stylesheet" type="text/css" href="css/rapidjs/tools/tools.css" />
	<link rel="stylesheet" type="text/css" href="css/rapidjs/treegrid/treegrid.css" />



    <jsec:isNotLoggedIn>
	  <g:javascript>window.location='auth/login?targetUri=/index.gsp'</g:javascript>
	</jsec:isNotLoggedIn>
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
		.yui-skin-sam .yui-resize .yui-resize-handle-r {
			background-image: url(images/rapidjs/component/layout/e-handle.gif);
			background-position: left center;
			background-color:#C3DAF9;

		}
		.yui-skin-sam .yui-layout .yui-resize-proxy div{
			background-color:#C3DAF9;
		}
		.yui-skin-sam .yui-layout-unit .yui-resize-handle-r .yui-layout-resize-knob{
			background-image : none;

		}
    </style>
</head>
<body class=" yui-skin-sam">
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
    </tr></tbody></table>
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
    	return (key == "severity" && value != '5') || (key == "suppressescl" && value != '6') ||
    		   (key != "severity" && key != "suppressescl" && searchListPropertyMenuConditionFunction(key, value, data));
    }
    function searchListPropertyMenuConditionFunctionLessThan(key, value, data)
	{
    	return (key == "severity" && value != '0') || (key == "suppressescl" && value != '0')||
    	 	   (key != "severity" && key != "suppressescl" && searchListPropertyMenuConditionFunction(key, value, data));
    }

    function searchListPropertyMenuConditionFunction(key, value, data)
    {
           return YAHOO.lang.isNumber(parseInt(value));
    }

    function searchListHeaderMenuConditionFunctionAcknowledge(data)
    {
        return data.getAttribute("acknowledged") == "No";
    }

    function searchListHeaderMenuConditionFunctionDeacknowledge(data)
    {
        return data.getAttribute("acknowledged") == "Yes";
    }

    function searchListHeaderMenuConditionFunctionAddTaskToList(data)
    {
        return data.getAttribute("tasklist") == 0;
    }

    function searchListHeaderMenuConditionFunctionRemoveTaskFromList(data)
    {
        return data.getAttribute("tasklist") == 1;
    }
    function searchListHeaderMenuConditionFunctionSeveritySubmenu( data, item )
    {
        var severity = data.getAttribute("severity");
        return !(severity == item.id);
    }

    function searchListHeaderMenuConditionFunctionSuppressSubmenu (data, item)
    {
        var suppress = data.getAttribute("suppressescl");
        return !(suppress == item.id);
    }

    YAHOO.rapidjs.ErrorManager.serverDownEvent.subscribe(function(){
        YAHOO.util.Dom.setStyle(document.getElementById('serverDownEl'), 'display', '');
    }, this, true);
    YAHOO.rapidjs.ErrorManager.serverUpEvent.subscribe(function(){
        YAHOO.util.Dom.setStyle(document.getElementById('serverDownEl'), 'display', 'none');
    }, this, true);


    var conf = {width:500, height:400, iframe:false};
    var html = new YAHOO.rapidjs.component.Html(conf);
    html.hide();
    var actionConfig = {url:'searchQuery/delete.xml'}
    var deleteQueryAction = new YAHOO.rapidjs.component.action.RequestAction(actionConfig);

    var actionGroupConfig = {url:'searchQueryGroup/delete.xml'}
    var deleteQueryGroupAction = new YAHOO.rapidjs.component.action.RequestAction(actionGroupConfig);


    var searchConfig = {
        id:'searchList',
        url:'search.xml',
        searchQueryParamName:'query',
        rootTag:'Objects',
        contentPath:'Object',
        keyAttribute:'id',
        totalCountAttribute:'total',
        offsetAttribute:'offset',
        sortOrderAttribute:'sortOrder',
        titleAttribute:"serverserial",
        lineSize:3,
        title:'Netcool Events',
        defaultFields:['node', 'owneruid', 'ownergid', 'acknowledged','agent','manager', 'summary','tally','severity','suppressescl','tasklist','lastoccurrence','statechange','alertgroup','alertkey'],
        menuItems:{
            item1 : { id : 'eventDetails', label : 'Event Details' },
            item2 : { id : 'acknowledge', label : 'Acknowledge', condition: searchListHeaderMenuConditionFunctionAcknowledge },
            item3 : { id : 'deacknowledge', label : 'Deacknowledge', condition: searchListHeaderMenuConditionFunctionDeacknowledge },
            item4 : { id : 'takeOwnership', label : 'Take Ownership' },
            item5 : { id : 'addTaskToList', label : 'Add Task To List', condition: searchListHeaderMenuConditionFunctionAddTaskToList },
            item6 : { id : 'removeTaskFromList', label : 'Remove Task From List', condition: searchListHeaderMenuConditionFunctionRemoveTaskFromList },
            item7 : { id : 'suppressEscalate', label : 'Suppress/Escalate',  submenuItems : {
                            subItem1 : { id: '0', label : 'Normal', condition: searchListHeaderMenuConditionFunctionSuppressSubmenu},
                            subItem2 : { id: '1', label : 'Escalated', condition: searchListHeaderMenuConditionFunctionSuppressSubmenu},
                            subItem3 : { id: '2', label : 'Escalated-Level 2', condition: searchListHeaderMenuConditionFunctionSuppressSubmenu },
                            subItem4 : { id: '3', label : 'Escalated-Level 3',condition: searchListHeaderMenuConditionFunctionSuppressSubmenu },
                            subItem5 : { id: '4', label : 'Suppressed',condition: searchListHeaderMenuConditionFunctionSuppressSubmenu },
                            subItem6 : { id: '5', label : 'Hidden',condition: searchListHeaderMenuConditionFunctionSuppressSubmenu },
                            subItem7 : { id: '6', label : 'Maintenance',condition: searchListHeaderMenuConditionFunctionSuppressSubmenu }
                        }
                    },
            item8 : { id : 'severity', label : 'Change Severity', submenuItems : {
                            subItem1 : { id: '5', label : 'Critical', condition: searchListHeaderMenuConditionFunctionSeveritySubmenu},
                            subItem2 : { id: '4', label : 'Major', condition: searchListHeaderMenuConditionFunctionSeveritySubmenu},
                            subItem3 : { id: '3', label : 'Minor', condition: searchListHeaderMenuConditionFunctionSeveritySubmenu },
                            subItem4 : { id: '2', label : 'Warning',condition: searchListHeaderMenuConditionFunctionSeveritySubmenu },
                            subItem5 : { id: '1', label : 'Indeterminate',condition: searchListHeaderMenuConditionFunctionSeveritySubmenu },
                            subItem6 : { id: '0', label : 'Clear',condition: searchListHeaderMenuConditionFunctionSeveritySubmenu }
                        }
                    }
        } ,
        images:[
            {exp:'data["severity"] == 5', src:'images/rapidjs/component/searchlist/red.png'},
            {exp:'data["severity"] == 4', src:'images/rapidjs/component/searchlist/orange.png'},
            {exp:'data["severity"] == 3', src:'images/rapidjs/component/searchlist/yellow.png'},
            {exp:'data["severity"] == 2', src:'images/rapidjs/component/searchlist/blue.png'},
            {exp:'data["severity"] == 1', src:'images/rapidjs/component/searchlist/purple.png'},
            {exp:'data["severity"] == 0', src:'images/rapidjs/component/searchlist/green.png'}
        ],
        propertyMenuItems:{
            item1 : { id : 'sortAsc', label : 'Sort asc' },
            item2 : { id : 'sortDesc', label : 'Sort desc' },
            item3 : { id : 'greaterThan', label : 'Greater than',  condition: searchListPropertyMenuConditionFunctionGreaterThan},
            item4 : { id : 'lessThan', label : 'Less than' , condition: searchListPropertyMenuConditionFunctionLessThan},
            item5 : { id : 'greaterThanOrEqualTo', label : 'Greater than or equal to',  condition: searchListPropertyMenuConditionFunction},
            item6 : { id : 'lessThanOrEqualTo', label : 'Less than or equal to' , condition: searchListPropertyMenuConditionFunction},
            item7 : { id : 'except', label : 'Except' , condition: function(){return true;}}
        } ,
        saveQueryFunction: function(query) {
            dialog.show(dialog.CREATE_MODE, null, {query:query, sortProperty:searchList.getSortAttribute(), sortOrder: searchList.getSortOrder()});
        },
        renderCellFunction : function(key, value, data){
        	if(key == "lastoccurrence" || key == "statechange"){
                var d = new Date();
                d.setTime(parseFloat(value)*1000)
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
            else if(key == "suppressescl")
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
    }

    var searchList = new YAHOO.rapidjs.component.search.SearchList(document.getElementById("searchDiv"), searchConfig);


    var acknowledgeConfig = { url: 'script/run/acknowledge?format=xml' };
	var acknowledgeAction = new YAHOO.rapidjs.component.action.MergeAction(acknowledgeConfig);

	var taskListConfig = { url: 'script/run/taskList?format=xml' };
	var taskListAction = new YAHOO.rapidjs.component.action.MergeAction(taskListConfig);

	var severityConfig = { url: 'script/run/severity?format=xml' };
	var severityAction = new YAHOO.rapidjs.component.action.MergeAction(severityConfig);

    var suppressConfig = { url: 'script/run/suppress?format=xml' };
	var suppressAction = new YAHOO.rapidjs.component.action.MergeAction(suppressConfig);

    var takeOwnershipConfig = { url: 'script/run/takeOwnership?format=xml' };
	var takeOwnershipAction = new YAHOO.rapidjs.component.action.MergeAction(takeOwnershipConfig);

    searchList.events["rowHeaderMenuClick"].subscribe(function(xmlData, id, parentId) {
    	var serverName = xmlData.getAttribute("servername");
       	var serverSerial = xmlData.getAttribute("serverserial");

        if( id == "eventDetails"){
            var eventId = xmlData.getAttribute("id");
            var url = "getDetails.gsp?id="+eventId;
            html.show(url);
        }
        else if( id == 'acknowledge' )
            acknowledgeAction.execute({servername:serverName, serverserial : serverSerial, acknowledged:"true"}, [searchList]);

        else if( id == 'deacknowledge' )
    		acknowledgeAction.execute({servername:serverName, serverserial : serverSerial, acknowledged:"false"}, [searchList]);

        else if( id == 'addTaskToList' )
        	taskListAction.execute({servername:serverName, serverserial : serverSerial, taskList:"true"}, [searchList]);

        else if( id == 'removeTaskFromList' )
        	taskListAction.execute({servername:serverName, serverserial : serverSerial, taskList:"false"}, [searchList]);

        else if( parentId == 'suppressEscalate' )
        	suppressAction.execute({servername:serverName, serverserial : serverSerial, suppressescl:id }, [searchList]);

        else if (parentId == 'severity')
        	severityAction.execute({servername:serverName, serverserial : serverSerial, severity:id}, [searchList]);
        else if (id == 'takeOwnership')
        	takeOwnershipAction.execute({servername:serverName, serverserial : serverSerial}, [searchList]);

    }, this, true);
    searchList.events["rowDoubleClicked"].subscribe(function(xmlData){
    		var eventId = xmlData.getAttribute("id");
            var url = "getDetails.gsp?id="+eventId;
            html.show(url);

    }, true, true);

    searchList.events["cellMenuClick"].subscribe(function(key, value, xmlData, id) {
			if(	id == "except"){
				 if(searchList.searchBox.dom.getElementsByTagName('input')[0].value!= "")
                	searchList.appendToQuery("NOT " + key + ": \""+ value + "\"");
                 else
                    searchList.appendToQuery(key + ":[0 TO *] NOT "+ key + ": \""+ value + "\"");
			}
	        else if (id == "sortAsc") {
	            searchList.setSortDirection(key, true);
	        }
	        else if (id == "sortDesc") {
	            searchList.setSortDirection(key, false);
	        }
	        else if (id == "greaterThan") {
	           	searchList.appendToQuery(key + ":{" + value + " TO *}");
	        }
            else if (id == "greaterThanOrEqualTo") {
	        	searchList.appendToQuery(key + ":[" + value + " TO *]");
	        }
            else if (id == "lessThanOrEqualTo") {
	        	searchList.appendToQuery(key + ":[* TO " + value + "]");
	        }
            else if (id == "lessThan") {
	        	searchList.appendToQuery(key + ":{* TO " + value + "}");
	        }

    }, this, true);

    var treeDisplayAttribute = "name";
    function treeNodesUpdateDeleteConditionFunction(data)
    {
    	return data.getAttribute(treeDisplayAttribute) != "Default" && data.getAttribute(treeDisplayAttribute)!="By Severity" &&
    			data.getAttribute("isDefault")!='true';
    }
    function treeNodesCopyConditionFunction(data)
    {
    	return data.getAttribute("nodeType") == "filter";
    }

    var groupDefinitionDialogConfig = {
        width:"30em",
        saveUrl:"searchQueryGroup/save.xml",
        updateUrl:"searchQueryGroup/update.xml",
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
                dialog.show(dialog.EDIT_MODE, {id:data.getAttribute("id")})
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
        width:"35em",
        createUrl:"searchQuery/create.xml",
        editUrl:"searchQuery/edit.xml",
        saveUrl:"searchQuery/save.xml",
        updateUrl:"searchQuery/update.xml",
        successfulyExecuted: function () {
            tree.poll()
        }
    };
    var dialog = new YAHOO.rapidjs.component.Form(document.getElementById("filterDialog"), filterDefinitionDialogConfig);
     var changePassDialogConfig = {
        width:"35em",
        saveUrl:"rsUser/changePassword.xml",
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
                { position: 'top', body: 'top', resize: false, height:40},
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