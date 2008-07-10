YAHOO.rapidjs.component.Tree = function(container, config)
{
	YAHOO.rapidjs.component.Tree.superclass.constructor.call(this, container, config);
    this.tree = new YAHOO.widget.TreeView(container);
    this.nodeId = config.nodeId;
    this.nodeTag = config.nodeTag;

};

YAHOO.lang.extend(YAHOO.rapidjs.component.Tree, YAHOO.rapidjs.component.PollingComponentContainer, {
    processData: function(response)
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

            this.rootTreeNode = new  YAHOO.rapidjs.component.TreeNode(node, this.tree, this.nodeTag, null, "Company");
        }

        this.tree.draw();

    }
})


YAHOO.rapidjs.component.TreeNode = function(xmlData, tree, nodeTag, parentNode, attributeToBeDisplayed)
{
	YAHOO.rapidjs.component.TreeNode.superclass.constructor.call(this, xmlData);
    this.nodeTag = nodeTag;
    this.tree = tree;
    this.attributeToBeDisplayed = attributeToBeDisplayed;
    this.treeNode = null;
    if(parentNode != null)
    {
        var text = xmlData.getAttribute(attributeToBeDisplayed);
        this.treeNode = new YAHOO.widget.TextNode( text, parentNode.treeNode, true);
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
        new YAHOO.rapidjs.component.TreeNode(newChild, this.tree, this.nodeTag, this, this.attributeToBeDisplayed);
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

