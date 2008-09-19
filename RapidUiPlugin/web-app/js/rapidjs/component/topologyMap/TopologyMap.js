YAHOO.namespace('rapidjs', 'rapidjs.component');
function topologyMapComponentAdapter ( params )
{
  var id = params["id"];
  var functionName = params["functionName"];

  var component = YAHOO.rapidjs.Components[id];
  if(functionName == "refreshTopology" )
  {
      component.refresh();
  }
  else if(functionName == "resetMap" )
  {
      component.resetMap();
  }
  else if(functionName == "expandNode" )
  {
      component.expandNode(params.data.id);
  }
}
YAHOO.rapidjs.component.TopologyMap = function(container, config){
	YAHOO.rapidjs.component.TopologyMap.superclass.constructor.call(this,container, config);
	YAHOO.ext.util.Config.apply(this, config);

    this.configFunctionName = config.configFunctionName;

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
    YAHOO.util.Event.addListener(window, "load", this.initializeFlash, this, true);
};

YAHOO.extend(YAHOO.rapidjs.component.TopologyMap, YAHOO.rapidjs.component.PollingComponentContainer, {

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
        this.body.dom.innerHTML = "<iframe src=\"images/rapidjs/component/topologyMap/topologymap.gsp?configFunction="+this.configFunctionName+"\" frameborder=\"0\" width=\"%100\" height=\"%100\" style=\"margin: 0px;width:100%;height:100%\">" +
                              "</iframe>"
        this.iframe = this.body.dom.getElementsByTagName("iframe")[0];
        this.flashTimer = new YAHOO.ext.util.DelayedTask(this.isFlashLoaded, this);
        this.flashTimer.delay(100);
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
                var doesExpand = nodeXmlData[index].getAttribute("expands");
                var x = nodeXmlData[index].getAttribute("x");
                var y = nodeXmlData[index].getAttribute("y");
                var node = {
                    id : nodeID, model : nodeModel, type : nodeType, gauged : isGauged, expands : doesExpand
                };
                if( x && y)
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

            if( this.url == this.expandURL)
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

    loadMap : function( mapName)
    {
        var params = { mapName : mapName};
        this.url = this.mapURL;
        this.lastLoadMapRequestData = {isMap:true, params:params}
        this.firstLoadMapRequestData = {isMap:true, params:params};
        this.doRequest(this.url, this.params);
    },
    
    loadMapForNode : function( nodeName)
    {
        var params =  { expandedNodeName : nodeName, nodes : nodeName, edges : "" };
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
            var nodes = this.getPropertiesString(this.getNodes(), ["id"]);
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

