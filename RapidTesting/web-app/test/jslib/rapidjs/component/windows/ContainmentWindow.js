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
YAHOO.rapidjs.component.windows.ContainmentWindow = function(container, config){
	YAHOO.rapidjs.component.windows.ContainmentWindow.superclass.constructor.call(this,container, config);
	this.gridRowTag = 'GridRowTag';
	this.gridFields = ['Name', 'Value'];
	YAHOO.ext.util.Config.apply(this, config, {"treeWidth":250});
	this.configureTimeout(config);
	var treeContainer = YAHOO.ext.DomHelper.append(document.body, {tag:'div'});
	treeContainer.id = YAHOO.util.Dom.generateId(treeContainer);
	var gridContainer = YAHOO.ext.DomHelper.append(document.body, {tag:'div'});
	gridContainer.id = YAHOO.util.Dom.generateId(gridContainer);
	this.tree = new YAHOO.rapidjs.component.tree.TreeGrid(treeContainer, {
					columns:[{colLabel:'', width:this.treeWidth, attributeName:this.treeLabelAttribute}], 
					contentPath:this.treeContentPath});
	this.tree.render();
	this.tree.events['selectionchanged'].subscribe(this.selectionChanged, this, true);
	var sm = new YAHOO.rapidjs.component.grid.RapidSelectionModel();
	var cm = new YAHOO.ext.grid.DefaultColumnModel([{header:'Name', width: 200}, {header:'Value', width: 200}]);
	cm.defaultSortable = true;
	var dm = new YAHOO.rapidjs.component.grid.RapidXmlDataModel({
	    tagName: this.gridRowTag,
	    fields: this.gridFields
	});
	dm.setDefaultSort(null, 0, "ASC");
	this.grid = new YAHOO.rapidjs.component.grid.RapidGrid(gridContainer, dm, cm, sm);
	this.grid.render();
	this.layout = new YAHOO.ext.BorderLayout(this.container, { 
					west: {
			            split:true,
			            autoScroll:true, 
			            initialSize:this.treeWidth
			        },
			        center: {
			            autoScroll:true
			            
			        }
	});
	this.layout.beginUpdate();
	this.layout.add('west', new YAHOO.rapidjs.component.layout.RapidPanel(treeContainer));
	this.layout.add('center', new YAHOO.rapidjs.component.layout.GridPanel(this.grid, {fitToFrame:true}));
	this.layout.endUpdate();
	this.panel = new YAHOO.rapidjs.component.layout.NestedLayoutPanel(this.layout, {title: this.title, fitToFrame:true });
	this.subscribeToPanel();
};

YAHOO.extendX(YAHOO.rapidjs.component.windows.ContainmentWindow, YAHOO.rapidjs.component.PollingComponentContainer, {
	poll: function()
	{
		if(this.rootNode)
		{
			if(this.lastSelectedNode && this.lastSelectedNode.xmlData.getAttribute('RI_IsPropertiesBrowsed') != '1')
			{
				this.lastSelectedNode.xmlData.setAttribute('RI_IsPropertiesBrowsed', "0");
				this.selectionChanged(this.lastSelectedNode, true);
			}
		}
		else
		{
			YAHOO.rapidjs.component.windows.ContainmentWindow.superclass.poll.call(this);
		}
	},
	processData : function(response){
		var data = new YAHOO.rapidjs.data.RapidXmlDocument(response);
		var node = this.getRootNode(data, response.responseText);
		if(node){
			this.rootNode = node;
			this.tree.handleData(this.rootNode, true);
			var gridNode = this.createGridNode(this.rootNode.firstChild());
			this.loadGridData(gridNode);
		}
		this.events["loadstatechanged"].fireDirect(this, false);
	}, 
	
	createGridNode : function(node){
		var nodeForGrid = new YAHOO.rapidjs.data.RapidXmlNode(null, null, 1, 'GridNode',[this.gridFields[0]]);
		var attributes = node.getAttributes();
		for(var att in attributes) {
			if(att != this.treeLabelAttribute && att.substr(0,3) != 'RI_'){
				var childNode = new YAHOO.rapidjs.data.RapidXmlNode(null, null, 1, this.gridRowTag, [this.gridFields[0]]);
				var attValue = attributes[att];
				childNode.attributes[this.gridFields[0]] = att;
				childNode.attributes[this.gridFields[1]] = attValue;
				nodeForGrid.appendChild(childNode);
			}
		}
		return nodeForGrid;
	}, 
	
	loadGridData: function(node)
	{
		var dm = this.grid.dataModel;
		dm.isSortingDisabled = true;
		if(!this.gridRootNode)
		{
			dm.setRootNode(node);
			dm.loadData(node);
			this.gridRootNode = node;
		}
		else
		{
			this.gridRootNode.mergeData(node, this.gridFields[0]);
			dm.purgeRemovedData();
		}
		dm.isSortingDisabled = false;
		this.grid.view.updateBodyHeight();
		this.grid.view.adjustForScroll(true);
		dm.applySort();
	},
	
	selectionChanged: function(treeNode, keepExisting){
		this.lastSelectedNode = treeNode;
		var xmlNode = treeNode.xmlData;
		var isPropertiesBrowsed = xmlNode.getAttribute('RI_IsPropertiesBrowsed');
		if(isPropertiesBrowsed && isPropertiesBrowsed == '1'){
			var nodeForGrid = this.createGridNode(xmlNode);
			this.loadGridData(nodeForGrid);
		}
		else{
			if(!keepExisting || keepExisting == false)
			{
				this.gridRootNode = null;
				this.grid.dataModel.removeAll();
				this.grid.view.updateBodyHeight();
				this.grid.view.adjustForScroll(true);
				this.grid.dataModel.applySort();
			}
			this.onDemandRequest(xmlNode);
		}
		
	}, 
	
	onDemandRequest: function(node){
		if(this.lastConnection){
			var callStatus = YAHOO.util.Connect.isCallInProgress(this.lastConnection); 
			if(callStatus == true){
				YAHOO.util.Connect.abort(this.lastConnection);
				this.events["loadstatechanged"].fireDirect(this, false);
				this.lastConnection = null;
			}
		}
		var postData = "";
		for(var paramName in this.onDemandParameters) {
			var paramValue = node.getAttribute(this.onDemandParameters[paramName]);
			postData = postData + paramName + "=" + escape(paramValue)+"&";
		}
		if(postData != "")
		{
			postData = postData.substring(0, postData.length-1);
		}
		var callback = {
			success: this.onDemandSuccess,
			failure: this.onDemandFailure,
			timeout: this.timeout,
			scope: this
		};
		this.lastConnection = YAHOO.util.Connect.asyncRequest('POST', this.url, callback, postData);
		this.events["loadstatechanged"].fireDirect(this, true);
	}, 
	
	onDemandSuccess : function(response){
		YAHOO.rapidjs.ServerStatus.refreshState(true);
		if(YAHOO.rapidjs.Connect.isAuthenticated(response) == true){
			if(YAHOO.rapidjs.Connect.containsError(response) == false)
			{
				var data = new YAHOO.rapidjs.data.RapidXmlDocument(response);
				var node = this.getOnDemandRootNode(data);
				if(node){
					var nodeForGrid = this.createGridNode(node.firstChild());
					this.loadGridData(nodeForGrid);	
				}
				this.events["erroroccurred"].fireDirect(this, false, '');
			}
			else
			{
				this.events["erroroccurred"].fireDirect(this, true, response.responseXML);
			}
			this.events["loadstatechanged"].fireDirect(this, false);
		}
		else{
			window.location = "login.html?page=" + window.location.pathname;
		}
		
	}, 
	
	onDemandFailure : function(response){
		if(!this.lastConnection || YAHOO.util.Connect.isCallInProgress(this.lastConnection) == false){
			this.events["loadstatechanged"].fireDirect(this, false);
		}
		var st = response.status;
		if(st == -1){
			this.events["erroroccurred"].fireDirect(this, true, 'Request received a timeout');
		}
		else if(st == 404){
			this.events["erroroccurred"].fireDirect(this, true, 'Specified url cannot be found');
		}
		else if(st == 0){
			YAHOO.rapidjs.ServerStatus.refreshState(false);
			this.events["erroroccurred"].fireDirect(this, true, 'Server is not available');
		}
	}, 
	
	clearData: function(){
		if(this.rootNode){
			this.tree.clear();
			this.rootNode = null;
		}
		this.gridRootNode = null;
		this.grid.dataModel.removeAll();
		this.grid.view.updateBodyHeight();
		this.grid.view.adjustForScroll(true);
		this.grid.dataModel.applySort();
		this.lastSelectedNode = null;
	}, 
	
	getOnDemandRootNode: function(data){
		var node = data.getRootNode(this.rootTag);
		if(!node){
			this.gridRootNode = null;
			this.grid.dataModel.removeAll();
			this.grid.view.updateBodyHeight();
			this.grid.view.adjustForScroll(true);
			this.grid.dataModel.applySort();
		}
		return node;
	}
});