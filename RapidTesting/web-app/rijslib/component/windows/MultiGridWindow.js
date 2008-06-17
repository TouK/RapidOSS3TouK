YAHOO.rapidjs.component.windows.MultiGridWindow = function(container, config){
	YAHOO.rapidjs.component.windows.MultiGridWindow.superclass.constructor.call(this,container, config);
	this.keyAttribute = config.nodeId;
	this.rootTag = config.rootTag;
	this.tagName = config.contentPath;
	this.tooltip = config.tooltip;
	this.fieldsConfig = config.fields;
	this.rowColorConfig = config["rowColors"];
	this.viewNameAtt = config.viewParameterAttribute;
	this.grids = {};
	this.currentGrid = this.addDefaultGrid(config);
	this.panel = new YAHOO.rapidjs.component.layout.MultiGridPanel(this.grids, {title:this.title, fitToFrame:true});
	this.subscribeToData();
	this.subscribeToPanel();
	if(!YAHOO.rapidjs.component.windows.MultiGridWindow.ViewBuilder){
		YAHOO.rapidjs.component.windows.MultiGridWindow.ViewBuilder = new YAHOO.rapidjs.component.windows.ViewBuilder()
	}
};
YAHOO.extendX(YAHOO.rapidjs.component.windows.MultiGridWindow, YAHOO.rapidjs.component.PollingComponentContainer, {
	rowSelected: function(selectionModel, row, selected){
		if(selected == true){
			this.sendOutputs(row.dummyRow.xmlData);	
		}
	}, 
	
	processData : function(response, keepExisting, removeAttribute){
		var data = new YAHOO.rapidjs.data.RapidXmlDocument(response,[this.keyAttribute]);
		var node = this.getRootNode(data, response.responseText);
		var dataModel = this.currentGrid.dataModel;
		if(node){
			if(!this.currentGrid.rootNode ){
				dataModel.isSortingDisabled = true;
				this.currentGrid.rootNode = node;
				dataModel.setRootNode(node);
				dataModel.loadData(data);
				dataModel.isSortingDisabled = false;
			}
			else
			{
				dataModel.isSortingDisabled = true;
				this.currentGrid.rootNode.mergeData(node, this.keyAttribute, keepExisting, removeAttribute);
				dataModel.isSortingDisabled = false;
				dataModel.purgeRemovedData();
			}
			this.currentGrid.view.updateBodyHeight();
			this.currentGrid.view.adjustForScroll(true);
			dataModel.applySort();
		}
		
	} , 
	clearData: function(){
		var grid = this.currentGrid;
		if(grid){
			var dataModel = grid.dataModel;
			dataModel.removeAll();
			grid.view.updateBodyHeight();
			grid.view.adjustForScroll(true);
			dataModel.applySort();
			grid.rootNode = null;	
		}
	}, 
	
	addGrid : function(node){
		var gridId = node.getAttribute('Name');
		var defaultSortColumn = node.getAttribute('DefaultSortColumn');
		if(!this.grids[gridId]){
			var defaultSortColIndex = 0;
			var sortType;
			var columnNodes = node.getElementsByTagName('Column');
			var nOfColumnNodes = columnNodes.length;
			var headerConf = new Array(nOfColumnNodes);
			var fields = new Array(nOfColumnNodes);
			for(var index=0; index<nOfColumnNodes; index++) {
				var columnNode = columnNodes[index];
				var columnOrder = parseInt(columnNode.getAttribute('Order'), 10);
				var header = columnNode.getAttribute('Header');
				var att = columnNode.getAttribute('AttributeName');
				var width = parseInt(columnNode.getAttribute('Width'), 10);
				var fieldConfig = this.fieldsConfig[att];
				if(fieldConfig){
					headerConf[columnOrder-1] = {
						header: header || fieldConfig['header'],
						sortType: fieldConfig['sortType'] || YAHOO.ext.grid.DefaultColumnModel.sortTypes.none,
						width: width || fieldConfig['width']*1,
						type:fieldConfig['type'],
						images:fieldConfig['images'],
						action:fieldConfig['action'],
						align:fieldConfig['align']};
				}
				else{
					headerConf[columnOrder-1] = {header:header, width:width, type:'Text'};	
				}
				if(att == defaultSortColumn){
					defaultSortColIndex = columnOrder-1;
					sortType = headerConf[columnOrder-1]['sortType'];
				}
				fields[columnOrder-1] = att;
			}
			var container = YAHOO.ext.DomHelper.append(document.body, {tag:'div'});
			return this._addGrid(gridId, container, headerConf, fields, sortType, defaultSortColIndex);	
		}
		else{
			return this.grids[gridId];
		}
		
	},
	
	_addGrid: function(gridId, container, headerConf, fields, sortType, defaultSortColIndex){
		var sm = new YAHOO.rapidjs.component.grid.RapidSelectionModel();
		sm.addListener('rowselect', this.rowSelected, this, true);
		var cm = new YAHOO.ext.grid.DefaultColumnModel(headerConf);
		cm.defaultSortable = true;
		var dm = new YAHOO.rapidjs.component.grid.RapidXmlDataModel({
		    tagName: this.tagName,
		    fields: fields
		});
		dm.setDefaultSort(sortType, defaultSortColIndex, "ASC");
		var grid = new YAHOO.rapidjs.component.grid.RapidGrid(container, dm, cm, sm);
		grid.setRowColorConfig(this.rowColorConfig);
		grid.render();
		grid.view.contextMenuClicked.subscribe(this.handleContextMenu, this, true);
		grid.view.linkClickedEvent.subscribe(this.linkClicked, this, true);
		if(this.tooltip == true){
			grid.view.addTooltip();
		}
		if(this.panel){
			this.panel.addGrid(grid);
		}
		this.grids[gridId] = grid;
		return grid;
	}, 
	
	addDefaultGrid : function(config){
		var content = config.defaultView;
		var headerConf = [];
		var fields = [];
		var defaultSortColIndex = 0;
		var sortType;
		for(var index=0; index<content.length; index++) {
			var el = content[index];
			var att = el['attribute'];
			var fieldConfig = this.fieldsConfig[att];
			if(fieldConfig){
				headerConf[index] = {
				header: el["header"] || fieldConfig['header'],
				sortType: el["sortType"] || fieldConfig['sortType'],
				width: el["width"]*1 || fieldConfig['width']*1,
				type:el["type"] || fieldConfig['type'],
				images:el["images"] || fieldConfig['images'],
				action: el["action"] || fieldConfig['action'],
				align: el['align'] || fieldConfig['align']};	
			}
			else{
				headerConf[index] = {header: el["header"], sortType: el["sortType"], width: el["width"]*1, type:el["type"],images:el["images"], action: el["action"],align: el['align']};
			}
			fields[index] = att;
			if(el['sortBy'] == true){
				defaultSortColIndex = index;
				sortType = el['sortType'];
			}
		}
		return this._addGrid('default', this.container, headerConf, fields, sortType, defaultSortColIndex);
	}, 
	
	activateGrid : function(gridId){
		var grid = this.grids[gridId];
		if(grid && grid != this.currentGrid){
			this.panel.activateGrid(grid);
			if(this.currentGrid){
				this.clearData();	
			}
			this.currentGrid = grid;
			this.poll();
		}
	}, 
	
	removeGrid : function(gridId){
		var grid = this.grids[gridId];
		if(grid){
			grid.destroy();
			grid.rootNode = null;
			delete this.grids[gridId];
			if(grid == this.currentGrid){
				this.currentGrid = null;
				for(var gridIndex in this.grids) {
					var newGrid = this.grids[gridIndex];
					SelectUtils.selectTheValue(this.viewInput, newGrid, 0);
					this.viewChanged();
					break;
				}
			}
		}
	}, 
	
	removeUpdatedGrid: function(gridId){
		var grid = this.grids[gridId];
		if(grid){
			grid.destroy();
			grid.rootNode = null;
			delete this.grids[gridId];
			if(grid == this.currentGrid){
				this.currentGrid = null;
			}
		}
	},
	
	subscribeToPanel : function(){
		YAHOO.rapidjs.component.windows.MultiGridWindow.superclass.subscribeToPanel.call(this);
		this.panel.events['activate'].subscribe(this.createTools, this, true);
	}, 
	
	createTools : function(){
		if(!this.viewInput){
			var dh = YAHOO.ext.DomHelper;
			var tools = dh.append(this.panel.region.titleEl.dom, {tag:'div', cls:'r-mgrid-tools'});
			this.removeViewButton = new YAHOO.rapidjs.component.Button(dh.append(tools, {tag:'div', cls:'r-mgrid-removeview'}),
    		 {className:'', scope:this, click:this.handleRemoveView, tooltip:'Remove View'});
    		this.updateViewButton = new YAHOO.rapidjs.component.Button(dh.append(tools, {tag:'div', cls:'r-mgrid-updateview'}),
    		 {className:'', scope:this, click:this.handleUpdateView, tooltip:'Update View'});
    		this.addViewButton = new YAHOO.rapidjs.component.Button(dh.append(tools, {tag:'div', cls:'r-mgrid-addview'}),
    		 {className:'', scope:this, click:this.handleAddView, tooltip:'Add View'});
			this.viewInput = dh.append(dh.append(tools, {tag:'div', cls:'r-mgrid-viewselect-wrp'}), {tag:'select', cls:'r-mgrid-viewselect'});
			YAHOO.util.Event.addListener(this.viewInput, 'change', this.viewChanged, this, true);
    		 this.updateViewButton.disable();
    		 this.removeViewButton.disable();
    		 
    		 this.removeFilterButton = new YAHOO.rapidjs.component.Button(dh.append(tools, {tag:'div', cls:'r-mgrid-removeview'}),
    		 {className:'', scope:this, click:this.removeQuickFilter, tooltip:'Remove Quick Filter'});
    		 this.removeFilterButton.disable();
    		 this.qFilterInput = dh.append(dh.append(tools, {tag:'div', cls:'r-mgrid-quickfilter-wrp'}), {tag:'input', cls:'r-mgrid-quickfilter'});
    		 YAHOO.util.Event.addListener(this.qFilterInput, 'keydown', this.quickFilterKeyDown, this, true)
    		 dh.append(tools, {tag:'div', cls:'r-mgrid-tools-text', html:'Quick Filter:'});
    		 if(MultiGridData.viewData){
    		 	this.loadViews();	
    		 }
    		 else{
    		 	MultiGridData.events['datareceived'].subscribe(this.loadViews, this, true);
    		 	MultiGridData.getViews();
    		 }
		}
		
	}, 
	
	removeQuickFilter: function(){
		if(this.params){
			delete this.params['QuickFilter'];
			this.qFilterInput.value = '';
		}
		this.poll();
	}, 
	
	quickFilterKeyDown: function(event){
		if(event.keyCode == 13){
			var quickfilter = this.qFilterInput.value;
			if (quickfilter == '') {
				delete this.params['QuickFilter'];
				this.removeFilterButton.disable();
			} else {
				this.params['QuickFilter'] = quickfilter;	
				this.removeFilterButton.enable();
			}
			this.poll();			
		}
	}, 
	loadViews: function(){
		SelectUtils.clear(this.viewInput);
		SelectUtils.addOption(this.viewInput, 'default', 'default');
		var viewNodes = MultiGridData.viewData.getElementsByTagName('View');
		for(var index=0; index<viewNodes.length; index++) {
			var viewNode = viewNodes[index];
			var viewName = viewNode.getAttribute('Name');
			this.addGrid(viewNodes[index]);
			SelectUtils.addOption(this.viewInput, viewName, viewName);
		}
	},
	
	handleUpdateView : function(){
		var gridId = this.viewInput.options[this.viewInput.selectedIndex].value;
		var viewNode = MultiGridData.viewData.findChildNode('Name', gridId, 'View')[0];
		YAHOO.rapidjs.component.windows.MultiGridWindow.ViewBuilder.show(this.updateViewButton.el.dom, true, viewNode);
	},
	handleRemoveView : function(){
		var currentView = this.viewInput.options[this.viewInput.selectedIndex].value;
		if(confirm('Remove ' + currentView + '?')){
			YAHOO.rapidjs.component.windows.MultiGridWindow.ViewBuilder.removeView(currentView);	
		}
	},
	handleAddView : function(){
		YAHOO.rapidjs.component.windows.MultiGridWindow.ViewBuilder.show(this.addViewButton.el.dom, false);
	}, 
	viewAdded: function(view){
		var viewNode = MultiGridData.viewData.findChildNode('Name', view, 'View')[0];
		SelectUtils.addOption(this.viewInput, view, view);
		this.addGrid(viewNode);
		SelectUtils.selectTheValue(this.viewInput, view, 0);
		this.viewChanged();	
	},
	
	viewUpdated: function(view){
		var viewNode = MultiGridData.viewData.findChildNode('Name', view, 'View')[0];
		this.removeUpdatedGrid(view);
		this.addGrid(viewNode);
		SelectUtils.selectTheValue(this.viewInput, view, 0);
		this.viewChanged();
	},
	viewRemoved : function(view){
		SelectUtils.remove(this.viewInput, view);
		this.removeGrid(view);
	}, 
	viewChanged: function(){
		var gridId = this.viewInput.options[this.viewInput.selectedIndex].value;
		if(gridId == 'default'){
			this.updateViewButton.disable();
			this.removeViewButton.disable();
		}
		else{
			this.updateViewButton.enable();
			this.removeViewButton.enable();
		}
		this.activateGrid(gridId);
	}, 
	
	subscribeToData: function(){
		MultiGridData.events['viewadded'].subscribe(this.viewAdded, this, true);
		MultiGridData.events['viewupdated'].subscribe(this.viewUpdated, this, true);
		MultiGridData.events['viewremoved'].subscribe(this.viewRemoved, this, true);
		MultiGridData.events['erroroccurred'].subscribe(this.errorOccured, this, true);
		
	},
	
	errorOccured: function(error, errorText){
		this.events['erroroccured'].fireDirect(this, error, errorText);
	},
	
	setFocusedContent: function(node, params, dynamicTitleAtt, dialogCall)
	{
		YAHOO.rapidjs.component.PollingComponentContainer.superclass.setFocusedContent.call(this,node, params, dynamicTitleAtt, dialogCall);
		if(this.params){
			params['QuickFilter'] = this.params['QuickFilter'];
			this.removeFilterButton.enable();	
		}
		this.params = params;
		var viewName = this.params[this.viewNameAtt];
		if(viewName == ''){
			viewName = 'default';
		}
		if(viewName && this.grids[viewName] && this.currentGrid != this.grids[viewName]){
			SelectUtils.selectTheValue(this.viewInput, viewName, 0);
			this.viewChanged();
		}
		else{
			this.poll();
		}
		
	}, 
	setContextMenuContent: function(node, params, dynamicTitleAtt, dialogCall)
	{
		YAHOO.rapidjs.component.PollingComponentContainer.superclass.setContextMenuContent.call(this,node, params, dynamicTitleAtt, dialogCall);
		if(this.params){
			params['QuickFilter'] = this.params['QuickFilter'];	
			this.removeFilterButton.enable();
		}
		this.params = params;
		var viewName = this.params[this.viewNameAtt];
		if(viewName == ''){
			viewName = 'default';
		}
		if(viewName && this.grids[viewName] && this.currentGrid != this.grids[viewName]){
			SelectUtils.selectTheValue(this.viewInput, viewName, 0);
			this.viewChanged();
		}
		else{
			this.poll();
		}
	},
	
	linkClicked: function(action, data, node){
		action.execute(this, null, data, node);
	}, 
	
	getSortType: function(sortType){
		var sortTypes = YAHOO.ext.grid.DefaultColumnModel.sortTypes;
		if(sortType == 'Int'){
			return sortTypes.asInt;
		}
		else if(sortType == 'Date'){
			return sortTypes.asDate;
		}
		else if(sortType == 'Float'){
			return sortTypes.asFloat; 
		}
		else if(sortType == 'String'){
			return sortTypes.none; 
		}
		else if(sortType == 'UCString'){
			return sortTypes.asUCString; 
		}
	}
});
YAHOO.rapidjs.component.windows.MultiGridWindow.ViewBuilder = null;

YAHOO.rapidjs.component.windows.MultiGridData = new function(){
	this.events = {
		'viewupdated' : new YAHOO.util.CustomEvent('viewupdated'), 
		'viewremoved' : new YAHOO.util.CustomEvent('viewremoved'),
		'viewadded' : new YAHOO.util.CustomEvent('viewadded'),
		'datareceived' : new YAHOO.util.CustomEvent('datareceived'),
		'erroroccurred': new YAHOO.util.CustomEvent('erroroccurred')
	};
	this.viewData = null;
	
	this.getViews = function(view, action){
		if(this.connection){
			YAHOO.util.Connect.abort(this.connection);
		}
		var callback = {
			success: this.getViewSuccess,
			failure: this.processViewFailure,
			scope:this, 
			argument: [view, action]
		}
		var url = '/RapidInsight/View/list';
		this.connection = YAHOO.util.Connect.asyncRequest('GET', url, callback);
	};
	this.getViewSuccess = function(response){
		if(this.checkResponse(response)){
			var data = new YAHOO.rapidjs.data.RapidXmlDocument(response,['Name']);
			var rootNode = data.getRootNode('Views');
			if(rootNode){
				this.viewData = rootNode;
				if(response.argument && response.argument[0]){//Request for view add, update or remove
					var gridId = response.argument[0];
					if(response.argument[1] == 1){//view added
						this.events['viewadded'].fireDirect(gridId);
					}
					else if(response.argument[1] == 2){//view updated
						this.events['viewupdated'].fireDirect(gridId);
					}
					else{//view removed
						this.events['viewremoved'].fireDirect(gridId);
					}
				}
				else{//First request
					this.events['datareceived'].fireDirect();
				}
			}	
		}
	};
	this.checkResponse = function(response)
	{
		if(YAHOO.rapidjs.Connect.containsError(response) == true)
		{
			this.events["erroroccurred"].fireDirect(true,YAHOO.rapidjs.Connect.getErrorMessages(response.responseXML));
			return null;
		}
		else if(YAHOO.rapidjs.Connect.isAuthenticated(response) == false)
		{
			window.location = "login.html?page=" + window.location.pathname;
			return null;
		}
		return true;
	};
	this.processViewFailure = function(response){
		var st = response.status;
		if(st == -1){
			this.events["erroroccurred"].fireDirect(true, 'Request received a timeout');
		}
		else if(st == 404){
			this.events["erroroccurred"].fireDirect(true, 'Specified url cannot be found');
		}
		else if(st == 0){
			this.events["erroroccurred"].fireDirect(true, 'Server is not available');
			YAHOO.rapidjs.ServerStatus.refreshState(false);
		}
	};
};

var MultiGridData = YAHOO.rapidjs.component.windows.MultiGridData;
