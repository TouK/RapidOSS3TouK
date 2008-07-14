YAHOO.rapidjs.component.Tree = function(container, config)
{
	YAHOO.rapidjs.component.Tree.superclass.constructor.call(this, container, config);
    this.tree = new YAHOO.widget.TreeView(container);
    this.tree.subscribe("labelClick", this.fireTreeNodeClick.createDelegate(this));

    this.nodeId = config.nodeId;
    this.nodeTag = config.nodeTag;
    this.nodeTypeAttribute = config.nodeTypeAttribute;
    this.attributeToBeDisplayed = config.displayAttribute;
    this.queryAttribute = config.queryAttribute;
    this.events = {
        'treenodeclick' : new YAHOO.util.CustomEvent('treenodeclick')    
    };


};

YAHOO.lang.extend(YAHOO.rapidjs.component.Tree, YAHOO.rapidjs.component.PollingComponentContainer, {
    handleSuccess: function(response)
    {
        var data = new YAHOO.rapidjs.data.RapidXmlDocument(response,[this.nodeId]);
		var node = this.getRootNode(data, response.responseText);
        if(this.rootNode != null)
        {
            this.rootNode.mergeData(node, this.nodeId, false, null);
        }
        else
        {
            this.rootNode = node;

            this.rootTreeNode = new  YAHOO.rapidjs.component.TreeNode(node, this.tree, this.nodeTag, null, this.attributeToBeDisplayed, this.nodeTypeAttribute, this.queryAttribute);
        }

        this.tree.draw();

    },
    fireTreeNodeClick: function(node){
        var nodeType = node.data.nodeType;
        var query = node.data.query;
        this.events['treenodeclick'].fireDirect(nodeType, query);
    }
})


YAHOO.rapidjs.component.TreeNode = function(xmlData, tree, nodeTag, parentNode, attributeToBeDisplayed, nodeTypeAttribute, queryAttribute)
{
	YAHOO.rapidjs.component.TreeNode.superclass.constructor.call(this, xmlData);
    this.nodeTag = nodeTag;
    this.tree = tree;
    this.attributeToBeDisplayed = attributeToBeDisplayed;
    this.nodeTypeAttribute = nodeTypeAttribute;
    this.treeNode = null;
    this.queryAttribute =  queryAttribute;
    if(parentNode != null)
    {
        var text = xmlData.getAttribute(attributeToBeDisplayed);
        var nodeType = xmlData.getAttribute(nodeTypeAttribute);
        var query = xmlData.getAttribute(queryAttribute);
        this.treeNode = new YAHOO.widget.TextNode( text, parentNode.treeNode, true);
        this.treeNode.data = { "nodeType": nodeType, "query": query};
        this.treeNode.href = "#";
    }
    else
    {
        this.treeNode = tree.getRoot();
    }
    this.loadData();

};

YAHOO.lang.extend(YAHOO.rapidjs.component.TreeNode, YAHOO.rapidjs.component.RapidElement, {
    loadData: function()
    {
        var childData = this.xmlData.childNodes();
        var numberOfChilds = childData.length;
        for(var index = 0; index < numberOfChilds; index++) {
            var childDataNode = childData[index];
            if(childDataNode.nodeType == 1 && childDataNode.nodeName == this.nodeTag)
            {
                this.childAdded(childDataNode);
            }
        }
    },
    childAdded : function(newChild){
        new YAHOO.rapidjs.component.TreeNode(newChild, this.tree, this.nodeTag, this, this.attributeToBeDisplayed, this.nodeTypeAttribute, this.queryAttribute);
    },
	childAddedBefore : function(newChild, refChild){
    },
	childRemoved : function(oldChild){

    },
	dataChanged : function(attributeName, attributeValue){
        if(attributeName == this.attributeToBeDisplayed)
        {
            this.treeNode.label = attributeValue;
        }
    },
	batchDataChanged : function(){

    },
	dataDestroyed : function(){
        if(this.tree.getNodeByIndex(this.treeNode.index) != null)
        {
            this.tree.removeNode(this.treeNode, true) ;
        }
    },
	mergeStarted: function(){

    },
	mergeFinished: function(){

    }
})

