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
YAHOO.rapidjs.component.windows.TreeWindow = function(container, config){
	YAHOO.rapidjs.component.windows.TreeWindow.superclass.constructor.call(this,container, config);
	this.nodeId = config.nodeId;
	this.rootTag = config.rootTag;
	this.tree = new YAHOO.rapidjs.component.tree.TreeGrid(this.container, config);
	this.tree.render();
	this.panel = new YAHOO.rapidjs.component.windows.TreePanel(this.tree, {title: this.title, fitToFrame:true});
	this.subscribeToPanel();
	this.tree.events['selectionchanged'].subscribe(this.selectionChanged, this, true);
	this.tree.events['contextmenuclicked'].subscribe(this.handleContextMenu, this, true);
};
YAHOO.extendX(YAHOO.rapidjs.component.windows.TreeWindow, YAHOO.rapidjs.component.PollingComponentContainer, {
	
	selectionChanged: function(treeNode)
	{
		this.sendOutputs(treeNode.xmlData);
	},
	
	processData : function(response, keepExisting, removeAttribute){
		var data = new YAHOO.rapidjs.data.RapidXmlDocument(response,[this.nodeId]);
		var node = this.getRootNode(data, response.responseText);
		if(node){
			if(!this.rootNode || keepExisting == false){
				this.rootNode = node;
				this.tree.handleData(this.rootNode);
			}
			else
			{
				this.tree.isSortingDisabled = true;
				this.rootNode.mergeData(node, this.nodeId, keepExisting, removeAttribute);
				this.tree.refreshData();
				this.tree.isSortingDisabled = false;
			}
		}
	}, 
 
	
	clearData: function(){
		this.tree.clear();
	}
});

YAHOO.rapidjs.component.windows.TreePanel = function(tree, config){
	this.tree = tree;
	YAHOO.rapidjs.component.windows.TreePanel.superclass.constructor.call(this, this.tree.container, config);
};
YAHOO.extendX(YAHOO.rapidjs.component.windows.TreePanel , YAHOO.rapidjs.component.layout.RapidPanel, {
	setSize: function(width, height)
	{
		this.tree.resize(width, height);
		YAHOO.rapidjs.component.windows.TreePanel.superclass.setSize.call(this, width, height)
	}
});
