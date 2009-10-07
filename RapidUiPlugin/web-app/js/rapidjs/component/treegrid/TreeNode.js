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
YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.treegrid');
YAHOO.rapidjs.component.treegrid.TreeNode = function(xmlData, contentPath, level, hideAttribute, expandNodeAttribute)
{
    YAHOO.rapidjs.component.treegrid.TreeNode.superclass.constructor.call(this, xmlData);
    this.childNodes = [];
    this.expandNodeAttribute = expandNodeAttribute;
    this.level = level;
    this.contentPath = contentPath;
    this.isExpanded = xmlData.getAttribute(this.expandNodeAttribute) == "true";
    this.isLeaf = xmlData.childNodes().length == 0
    this.isSelected = false;
    this.isRemoved = false;
    this.indexInParent = null;
    this.hideAttribute = hideAttribute;
    this.loadData();
};

YAHOO.lang.extend(YAHOO.rapidjs.component.treegrid.TreeNode, YAHOO.rapidjs.component.RapidElement, {

    loadData: function() {
        var childData = this.xmlData.childNodes();
        this.processChildNodes(0, childData);
    },
    childAdded : function(newChild) {
        if (!this.hideAttribute || !newChild.getAttribute(this.hideAttribute)) {
            var childNode = new YAHOO.rapidjs.component.treegrid.TreeNode(newChild, this.contentPath, this.level + 1, this.hideAttribute, this.expandNodeAttribute);
            this.childNodes[this.childNodes.length] = childNode;
        }
    },
    setChildNodes: function(newChildNodes){
        this.childNodes = newChildNodes;
        this.isLeaf = this.childNodes.length == 0;
    },
    dataDestroyed: function() {
        this.isRemoved = true;
    },

    destroy: function() {
        this.childNodes = null;
        this.xmlData = null;
    },

    processChildNodes: function(lastIndex, childData)
    {
        if (childData)
        {
            var numberOfChilds = childData.length;
            var childToBeProcessed = lastIndex + 100;
            for (var index = lastIndex; index < childToBeProcessed && index < numberOfChilds; index++) {
                var childDataNode = childData[index];
                if (childDataNode.nodeType == 1 && childDataNode.nodeName == this.contentPath && !(this.hideAttribute && childDataNode.getAttribute(this.hideAttribute)))
                {
                    var childLevel = this.level + 1;
                    var childNode = new YAHOO.rapidjs.component.treegrid.TreeNode(childDataNode, this.contentPath, childLevel, this.hideAttribute, this.expandNodeAttribute);
                    this.childNodes[this.childNodes.length] = childNode;
                    childNode.indexInParent = this.childNodes.length;
                }
            }
            if (childToBeProcessed < numberOfChilds)
            {
                this.processChildNodes.defer(1, this, [childToBeProcessed, childData]);
            }
        }
    }
});
YAHOO.rapidjs.component.treegrid.TreeRootNode = function(xmlData, contentPath, hideAttribute, expandNodeAttribute) {
    YAHOO.rapidjs.component.treegrid.TreeRootNode.superclass.constructor.call(this, xmlData, contentPath, -1, hideAttribute, expandNodeAttribute);
};

YAHOO.lang.extend(YAHOO.rapidjs.component.treegrid.TreeRootNode, YAHOO.rapidjs.component.treegrid.TreeNode, {
    childAdded : function(newChild) {
        if (!this.hideAttribute || !newChild.getAttribute(this.hideAttribute)) {
            var childNode = new YAHOO.rapidjs.component.treegrid.TreeNode(newChild, this.contentPath, this.level + 1, this.hideAttribute, this.expandNodeAttribute);
            this.childNodes[this.childNodes.length] = childNode;
        }
    }
});