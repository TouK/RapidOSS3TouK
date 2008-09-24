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
}

function topologyToolbarMapManuHandler ( params )
{
    var componentId = params["componentId"];
    var menuId = params["menuId"];
    var component = YAHOO.rapidjs.Components[componentId];
    if(menuId == "refreshTopology" )
    {
        component.refresh();
    }
    component.events.toolbarMenuItemClicked.fireDirect(params);
}

function topologyNodeManuHandler ( params )
{
    var componentId = params["componentId"];
    var menuId = params["menuId"];
    var data = params.data;
    var component = YAHOO.rapidjs.Components[componentId];
    component.events.nodeMenuItemClicked.fireDirect(params);
}
YAHOO.rapidjs.component.TopologyMap = function(container, config){
	YAHOO.rapidjs.component.TopologyMap.superclass.constructor.call(this,container, config);
    this.addDefaultToolbarMenuItems(config);
    config.toolbarMapMenuHandler = "topologyToolbarMapManuHandler";
    config.nodeMenuHandler = "topologyNodeManuHandler"
    config.defaultMapAdapterFunction = "topologyMapComponentAdapter"
    YAHOO.ext.util.Config.apply(this, config);
    this.height = config.height || 540;
    this.width = config.width || 680;
    this.wMode = config.wMode;
    this.bgColor = config.bgColor;
    this.nodeTag = config.nodeTag;
    this.dataTag = config.dataTag;
    this.dataKeys = config.dataKeys;
    this.id = config.id || YAHOO.util.Dom.generateId(null, "yuigen");
    this.mapURL = config.mapURL;
    this.dataURL = config.dataURL;
    this.initialMapURL = config.initialMapURL;
    this.expandURL = config.expandURL;

    this.configureTimeout(config);
    this.attributes = {};

    this.header = YAHOO.ext.DomHelper.append(this.container, {tag:'div'});
    this.toolbar = new YAHOO.rapidjs.component.tool.ButtonToolBar(this.header, {title: this.title});
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.LoadingTool(document.body, this));
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.SettingsTool(document.body, this));
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.ErrorTool(document.body, this));
    this.body = YAHOO.ext.DomHelper.append(this.container, {tag:'div'}, true);
    this.body.setHeight(this.height);
	this.body.setWidth(this.width);
    this.body = YAHOO.ext.DomHelper.append(this.body.dom, {tag:'div'}, true);
    this.lastLoadMapRequestData = null;
    this.firstLoadMapRequestData = null;
    this.events.mapInitialized = new YAHOO.util.CustomEvent("mapInitialized");
    this.events.toolbarMenuItemClicked = new YAHOO.util.CustomEvent("toolbarMenuItemClicked");
    this.events.nodeMenuItemClicked = new YAHOO.util.CustomEvent("nodeMenuItemClicked");
    YAHOO.util.Event.addListener(window, "load", this.initializeFlash, this, true);
};

YAHOO.extend(YAHOO.rapidjs.component.TopologyMap, YAHOO.rapidjs.component.PollingComponentContainer, {
    addDefaultToolbarMenuItems: function(config){
        if(config.toolbarMenuItems == null) config.toolbarMenuItems = [];
        var mapMenu = null;
        for(var i=0; i < config.toolbarMenuItems.length; i++)
        {
            var menuConfig = config.toolbarMenuItems[i];
            if(menuConfig.id == "mapMenu")
            {
                mapMenu = menuConfig;
                break;
            }
        }
        if(mapMenu == null)
        {
            mapMenu =
            {
                "id" : "mapMenu",
                "label" : "Map",
                "submenuItems":[]
            }
            config.toolbarMenuItems[config.toolbarMenuItems.length] =mapMenu;

        }
        var newMapSubMenus = [
                                  {
                                    "id" 	: "refreshTopology",
                                    "submenuItem" : {
                                                        "label"	: "Refresh Topology",
                                                        "groupName" : "mapMenu",
                                                        "toggled"	: "true"
                                                    }
                                  }
                              ]
        mapMenu.submenuItems = mapMenu.submenuItems.concat(newMapSubMenus);
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
            this.body.dom.innerHTML = "<iframe src=\"images/rapidjs/component/topologyMap/topologymap.gsp?configFunction="+this.configFunctionName+"\" frameborder=\"0\" width=\"%100\" height=\"%100\" style=\"margin: 0px;width:100%;height:100%\">" +
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
        if(this.firstLoadMapRequestData != null)
        {
            if(this.firstLoadMapRequestData.isMap)
            {
                this.url = this.mapURL;
                this.doRequest(this.url, this.firstLoadMapRequestData.params);
            }
            else
            {
                this.url = this.expandURL;
                this.doPostRequest(this.url, this.firstLoadMapRequestData.params);
            }

        }
    },

    refresh : function()
    {
        if(this.lastLoadMapRequestData != null)
        {
            if(this.lastLoadMapRequestData.isMap)
            {
                this.url = this.mapURL;
                this.doRequest(this.url, this.lastLoadMapRequestData.params);
            }
            else
            {
                this.url = this.expandURL;
                this.doPostRequest(this.url, this.lastLoadMapRequestData.params);
            }

        }
    },


    handleSuccess: function(response){

        if ( this.url == this.dataURL )
        {
            this.handleLoadData(response);
        }
        else // mapURL || initialMapURL || expandURL
        {
            this.handleLoadMap( response);
        }

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
                var nodeID = nodeXmlData[index].getAttribute("id");
                var nodeStatus = nodeXmlData[index].getAttribute("state");
                var load = nodeXmlData[index].getAttribute("load");
                nodes.push( { id : nodeID, state : nodeStatus, load: load } );
            }

            for (var index = 0; index < edgeXmlData.length; index++) {
                var source = edgeXmlData[index].getAttribute("source");
                var target = edgeXmlData[index].getAttribute("target");
                var edgeStatus =  edgeXmlData[index].getAttribute("state");

                edges.push( { source : source, target : target, state : edgeStatus} );
            }

            var data = { nodes : nodes, edges : edges };
            this.loadData(data);
        }
    },

    handleLoadMap : function( response) {
        var newData = new YAHOO.rapidjs.data.RapidXmlDocument(response);
        var node = newData.rootNode;
        if (node) {
            var nodeXmlData = node.getElementsByTagName("node");
            var edgeXmlData = node.getElementsByTagName("edge");
            var nodes = [];
            var edges = [];

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

        this.body.setHeight( height - this.header.offsetHeight);
        this.body.setWidth( width);

    },
    
    loadMapForNode : function( nodeName)
    {
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

