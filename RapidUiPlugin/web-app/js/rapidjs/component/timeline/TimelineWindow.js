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
YAHOO.rapidjs.component.TimelineWindow = function(container, config){
	YAHOO.rapidjs.component.TimelineWindow.superclass.constructor.call(this,container, config);

    var events = {
        'tooltipClick' : new YAHOO.util.CustomEvent('tooltipClick')
    };

    this.eventSource = new Timeline.DefaultEventSource();
    YAHOO.ext.util.Config.apply(this.events, events);
    this.header = YAHOO.ext.DomHelper.append(this.container, {tag:'div'});
    this.toolbar = new YAHOO.rapidjs.component.tool.ButtonToolBar(this.header, {title:this.title});
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

YAHOO.extend(YAHOO.rapidjs.component.TimelineWindow, YAHOO.rapidjs.component.PollingComponentContainer, {
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
	    this.body.setHeight( height - this.header.offsetHeight);
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
