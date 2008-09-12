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
        <table width="100%">
        <table width="100%">
        <tr><td width="50%"><label>Group Name:</label></td><td width="50%"><select name="groupName" style="width:175px"/></td></tr>
        <tr><td width="50%"><label>Map Name:</label></td><td width="50%"><input type="textbox" name="mapName" style="width:175px"/></td></tr>
        </table>
        <input type="hidden" name="nodes">
        <input type="hidden" name="edges">
        <input type="hidden" name="id">
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
        <td width="0%" style="padding-left:10px;padding-top:5px;padding-right:60px;">
            <img src="images/RapidInsight-blue.png">
        </td>
        <td width="100%" style="vertical-align: bottom;;">
      <div class="yui-navset">
      <ul class="yui-nav" style="border-style: none">
          <li><a href="${createLinkTo(file: 'index.gsp')}"><em>Topology</em></a></li>
          <li><a href="${createLinkTo(file: 'notify.gsp')}"><em>Notifications</em></a></li>
                <li class="selected"><a href="${createLinkTo(file: 'topology.gsp')}"><em>Map</em></a></li>
            </ul>
      </div>
  </td>
        <td width="0%"></td>
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
    </tbody></table>
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


    function configFunction()
    {
        var config = new Object();
        config.id = "mapDiv";
        config.icons = {
          "Host":{ "url":"images/rapidjs/component/topologyMap/server_icon.png"},
          "Router":{ "url":"images/rapidjs/component/topologyMap/router_icon.png"},
          "Switch":{ "url":"images/rapidjs/component/topologyMap/switch_icon.png"}
        };
        config.infoFunction = "showInfo";
        config.menuItems = { "item1": { "text": "Browse" }//,
                             //"item2": { "text": "Item text 2" },
                             //"item3": { "text": "Item text 3" }
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
        config.menuItemClickedFunction = "menuItemClickedFunction"
        config.saveMapFunction = "saveMapFunction";
        config.statusColors = { "1" : 0xde2c26, "2" : 0x7b4a1a, "3": 0xfae500, "4" : 0x20b4e0, "5":0x0d4702, "default" : 0x0d4702 };
		config.edgeColors = { "1" : 0xffde2c26,"2" :  0xfff79229,"3":  0xfffae500, "4" :  0xff20b4e0,"5": 0xff62b446, "default" : 0xff62b446 };
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

      function menuItemClickedFunction( data )
      {
          var id = data["id"];
          if( id == "item1" )
          {
            var url = "getObjectDetails.gsp?name="+data["deviceID"];
            objectDetailsDialog.show(url, "Details of " + data["deviceType"] + " " + data["deviceID"]);
          }

      }

      function topologyMapComponentAdapter ( params )
      {
          var id = params["id"];
          var functionName = params["functionName"];

          var component = YAHOO.rapidjs.Components[id];

          if( functionName == "mapContentReady" )
          {
            var deviceName = getURLParam( "name");
            if( deviceName )
            {
                topMap.getInitialMap( deviceName);
            }
          }
          else if(functionName == "refreshTopology" )
          {
              component.loadHandler();
          }
      }

    function menuItemFilter(id) {
        var items = [];
        /*
        if(id == 'device1')
            items = ["item2"];
        else if(id == 'device2')
            items = ["item1", "item2", "item3"];
        else
            items = ["item1", "item2"];
        */

        items = ["item1"];

        return items;
    }

    function expandRequestFunction( data)
    {
        topMap.expandMap( data.id );
    }

    var topMapConfig = {
        swfURL 	: "images/rapidjs/component/topologyMap/TopologyMapping.swf",
        id 		: "mapDiv",
        configFunctionName : "configFunction",
        bgColor : "#eeeeee",
        dataTag : "device",
        dataKeys : { id : "id", status : "status", load : "load" },
        mapURL : "script/run/getMap",
        expandURL : "script/run/expandMap",
        initialMapURL : "script/run/getDeviceTopoMap",
        dataURL : "script/run/getMapData",
        pollingInterval : 0,
        wMode : "Transparent"
    };

    var topMap = new YAHOO.rapidjs.component.TopologyMap(document.getElementById("mapDiv"),topMapConfig );

    var conf = {id:'objectDetails', width:500, height:400, iframe:false};
    var objectDetailsDialog = new YAHOO.rapidjs.component.Html(conf);
    objectDetailsDialog.hide();
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
         nodeId:"id",
         contentPath:"Map",
         title:'Saved Maps',
         mouseOverCursor: 'pointer',
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
        tooltip: 'Add query',
        click:function() {
            var data = topMap.getMapData();
            if( data ) {
                var nodes = data["nodes"];
                var edges = data["edges"];
                var postData = { nodes : nodes, edges : edges };
                dialog.show(dialog.CREATE_MODE, null, postData );
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
            else if (data.getAttribute("noinitialdeType") == "group")
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