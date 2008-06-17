YAHOO.rapidjs.component.windows.FilterTree = function(container, config){
	YAHOO.rapidjs.component.windows.FilterTree.superclass.constructor.call(this,container, config);
	this.nodeId = 'Name';
	this.rootTag = 'Filters';
	config.contentPath = 'Filter';
	if(!config.rootImages){
		config.rootImages =[
			{visible:'data["Expression"] == null', expanded:'../images/layout/fileopen.gif', collapsed:'../images/layout/folder_closed.gif'},
			{visible:'data["Expression"] != null', expanded:'../images/filterTool/filter.png', collapsed:'../images/filterTool/filter.png'}
		];
	}
	this.tree = new YAHOO.rapidjs.component.tree.TreeGrid(this.container, config);
	this.tree.render();
	this.panel = new YAHOO.rapidjs.component.windows.TreePanel(this.tree, {title: this.title, fitToFrame:true});
	if(!YAHOO.rapidjs.component.windows.FilterTree.errorDlg){
		YAHOO.rapidjs.component.windows.FilterTree.errorDlg = new YAHOO.rapidjs.component.dialogs.ErrorDialog();
	}
	if(!YAHOO.rapidjs.component.windows.FilterTree.addGroupDlg){
		YAHOO.rapidjs.component.windows.FilterTree.addGroupDlg = new YAHOO.rapidjs.component.windows.AddFilterGroupDlg(this,
			YAHOO.rapidjs.component.windows.FilterTree.errorDlg);
	}
	if(!YAHOO.rapidjs.component.windows.FilterTree.addFilterDlg){
		YAHOO.rapidjs.component.windows.FilterTree.addFilterDlg = new YAHOO.rapidjs.component.windows.AddFilterDlg(this,
			YAHOO.rapidjs.component.windows.FilterTree.errorDlg);
	}
	this.createContextMenu();
	this.subscribeToPanel();
	this.tree.events['selectionchanged'].subscribe(this.selectionChanged, this, true);
	this.tree.events['contextmenuclicked'].subscribe(this.handleContextMenu, this, true);
};
YAHOO.extendX(YAHOO.rapidjs.component.windows.FilterTree, YAHOO.rapidjs.component.PollingComponentContainer, {
	
	selectionChanged: function(treeNode)
	{
		if(treeNode.xmlData.getAttribute('Expression')){// Filter groups don't send parameters
			this.sendOutputs(treeNode.xmlData);	
		}
		
	},
	
	
	filterUpdated: function(filterNode){
		this.sendOutputs(filterNode);	
	}, 
	
	processData : function(response, keepExisting){
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
				this.rootNode.mergeData(node, this.nodeId, keepExisting);
				this.tree.refreshData();
				this.tree.isSortingDisabled = false;
			}
		}
	}, 
 
 	subscribeToPanel : function(){
		YAHOO.rapidjs.component.windows.MultiGridWindow.superclass.subscribeToPanel.call(this);
		this.panel.events['activate'].subscribe(this.createTools, this, true);
	}, 
	
	handleAddFilter : function(){
		YAHOO.rapidjs.component.windows.FilterTree.addFilterDlg.setFilterTree(this);
		YAHOO.rapidjs.component.windows.FilterTree.addFilterDlg.show(this.addFilterButton.el.dom, 0);
	}, 
	
	handleAddFilterToGroup : function(menuItem, data, node){
		YAHOO.rapidjs.component.windows.FilterTree.addFilterDlg.setFilterTree(this);
		YAHOO.rapidjs.component.windows.FilterTree.addFilterDlg.showForAddFilterToGroup(menuItem.el.dom, data['Name']);
	}, 
	
	handleAddGroup : function(){
		YAHOO.rapidjs.component.windows.FilterTree.addGroupDlg.setFilterTree(this);
		YAHOO.rapidjs.component.windows.FilterTree.addGroupDlg.show(this.addGroupButton.el.dom);
	}, 
	handleRemoveFilter : function(menuItem, data, node){
		this.menu.hide();
		var filterName = node.getAttribute('Name');
		if(confirm('Remove ' + filterName + '?')){
			YAHOO.rapidjs.component.windows.FilterTree.addFilterDlg.setFilterTree(this);
			YAHOO.rapidjs.component.windows.FilterTree.addFilterDlg.removeFilter(filterName);	
		}
	}, 
	handleRemoveGroup : function(menuItem, data, node){
		this.menu.hide();
		var groupName = node.getAttribute('Name');
		if(confirm('Remove ' + groupName + '?')){
			YAHOO.rapidjs.component.windows.FilterTree.addGroupDlg.setFilterTree(this);
			YAHOO.rapidjs.component.windows.FilterTree.addGroupDlg.removeGroup(groupName);	
		}
	}, 
	handleUpdateFilter : function(menuItem, data, node){
		YAHOO.rapidjs.component.windows.FilterTree.addFilterDlg.setFilterTree(this);
		YAHOO.rapidjs.component.windows.FilterTree.addFilterDlg.show(this.addFilterButton.el.dom, 1, node);
	}, 
	handleViewFilter : function(menuItem, data, node){
		YAHOO.rapidjs.component.windows.FilterTree.addFilterDlg.setFilterTree(this);
		YAHOO.rapidjs.component.windows.FilterTree.addFilterDlg.show(this.addFilterButton.el.dom, 2, node);
	}, 
	handleCopyFilter : function(menuItem, data, node){
		YAHOO.rapidjs.component.windows.FilterTree.addFilterDlg.setFilterTree(this);
		YAHOO.rapidjs.component.windows.FilterTree.addFilterDlg.show(this.addFilterButton.el.dom, 3, node);
	}, 
	createTools : function(){
		if(!this.addFilterButton){
			var dh = YAHOO.ext.DomHelper;
			var tools = this.panel.region.tools.dom;
			this.addFilterButton = new YAHOO.rapidjs.component.Button(dh.append(tools, {tag:'div', cls:'r-ftree-addfilter'}),
    		 {className:'', scope:this, click:this.handleAddFilter, tooltip:'Add Filter'});
			this.addGroupButton = new YAHOO.rapidjs.component.Button(dh.append(tools, {tag:'div', cls:'r-ftree-addgroup'}),
    		 {className:'', scope:this, click:this.handleAddGroup, tooltip:'Add Filter Group'});
		}
	}, 
	
	clearData: function(){
		this.tree.clear();
	}, 
	handleContextMenu : function(event, node){
		this.menu.contextMenuClicked(this, event, node, this.id);
	},
	createContextMenu : function(){
		this.menu = new YAHOO.rapidjs.component.menu.ContextMenu();	
		var MenuAction = YAHOO.rapidjs.component.menu.MenuAction;
		this.menu.addMenuItem('Copy Filter', new MenuAction(this.handleCopyFilter, this), 'data["Expression"] != null');
		this.menu.addMenuItem('View', new MenuAction(this.handleViewFilter, this), 'data["Expression"] != null && data["IsDefault"] == "true"');
		this.menu.addMenuItem('Update', new MenuAction(this.handleUpdateFilter, this), 'data["Expression"] != null && data["IsDefault"] != "true"');
		this.menu.addMenuItem('Remove', new MenuAction(this.handleRemoveFilter, this), 'data["Expression"] != null && data["IsDefault"] != "true"');
		this.menu.addMenuItem('Add Filter', new MenuAction(this.handleAddFilterToGroup, this), 'data["Expression"] == null && data["IsDefault"] != "true"');
		this.menu.addMenuItem('Remove', new MenuAction(this.handleRemoveGroup, this), 'data["Expression"] == null && data["IsDefault"] != "true"');
	}
});
YAHOO.rapidjs.component.windows.FilterTree.errorDlg = null;
YAHOO.rapidjs.component.windows.FilterTree.addFilterDlg = null;
YAHOO.rapidjs.component.windows.FilterTree.addGroupDlg = null;