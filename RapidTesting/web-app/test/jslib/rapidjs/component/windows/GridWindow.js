YAHOO.rapidjs.component.windows.GridWindow = function(container, config){
	YAHOO.rapidjs.component.windows.GridWindow.superclass.constructor.call(this,container, config);
	this.keyAttribute = config.nodeId;
	this.sm = new YAHOO.rapidjs.component.grid.RapidSelectionModel();
	this.sm.addListener('rowselect', this.rowSelected, this, true);
	this.rootTag = config.rootTag;
	this.tagName = config.contentPath;
	var content = config.content;
	var headerConf = [];
	var fields = [];
	var defaultSortColIndex = 0;
	var sortType;
	for(var index=0; index<content.length; index++) {
		var el = content[index];
		headerConf[index] = {header: el["header"], sortType: el["sortType"], width: el["width"]*1, type:el["type"], images:el["images"], actions: el["actions"], action: el["action"], align: el['align']};
		fields[index] = el["attribute"];
		if(el['sortBy'] == true){
			defaultSortColIndex = index;
			sortType = el['sortType'];
		}
	}
	
	this.cm = new YAHOO.ext.grid.DefaultColumnModel(headerConf);
	this.cm.defaultSortable = true;
	this.dm = new YAHOO.rapidjs.component.grid.RapidXmlDataModel({
	    tagName: this.tagName,
	    fields: fields
	});
	this.dm.setDefaultSort(sortType, defaultSortColIndex, "ASC");
	this.grid = new YAHOO.rapidjs.component.grid.RapidGrid(this.container, this.dm, this.cm, this.sm);
	this.grid.setRowColorConfig(config["rowColors"]);
	this.grid.render();
	this.grid.view.contextMenuClicked.subscribe(this.handleContextMenu, this, true);
	this.grid.view.linkClickedEvent.subscribe(this.linkClicked, this, true);
	if(config.tooltip == true){
		this.grid.view.addTooltip();
	}
	this.panel = new YAHOO.rapidjs.component.layout.GridPanel(this.grid, {title:this.title, fitToFrame:true});
	this.subscribeToPanel();
};
YAHOO.extendX(YAHOO.rapidjs.component.windows.GridWindow, YAHOO.rapidjs.component.PollingComponentContainer, {
	rowSelected: function(selectionModel, row, selected){
		if(selected == true){
			this.sendOutputs(row.dummyRow.xmlData);	
		}
	}, 
	
	processData : function(response, keepExisting, removeAttribute){
		var data = new YAHOO.rapidjs.data.RapidXmlDocument(response,[this.keyAttribute]);
		var node = this.getRootNode(data, response.responseText);
		if(node){
			
			if(!this.rootNode ){
				this.dm.isSortingDisabled = true;
				this.rootNode = node;
				this.dm.setRootNode(this.rootNode);
				this.dm.loadData(data);
				this.dm.isSortingDisabled = false;
			}
			else
			{
				this.dm.isSortingDisabled = true;
				this.rootNode.mergeData(node, this.keyAttribute, keepExisting, removeAttribute);
				this.dm.isSortingDisabled = false;
				this.dm.purgeRemovedData();
			}
			this.grid.view.updateBodyHeight();
			this.grid.view.adjustForScroll(true);
			this.dm.applySort();
		}
		
	} , 
	clearData: function(){
		this.dm.removeAll();
		this.grid.view.updateBodyHeight();
		this.grid.view.adjustForScroll(true);
		this.dm.applySort();
		this.rootNode = null;
	}, 
	
	linkClicked: function(action, data, node){
		action.execute(this, null, data, node);
	}
});
