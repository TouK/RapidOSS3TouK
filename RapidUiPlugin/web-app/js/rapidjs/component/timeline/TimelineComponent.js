YAHOO.namespace('rapidjs', 'rapidjs.component');
YAHOO.rapidjs.component.TimelineComponent = function(container, config){
	YAHOO.rapidjs.component.TimelineComponent.superclass.constructor.call(this,container, config);

    var events = {
        'tooltipClick' : new YAHOO.util.CustomEvent('tooltipClick')
    };

    this.container = container;
    this.eventSource = new Timeline.DefaultEventSource();
    this.url = config.url;
    YAHOO.ext.util.Config.apply(this.events, events);
    this.header = YAHOO.ext.DomHelper.append(this.container, {tag:'div'});
    this.toolbar = new YAHOO.rapidjs.component.tool.ButtonToolBar(this.header, {title:"Event History"});
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.LoadingTool(document.body, this));
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.SettingsTool(document.body, this));
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.ErrorTool(document.body, this));
    this.body = YAHOO.ext.DomHelper.append(this.container, {tag:'div'}, true);
    this.layoutTask = new YAHOO.ext.util.DelayedTask(this.layout, this);

	var bandsConfig = config.Bands;
	var bandInfos = [];
	for(var index=0; index<bandsConfig.length; index++) {
		var bandConfig = bandsConfig[index];
		var newConfig = {eventSource:	this.eventSource,
			width:			bandConfig.width,
			intervalUnit:	bandConfig.intervalUnit,
			intervalPixels: bandConfig.intervalPixels
		};
		if(bandConfig.showText != null){
			newConfig['showEventText'] = bandConfig.showText;
		}
		if(bandConfig.date != null){
			newConfig['date']= bandConfig['date'];
		}
		if(bandConfig.trackHeight != null){
			newConfig['trackHeight'] = bandConfig['trackHeight'];
		}
		if(bandConfig.trackGap != null){
			newConfig['trackGap'] = bandConfig['trackGap'];
		}
		var bandInfo = Timeline.createBandInfo(newConfig);
		if(bandConfig.syncWith != null){
			bandInfo.syncWith = bandConfig.syncWith;
		}
		if(bandConfig.highlight){
			bandInfo.highlight = bandConfig.highlight;
		}
		bandInfo.eventPainter._theme.event.label.width = bandConfig.textWidth || 200;
		bandInfos.push(bandInfo);
	}
	for(var index=0; index<bandsConfig.length; index++) {
		var bandConfig = bandsConfig[index];
		if(bandConfig.layoutWith != null){
			bandInfos[index].eventPainter.setLayout(bandInfos[bandConfig.layoutWith].eventPainter.getLayout());
		}
	}
	this.bandInfos = bandInfos;

};

YAHOO.extend(YAHOO.rapidjs.component.TimelineComponent, YAHOO.rapidjs.component.PollingComponentContainer, {
	handleSuccess: function(response){
		this.clearData();
		this.eventSource.loadXML(response.responseXML, this.url);
	},
	bubbleClicked: function( bubble )
	{
		var node = bubble.node;
		var data = {};
		var attributeNodes = node.attributes;
	 	if(attributeNodes != null)
	 	{
	 		var nOfAtts = attributeNodes.length
		 	for(var index=0; index <nOfAtts ; index++) {
		 		var attNode = attributeNodes.item(index);
		 		data[attNode.nodeName] = attNode.nodeValue;
		 	}
	 	}
	 	bubble.close();
	 	this.fireTooltipClick( bubble, data );
	},
	clearData: function(){
		this.eventSource.clear();
	},
	fireTooltipClick : function(bubble, data){
        this.events['tooltipClick'].fireDirect(bubble, data);
    },
    resize : function(width, height) {
	    this.body.setStyle("height", height - this.header.offsetHeight);
	    if( !this.timeline ){

		    this.timeline = Timeline.create(this.body.dom, this.bandInfos);
		    for(var index=0; index<this.bandInfos.length; index++) {
					var bandInfo = this.bandInfos[index];
					bandInfo.eventPainter.bubbleClickedEvent.subscribe(this.bubbleClicked, this, true);
			}
		}
		else
		{
			this.layoutTask.delay(200);
		}

    },
    layout: function(){
		this.timeline.layout();
	}

});
