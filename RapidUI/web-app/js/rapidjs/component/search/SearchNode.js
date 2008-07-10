YAHOO.rapidjs.component.search.SearchNode = function(xmlData)
{
    YAHOO.rapidjs.component.search.SearchNode.superclass.constructor.call(this, xmlData);
    this.isRemoved = false;
    this.indexInParent = null;
};

YAHOO.lang.extend(YAHOO.rapidjs.component.search.SearchNode, YAHOO.rapidjs.component.RapidElement, {

    dataDestroyed: function() {
        this.isRemoved = true;
    },

    destroy: function() {
        this.xmlData = null;
    }
});
YAHOO.rapidjs.component.search.RootSearchNode = function(xmlData, contentPath) {
    YAHOO.rapidjs.component.search.RootSearchNode.superclass.constructor.call(this, xmlData);
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
                var childNode = new YAHOO.rapidjs.component.search.SearchNode(childDataNode);
                this.childNodes[this.childNodes.length] = childNode;
                childNode.indexInParent = this.childNodes.length;
            }
        }
    }
};

YAHOO.lang.extend(YAHOO.rapidjs.component.search.RootSearchNode, YAHOO.rapidjs.component.search.SearchNode, {
    destroy: function() {
        this.childNodes = null;
        this.xmlData = null;
    },
    childAdded : function(newChild) {
        var childNode = new YAHOO.rapidjs.component.search.SearchNode(newChild);
        this.childNodes[this.childNodes.length] = childNode;
    }
});