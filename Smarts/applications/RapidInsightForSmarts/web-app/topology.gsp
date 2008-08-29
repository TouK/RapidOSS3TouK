<html>
<head>
	<title>iFountain - RapidInsight for Smarts</title>
    <script type="text/javascript" src="js/yui/utilities/utilities.js"></script>
    <script type="text/javascript" src="js/yui/resize/resize-beta-min.js"></script>
    <script type="text/javascript" src="js/yui/layout/layout-beta-min.js"></script>
    <script type="text/javascript" src="js/ext/ext.js"></script>
    <script type="text/javascript" src="js/yui/charts/charts-experimental-min.js"></script>

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
    <script type="text/javascript" src="js/rapidjs/component/topologyMap/TopologyMap.js"></script>

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

	<script type="text/javascript" src="js/yui/dom/dom.js"></script>
	<script type="text/javascript" src="flasha.js"></script>


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
<body class=" yui-skin-sam admin">

<div id="left">
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
		        <li class="selected"><a href="${createLinkTo(file: 'index.gsp')}"><em>Devices</em></a></li>
		        <li><a href="${createLinkTo(file: 'notify.gsp')}"><em>Notifications</em></a></li>

		    </ul>
		    </div>
		</td>
	</tr>
    </tbody></table>
</div>
<div id="right">
	<div id="myTopMap"> </div>
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
	function configFunction()
	  {
	  	var config = new Object();
	  	config.id = "topMap";
	  	config.mapRefreshTime = 16000;
	  	config.dataRefreshTime = 8000;
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
									},
									{
										"id" : "customMenuItem2",
										"label" : "Menu2",
										"submenuItems"  : [
															  {
																"id" 	: "submenuItem1",
																"submenuItem" : {
																					"label"	: "Submenu 1",
																					"type"	: "radio",
																					"groupName" : "menu2",
																					"toggled"	: "true"
																				}
															  },
															  {
																"id" 	: "submenuItem2",
																"submenuItem" : {
																					"label"	: "Submenu 2",
																					"type"	: "radio",
																					"groupName" : "menu2"
																				}
															  }
														  ]
									},
									{
										"id" : "customMenuItem3",
										"label" : "Menu3",
										"submenuItems"  : [
															  {
																"id" 	: "submenuItem3",
																"submenuItem" : {
																					"label"	: "Submenu 1"
																				}
															  },
															  {
																"id" 	: "submenuItem4",
																"submenuItem" : {
																					"label"	: "Submenu 2"
																				}
															  }
														   ]
									}
							  ];

		config.contentReadyFunction = "contentReady";
		config.toolbarMenuFunction = "toolbarMenuFcn";
		config.nodeMenuFunction = "nodeMenuFcn";
	  	return config;
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

		  	if( functionName == "mapContentReady" || functionName == "refreshTopology" )
		  		component.loadHandler();


	  	}

		function menuItemFilter(id) {
			var items = []
			if(id == 'device1')
				items = ["item1"];
			else if(id == 'device2')
				items = ["item1", "item2", "item3"];
			//else
				//items = [1, 2];

			return items;
		}

		function expandRequestFunction( data)
		{
			alert( data.id + " " + data.model + ' ' + data.type );
		}

		var topMapConfig = {
	        swfURL 	: "images/rapidjs/component/topologyMap/TopologyMapping.swf",
	        id 		: "topMap",
	        configFunctionName : "configFunction",
	        bgColor : "#eeeeee",
	        url : "topData.xml",
	        dataTag : "device",
	        dataKeys : { id : "id", status : "status", load : "load" },
	        mapURL : "topData.xml",
	        dataURL : "topData2.xml",
	        pollingInterval : 5
        };

        var topMap = new YAHOO.rapidjs.component.TopologyMap(document.getElementById("myTopMap"),topMapConfig );




    var Dom = YAHOO.util.Dom, Event = YAHOO.util.Event;

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

        layout.on('resize', function() {
        });
        window.layout = layout;

    })
</script>

</body>
</html>