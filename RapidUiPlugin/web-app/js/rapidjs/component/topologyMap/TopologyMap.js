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

    this._initAttributes();

    this.fa = new YAHOO.widget.FlashAdapter( this.swfURL, this.body.dom.id, this.attributes );
};

YAHOO.extend(YAHOO.rapidjs.component.TopologyMap, YAHOO.rapidjs.component.PollingComponentContainer, {

    _initAttributes : function() {
        this.attributes["backgroundColor"] = this.bgColor || "#ffffff";
        this.attributes["wmode"] = this.wMode;
        this.attributes["id"] = this.id;
    },
    loadGraph : function( nodes, edges) {
        this.body.dom.getElementsByTagName("embed")[0].loadGraph(nodes, edges);
    },
    loadData : function( data) {

        this.body.dom.getElementsByTagName("embed")[0].loadData(data);
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
        else
        {
            this.handleLoadMap( response);
        }

    },

    handleLoadData : function (response) {
        var newData = new YAHOO.rapidjs.data.RapidXmlDocument(response);
        var node = newData.rootNode;
        if (node) {
            var dataNodes = node.getElementsByTagName(this.dataTag);
            var data = [];

            for (var index = 0; index < dataNodes.length; index++) {
                var deviceID = dataNodes[index].getAttribute("id");
                var deviceStatus = dataNodes[index].getAttribute("status");
                var deviceLoad = dataNodes[index].getAttribute("load");
                data.push( { id : deviceID, status : deviceStatus, load : deviceLoad } );
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
                devices.push( {
                    id : deviceID, model : deviceModel, type : deviceType, gauged : isGauged, expands : doesExpand
                })
            }

            for (var index = 0; index < edgeNodes.length; index++) {
                var edgeSource = edgeNodes[index].getAttribute("target");
                var edgeTarget = edgeNodes[index].getAttribute("source");
                edges.push( {
                    source : edgeSource, target : edgeTarget
                })
            }

            this.loadGraph(devices, edges);
        }
        this.url = this.dataURL;
        this.poll();
    },

	clearData: function(){

	},

    resize : function(width, height) {

        this.body.setHeight( height - this.header.offsetHeight);
        this.body.setWidth( width);

    },
    layout: function(){
		this.timeline.layout();
	}
});

