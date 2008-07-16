YAHOO.rapidjs.component.Tree = function(container, config)
{
	YAHOO.rapidjs.component.Tree.superclass.constructor.call(this, container, config);
    this.id = null;
    this.menuItems = config.menuItems;
    this.tree = new YAHOO.widget.TreeView(container);

    YAHOO.util.Event.addListener(container, 'click', this.fireTreeNodeClick.createDelegate(this), this, true);
    this.selectedNode = null;
    this.nodeId = config.nodeId;

    this.nodeTag = config.nodeTag;
    this.nodeTypeAttribute = config.nodeTypeAttribute;
    this.attributeToBeDisplayed = config.displayAttribute;
    this.queryAttribute = config.queryAttribute;
    this.events = {
        'treenodeclick' : new YAHOO.util.CustomEvent('treenodeclick')    
    };





    this.treeNodeMenu = new YAHOO.widget.Menu(this.id + "_treeNodeMenu", {position: "dynamic"});

    for (var i in this.menuItems)
    {
            this.treeNodeMenu.addItem( {text:i, onclick: { fn: this.menuItems[i].onClickFunction, scope: this } });
    }

    this.treeNodeMenu.render(document.body);


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

            this.rootTreeNode = new  YAHOO.rapidjs.component.TreeNode(node, this.tree, this.nodeTag, null, this.attributeToBeDisplayed, this.nodeTypeAttribute, this.queryAttribute, this.menuItems);
        }
        if(this.selectedNode != null)
        {
            YAHOO.util.Dom.addClass( node.getEl().firstChild , 'selected_tree_node');
        }
        this.tree.draw();
        
    },
    fireTreeNodeClick: function(e){
        var target = YAHOO.util.Event.getTarget(e);
        if( YAHOO.util.Dom.hasClass( target, "treeNodeLabel") )
        {
            var parentId = target.parentNode.parentNode.parentNode.parentNode.parentNode.id;
            var nodeIndex = parseInt( parentId.substr(13, parentId.length) );
            var node = this.tree.getNodeByIndex(nodeIndex);

            var nodeType = node.data.nodeType;
            var query = node.data.query;

            if(this.selectedNode != null)
            {
                YAHOO.util.Dom.removeClass( this.selectedNode , 'selected_tree_node');
            }
            YAHOO.util.Dom.addClass( e.target , 'selected_tree_node');
            this.selectedNode = e.target;
            this.events['treenodeclick'].fireDirect(nodeType, query);
        }
        else if ( YAHOO.util.Dom.hasClass(target, "rcmdb-tree-node-headermenu" ) )
        {
            var parentId = target.parentNode.parentNode.parentNode.parentNode.id;
            var nodeIndex = parseInt( parentId.substr(4, parentId.length) );
            var node = this.tree.getNodeByIndex(nodeIndex);

            this.treeNodeMenu.cfg.setProperty("context", [target, 'tl', 'bl']);
            var index = 0;
            var invisibleCount = 0;
            for (var i in this.menuItems){
                if( this.menuItems[i].condition != null ){
                    var menuItem = this.treeNodeMenu.getItem(index);
                    var condRes = this.menuItems[i].condition( node);
                    if( !condRes)
                    {
                        menuItem.element.style.display = "none";
                        invisibleCount++;
                    }
                    else
                        menuItem.element.style.display = "";
                }
                index++;
            }
            if( invisibleCount == index)
            {
                // disable menu
            }
            else if( invisibleCount < index)
                this.treeNodeMenu.show();
        }

    }
})


YAHOO.rapidjs.component.TreeNode = function(xmlData, tree, nodeTag, parentNode, attributeToBeDisplayed, nodeTypeAttribute, queryAttribute, menuItems)
{
	YAHOO.rapidjs.component.TreeNode.superclass.constructor.call(this, xmlData);
    this.nodeTag = nodeTag;
    this.tree = tree;
    this.attributeToBeDisplayed = attributeToBeDisplayed;
    this.nodeTypeAttribute = nodeTypeAttribute;
    this.treeNode = null;
    this.queryAttribute =  queryAttribute;
    this.menuItems = menuItems;
    if(parentNode != null)
    {
        var text = xmlData.getAttribute(attributeToBeDisplayed);
        var nodeType = xmlData.getAttribute(nodeTypeAttribute);
        var query = xmlData.getAttribute(queryAttribute);


        this.treeNode = new YAHOO.widget.HTMLNode("abc" ,parentNode.treeNode, true, true);

        this.treeNode.data = { "nodeType": nodeType, "query": query, "label": text};

        var htmlString = "<table><tr>";
        var index = 0;
        var invisibleCount = 0;
        
        for (var i in this.menuItems)
        {
            if( this.menuItems[i].condition != null ){
                var condRes = this.menuItems[i].condition( this.treeNode);
                if( !condRes)
                {
                    invisibleCount++;
                }
            }
            index++;
        }
        htmlString += '<td><label class="treeNodeLabel">' + text + "</label></td></tr></table>"
        if( invisibleCount < index)
        {
            htmlString += '<td class="rcmdb-tree-node-headermenu"></td>';
        }
        /*else
        {
             htmlString += '<td class="disabledMenu"></td>'
        } */


        this.treeNode.html = htmlString;

        //this.treeNode.href = "#";
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
        new YAHOO.rapidjs.component.TreeNode(newChild, this.tree, this.nodeTag, this, this.attributeToBeDisplayed, this.nodeTypeAttribute, this.queryAttribute, this.menuItems);
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

