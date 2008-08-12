YAHOO.namespace('rapidjs', 'rapidjs.component');
YAHOO.rapidjs.component.TreeGrid = function(container, config) {
    YAHOO.rapidjs.component.TreeGrid.superclass.constructor.call(this,container, config);
	this.nodeId = config.nodeId;
	this.rootTag = config.rootTag;
    var events = {
        'selectionChange' : new YAHOO.util.CustomEvent('selectionChange'),
        'rowMenuClick' : new YAHOO.util.CustomEvent('rowMenuClick')
    };
    YAHOO.ext.util.Config.apply(this.events, events);
    this.header = YAHOO.ext.DomHelper.append(this.container, {tag:'div'});
    this.toolbar = new YAHOO.rapidjs.component.tool.ButtonToolBar(this.header, {title:this.title});
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.LoadingTool(document.body, this));
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.SettingsTool(document.body, this));
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.ErrorTool(document.body, this));
    this.body = YAHOO.ext.DomHelper.append(this.container, {tag:'div'}, true);
    this.treeGridView = new YAHOO.rapidjs.component.treegrid.TreeGridView(this.body.dom, config);
	this.treeGridView.render();
    this.treeGridView.events['selectionchanged'].subscribe(this.fireSelectionChange, this, true);
	this.tree.events['rowMenuClick'].subscribe(this.fireRowMenuClick, this, true);
}
YAHOO.lang.extend(YAHOO.rapidjs.component.TreeGrid, YAHOO.rapidjs.component.PollingComponentContainer, {
    handleSuccess: function(response, keepExisting, removeAttribute)
    {
        var data = new YAHOO.rapidjs.data.RapidXmlDocument(response,[this.nodeId]);
		var node = this.getRootNode(data, response.responseText);
		if(node){
			if(!this.rootNode || keepExisting == false){
				this.rootNode = node;
				this.treeGridView.handleData(this.rootNode);
			}
			else
			{
				this.treeGridView.isSortingDisabled = true;
				this.rootNode.mergeData(node, this.nodeId, keepExisting, removeAttribute);
				this.treeGridView.refreshData();
				this.treeGridView.isSortingDisabled = false;
			}
		}
    },

    clearData: function() {
        this.treeGridView.clear();
    },

    resize: function(width, height){
        var bodyHeight =  height-this.header.offsetHeight;
        this.body.setHeight(bodyHeight);
        this.treeGridView.resize(width,bodyHeight);
    },

    fireSelectionChange: function(treeNode){
        this.events['selectionChange'].fireDirect(treeNode.xmlData);
    },
    fireRowMenuClick: function(xmlData, id, parentId){
        this.events['rowMenuClick'].fireDirect(xmlData, id, parentId);
    }
});