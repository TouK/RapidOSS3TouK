<html>
<head>
    <meta name="layout" content="indexLayout" />
    <script type="text/javascript" src="js/yui/charts/charts-experimental-min.js"></script>
</head>
<body>
<div id="filterDialog">
    <div class="hd">Save Map</div>
    <div class="bd">
    <form method="POST" action="javascript://nothing">
        <table>
        <tr><td width="50%"><label>Group Name:</label></td><td width="50%"><select name="groupName" style="width:175px"/></td></tr>
        <tr><td width="50%"><label>Map Name:</label></td><td width="50%"><input type="textbox" name="mapName" style="width:175px"/></td></tr>
        </table>
        <input type="hidden" name="nodes"/>
        <input type="hidden" name="edges"/>
        <input type="hidden" name="id"/>
        <input type="hidden" name="layout"/>
    </form>

    </div>
</div>
<div id="filterGroup">
    <div class="hd">Save group</div>
    <div class="bd">
    <form method="POST" action="javascript://nothing">
        <table>
        <tr><td width="50%"><label>Group Name:</label></td><td width="50%"><input type="textbox" name="groupName" style="width:175px"/></td></tr>
        </table>
        <input type="hidden" name="id">
    </form>

    </div>
</div>
<div id="left">
   <div id="treeDiv1"></div>
</div>
<div id="right">
	<div id="mapDiv"></div>
</div>

<script type="text/javascript">

    function getURLParam(strParamName){
		var strReturn = "";
		var strHref = window.location.href;
		if ( strHref.indexOf("?") > -1 ){
			var strQueryString = strHref.substr(strHref.indexOf("?")).toLowerCase();
			var aQueryString = strQueryString.split("&");
			for ( var iParam = 0; iParam < aQueryString.length; iParam++ ){
				if (
					aQueryString[iParam].indexOf(strParamName.toLowerCase() + "=") > -1 ){
					var aParam = aQueryString[iParam].split("=");
					strReturn = aParam[1];
					break;
				}
			}
		}
		return unescape(strReturn);
	}



    YAHOO.rapidjs.ErrorManager.serverDownEvent.subscribe(function(){
        YAHOO.util.Dom.setStyle(document.getElementById('serverDownEl'), 'display', '');
    }, this, true);
    YAHOO.rapidjs.ErrorManager.serverUpEvent.subscribe(function(){
        YAHOO.util.Dom.setStyle(document.getElementById('serverDownEl'), 'display', 'none');
    }, this, true);

	/*
    var conf = {id:"htmlComp", width:500, height:400, iframe:false};
    var html = new YAHOO.rapidjs.component.Html(conf);
    html.hide();
    */


	function saveMapFunction( data )
	{
	    var nodes = topMap.getPropertiesString(topMap.getNodes(), ["id", "x", "y", "expanded", "expandable"]) ;
        dialog.show(dialog.CREATE_MODE, null, { nodes : nodes, layout:topMap.getLayout()} );
	}

      function toolbarMenuFcn( id )
      {
          alert( id);

      }


    function menuItemFilter(id) {
        var items = ["item1"];
        return items;
    }
    var topMapConfig = {
        id 		: "mapDiv",
        dataTag : "device",
        dataKeys : { id : "id", status : "status", load : "load" },
        expandURL : "script/run/expandMap",
        dataURL : "script/run/getMapData",
        pollingInterval : 0,
        icons : {
          "Host":{ "url":"images/rapidjs/component/topologyMap/server_icon.png"},
          "Router":{ "url":"images/rapidjs/component/topologyMap/router_icon.png"},
          "Switch":{ "url":"images/rapidjs/component/topologyMap/switch_icon.png"}
        },
        menuItems : { "item1": { "text": "Browse" }},
        menuItemFilter : "menuItemFilter",
        statusColors : { "1" : 0xde2c26, "2" : 0x7b4a1a, "3": 0xfae500, "4" : 0x20b4e0, "5":0x0d4702, "default" : 0x0d4702 },
		edgeColors : { "1" : 0xffde2c26,"2" :  0xfff79229,"3":  0xfffae500, "4" :  0xff20b4e0,"5": 0xff62b446, "default" : 0xff62b446 },
        toolbarMenuItems : [{
            "id" : "mapMenu",
            "label" : "Map",
            "submenuItems"  : [
                                  {
                                    "id" 	: "saveMap",
                                    "submenuItem" : {
                                                        "label"	: "Save Map",
                                                        "groupName" : "mapMenu",
                                                        "toggled"	: "true"
                                                    }
                                  }
                              ]
        }]
    };

    var topMap = new YAHOO.rapidjs.component.TopologyMap(document.getElementById("mapDiv"),topMapConfig );

    topMap.events.mapInitialized.subscribe(function(){
        var deviceName = this.getURLParam( "name");
        if( deviceName )
        {
            this.topMap.loadMapForNode( deviceName);
        }
    }, this, true);

    topMap.events.nodeMenuItemClicked.subscribe(function(params){
        var componentId = params["componentId"];
        var menuId = params["menuId"];
        var data = params.data;
        if(menuId == "item1")
        {
            var url = "getObjectDetails.gsp?name="+ encodeURIComponent(data["id"]);
            objectDetailsDialog.show(url, "Details of " + data["type"] + " " + data["id"]);    
        }
    }, this, true);

    topMap.events.toolbarMenuItemClicked.subscribe(function(params){
        var componentId = params["componentId"];
        var menuId = params["menuId"];
        if(menuId == "saveMap")
        {
            saveMapFunction();
        }
    }, this, true);

    var conf = {id:'objectDetails', width:500, height:400, iframe:false};
    var objectDetailsDialog = new YAHOO.rapidjs.component.Html(conf);
    objectDetailsDialog.hide();
    var actionConfig = {url:'topoMap/delete?format=xml'}
    var deleteMapAction = new YAHOO.rapidjs.component.action.RequestAction(actionConfig);

    var actionGroupConfig = {url:'mapGroup/delete?format=xml'}
    var deleteMapGroupAction = new YAHOO.rapidjs.component.action.RequestAction(actionGroupConfig);

    var actionSaveMapConfig = {url:'script/run/saveMap'}
    var saveMapAction = new YAHOO.rapidjs.component.action.RequestAction(actionSaveMapConfig);
    var actionLoadMapConfig = {url:'script/run/getMap'}
    var loadMapAction = new YAHOO.rapidjs.component.action.RequestAction(actionLoadMapConfig);

    function treeNodesUpdateDeleteConditionFunction(data)
    {
    	return data.getAttribute("isPublic") != "true" && !(data.getAttribute("nodeType") == "group" && data.getAttribute("name") == "Default");
    }
    function treeNodesCopyConditionFunction(data)
    {
    	return data.getAttribute("nodeType") == "filter";
    }

    var groupDefinitionDialogConfig = {
    	id: "groupDef",
        width:"30em",
        saveUrl:"mapGroup/save?format=xml",
        updateUrl:"mapGroup/update?format=xml",
        successfulyExecuted: function () {
            tree.poll()
        }

    };
    var groupDialog = new YAHOO.rapidjs.component.Form(document.getElementById("filterGroup"), groupDefinitionDialogConfig);
    var treeGridConfig = {
         id:"filterTree",
         url:"script/run/mapList",
         rootTag:"Maps",
         keyAttribute:"id",
         contentPath:"Map",
         title:'Saved Maps',
         expanded:true,
         columns: [
            {attributeName:'name', colLabel:'Name', width:248, sortBy:true}
         ],
        menuItems:{
            Delete : { id: 'delete', label : 'Delete',  condition : treeNodesUpdateDeleteConditionFunction },
            Update : { id: 'update', label : 'Update',  condition : treeNodesUpdateDeleteConditionFunction }
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
        tooltip: 'Save Map',
        click:function() {
            saveMapFunction();
        }
    });
    tree.poll();

    deleteMapAction.events.success.subscribe(tree.poll, tree, true);

    deleteMapGroupAction.events.success.subscribe(tree.poll, tree, true);

    saveMapAction.events.success.subscribe(tree.poll, tree, true);
    loadMapAction.events.success.subscribe(function(response, responseArgs){
        topMap.setLayout(responseArgs.mapLayout*1);
        topMap.handleLoadMap(response);
    }, this, true);

    tree.events["treeNodeClick"].subscribe(function(data) {
        if (data.getAttribute("nodeType") == "filter")
        {
           loadMapAction.execute( {"mapName":data.getAttribute("name")}, {"mapLayout":data.getAttribute("layout")} );
            
        }
    }, this, true);

    tree.events["rowMenuClick"].subscribe(function(data, id, parentId) {
    	if (id == "delete")
        {
            if (data.getAttribute("nodeType") == "filter")
                deleteMapAction.execute({id:data.getAttribute("id")});
            else if (data.getAttribute("nodeType") == "group")
                deleteMapGroupAction.execute({id:data.getAttribute("id")});
        }
        else if(id == "update"){
            if (data.getAttribute("nodeType") == "filter")
                dialog.show(dialog.EDIT_MODE, {mapId:data.getAttribute("id")})
            else if(data.getAttribute("nodeType") == "group"){
                groupDialog.show(groupDialog.EDIT_MODE)
                groupDialog.dialog.form.groupName.value = data.getAttribute("name");
                groupDialog.dialog.form.id.value = data.getAttribute("id")
            }
       }
    }, this, true);

    var filterDefinitionDialogConfig = {
    	id : "filterDef",
        width:"35em",
        createUrl:"script/run/createMap",
        editUrl:"script/run/editMap",
        saveObject: { url : "script/run/saveMap", requestType : "POST" },
        updateUrl:"topoMap/update?format=xml",
        successfulyExecuted: function () {
            tree.poll()
        }
    };
    var dialog = new YAHOO.rapidjs.component.Form(document.getElementById("filterDialog"), filterDefinitionDialogConfig);
     var changePassDialogConfig = {
     	id : "changePass",
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

        topMap.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
        layout.on('resize', function() {
            topMap.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
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