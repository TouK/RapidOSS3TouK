YAHOO.rapidjs.rcmdb.SearchNode = function(xmlData)
{
    YAHOO.rapidjs.rcmdb.SearchNode.superclass.constructor.call(this, xmlData);
    this.isRemoved = false;
    this.indexInParent = null;
};

YAHOO.extendX(YAHOO.rapidjs.rcmdb.SearchNode, YAHOO.rapidjs.component.RapidElement, {

    dataDestroyed: function() {
        this.isRemoved = true;
    },

    destroy: function() {
        this.xmlData = null;
    }
});
YAHOO.rapidjs.rcmdb.RootSearchNode = function(xmlData, contentPath) {
    YAHOO.rapidjs.rcmdb.RootSearchNode.superclass.constructor.call(this, xmlData);
    this.childNodes = [];
    this.contentPath = contentPath;
    var childData = this.xmlData.childNodes();
    if (childData)
    {
        var numberOfChilds = childData.length;
        for (var index = 0; index < numberOfChilds; index++) {
            var childDataNode = childData[index];
            if (childDataNode.nodeType == 1 && childDataNode.nodeName == this.contentPath)
            {
                var childNode = new YAHOO.rapidjs.rcmdb.SearchNode(childDataNode);
                this.childNodes[this.childNodes.length] = childNode;
                childNode.indexInParent = this.childNodes.length;
            }
        }
    }
};

YAHOO.extendX(YAHOO.rapidjs.rcmdb.RootSearchNode, YAHOO.rapidjs.rcmdb.SearchNode, {
    destroy: function() {
        this.childNodes = null;
        this.xmlData = null;
    },
    childAdded : function(newChild) {
        var childNode = new YAHOO.rapidjs.rcmdb.SearchNode(newChild);
        this.childNodes[this.childNodes.length] = childNode;
    }
});