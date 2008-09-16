YAHOO.namespace('rapidjs', 'rapidjs.component');
YAHOO.rapidjs.component.TopologyMap = function(container, config){
	YAHOO.rapidjs.component.TopologyMap.superclass.constructor.call(this,container, config);
	YAHOO.ext.util.Config.apply(this, config);

    this.configFunctionName = config.configFunctionName;
    this.swfURL = config.swfURL + "?configFunction=" + this.configFunctionName;

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
    this.params = {};

    this.configureTimeout(config);
    this.attributes = {};
    var divs = this.container.getElementsByTagName("div");
    this.header = divs[0];
    this.body = YAHOO.ext.Element.get(divs[1]);
    this.body.setHeight(this.height);
	this.body.setWidth(this.width);
    this.toolbar = new YAHOO.rapidjs.component.tool.ButtonToolBar(this.header, {title: this.title});
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.LoadingTool(document.body, this));
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.SettingsTool(document.body, this));
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.ErrorTool(document.body, this));


    this._initAttributes();

//    this.fa = new YAHOO.widget.FlashAdapter( this.swfURL, this.body.dom.id, this.attributes );
    if(YAHOO.util.Event.isIE)
    {
        this.flashObject  = this.body.dom.getElementsByTagName("object")[0];
    }
    else
    {
        this.flashObject  = this.body.dom.getElementsByTagName("embed")[0];    
    }
};

YAHOO.extend(YAHOO.rapidjs.component.TopologyMap, YAHOO.rapidjs.component.PollingComponentContainer, {

    _initAttributes : function() {
//        this.attributes["backgroundColor"] = this.bgColor || "#ffffff";
//        this.attributes["wmode"] = this.wMode;
//        this.attributes["id"] = this.id;
    },
    loadGraph : function( nodes, edges) {
        this.flashObject.loadGraph(nodes, edges);
    },
    loadGraphWithUserLayout : function( nodes, edges) {
        this.flashObject.loadUserLayout(nodes, edges);
    },
    loadData : function( data) {
        this.flashObject.loadData(data);
    },

    getMapData : function () {
        return this.flashObject.getMapData();
    },

    getDevices : function () {
        return this.flashObject.getDevices();
    },

    getEdges : function() {
        return this.flashObject.getEdges();
    },

    loadHandler : function()
    {
        this.url = this.mapURL;
        this.poll();
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
            var deviceNodes = node.getElementsByTagName("device");
            var edgeNodes = node.getElementsByTagName("edge");
            var devices = [];
            var edges = [];


            for (var index = 0; index < deviceNodes.length; index++) {
                var deviceID = deviceNodes[index].getAttribute("id");
                var deviceStatus = deviceNodes[index].getAttribute("state");
                var load = deviceNodes[index].getAttribute("load");
                devices.push( { id : deviceID, state : deviceStatus, load: load } );
            }

            for (var index = 0; index < edgeNodes.length; index++) {
                var source = edgeNodes[index].getAttribute("source");
                var target = edgeNodes[index].getAttribute("target");
                var edgeStatus =  edgeNodes[index].getAttribute("state");

                edges.push( { source : source, target : target, state : edgeStatus} );
            }

            /*
            for (var index = 0; index < dataNodes.length; index++) {
                var newDataObject = new Object();
                for( var item in this.dataKeys)
                {
                    newDataObject[item] = dataNodes[index].getAttribute(item);
                }
                data.push( newDataObject);
            }
             */

            var data = { devices : devices, edges : edges };
            this.loadData(data);
        }
    },

    handleLoadMap : function( response) {
        var newData = new YAHOO.rapidjs.data.RapidXmlDocument(response);
        var node = newData.rootNode;
        if (node) {
            var deviceNodes = node.getElementsByTagName("device");
            var edgeNodes = node.getElementsByTagName("edge");
            var devices = [];
            var edges = [];

            for (var index = 0; index < deviceNodes.length; index++) {
                var deviceID = deviceNodes[index].getAttribute("id");
                var deviceModel = deviceNodes[index].getAttribute("model");
                var deviceType = deviceNodes[index].getAttribute("type");
                var isGauged = deviceNodes[index].getAttribute("gauged");
                var doesExpand = deviceNodes[index].getAttribute("expands");
                var x = deviceNodes[index].getAttribute("x");
                var y = deviceNodes[index].getAttribute("y");
                var device = {
                    id : deviceID, model : deviceModel, type : deviceType, gauged : isGauged, expands : doesExpand
                };
                if( x && y)
                {
                    device["x"] = x;
                    device["y"] = y;
                }

                devices.push( device);
            }

            for (var index = 0; index < edgeNodes.length; index++) {
                var edgeSource = edgeNodes[index].getAttribute("target");
                var edgeTarget = edgeNodes[index].getAttribute("source");
                edges.push( {
                    source : edgeSource, target : edgeTarget
                })
            }

            if( this.url == this.expandURL)
            {
                this.loadGraph(devices, edges);
            }
            else
            {
                this.loadGraphWithUserLayout(devices, edges);
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
    layout: function(){
		this.timeline.layout();
	},

    getMap : function( mapName)
    {
        this.params = { mapName : mapName};
        this.url = this.mapURL;
        this.poll();
    },

    getInitialMap : function( deviceName)
    {
        this.params =  { expandedDeviceName : deviceName, nodes : deviceName, edges : "" };
        this.url = this.expandURL;
        this.doPostRequest(this.url, this.params);
        this.params = {};
    },
    expandMap : function( expandedDeviceName)
    {
        var data = this.getMapData();
        if( data ) {
            var nodes = data["nodes"];
            var edges = data["edges"];
            this.params = { expandedDeviceName : expandedDeviceName, nodes : nodes, edges : edges };
            this.url = this.expandURL;
            this.doPostRequest(this.url, this.params);
       }
    },

    getData : function()
    {
        var devices = this.getDevices();
        var edges = this.getEdges();
        this.params = { devices : devices, edges : edges };
        this.url = this.dataURL;
        this.doPostRequest(this.url, this.params);
    },
    poll : function()
    {
        if( this.url == this.mapURL)
        {
            this.doRequest(this.url, this.params);
        }
        else
        {
            this.getData();
        }
    }
});

