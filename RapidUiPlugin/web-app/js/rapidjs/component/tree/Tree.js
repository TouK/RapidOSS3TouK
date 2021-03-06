/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
YAHOO.namespace('rapidjs', 'rapidjs.component');
YAHOO.rapidjs.component.Tree = function(container, config)
{
    YAHOO.rapidjs.component.Tree.superclass.constructor.call(this, container, config);
    this.id = container.id;
    this.menuItems = config.menuItems;
    var dh = YAHOO.ext.DomHelper;
    this.wrapper = dh.append(this.container, {tag: 'div', cls:'r-yui-tree'});
    this.header = dh.append(this.container, {tag: 'div', cls:'r-yui-tree-header'});
    this.toolbar = new YAHOO.rapidjs.component.tool.ButtonToolBar(this.header, {title:this.title});
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.LoadingTool(document.body, this));
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.SettingsTool(document.body, this));
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.ErrorTool(document.body, this));
    this.body = dh.append(this.container, {tag: 'div', cls:'r-yui-tree-body'});
    this.tree = new YAHOO.widget.TreeView(this.body);

    YAHOO.util.Event.addListener(this.body, 'click', this.fireTreeClick, this, true);

    this.menuSelectedIndex = null;
    this.keyAttribute = config.keyAttribute;

    this.nodeTag = config.nodeTag;
    this.attributeToBeDisplayed = config.displayAttribute;
    var events = {
        'treeClick' : new YAHOO.util.CustomEvent('treeClick'),
        'treeMenuItemClick' : new YAHOO.util.CustomEvent('treeMenuItemClick')
    };
    YAHOO.ext.util.Config.apply(this.events, events);

    this.treeRootNode = this.tree.getRoot();
    this.treeRootNode.data = null;


    this.treeNodeMenu = new YAHOO.widget.Menu(this.id + "_treeNodeMenu", {position: "dynamic"});

    for (var i in this.menuItems)
    {
        var item = this.treeNodeMenu.addItem({text:this.menuItems[i].label});
        YAHOO.util.Event.addListener(item.element, "click", this.fireTreeMenuItemClick, i, this);
    }

    this.treeNodeMenu.render(document.body);


};

YAHOO.lang.extend(YAHOO.rapidjs.component.Tree, YAHOO.rapidjs.component.PollingComponentContainer, {

    handleSuccess: function(response, keepExisting, removeAttribute)
    {
        var data = new YAHOO.rapidjs.data.RapidXmlDocument(response, [this.keyAttribute]);
        var node = this.getRootNode(data, response.responseText);
        if (node) {
            if (this.rootNode != null)
            {
                this.rootNode.mergeData(node, this.keyAttribute, keepExisting, removeAttribute);
            }
            else
            {
                this.rootNode = node;

                this.rootTreeNode = new YAHOO.rapidjs.component.TreeNode(node, this.tree, this.nodeTag, null, this.attributeToBeDisplayed, this.menuItems, this);
            }


            this.tree.draw();

            if (this.treeRootNode.data != null)
            {

                YAHOO.util.Dom.addClass(this.treeRootNode.data.getEl().getElementsByTagName('label')[0], 'selected_tree_node');
            }
        }
    },
    fireTreeClick: function(e) {
        var target = YAHOO.util.Event.getTarget(e);
        if (YAHOO.util.Dom.hasClass(target, "treeNodeLabel"))
        {
            if (this.treeRootNode.data != null)
            {
                YAHOO.util.Dom.removeClass(this.treeRootNode.data.getEl().getElementsByTagName('label')[0], 'selected_tree_node');
            }

            var parentId = target.id;
            var nodeIndex = parseInt(parentId.substr(3, parentId.length));
            var node = this.tree.getNodeByIndex(nodeIndex);

            this.treeRootNode.data = this.tree.getNodeByIndex(nodeIndex);
            YAHOO.util.Dom.addClass(this.treeRootNode.data.getEl().getElementsByTagName('label')[0], 'selected_tree_node');

            this.events['treeClick'].fireDirect(node.data);
        }
        else if (YAHOO.util.Dom.hasClass(target, "rcmdb-tree-node-headermenu"))
        {
            var parentId = target.id;
            var nodeIndex = parseInt(parentId.substr(3, parentId.length));
            this.menuSelectedIndex = nodeIndex;
            var node = this.tree.getNodeByIndex(nodeIndex);

            this.treeNodeMenu.cfg.setProperty("context", [target, 'tl', 'bl']);
            var index = 0;
            var invisibleCount = 0;
            for (var i in this.menuItems) {
                if (this.menuItems[i].condition != null) {
                    var menuItem = this.treeNodeMenu.getItem(index);
                    var condRes = this.menuItems[i].condition(node.data);
                    if (!condRes)
                    {
                        menuItem.element.style.display = "none";
                        invisibleCount++;
                    }
                    else
                        menuItem.element.style.display = "";
                }
                index++;
            }
            if (invisibleCount == index)
            {
                // disable menu
            }
            else if (invisibleCount < index)
                this.treeNodeMenu.show();
        }

    },
    fireTreeMenuItemClick: function(e, i)
    {
        var id = this.menuItems[i].id;
        var data = this.tree.getNodeByIndex(this.menuSelectedIndex).data;
        this.events['treeMenuItemClick'].fireDirect(id, data);
    },

    clearData: function() {
        if (this.rootNode) {
            var childNodes = this.rootNode.childNodes();
            while (childNodes.length > 0) {
                var childNode = childNodes[0];
                this.rootNode.removeChild(childNode);
            }
        }
        this.tree.draw();
    }
})


YAHOO.rapidjs.component.TreeNode = function(xmlData, tree, nodeTag, parentNode, attributeToBeDisplayed, menuItems)
{
    YAHOO.rapidjs.component.TreeNode.superclass.constructor.call(this, xmlData);
    this.nodeTag = nodeTag;
    this.tree = tree;
    this.attributeToBeDisplayed = attributeToBeDisplayed;
    this.treeNode = null;
    this.menuItems = menuItems;
    if (parentNode != null)
    {
        var text = xmlData.getAttribute(attributeToBeDisplayed);

        this.treeNode = new YAHOO.widget.HTMLNode("abc", parentNode.treeNode, true, true);
        var htmlString = "<table><tr>";
        var index = 0;
        var invisibleCount = 0;

        for (var i in this.menuItems)
        {
            if (this.menuItems[i].condition != null) {
                var condRes = this.menuItems[i].condition(this.xmlData);
                if (!condRes)
                {
                    invisibleCount++;
                }
            }
            index++;
        }
        htmlString += '<td><label id="tnl' + this.treeNode.index + '" class="treeNodeLabel">' + text + "</label></td>"
        if (invisibleCount < index)
        {
            htmlString += '<td id="tnm' + this.treeNode.index + '" class="rcmdb-tree-node-headermenu"></td>';
        }
        htmlString += '</tr></table>';

        this.treeNode.html = htmlString;
        this.treeNode.data = this.xmlData;
        /*else
        {
             htmlString += '<td class="disabledMenu"></td>'
        } */

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
        for (var index = 0; index < numberOfChilds; index++) {
            var childDataNode = childData[index];
            if (childDataNode.nodeType == 1 && childDataNode.nodeName == this.nodeTag)
            {
                this.childAdded(childDataNode);
            }
        }
    },
    childAdded : function(newChild) {
        new YAHOO.rapidjs.component.TreeNode(newChild, this.tree, this.nodeTag, this, this.attributeToBeDisplayed, this.menuItems);
    },
    childAddedBefore : function(newChild, refChild) {
    },
    childRemoved : function(oldChild) {

    },
    dataChanged : function(attributeName, attributeValue) {
        if (attributeName == this.attributeToBeDisplayed)
        {
            var htmlString = "<table><tr>";
            var index = 0;
            var invisibleCount = 0;

            for (var i in this.menuItems)
            {
                if (this.menuItems[i].condition != null) {
                    var condRes = this.menuItems[i].condition(this.xmlData);
                    if (!condRes)
                    {
                        invisibleCount++;
                    }
                }
                index++;
            }
            htmlString += '<td><label id="tnl' + this.treeNode.index + '" class="treeNodeLabel">' + attributeValue + "</label></td>"
            if (invisibleCount < index)
            {
                htmlString += '<td id="tnm' + this.treeNode.index + '" class="rcmdb-tree-node-headermenu"></td>';
            }
            htmlString += '</tr></table>';

            this.treeNode.html = htmlString;
	        //this.tree.getNodeByIndex(this.treeNode.index).getEl().getElementsByTagName('label')[0].innerHTML = attributeValue;
            //this.tree._nodes[this.treeNode.index].getEl().getElementsByTagName('label')[0].innerHTML = attributeValue;

        }
    },
    batchDataChanged : function() {

    },
    dataDestroyed : function() {
        if (this.tree.getNodeByIndex(this.treeNode.index) != null)
        {
            if (this.tree.getRoot().data == this.treeNode)
            {
                this.tree.getRoot().data = null;
            }
            this.tree.removeNode(this.treeNode, true);
        }
    },
    mergeStarted: function() {

    },
    mergeFinished: function() {

    }
})