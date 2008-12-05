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
YAHOO.rapidjs.component.windows.MapWindow = function(container, config){
	YAHOO.rapidjs.component.windows.MapWindow.superclass.constructor.call(this,container, config);
	this.nodeId = config.nodeId;
	this.rootTag = config.rootTag;
	this.map = new YAHOO.rapidjs.component.map.Map(this.container, config);
	this.map.render();
	this.panel = new YAHOO.rapidjs.component.windows.MapPanel(this.map, {title: this.title, fitToFrame:true});
	this.subscribeToPanel();
	this.map.events['selectionchanged'].subscribe(this.selectionChanged, this, true);
	this.map.events['contextmenuclicked'].subscribe(this.handleContextMenu, this, true);
};
YAHOO.extendX(YAHOO.rapidjs.component.windows.MapWindow, YAHOO.rapidjs.component.PollingComponentContainer, {
	selectionChanged: function(xmlData)
	{
		this.sendOutputs(xmlData);
	},
	
	processData : function(response, keepExisting){
		var data = new YAHOO.rapidjs.data.RapidXmlDocument(response,[this.nodeId]);
		var node = this.getRootNode(data, response.responseText);
		if(node){
			if(!this.rootNode){
				this.rootNode = node;
				this.map.handleData(this.rootNode);
			}
			else
			{
				this.rootNode.mergeData(node, this.nodeId, keepExisting);
				this.map.refreshZoneSizes();
			}
		}
	} 
});

 

YAHOO.rapidjs.component.windows.MapPanel = function(map, config){
	this.map = map;
	YAHOO.rapidjs.component.windows.MapPanel.superclass.constructor.call(this, this.map.container, config);
};
YAHOO.extendX(YAHOO.rapidjs.component.windows.MapPanel , YAHOO.rapidjs.component.layout.RapidPanel, {
	setSize: function(width, height)
	{
		this.map.resize(width, height);
		YAHOO.rapidjs.component.windows.MapPanel.superclass.setSize.call(this, width, height)
	}
});