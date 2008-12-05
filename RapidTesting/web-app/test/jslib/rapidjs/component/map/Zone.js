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
YAHOO.rapidjs.component.map.Zone = function(xmlData, container, parentZone, config,index){
	YAHOO.rapidjs.component.map.Zone.superclass.constructor.call(this, xmlData);
	this.container = container;
	this.config = config;
	this.parentZone = parentZone;
	this.index = index;
	this.idSeed = 0;
	this.rows = {};
	this.events = {
		'zoneclicked': new YAHOO.util.CustomEvent('zoneclicked'), 
		'contextmenu': new YAHOO.util.CustomEvent('contextmenu')
	};
	this.render();
	this.refreshSize();
};

YAHOO.extendX(YAHOO.rapidjs.component.map.Zone, YAHOO.rapidjs.component.RapidElement, {
	render: function(){
		var dh = YAHOO.ext.DomHelper;
		this.wrapper = dh.append(this.container, {tag:'span', cls:'r-map-zone'}, true);
		var topPosStr = this.xmlData.getAttribute(this.config.siteTopPosition) || 0;
		var leftPosStr = this.xmlData.getAttribute(this.config.siteLeftPosition) || 0;
		var topPos = parseInt(topPosStr, 10);
		var leftPos = parseInt(leftPosStr, 10);
		YAHOO.util.Dom.setStyle(this.wrapper.dom, 'top', topPos + '%');
		YAHOO.util.Dom.setStyle(this.wrapper.dom, 'left', leftPos + '%');
		var hdWrp = dh.append(this.wrapper.dom, {tag:'span', cls:'r-map-zone-hdwrp'});
		this.header = dh.append(hdWrp, {tag:'span', cls:'r-map-zone-hd'}, true);
		this.header.dom.innerHTML = this.xmlData.getAttribute(this.config.displayAttributeName);
		this.body = dh.append(this.wrapper.dom, {tag:'span', cls:'r-map-zone-body'}, true);
		YAHOO.util.Dom.addClass(this.body.dom, 'r-map-zone-body-unselected');
		this.bodyPos = dh.append(this.body.dom, {tag:'span', cls:'r-map-zone-bpos'});
		
		YAHOO.util.Event.addListener(this.wrapper.dom, 'click', this.fireClick, this, true);
		YAHOO.util.Event.addListener(this.wrapper.dom, 'contextmenu', this.fireContextMenu, this, true);
		
        this.dd = new YAHOO.util.DD(this.wrapper.dom, 'WindowDrag');
        this.dd.setHandleElId(this.header.dom.id);
        this.dd.endDrag = this.endMove.createDelegate(this);
        
		var rows = this.xmlData.getElementsByTagName(this.config.contentPath);
		for(var index=0; index<rows.length; index++) {
			this.createRow(rows[index]);
		}
		this.setHeaderBackground();
	},
	
	refreshSize :function(){
		YAHOO.util.Dom.setStyle(this.header.dom, 'width', '');
		YAHOO.util.Dom.setStyle(this.body.dom, 'width', '');
		var maxWidth = Math.max(this.header.getWidth(), this.body.getWidth());
		maxWidth = Math.max(maxWidth, this.config.minSize);
		this.header.setWidth(maxWidth);
		this.body.setWidth(maxWidth);
	},
	
	fireClick : function(event){
		this.events['zoneclicked'].fireDirect(event, this);	
	},
	fireContextMenu : function(event){
		this.events['contextmenu'].fireDirect(event, this);	
	}, 
	
	createRow: function(xmlData){
		this.idSeed ++;
		var zoneRow = new YAHOO.rapidjs.component.map.ZoneRow(xmlData, this.bodyPos, this, this.config, this.idSeed);
		this.rows[zoneRow.index] = zoneRow;
	},
	
	childAdded: function(newChild){
		this.createRow(newChild);
	},
	
	dataDestroyed: function(){
		this.destroy();	
	},
	
	destroy: function(){
		delete this.parentZone.zones[this.index];
		this.parentZone = null;
		this.dd.unreg();
		this.dd = null;
		YAHOO.util.Event.purgeElement(this.wrapper.dom, false);
		for(var eventName in this.events) {
			var event = this.events[eventName];
			event.unsubscribeAll();
		}
		this.wrapper.remove();
		this.container = null;
	}, 
	
	
	endMove: function(){
		var cEl = getEl(this.container);
		var height = cEl.getHeight();
		var width = cEl.getWidth();
		var x = this.wrapper.getX() - cEl.getX();
		var y = this.wrapper.getY() - cEl.getY();
		
		var topPos = Math.round(y/height*100);
		var leftPos = Math.round(x/width*100);
		YAHOO.util.Dom.setStyle(this.wrapper.dom, 'top', topPos + '%');
		YAHOO.util.Dom.setStyle(this.wrapper.dom, 'left', leftPos + '%');
	}, 
	
	setHeaderBackground : function(){
		var state = this.xmlData.getAttribute('State');
		var backgroundColor;
		if(state == '1'){
			backgroundColor = '#F11B25';
		}
		else if(state == '2'){
			backgroundColor = 'orange';
		}
		else if(state == '3'){
			backgroundColor = 'yellow';
		}
		else if(state == '4'){
			backgroundColor = '#5ED5DD';
		}
		else{
			backgroundColor = '#15B304';
		}
		YAHOO.util.Dom.setStyle(this.header.dom, 'background-color', backgroundColor);
	}, 
	
	batchDataChanged : function(){
		this.setHeaderBackground();
	}
	
});

YAHOO.rapidjs.component.map.RootZone = function(xmlData, container, config){
	YAHOO.rapidjs.component.map.RootZone.superclass.constructor.call(this, xmlData, container, null, config);
	this.zones = {};
	var children = xmlData.childNodes();
	for(var index=0; index<children.length; index++) {
		var childNode = children[index];
		if(childNode.nodeName == this.config.contentPath){
			this.createZone(childNode);
		}
	}
};

YAHOO.extendX(YAHOO.rapidjs.component.map.RootZone, YAHOO.rapidjs.component.map.Zone, {
	childAdded : function(newChild){
		this.createZone(newChild);
	}, 
	
	fireZoneClicked: function(event, zone){
		this.events['zoneclicked'].fireDirect(event, zone);
	}, 
	
	fireContextMenu: function(event, zone){
		this.events['contextmenu'].fireDirect(event, zone);
	}, 
	
	render : function(){
	},
	
	refreshSize : function(){
	},
	batchDataChanged: function(){
		
	},
	
	createZone : function(xmlData){
		this.idSeed ++;
		var zone = new YAHOO.rapidjs.component.map.Zone(xmlData, this.container, this, this.config, this.idSeed);
		this.zones[zone.index] = zone;
		zone.events['zoneclicked'].subscribe(this.fireZoneClicked, this, true);
		zone.events['contextmenu'].subscribe(this.fireContextMenu, this, true);
	}, 
	
	refreshZoneSizes : function(){
		for(var zoneIndex in this.zones) {
			var zone = this.zones[zoneIndex];
			zone.refreshSize();
		}
	}
	
});