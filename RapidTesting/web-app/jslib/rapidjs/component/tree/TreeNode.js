YAHOO.rapidjs.component.tree.TreeNode = function(xmlData, contentPath,level)
{
	YAHOO.rapidjs.component.tree.TreeNode.superclass.constructor.call(this, xmlData);
	this.childNodes = [];
	this.level = level; 
	this.contentPath = contentPath;
	this.isExpanded = false;
	this.isSelected = false;
	this.isRemoved = false;
	this.indexInParent = null;
	this.loadData();
};

YAHOO.extendX(YAHOO.rapidjs.component.tree.TreeNode, YAHOO.rapidjs.component.RapidElement, {
	
	loadData: function(){
		var childData = this.xmlData.childNodes();
		this.processChildNodes(0, childData);
	},
	childAdded : function(newChild){
		if(this.childNodes.length == 0){
			this.isExpanded = false;
		}
		var childNode = new YAHOO.rapidjs.component.tree.TreeNode(newChild, this.contentPath, this.level + 1);
		this.childNodes[this.childNodes.length] = childNode;
	}, 
	
	dataDestroyed: function(){
		this.isRemoved = true;
	},
	
	destroy: function(){
		this.childNodes = null;
		this.xmlData = null;
	},
	
	processChildNodes: function(lastIndex, childData)
	{
		if(childData)
		{
			var numberOfChilds = childData.length;
			var childToBeProcessed = lastIndex + 100;
			for(var index = lastIndex; index < childToBeProcessed && index < numberOfChilds; index++) {
				var childDataNode = childData[index];
				if(childDataNode.nodeType == 1 && childDataNode.nodeName == this.contentPath)
				{
					var childLevel = this.level + 1;
					var childNode = new YAHOO.rapidjs.component.tree.TreeNode(childDataNode, this.contentPath, childLevel);
					this.childNodes[this.childNodes.length] = childNode;
					childNode.indexInParent = this.childNodes.length;
				}
			}
			if(childToBeProcessed < numberOfChilds)
			{
				this.processChildNodes.defer(1, this, [childToBeProcessed, childData]);
			}
		}
	}
});
YAHOO.rapidjs.component.tree.TreeRootNode = function(xmlData, contentPath){
	 YAHOO.rapidjs.component.tree.TreeRootNode.superclass.constructor.call(this, xmlData, contentPath, -1);
};

YAHOO.extendX(YAHOO.rapidjs.component.tree.TreeRootNode, YAHOO.rapidjs.component.tree.TreeNode, {
	childAdded : function(newChild){
		var childNode = new YAHOO.rapidjs.component.tree.TreeNode(newChild, this.contentPath, this.level + 1);
		this.childNodes[this.childNodes.length] = childNode;
	}
});