YAHOO.rapidjs.component.windows.EditorGridWindow = function(container, config){
	YAHOO.rapidjs.component.windows.GridWindow.superclass.constructor.call(this,container, config);
	this.keyAttribute = config.nodeId;
	this.sm = new YAHOO.rapidjs.component.grid.RapidEditorSelectionModel();
	this.sm.addListener('selectionchange', this.onSelection);
	this.sm.clicksToActivateCell = 2;
	this.rootTag = config.rootTag;
	this.tagName = config.contentPath;
	this.requestedInPollingMode = false;
	var content = config.content;
	var headerConf = [];
	var fields = [];
	for(var index=0; index<content.length; index++) {
		var el = content[index];
		headerConf[index] = {header: el["header"], width: el["width"]*1, editor: el['editor']};
		fields[index] = el["attribute"];
	}
	
	this.cm = new YAHOO.ext.grid.DefaultColumnModel(headerConf);
	this.cm.defaultSortable = true;
	this.dm = new YAHOO.rapidjs.component.grid.RapidXmlDataModel({
	    tagName: this.tagName,
	    fields: fields
	});
	this.grid = new YAHOO.rapidjs.component.grid.RapidGrid(this.container, this.dm, this.cm, this.sm);
	this.grid.container.addClass('yeditgrid');
	this.grid.render();
	this.grid.view.contextMenuClicked.subscribe(this.handleContextMenu, this, true);
	this.panel = new YAHOO.rapidjs.component.layout.GridPanel(this.grid, {title:this.title, fitToFrame:true});
	this.subscribeToPanel();
};
YAHOO.extendX(YAHOO.rapidjs.component.windows.EditorGridWindow, YAHOO.rapidjs.component.windows.GridWindow);
