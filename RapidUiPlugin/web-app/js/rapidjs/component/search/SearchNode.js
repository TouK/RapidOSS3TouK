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
YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.search');
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