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
<body>
<div id="filterDialog">
    <div class="hd">Save Map</div>
    <div class="bd">
    <form method="POST" action="javascript://nothing">
        <table width="100%">
        <tr><td width="50%"><label>Group Name:</label></td><td width="50%"><select name="groupName" style="width:175px"/></td></tr>
        <tr><td width="50%"><label>Map Name:</label></td><td width="50%"><input type="textbox" name="mapName" style="width:175px"/></td></tr>
        </table>
        <input type="hidden" name="nodes">
        <input type="hidden" name="edges">
    </form>

    </div>
</div>
<div id="filterGroup">
    <div class="hd">Save group</div>
    <div class="bd">
    <form method="POST" action="javascript://nothing">
        <table width="100%">
        <tr><td width="50%"><label>Group Name:</label></td><td width="50%"><input type="textbox" name="groupName" style="width:175px"/></td></tr>
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
    	<td width="100%">
		    <div class="yui-navset">
		    <ul class="yui-nav" style="border-style: none">
		        <li><a href="${createLinkTo(file: 'index.gsp')}"><em>Devices</em></a></li>
		        <li><a href="${createLinkTo(file: 'notify.gsp')}"><em>Notifications</em></a></li>
                <li class="selected"><a href="${createLinkTo(file: 'map.gsp')}"><em>Map</em></a></li>
            </ul>
		    </div>
		</td>
	</tr>
    </tbody></table>
</div>
<div id="right">
	<div id="mapDiv"></div>
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
	YAHOO.rapidjs.ErrorManager.serverDownEvent.subscribe(function(){
        YAHOO.util.Dom.setStyle(document.getElementById('serverDownEl'), 'display', '');
    }, this, true);
    YAHOO.rapidjs.ErrorManager.serverUpEvent.subscribe(function(){
        YAHOO.util.Dom.setStyle(document.getElementById('serverDownEl'), 'display', 'none');
    }, this, true);


    var conf = {width:500, height:400, iframe:false};
    var html = new YAHOO.rapidjs.component.Html(conf);
    html.hide();


    function configFunction()
    {
        var config = new Object();
        config.id = "mapDiv";
        config.icons = {
          "host":{ "url":"images/rapidjs/component/topologyMap/alert_critical_icon.png"},
          "router":{ "url":"images/rapidjs/component/topologyMap/alert_informational_icon.png"},
          "other":{ "url":"images/rapidjs/component/topologyMap/alert_warning_icon.png"}
        };
        config.infoFunction = "showInfo";
        config.menuItems = { "item1": { "text": "Item text 1" },
                             "item2": { "text": "Item text 2" },
                             "item3": { "text": "Item text 3" }
                           };
        config.menuItemFilter = "menuItemFilter";
        config.expandRequestFunction = "expandRequestFunction";
        config.toolbarItems = [
                                    {
                                        "id" : "customMenuItem1",
                                        "label" : "Menu1",
                                        "submenuItems"  : [
                                                              {
                                                                "id" 	: "submenuItem1",
                                                                "submenuItem" : {
                                                                                    "label"	: "Submenu 1",
                                                                                    "type"	: "radio",
                                                                                    "groupName" : "menu1",
                                                                                    "toggled"	: "true"
                                                                                }
                                                              },
                                                              {
                                                                "id" 	: "submenuItem2",
                                                                "submenuItem" : {
                                                                                    "label"	: "Submenu 2",
                                                                                    "type"	: "radio",
                                                                                    "groupName" : "menu1"
                                                                                }
                                                              }
                                                          ]
                                    },
                                    {
                                        "id" : "customMenuItem2",
                                        "label" : "Menu2",
                                        "submenuItems"  : [
                                                              {
                                                                "id" 	: "submenuItem3",
                                                                "submenuItem" : {
                                                                                    "label"	: "Submenu 3"
                                                                                }
                                                              },
                                                              {
                                                                "id" 	: "submenuItem4",
                                                                "submenuItem" : {
                                                                                    "label"	: "Submenu 4"
                                                                                }
                                                              }
                                                           ]
                                    }
                              ];

        config.contentReadyFunction = "contentReady";
        config.toolbarMenuFunction = "toolbarMenuFcn";
        config.nodeMenuFunction = "nodeMenuFcn";
        config.saveMapFunction = "saveMapFunction";
        return config;
      }

        function saveMapFunction( data )
        {
            var nodes = data["nodes"];
            var edges = data["edges"];
            dialog.show(dialog.CREATE_MODE, null, { nodes : nodes, edges : edges} );
        }

      function toolbarMenuFcn( id )
      {
          alert( id);

      }

      function nodeMenuFcn( data )
      {
          var id = data["id"];
          var nodeData = data["data"];
          alert( id + ' ' + nodeData["id"] );
      }

      function topologyMapComponentAdapter ( params )
      {
          var id = params["id"];
          var functionName = params["functionName"];

          var component = YAHOO.rapidjs.Components[id];

          if( functionName == "mapContentReady" )
          {
          }
          else if(functionName == "refreshTopology" )
          {
              component.loadHandler();
          }
      }

    function menuItemFilter(id) {
        var items = []
        if(id == 'device1')
            items = ["item2"];
        else if(id == 'device2')
            items = ["item1", "item2", "item3"];
        else
            items = ["item1", "item2"];

        return items;
    }

    function expandRequestFunction( data)
    {
        alert( data.id + " " + data.model + ' ' + data.type );
    }

    var topMapConfig = {
        swfURL 	: "images/rapidjs/component/topologyMap/TopologyMapping.swf",
        id 		: "mapDiv",
        configFunctionName : "configFunction",
        bgColor : "#eeeeee",
        dataTag : "device",
        dataKeys : { id : "id", status : "status", load : "load" },
        mapURL : "script/run/getMap",
        dataURL : "d2.xml",
        pollingInterval : 0,
        wMode : "Transparent"
    };

    var topMap = new YAHOO.rapidjs.component.TopologyMap(document.getElementById("mapDiv"),topMapConfig );


    var actionConfig = {url:'topoMap/delete?format=xml'}
    var deleteMapAction = new YAHOO.rapidjs.component.action.RequestAction(actionConfig);

    var actionGroupConfig = {url:'mapGroup/delete?format=xml'}
    var deleteMapGroupAction = new YAHOO.rapidjs.component.action.RequestAction(actionGroupConfig);

    var actionSaveMapConfig = {url:'script/run/saveMap'}
    var saveMapAction = new YAHOO.rapidjs.component.action.RequestAction(actionSaveMapConfig);

    function treeNodesUpdateDeleteConditionFunction(data)
    {
    	return data.getAttribute("isPublic") != "true" && !(data.getAttribute("nodeType") == "group" && data.getAttribute("name") == "Default");
    }
    function treeNodesCopyConditionFunction(data)
    {
    	return data.getAttribute("nodeType") == "filter";
    }

    var groupDefinitionDialogConfig = {
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
         nodeId:"id",
         contentPath:"Map",
         title:'Saved Maps',
         mouseOverCursor: 'pointer',
         columns: [
            {attributeName:'name', colLabel:'Name', width:248, sortBy:true}
         ],
        menuItems:{
            Delete : { id: 'delete', label : 'Delete',  condition : treeNodesUpdateDeleteConditionFunction },
            Update : { id: 'update', label : 'Update',  condition : treeNodesUpdateDeleteConditionFunction },
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
            var data = topMap.getMapData();
            if( data ) {
                var nodes = data["nodes"];
                var edges = data["edges"];
                dialog.show(dialog.CREATE_MODE, null, { nodes : nodes, edges : edges} );
            }
        }
    });
    tree.poll();

    deleteMapAction.events.success.subscribe(tree.poll, tree, true);

    deleteMapGroupAction.events.success.subscribe(tree.poll, tree, true);

    saveMapAction.events.success.subscribe(tree.poll, tree, true);

    tree.events["treeNodeClick"].subscribe(function(data) {
        if (data.getAttribute("nodeType") == "filter")
        {
           topMap.getMap( data.getAttribute("name") );
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
                groupDialog.dialog.form.name.value = data.getAttribute("name");
                groupDialog.dialog.form.id.value = data.getAttribute("id")
            }
       }
    }, this, true);

    var filterDefinitionDialogConfig = {
        width:"35em",
        createUrl:"script/run/createMap",
        editUrl:"script/run/editMap",
        saveUrl:"script/run/saveMap",
        updateUrl:"topoMap/update?format=xml",
        successfulyExecuted: function () {
            tree.poll()
        }
    };
    var dialog = new YAHOO.rapidjs.component.Form(document.getElementById("filterDialog"), filterDefinitionDialogConfig);
     var changePassDialogConfig = {
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