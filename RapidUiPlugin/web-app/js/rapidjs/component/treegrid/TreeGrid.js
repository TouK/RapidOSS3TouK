YAHOO.namespace('rapidjs', 'rapidjs.component');
YAHOO.rapidjs.component.TreeGrid = function(container, config) {
    YAHOO.rapidjs.component.TreeGrid.superclass.constructor.call(this,container, config);
	this.nodeId = config.nodeId;
	this.rootTag = config.rootTag;
    var header = YAHOO.ext.DomHelper.append(this.container, {tag:'div'});
    this.toolbar = new YAHOO.rapidjs.component.tool.ButtonToolBar(header, {title:this.title});
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.LoadingTool(document.body, this));
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.SettingsTool(document.body, this));
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.ErrorTool(document.body, this));
    var body = YAHOO.ext.DomHelper.append(this.container, {tag:'div'});
    this.treeGridView = new YAHOO.rapidjs.component.treegrid.TreeGridView(body, config);
	this.treeGridView.render();
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
        this.treeGridView.resize(width, height);
    }
});