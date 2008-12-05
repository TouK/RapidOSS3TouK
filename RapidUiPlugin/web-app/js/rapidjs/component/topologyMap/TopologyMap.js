/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
YAHOO.namespace('rapidjs', 'rapidjs.component');
function topologyMapComponentAdapter ( params )
{
  var componentId = params["componentId"];
  var functionName = params["functionName"];
  var component = YAHOO.rapidjs.Components[componentId];
  if(functionName == "resetMap" )
  {
      component.resetMap();
  }
  else if(functionName == "expandNode" )
  {
      component.expandNode(params.data.id);
  }
  else if(functionName == "nodeClicked" )
  {
      var x = params.x;
      var y = params.y;
      var itemsClicked = params.clickedItems;
      var data = params.data;
      component.nodeClicked(x, y, data, itemsClicked);
  }
}



YAHOO.rapidjs.component.TopologyMap = function(container, config){
	YAHOO.rapidjs.component.TopologyMap.superclass.constructor.call(this,container, config);
    this.addDefaultToolbarMenuItems(config);
    this.addDefaultNodeMenuItems(config);
    this.allNodeMenuIds = [];
    this.allToolbarMenuIds = [];
    this.addNodeContent(config);

    this.changeMenuIds(config.toolbarMenuItems, this.allToolbarMenuIds);
    this.changeMenuIds(config.nodeMenuItems, this.allNodeMenuIds);
    this.nodeMenuItems = config.nodeMenuItems;
    config.toolbarMapMenuHandler = "topologyToolbarMapManuHandler";
    config.nodeMenuHandler = "topologyNodeManuHandler"
    config.defaultMapAdapterFunction = "topologyMapComponentAdapter"
    YAHOO.ext.util.Config.apply(this, config);
    this.height = config.height || 540;
    this.width = config.width || 680;
    this.wMode = config.wMode;
    this.nodeTag = config.nodeTag;
    this.dataTag = config.dataTag;
    this.dataURL = config.dataURL;
    this.expandURL = config.expandURL;
    if(!config.nodeSize)
        config.nodeSize = 60;
    this.configureTimeout(config);
    this.attributes = {};
    this.mapNodes = {};
    this.header = YAHOO.ext.DomHelper.append(this.container, {tag:'div'}, true);
    this.toolbar = new YAHOO.rapidjs.component.tool.ButtonToolBar(this.header.dom, {title: this.title});
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.LoadingTool(document.body, this));
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.SettingsTool(document.body, this));
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.ErrorTool(document.body, this));
    this.menuBarElement = YAHOO.ext.DomHelper.append(this.container, {tag:'div'}, true);
    this.menuBar = new YAHOO.widget.MenuBar(this.menuBarElement.id);
    this.menuBar.addItems(config.toolbarMenuItems);
    this.menuBar.render();
    this.menuBar.subscribe("click", this.menuClicked, this, true);
    this.layoutMenuItems = null;
    var subMenus = this.menuBar.getItems();

     for(var i=0; i < subMenus.length; i++)
    {
		if(subMenus[i].cfg.getProperty("text") == "Layout")
        {
	        this.layoutMenuItems = subMenus[i].cfg.getProperty("subMenu").getItems();
        }
    }
    this.checkLayoutMenuItem("hierarchicalLayout");


    this.body = YAHOO.ext.DomHelper.append(this.container, {tag:'div'}, true);
	this.rowHeaderMenu = new YAHOO.widget.Menu(this.id + 'menu', {position: "dynamic", autofillheight:false});
    this.rowHeaderMenu.addItems(config.nodeMenuItems)
    this.rowHeaderMenu.render(this.body.dom);
    this.rowHeaderMenu.subscribe("click", this.nodeMenuClicked, this, true);
    this.body = YAHOO.ext.DomHelper.append(this.body.dom, {tag:'div'}, true);
    this.currentMenuData= null;


    this.lastLoadMapRequestData = null;
    this.firstLoadMapRequestData = null;
    this.firstResponse = null;
    var events = {
        'mapInitialized': new YAHOO.util.CustomEvent("mapInitialized"),
        'toolbarMenuItemClicked': new YAHOO.util.CustomEvent("toolbarMenuItemClicked"),
        'nodeMenuItemClicked': new YAHOO.util.CustomEvent("nodeMenuItemClicked"),
        'nodeClicked': new YAHOO.util.CustomEvent("nodeClicked")
    }
    YAHOO.ext.util.Config.apply(this.events,events);
    config.toolbarMenuItems = [];
    config.nodeMenuItems = [];
    YAHOO.util.Event.addListener(window, "load", this.initializeFlash, this, true);
};

YAHOO.extend(YAHOO.rapidjs.component.TopologyMap, YAHOO.rapidjs.component.PollingComponentContainer, {
    configureMenuItemVisibility: function(itemsWillBeShown)
    {
        for(var i=0; i < this.allNodeMenuIds.length; i++)
        {
            var id = this.allNodeMenuIds[i];
            document.getElementById(id).style.display = "none";
        }
        if(itemsWillBeShown)
        {
            for(var i=0; i < itemsWillBeShown.length; i++)
            {
                var id = itemsWillBeShown[i];
                document.getElementById(id).style.display = "";
            }
        }
    },
    addNodeContent: function(config){
        if(config.nodeContent == null)config.nodeContent= {};
        if(config.nodeContent.images == null)config.nodeContent.images= [];
        config.nodeContent.images[config.nodeContent.images.length] = {id:"showMenu", clickable:"true", x:0, y:0, dataKey:"showMenu", images:{
            "default":"arrow_down2.png"
        }}
        config.nodeContent.images[config.nodeContent.images.length] = {id:"expand", clickable:"true", x:20, y:0, dataKey:"canExpand", images:{
            "true":"plus.png"
        }}
    },
    showNodeMenu: function(x, y, data)
    {
        var nodeGraphData = this.mapNodes[data.id];
        var visibleMenuItems = [];
        var params = {data:data};
        for(var i=0; i < this.nodeMenuItems.length; i++)
        {

            var menuItemConfig = this.nodeMenuItems[i];
            var visible = true;
            if(menuItemConfig['visible'] != null){
                visible = eval(menuItemConfig['visible']);
            }
            if(visible){
                visibleMenuItems[visibleMenuItems.length] = menuItemConfig['id'];
            }
        }
        if(nodeGraphData.expandable == "true" && nodeGraphData.expanded == "false")
        {
            visibleMenuItems[visibleMenuItems.length] = this.id+"expand";            
        }
        if(visibleMenuItems.length > 0)
        {
            this.configureMenuItemVisibility(visibleMenuItems);
            this.currentMenuData = data;
            this.rowHeaderMenu.cfg.setProperty("x",x+YAHOO.util.Dom.getX(this.container));
            this.rowHeaderMenu.cfg.setProperty("y",y+YAHOO.util.Dom.getY(this.container)+this.header.getHeight()+this.menuBarElement.getHeight());
            this.rowHeaderMenu.show();
        }
    },


    changeMenuIds: function(items, allMenuIds)
    {
        if(items == null) return;
        for(var i=0; i < items.length; i++)
        {
            if(items[i].id != null)
            {
                items[i].id = this.id + items[i].id;
                allMenuIds[allMenuIds.length] = items[i].id;
            }
            if(items[i].subMenu != null)
            {
                this.changeMenuIds(items[i].subMenu.itemdata, allMenuIds);
            }
        }

    },

    nodeClicked: function(x,y,data,clickedItems)
    {
        var showMenu = false;
        var expand = false;
        for(var i in clickedItems)
        {
            var clickedItemId = clickedItems[i];
            if(clickedItemId == "showMenu")
            {
                showMenu = true;
            }
            if(clickedItemId == "expand")
            {
                expand = true;
            }
        }
        if(showMenu == true)
        {
            this.showNodeMenu(x,y,data);
        }
        else if(expand == true)
        {
            this.expandNode(data.id);
        }
        else
        {
            this.events.nodeClicked.fireDirect(data, clickedItems);
        }
    },
    nodeMenuClicked: function(pr1, pr2)
    {
        var menuItem = pr2[1]
        var id = menuItem.id;
        if(id != null && id.length >= this.id.length && id.substring(0, this.id.length) == this.id)
        {
            var menuId = id.substring(this.id.length);;
            var componentId = this.id;
            var data = this.currentMenuData;
            if(menuId == "expand" )
            {
                this.expandNode(data.id);
            }
            else
            {
                this.events.nodeMenuItemClicked.fireDirect(menuId, data);
            }
        }
    },
    menuClicked: function(pr1, pr2)
    {
        var menuItem = pr2[1]
        var id = menuItem.id;
        if(id != null && id.length >= this.id.length && id.substring(0, this.id.length) == this.id)
        {
            var menuId = id.substring(this.id.length);
            if(menuId == "refreshTopology" )
            {
                this.refreshData();
            }
            else if(menuId == "resetMap" )
            {
                this.resetMap();
            }
            else if(menuId == "resetPosition" )
            {
                this.getFlashObject().resetPosition();
            }
            else if(menuId == "hierarchicalLayout" )
            {
                this.setLayout(0);
            }
            else if(menuId == "circularLayout" )
            {
                this.setLayout(1);
            }
            else if(menuId == "customLayout" )
            {
                this.setLayout(2);
            }
            this.events.toolbarMenuItemClicked.fireDirect(menuId);
        }
    },

    checkMenuItem: function(menuItem)
    {
        var items = menuItem.parent.getItems();
        for(var i=0; i < items.length; i++)
        {
            items[i].cfg.setProperty("checked",false);
        }
        menuItem.cfg.setProperty("checked",true);
    },
    checkLayoutMenuItem: function(menuItemId)
    {
        var menuItem = null;
        for(var i =0; i < this.layoutMenuItems.length; i++)
        {
            if(this.layoutMenuItems[i].id == this.id + menuItemId)
            {
                menuItem = this.layoutMenuItems[i];
            }
        }
        if(menuItem != null)
        this.checkMenuItem(menuItem);
    },
    addDefaultNodeMenuItems: function(config){
        if(config.nodeMenuItems == null) config.nodeMenuItems = [];
        config.nodeMenuItems[config.nodeMenuItems.length] = {id:"expand", text:"Expand"}
    },
    addDefaultToolbarMenuItems: function(config){
        if(config.toolbarMenuItems == null) config.toolbarMenuItems = [];
        var mapMenu = null;
        var viewMenu = null;
        var layoutMenu = null;
        for(var i=0; i < config.toolbarMenuItems.length; i++)
        {
            var menuConfig = config.toolbarMenuItems[i];
            if(menuConfig.text == "Map")
            {
                mapMenu = menuConfig;
            }
            else if(menuConfig.text == "View")
            {
                viewMenu = menuConfig;
            }
            else if(menuConfig.text == "Layout")
            {
                layoutMenu = menuConfig;
            }
        }
        if(mapMenu == null)
        {
            mapMenu =
            {
                "text" : "Map",
                "subMenu":{id:"mapMenu", itemdata:[]}
            }
            config.toolbarMenuItems[config.toolbarMenuItems.length] =mapMenu;

        }
        if(viewMenu == null)
        {
            viewMenu =
            {
                "text" : "View",
                "subMenu":{id:"viewMenu", itemdata:[]}
            }
            config.toolbarMenuItems[config.toolbarMenuItems.length] =viewMenu;

        }
        if(layoutMenu == null)
        {
            layoutMenu =
            {
                "text" : "Layout",
                "subMenu":{id:"layoutMenu", itemdata:[]}
            }
            config.toolbarMenuItems[config.toolbarMenuItems.length] =layoutMenu;

        }
        mapMenu.subMenu.itemdata[mapMenu.subMenu.itemdata.length] = {id:"refreshTopology", text:"Refresh Topology"};
        viewMenu.subMenu.itemdata[viewMenu.subMenu.itemdata.length] = {id:"resetMap", text:"Reset Map"};
        viewMenu.subMenu.itemdata[viewMenu.subMenu.itemdata.length] = {id:"resetPosition", text:"Reset Position"};
        layoutMenu.subMenu.itemdata[layoutMenu.subMenu.itemdata.length] = {id:"hierarchicalLayout", text:"Hierarchical Layout"};
        layoutMenu.subMenu.itemdata[layoutMenu.subMenu.itemdata.length] = {id:"circularLayout", text:"Circular Layout"};
        layoutMenu.subMenu.itemdata[layoutMenu.subMenu.itemdata.length] = {id:"customLayout", text:"Custom Layout"};

    },

    getFlashObject: function(){
        if(this.flashObject == null)
        {
            if(YAHOO.util.Event.isIE)
            {
                this.flashObject  = this.iframe.contentWindow.document.body.getElementsByTagName("object")[0];
            }
            else
            {
                this.flashObject  = this.iframe.contentDocument.body.getElementsByTagName("embed")[0];
            }
        }
        return this.flashObject;
    },
    isFlashLoaded: function()
    {
        try
        {
            if(this.getFlashObject() != null)
            {
                this.getFlashObject().getNodes();
                this.getFlashObject().initializeMap(this.config)
                this.events.mapInitialized.fireDirect();
            }
            else
            {
                this.flashTimer.delay(100);
            }
        }catch(e)
        {
            this.flashTimer.delay(100);
        }
    },
    initializeFlash: function()
    {
        var requiredMajorVersion = 9;
        var requiredMinorVersion = 0;
        var requiredRevision = 115;

        var hasProductInstall = YAHOO.rapidjs.FlashUtils.DetectFlashVer(6, 0, 65);
        var hasRequestedVersion = YAHOO.rapidjs.FlashUtils.DetectFlashVer(requiredMajorVersion, requiredMinorVersion, requiredRevision);
        var containerElement = this.body.dom;
        if (!hasProductInstall || !hasRequestedVersion)
        {
            containerElement.innerHTML = "This application requires Flash player version " + requiredMajorVersion + ". Click <a href='http://www.adobe.com/shockwave/download/download.cgi?P1_Prod_Version=ShockwaveFlash'>here</a> to download";
        }
        else
        {
            this.body.dom.innerHTML = "<iframe id=\""+this.id+"frame\" name=\""+this.id+"frame\" src=\"images/rapidjs/component/topologyMap/topologymap.gsp?configFunction="+this.configFunctionName+"\" frameborder=\"0\" width=\"%100\" height=\"%100\" style=\"margin: 0px;width:100%;height:100%\">" +
                                  "</iframe>"
            this.iframe = this.body.dom.getElementsByTagName("iframe")[0];
            this.flashTimer = new YAHOO.ext.util.DelayedTask(this.isFlashLoaded, this);
            this.flashTimer.delay(100);
        }
    },
    getLayout : function( ) {
        return this.getFlashObject().getLayout();
    },
    setLayout : function( layout) {
        this.checkMenuItem(this.layoutMenuItems[layout*1]);
        this.getFlashObject().setLayout(layout);
    },
    loadGraph : function( nodes, edges) {
        this.getFlashObject().loadGraph(nodes, edges);
    },
    loadGraphWithUserLayout : function( nodes, edges) {
        this.getFlashObject().loadUserLayout(nodes, edges);
    },
    loadData : function( data) {
        this.getFlashObject().loadData(data);
    },

    getMapData : function () {
        var nodes = this.getPropertiesString(this.getNodes(), ["id", "x", "y"]);
        var edges = this.getPropertiesString(this.getEdges(), ["source", "target"]);
        return {nodes:nodes, edges:edges};
    },

    getNodes : function () {
        return this.getFlashObject().getNodes();
    },

    getEdges : function() {
        return this.getFlashObject().getEdges();
    },

    resetMap : function()
    {
        if(this.firstResponse != null)
        {
            this.getFlashObject().resetMapView();
            var layout = this.firstResponse.responseXML.firstChild.getAttribute("layout");
            if(layout == null)
            {
                this.setLayout(0);
            }
            this.handleLoadMap(this.firstResponse);

        }
    },


    refreshData : function()
    {

        if(this.lastLoadMapRequestData != null)
        {
                var nodes = this.getPropertiesString(this.getNodes(), ["id", "expanded", "x", "y"]);
                var edges = this.getPropertiesString(this.getEdges(), ["source", "target"]);
                var params = { expandedNodeName : this.lastLoadMapRequestData.params.expandedNodeName, nodes : nodes, edges : edges };
                this.url = this.expandURL;
                this.doPostRequest(this.url, params);
        }
    },


    handleSuccess: function(response){

        if ( this.url == this.dataURL )
        {
            this.handleLoadData(response);
        }
        else 
        {
            this.handleLoadMap( response);
        }

    },
    loadMap: function(response)
    {
        this.firstResponse = null;
        this.handleLoadMap(response);
    },
    handleLoadData : function (response) {
        var newData = new YAHOO.rapidjs.data.RapidXmlDocument(response);
        var node = newData.rootNode;
        if (node) {
            var nodeXmlData = node.getElementsByTagName("node");
            var edgeXmlData = node.getElementsByTagName("edge");
            var nodes = [];
            var edges = [];


            for (var index = 0; index < nodeXmlData.length; index++) {
                var attributes = nodeXmlData[index].attributes;
                var nodeData = {};
                for(var attrName in attributes)
                {
                    nodeData[attrName] = attributes[attrName];
                }

                nodeData["canExpand"] = ""+(this.mapNodes[nodeData.id].expandable == "true" && this.mapNodes[nodeData.id].expanded == "false");
                nodeData["showMenu"] = "true";
                nodes.push( nodeData );
            }

            for (var index = 0; index < edgeXmlData.length; index++) {
                var attributes = edgeXmlData[index].attributes;
                var edgeData = {};
                for(var attrName in attributes)
                {
                    edgeData[attrName] = attributes[attrName];
                }
                edges.push( edgeData );
            }

            var data = { nodes : nodes, edges : edges };
            this.loadData(data);
        }
    },

    handleLoadMap : function( response) {
        if(this.firstResponse == null)
        {
            this.firstResponse = response;
        }
        var newData = new YAHOO.rapidjs.data.RapidXmlDocument(response);
        var node = newData.rootNode;
        this.mapNodes = {};
        if (node) {
            var nodeXmlData = node.getElementsByTagName("node");
            var edgeXmlData = node.getElementsByTagName("edge");
            var nodes = [];
            var edges = [];
            var layout = node.firstChild().getAttribute("layout");
            if(layout != null)
            {
                this.setLayout(layout*1);
            }
            for (var index = 0; index < nodeXmlData.length; index++) {
                var nodeID = nodeXmlData[index].getAttribute("id");
                var nodeModel = nodeXmlData[index].getAttribute("model");
                var nodeType = nodeXmlData[index].getAttribute("type");
                var isGauged = nodeXmlData[index].getAttribute("gauged");
                var doesExpand = nodeXmlData[index].getAttribute("expandable");
                var expanded = nodeXmlData[index].getAttribute("expanded");
                var x = nodeXmlData[index].getAttribute("x");
                var y = nodeXmlData[index].getAttribute("y");
                var node = {
                    id : nodeID, model : nodeModel, type : nodeType, gauged : isGauged, expandable : doesExpand, expanded:expanded
                };
                if( this.getLayout() == 2 && (x == null || x == "" || y == null || y == "") )
                {
                    x = 250;
                    y=250;
                }
                if(x != null && y != null)
                {
                    node["x"] = x;
                    node["y"] = y;
                }
                this.mapNodes[nodeID] = node;
                nodes.push( node);
            }

            for (var index = 0; index < edgeXmlData.length; index++) {
                var edgeSource = edgeXmlData[index].getAttribute("target");
                var edgeTarget = edgeXmlData[index].getAttribute("source");
                edges.push( {
                    source : edgeSource, target : edgeTarget
                })
            }

            if( this.getLayout() != 2)
            {
                this.loadGraph(nodes, edges);
            }
            else
            {
                this.loadGraphWithUserLayout(nodes, edges);
            }
        }

        this.url = this.dataURL;
        this.getData();
    },

	clearData: function(){

	},

    resize : function(width, height) {

        this.body.setHeight( height - this.header.getHeight() - this.menuBarElement.getHeight()-3);
        this.body.setWidth( width);

    },

    loadMapForNode : function( nodeName)
    {
        this.firstResponse = null;
        var params =  { expandedNodeName : nodeName, nodes : nodeName+",true,250,250;"};
        this.url = this.expandURL;
        this.lastLoadMapRequestData = {isMap:false, params:params}
        this.firstLoadMapRequestData = {isMap:false, params:params};
        this.doPostRequest(this.url, params);

    },


    getPropertiesString: function(edges, propertyList)
    {
        var propList = [];
        for(var i=0; i < edges.length; i++)
        {
            var props =[];
            for(var j=0; j < propertyList.length; j++)
            {
                props[j] =  edges[i][propertyList[j]];
            }
            propList[propList.length] = props.join(",") ;
        }
        return propList.join(";");
    },

    getNodesString: function(){
        return this.getPropertiesString(this.getNodes(), ["id", "x", "y", "expanded", "expandable"]);
    },

    expandNode : function( expandedNodeName)
    {
        var data = this.getMapData();
        if( data ) {
            var nodes = this.getPropertiesString(this.getNodes(), ["id", "expanded", "x", "y"]);
            var edges = this.getPropertiesString(this.getEdges(), ["source", "target"]);
            var params = { expandedNodeName : expandedNodeName, nodes : nodes, edges : edges };
            this.lastLoadMapRequestData = {isMap:false, params:params}
            this.url = this.expandURL;
            this.doPostRequest(this.url, params);
       }
    },

    getData : function()
    {
       var nodes = this.getPropertiesString(this.getNodes(), ["id"]);
       var edges = this.getPropertiesString(this.getEdges(), ["source", "target"]);
        var params = {  nodes : nodes, edges : edges };
        this.url = this.dataURL;
        this.doPostRequest(this.url, params);
    },
    poll : function()
    {
        this.getData();
    }

});


