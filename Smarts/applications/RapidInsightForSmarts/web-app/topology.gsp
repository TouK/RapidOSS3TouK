<html>
<head>
    <meta name="layout" content="indexLayout" />
    <script type="text/javascript" src="js/yui/charts/charts-experimental-min.js"></script>
</head>
<body>

<rui:treeGrid id="filterTree" url="script/run/mapList" rootTag="Maps" keyAttribute="id"
     contentPath="Map" title="Saved Maps" expanded="true">
    <rui:tgColumns>
        <rui:tgColumn attributeName="name" colLabel="Name" width="248" sortBy="true"></rui:tgColumn>
    </rui:tgColumns>
    <rui:tgMenuItems>
        <rui:tgMenuItem id="delete" label="Delete" visible="data.isPublic != 'true' && !(data.name == 'Default' && data.nodeType == 'group')"></rui:tgMenuItem>
        <rui:tgMenuItem id="update" label="Update" visible="data.isPublic != 'true' && !(data.name == 'Default' && data.nodeType == 'group')"></rui:tgMenuItem>
    </rui:tgMenuItems>
    <rui:tgRootImages>
        <rui:tgRootImage visible="data.nodeType == 'group'" expanded='images/rapidjs/component/tools/folder_open.gif' collapsed='images/rapidjs/component/tools/folder.gif'></rui:tgRootImage>
        <rui:tgRootImage visible="data.nodeType == 'filter'" expanded='images/rapidjs/component/tools/filter.png' collapsed='images/rapidjs/component/tools/filter.png'></rui:tgRootImage>
    </rui:tgRootImages>
</rui:treeGrid>
<rui:form id="filterDialog" width="35em" createUrl="script/run/createMap" editUrl="script/run/editMap" updateUrl="topoMap/update?format=xml" saveUrl="script/run/saveMap" submitAction="POST">
    <div >
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
</rui:form>
<rui:form id="filterGroupDialog" width="30em" saveUrl="mapGroup/save?format=xml" updateUrl="mapGroup/update?format=xml">
    <div >
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
    
</rui:form>
<rui:html id="objectDetails" width="850" height="700" iframe="false"></rui:html>
<div id="right">
	<div id="mapDiv"></div>
</div>

<script type="text/javascript">
    
    var tree = YAHOO.rapidjs.Components['filterTree'];
    var groupDialog = YAHOO.rapidjs.Components['filterGroupDialog'];
    groupDialog.successful = function(){tree.poll()};
    var dialog = YAHOO.rapidjs.Components['filterDialog'];
    dialog.successful = function(){tree.poll()};
    var objectDetailsDialog = YAHOO.rapidjs.Components['objectDetails'];
    objectDetailsDialog.hide();
    
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
        nodeSize:60,
        nodeContent:{
            images:[
                {
                    id:"status", x:70, y:40, width:30, height:30, dataKey:"state", images:{
                    "0":"green.png",
                    "1":"red.png",
                    "2":"orange.png",
                    "3":"yellow.png",
                    "4":"blue.png",
                    "5":"green.png",
                    "default":"green.png"}
                },
                {
                    id:"icon",  x:70, y:0,  width:20, height:20, dataKey:"type", images:{
                    "Host":"server_icon.png",
                    "Router":"router_icon.png",
                    "Switch":"switch_icon.png"}
                }
            ]
        },
        menuFilterFunction : menuItemFilter,
        statusColors : { "1" : 0xde2c26, "2" : 0x7b4a1a, "3": 0xfae500, "4" : 0x20b4e0, "5":0x0d4702, "default" : 0x0d4702 },
		edgeColors : { "1" : 0xffde2c26,"2" :  0xfff79229,"3":  0xfffae500, "4" :  0xff20b4e0,"5": 0xff62b446, "default" : 0xff62b446 },
        toolbarMenuItems : [{
            "text" : "Map",
            "subMenu"  : {id:"mapMenu", itemdata:[{
                                    "id" 	: "saveMap",
                                    text:  "Save Map"}
                        ]
        }}],
        nodeMenuItems : [{id:"item1","text" : "Browse"}]
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

    var actionConfig = {url:'topoMap/delete?format=xml'}
    var deleteMapAction = new YAHOO.rapidjs.component.action.RequestAction(actionConfig);

    var actionGroupConfig = {url:'mapGroup/delete?format=xml'}
    var deleteMapGroupAction = new YAHOO.rapidjs.component.action.RequestAction(actionGroupConfig);

    var actionSaveMapConfig = {url:'script/run/saveMap'}
    var saveMapAction = new YAHOO.rapidjs.component.action.RequestAction(actionSaveMapConfig);
    var actionLoadMapConfig = {url:'script/run/getMap'}
    var loadMapAction = new YAHOO.rapidjs.component.action.RequestAction(actionLoadMapConfig);



    
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
        topMap.loadMap(response);
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


    var Dom = YAHOO.util.Dom, Event = YAHOO.util.Event;

    Event.onDOMReady(function() {
        var layout = new YAHOO.widget.Layout({
            units: [
                { position: 'top', body: 'top', resize: false, height:45},
                { position: 'center', body: 'right', resize: false, gutter: '1px' },
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