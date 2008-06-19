YAHOO.rapidjs.component.windows.TimelineWindow = function(container, config){
	YAHOO.rapidjs.component.windows.TimelineWindow.superclass.constructor.call(this,container, config);
	this.tooltipAction = config.tooltipAction;
	this.eventSource = new Timeline.DefaultEventSource();
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
	this.panel = new YAHOO.rapidjs.component.layout.TimelinePanel(this.container, {title: this.title, fitToFrame:true});
	this.subscribeToPanel();
};
YAHOO.extendX(YAHOO.rapidjs.component.windows.TimelineWindow, YAHOO.rapidjs.component.PollingComponentContainer, { 
	processData: function(response){
		this.clearData();
		this.eventSource.loadXML(response.responseXML, this.url);
	}, 
	clearData: function(){
		this.eventSource.clear();
	}, 
	
	bubbleClicked: function(bubble){
		if(this.tooltipAction){
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
			this.tooltipAction.execute(this, null, data, node);
		}
	}, 
	
	subscribeToPanel : function(){
		YAHOO.rapidjs.component.windows.TimelineWindow.superclass.subscribeToPanel.call(this);
		this.panel.layoutEvent.subscribe(this.panelSizeSet, this, true);
	}, 
	
	panelSizeSet: function(){
		this.timeline = Timeline.create(this.container, this.bandInfos);
		this.panel.setTimeline(this.timeline);
		for(var index=0; index<this.bandInfos.length; index++) {
			var bandInfo = this.bandInfos[index];
			bandInfo.eventPainter.bubbleClickedEvent.subscribe(this.bubbleClicked, this, true);
		}
	}
});

YAHOO.rapidjs.component.layout.TimelinePanel = function(container, config){
	this.layoutTask = new YAHOO.ext.util.DelayedTask(this.layout, this);
	this.layoutEvent = new YAHOO.util.CustomEvent('layout');
	YAHOO.rapidjs.component.layout.TimelinePanel.superclass.constructor.call(this, container, config);
};
YAHOO.extendX(YAHOO.rapidjs.component.layout.TimelinePanel , YAHOO.rapidjs.component.layout.RapidPanel, {
	setSize: function(width, height)
	{
		YAHOO.rapidjs.component.layout.TimelinePanel.superclass.setSize.call(this, width, height);
		if(this.timeline){
			this.layoutTask.delay(200);	
		}
		else{
			this.layoutEvent.fireDirect();
		}
	}, 
	
	layout: function(){
		this.timeline.layout();
	}, 
	
	setTimeline : function(timeline){
		this.timeline = timeline;
	}
});
