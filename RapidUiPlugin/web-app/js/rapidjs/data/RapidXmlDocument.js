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
YAHOO.namespace('rapidjs', 'rapidjs.data');


YAHOO.rapidjs.data.DataUtils =  new function()
{
    this.convertToMap = function(node)
    {
        var attributes = {}
        var xmlAttributes = node.attributes
        if (xmlAttributes != null)
        {
            var nOfAtts = xmlAttributes.length
            for (var index = 0; index < nOfAtts; index++) {
                var attNode = xmlAttributes.item(index);
                attributes[attNode.nodeName] = attNode.nodeValue;
            }
        }
        return  attributes;
    }
}
YAHOO.rapidjs.data.RapidXmlDocument = function(response, indexingAttributes)
{
    this.response = response;
    this.indexingAttributes = indexingAttributes;
    this.createIndexes();
    this.createRootNode();

}

YAHOO.rapidjs.data.RapidXmlDocument.prototype =
{
    createRootNode: function() {
        this.rootNode = YAHOO.rapidjs.data.NodeFactory.getRootNode(this);
    },
    childNodes: function()
    {
        return this.rootNode.childNodes();
    },

    nextSibling: function()
    {
        return this.rootNode.nextSibling();
    },

    firstChild: function()
    {
        return this.rootNode.firstChild();
    },

    lastChild: function()
    {
        return this.rootNode.lastChild();
    },

    getRootNode: function(tagName)
    {
        return this.rootNode.getElementByTagName(tagName);
    },
    getElementsByTagName: function(tagName)
    {

        return this.rootNode.getElementsByTagName(tagName);
    },

    findChildNode: function(attributeName, attributeValue, tagName)
    {
        return this.rootNode.findChildNode(attributeName, attributeValue, tagName);
    },

    findAllObjects: function(attributeName, attributeValue, tagName)
    {
        if (this.globalIndexes[attributeName] != null)
        {
            var objects = this.globalIndexes[attributeName][attributeValue];
            var returnedObjects = [];
            if (objects)
            {
                for (var index = 0; index < objects.length; index++) {
                    if (objects[index].nodeName == tagName)
                    {
                        returnedObjects[returnedObjects.length] = objects[index];
                    }
                }
            }
            return returnedObjects;
        }
        return null;
    },
    toString: function()
    {
        return this.rootNode.toString();
    },
    getIndex: function()
    {
        return this.globalIndexes;
    },

    createIndexes : function() {
        this.globalIndexes = {};
        if (this.indexingAttributes) {
            var numberOfIndexingAttr = this.indexingAttributes.length;
            for (var index = 0; index < numberOfIndexingAttr; index++) {
                this.globalIndexes[this.indexingAttributes[index]] = {};
            }
        }
    }
};


YAHOO.rapidjs.data.Node = function(mainDocument, xmlNode, nodeType, nodeName, indexingAttributes)
{
    this.children = null;
    this.attributes = null;
    this.nSibling = null;
    this.pSibling = null;
    this.fChild = null;
    this.lChild = null;
    this.pNode = null;
    this.index = -1;
    this.nodeType = nodeType;
    this.nodeName = nodeName;
    this.subscribers = [];
    this.mainDocument = mainDocument;
    this.indexingAttributes = indexingAttributes;
    this.createIndexes();
    this.createChildNodes(xmlNode);
};

YAHOO.rapidjs.data.Node.prototype =
{
    createIndexes: function() {
        this.indexes = {};
        if (this.indexingAttributes)
        {
            var numberOfIndexingAttr = this.indexingAttributes.length;
            for (var index = 0; index < numberOfIndexingAttr; index++) {
                this.indexes[this.indexingAttributes[index]] = {};
            }
        }
    },
    getIndex: function()
    {
        return this.indexes;
    },
    toString: function()
    {
        var escapeAttribute = function(att) {
            var APOS = "'"
            var LT = "<"
            var GT = ">"
            var AMP = "&"
            var ESCAPED_APOS = '&apos;'
            var ESCAPED_LT = '&lt;'
            var ESCAPED_GT = '&gt;'
            var ESCAPED_AMP = '&amp;'
            var aposRE = new RegExp(APOS, 'g')
            var ltRE = new RegExp(LT, 'g')
            var gtRE = new RegExp(GT, 'g')
            var ampRE = new RegExp(AMP, 'g')
            att = att.replace(ampRE, ESCAPED_AMP)
            att = att.replace(aposRE, ESCAPED_APOS)
            att = att.replace(ltRE, ESCAPED_LT)
            att = att.replace(gtRE, ESCAPED_GT)
            return att;
        }
        var result = [];
        result[result.length] = "<";
        result[result.length] = this.nodeName;
        for (var attribute in this.attributes)
        {
            result[result.length] = " ";
            result[result.length] = attribute;
            result[result.length] = "='";
            result[result.length] = escapeAttribute(this.attributes[attribute]);
            result[result.length] = "'";
        }
        result[result.length] = ">";
        var cNodes = this.childNodes();
        for (var i = 0; i < cNodes.length; i++)
        {
            result[result.length] = cNodes[i].toString();
        }
        result[result.length] = "</";
        result[result.length] = this.nodeName;
        result[result.length] = ">";
        return result.join("");
    },
    fireChildAdded: function(newChild)
    {
        var numberOfSubscribers = this.subscribers.length;
        for (var index = 0; index < numberOfSubscribers; index++) {
            if (this.subscribers[index].childAdded)
            {
                this.subscribers[index].childAdded(newChild);
            }
        }
    },
    fireChildAddedBefore: function(newChild, refChild)
    {
        var numberOfSubscribers = this.subscribers.length;
        for (var index = 0; index < numberOfSubscribers; index++) {
            if (this.subscribers[index].childAddedBefore)
            {
                this.subscribers[index].childAddedBefore(newChild, refChild);
            }
        }
    },
    fireChildRemoved: function(oldChild)
    {
        var numberOfSubscribers = this.subscribers.length;
        for (var index = 0; index < numberOfSubscribers; index++) {
            if (this.subscribers[index].childRemoved)
            {
                this.subscribers[index].childRemoved(oldChild);
            }
        }
    },
    fireNodeDestroyed: function()
    {
        var numberOfSubscribers = this.subscribers.length;
        for (var index = 0; index < numberOfSubscribers; index++) {
            if (this.subscribers[index].dataDestroyed)
            {
                this.subscribers[index].dataDestroyed();
            }
        }
    },
    fireDataChanged: function(attributeName, attributeValue)
    {
        var numberOfSubscribers = this.subscribers.length;
        for (var index = 0; index < numberOfSubscribers; index++) {
            if (this.subscribers[index].dataChanged)
            {
                this.subscribers[index].dataChanged(attributeName, attributeValue);
            }
        }
    },
    fireBatchDataChanged: function()
    {
        var numberOfSubscribers = this.subscribers.length;
        for (var index = 0; index < numberOfSubscribers; index++) {
            if (this.subscribers[index].batchDataChanged)
            {
                this.subscribers[index].batchDataChanged();
            }
        }
    },
    fireMergeStarted: function()
    {
        var numberOfSubscribers = this.subscribers.length;
        for (var index = 0; index < numberOfSubscribers; index++) {
            if (this.subscribers[index].mergeStarted)
            {
                this.subscribers[index].mergeStarted();
            }
        }
    },
    fireMergeFinished: function()
    {
        var numberOfSubscribers = this.subscribers.length;
        for (var index = 0; index < numberOfSubscribers; index++) {
            if (this.subscribers[index].mergeFinished)
            {
                this.subscribers[index].mergeFinished();
            }
        }
    },
    subscribe: function(rapidElement)
    {
        var numberOfSubscribers = this.subscribers.length;
        for (var index = 0; index < numberOfSubscribers; index++) {
            if (this.subscribers == rapidElement)
            {
                return;
            }
        }
        this.subscribers[numberOfSubscribers] = rapidElement;
    },
    unSubscribe: function(rapidElement)
    {
        var numberOfSubscribers = this.subscribers.length;
        for (var index = 0; index < numberOfSubscribers; index++) {
            if (this.subscribers == rapidElement)
            {
                YAHOO.rapidjs.ArrayUtils.remove(this.subscribers, index);
                return;
            }
        }
    },
    unsubscribeAll: function(rapidElement)
    {
        this.subscribers = [];
    },

    createAttributes: function(xmlNode)
    {
        if (xmlNode != null)
        {
            var attributeNodes = xmlNode.attributes;
            if (attributeNodes != null)
            {
                var nOfAtts = attributeNodes.length
                for (var index = 0; index < nOfAtts; index++) {
                    var attNode = attributeNodes.item(index);
                    this.attributes[attNode.nodeName] = attNode.nodeValue;
                }
            }
        }
    },

    childNodes: function(xmlNode)
    {
        return this.children;
    },

    getAttributes: function()
    {
        return this.attributes;
    },

    nextSibling: function()
    {
        return this.nSibling;
    },

    firstChild: function()
    {
        return this.fChild;
    },

    lastChild: function()
    {
        return this.lChild;
    },

    parentNode : function()
    {
        return this.pNode;
    },

    previousSibling : function() {
        return this.pSibling;
    },

    appendChild: function (newChild, recursive)
    {
        var numberOfChildren = this.children.length;
        newChild.nSibling = null;
        newChild.pSibling = null;
        newChild.pNode = null;
        newChild.mainDocument = null;
        if (numberOfChildren > 0)
        {
            var lastChild = this.children[numberOfChildren - 1];
            lastChild.nSibling = newChild;
            newChild.pSibling = lastChild;
        }
        else
        {
            this.fChild = newChild;
        }
        this.lChild = newChild;
        newChild.pNode = this;
        this.children[numberOfChildren] = newChild;
        newChild.index = numberOfChildren;
        this.fireChildAdded(newChild);
        this.indexWithAttributes(newChild, recursive, true);
        return newChild;
    },

    findChildNode: function(attributeName, attributeValue, tagName)
    {
        if (this.indexes[attributeName])
        {
            var objects = this.indexes[attributeName][attributeValue];
            var returnedObjects = [];
            if (objects)
            {
                for (var index = 0; index < objects.length; index++) {
                    if (objects[index].nodeName == tagName)
                    {
                        returnedObjects[returnedObjects.length] = objects[index];
                    }
                }
            }
            return returnedObjects;
        }
        return null;
    },

    indexWithAttributes: function(obj, recursive, indexToParent)
    {
        obj.mainDocument = this.mainDocument;
        if (this.indexingAttributes) {
            var numberOfIndexingAttr = this.indexingAttributes.length;
            for (var index = 0; index < numberOfIndexingAttr; index++) {
                var attributeName = this.indexingAttributes[index];
                var attributeValue = obj.getAttribute(attributeName) ;
                if (!attributeValue)
                {
                    attributeValue = "undefined";
                }
                if (indexToParent == true) {
                    this.addObjectToIndex(this.indexes, attributeName, attributeValue, obj);
                }
                if (this.mainDocument)
                    this.addObjectToIndex(this.mainDocument.globalIndexes, attributeName, attributeValue, obj);
            }
        }
        if (recursive == true)
        {
            var childObjects = obj.childNodes();
            for (var i = 0; i < childObjects.length; i++)
            {
                obj.indexWithAttributes(childObjects[i], true, false);
            }
        }
    },

    addObjectToIndex: function(indexMap, attributeName, attributeValue, obj)
    {
        var array = indexMap[attributeName][attributeValue];
        if (array == null)
        {
            array = [];
            indexMap[attributeName][attributeValue] = array;
        }
        array[array.length] = obj;
    },

    findAndRemoveObject: function(array, object)
    {
        if (array == null)
        {
            return;
        }
        for (var index = 0; index < array.length; index++) {
            if (array[index] == object)
            {
                YAHOO.rapidjs.ArrayUtils.remove(array, index);
                return;
            }
        }
    },

    removeIndexes: function(obj)
    {
        if (this.indexingAttributes && this.indexes) {
            var numberOfIndexingAttr = this.indexingAttributes.length;
            for (var index = 0; index < numberOfIndexingAttr; index++) {
                var attributeName = this.indexingAttributes[index];
                var attributeValue = obj.getAttribute(attributeName) ;
                if (!attributeValue)
                {
                    attributeValue = "undefined";
                }
                this.findAndRemoveObject(this.indexes[attributeName][attributeValue], obj);
                if (this.indexes[attributeName][attributeValue] && this.indexes[attributeName][attributeValue].length == 0)
                {
                    delete this.indexes[attributeName][attributeValue];
                }
                if (this.mainDocument)
                {
                    this.findAndRemoveObject(this.mainDocument.globalIndexes[attributeName][attributeValue], obj);
                    if (this.mainDocument.globalIndexes[attributeName][attributeValue] && this.mainDocument.globalIndexes[attributeName][attributeValue].length == 0)
                    {
                        delete this.mainDocument.globalIndexes[attributeName][attributeValue];
                    }
                }
            }
        }

    },

    cloneNode : function (deep, generateIndexAttributes)
    {
        var node = this.createChildNode(null, this.nodeType, this.nodeName);
        var cloneAttributes = {};
        var clonedChilren = [];
        for (var attribute in this.attributes) {
            cloneAttributes[attribute] = this.attributes[attribute];
        }
        if (generateIndexAttributes) {
            var numberOfIndexingAttr = this.indexingAttributes.length;
            for (var index = 0; index < numberOfIndexingAttr; index++) {
                var indexingAtt = this.indexingAttributes[index];
                var attValue = indexingAtt + (YAHOO.rapidjs.data.RapidXmlDocument.nextNumber ++);
                cloneAttributes[indexingAtt] = attValue;
            }
        }
        if (deep == true)
        {
            var numberOfChildren = this.children.length;
            for (var index = 0; index < numberOfChildren; index++) {
                clonedChilren[index] = this.children[index].cloneNode(deep);
            }
        }
        node.mainDocument = this.mainDocument;
        node.attributes = cloneAttributes;
        for (var index = 0; index < clonedChilren.length; index++) {
            node.appendChild(clonedChilren[index])
        }
        return node;
    },

    setAttribute: function(attributeName, attributeValue)
    {
        if (this.attributes[attributeName] != attributeValue)
        {
            this.attributes[attributeName] = attributeValue;
            this.fireDataChanged(attributeName, attributeValue);
        }
    },

    removeAttribute: function(attributeName)
    {
        delete this.attributes[attributeName]
        this.fireDataChanged(attributeName, null);
    },

    getAttribute: function(attributeName)
    {

        return this.attributes[attributeName];

    },


    hasAttributes: function()
    {
        if (this.attributes)
        {
            for (var attr in this.attributes) {
                return true;
            }
        }
        return false;
    },

    hasChildNodes: function()
    {
        return this.children.length > 0;
    },

    insertBefore: function(newChild, refChild)
    {
        var index = refChild.index;
        if (index > -1)
        {
            newChild.pNode = this;
            var prev = refChild.pSibling;
            if (prev) {
                prev.nSibling = newChild;
            }
            else
            {
                this.fChild = newChild;
            }

            refChild.pSibling = newChild;
            newChild.pSibling = prev;
            newChild.nSibling = refChild;
            var numberOfChildren = this.children.length;
            this.children[numberOfChildren] = newChild;
            newChild.index = numberOfChildren;
            this.fireChildAdded(newChild);
            this.indexWithAttributes(newChild, true, true);
            return newChild;
        }
        return null;
    },

    removeChild: function (oldChild) {
        var index = oldChild.index;
        if (index != -1)
        {
            YAHOO.rapidjs.ArrayUtils.remove(this.children, index);
            if (this.children.length > index)
            {
                this.children[index].index = index;
            }
            var prev = oldChild.pSibling;
            var next = oldChild.nSibling;
            if (prev) {
                prev.nSibling = next;
            }
            else
            {
                this.fChild = next;
            }
            if (next) {
                next.pSibling = prev;
            }
            else
            {
                this.lChild = prev;
            }
            this.fireChildRemoved(oldChild);
            oldChild.destroy();
        }

    },

    destroy: function() {
        this.pNode.removeIndexes(this);
        this.fireNodeDestroyed();
        this.unsubscribeAll();
        this.pSibling = null;
        this.nSibling = null;
        this.fChild = null;
        this.lChild = null;
        this.pNode = null;
        if (this.children) {
            var numberOfNodes = this.children.length;
            for (var index = 0; index < numberOfNodes; index++) {
                this.children[index].destroy();
            }
        }
        this.children = null;
        this.indexes = null;
    },

    getElementByTagName: function(tagName)
    {
        if (this.nodeName == tagName)
        {
            return this;
        }
        else
        {
            var childNodes = this.childNodes();
            var numberOfChildNodes = childNodes.length;
            for (var index = 0; index < numberOfChildNodes; index++) {
                var res = childNodes[index].getElementByTagName(tagName);
                if (res)
                {
                    return res;
                }
            }
            return null;
        }
    },

    getElementsByTagName: function(tagName, array)
    {
        if (array)
        {
            if (this.nodeName == tagName)
            {
                array[array.length] = this;
            }
        }
        else
        {
            array = [];
            array.item = function(index)
            {
                return array[index];
            };
        }
        var childNodes = this.childNodes();
        var numberOfChildNodes = childNodes.length;
        for (var index = 0; index < numberOfChildNodes; index++) {
            childNodes[index].getElementsByTagName(tagName, array);
        }
        return array;
    },

    mergeData : function(newData, keyAttribute, keepExisting, removeAttribute) {
        this.fireMergeStarted();
        if (newData) {
            var newAttributes = newData.getAttributes();
            for (var attribute in newAttributes)
            {
                this.setAttribute(attribute, newAttributes[attribute]);
            }
            this.fireBatchDataChanged();
            var oldChildren = this.childNodes();
            if (!keepExisting || keepExisting == false)
            {
                for (var index = 0; index < oldChildren.length; index++) {
                    var oldChild = oldChildren[index];
                    var newChild = newData.findChildNode(keyAttribute, oldChild.getAttribute(keyAttribute), oldChild.nodeName);
                    if (!newChild || newChild.length == 0)
                    {
                        this.removeChild(oldChild);
                        index--;
                    }

                }
            }
            var newChildren = newData.childNodes();
            for (var index = 0; index < newChildren.length; index++) {
                var newChild = newChildren[index];
                if (removeAttribute && newChild.getAttribute(removeAttribute) == "true")
                {
                    var oldChild = this.findChildNode(keyAttribute, newChild.getAttribute(keyAttribute), newChild.nodeName);
                    if (oldChild && oldChild.length > 0)
                    {
                        this.removeChild(oldChild[0]);
                        index--;
                    }
                }
                else
                {
                    if (newChild.getAttribute(keyAttribute) == null)
                    {
                        var found = false;
                        var oldChildIndex = 0;
                        while (found == false && oldChildIndex < oldChildren.length) {
                            if (oldChildren[oldChildIndex].nodeName == newChild.nodeName) {
                                this.removeChild(oldChildren[oldChildIndex]);
                                found = true;
                            }
                            oldChildIndex ++;
                        }
                        this.appendChild(newChild);
                    }
                    else
                    {
                        var oldChild = this.findChildNode(keyAttribute, newChild.getAttribute(keyAttribute), newChild.nodeName);
                        if (oldChild && oldChild.length > 0)
                        {
                            oldChild[0].mergeData(newChild, keyAttribute);
                        }
                        else
                        {
                            this.appendChild(newChild, true);
                        }
                    }
                }

            }
        }
        else {
            if (!keepExisting || keepExisting == false)
            {
                var oldChildren = this.childNodes();
                while (oldChildren.length > 0) {
                    this.removeChild(oldChildren[oldChildren.length - 1]);
                }

            }
        }
        this.fireMergeFinished();
    },

    //Abstract methods
    createChildNodes: function(xmlNode)
    {
        alert('Override createChildNodes');
    },


    createChildNode: function(xmlNode, nodeType, nodeName)
    {
        alert('Override createChildNode');
    }

};

YAHOO.rapidjs.data.RapidXmlNode = function(mainDocument, xmlNode, nodeType, nodeName, indexingAttributes) {
    YAHOO.rapidjs.data.RapidXmlNode.superclass.constructor.call(this, mainDocument, xmlNode, nodeType, nodeName, indexingAttributes);
};

YAHOO.lang.extend(YAHOO.rapidjs.data.RapidXmlNode, YAHOO.rapidjs.data.Node, {
    createChildNode: function(xmlNode, nodeType, nodeName)
    {
        return new YAHOO.rapidjs.data.RapidXmlNode(this.mainDocument, xmlNode, nodeType, nodeName, this.indexingAttributes);
    },
    createChildNodes: function(xmlNode)
    {
        this.children = [];
        this.attributes = {};
        if (xmlNode != null)
        {
            this.createAttributes(xmlNode);
            var xmlNodeChildren = xmlNode.childNodes;
            if (xmlNodeChildren)
            {
                var numberOfChildNodes = xmlNodeChildren.length;
                if (numberOfChildNodes > 0)
                {
                    var prevNode = null;
                    for (var index = 0; index < numberOfChildNodes; index++) {
                        var childNode = xmlNodeChildren[index];
                        if (childNode.nodeType == 1)
                        {
                            var newRapidNode = this.createChildNode(childNode, childNode.nodeType, childNode.nodeName);
                            this.appendChild(newRapidNode);
                        }
                    }
                }
            }
        }
    }
});


YAHOO.rapidjs.data.RapidJsonNode = function(mainDocument, xmlNode, nodeName, indexingAttributes)
{
    YAHOO.rapidjs.data.RapidJsonNode.superclass.constructor.call(this, mainDocument, xmlNode, 1, nodeName, indexingAttributes);
};

YAHOO.lang.extend(YAHOO.rapidjs.data.RapidJsonNode, YAHOO.rapidjs.data.Node, {
    createChildNodes: function(xmlNode)
    {
        this.attributes = {};
        this.children = [];
        var childKey = null;
        var subChild = null;
        var newJsonNode = null;
        for (childKey in xmlNode) {
            var child = xmlNode[childKey];
            if (typeof child == "string") {
                this.attributes[childKey] = child;
            }
            else if (child instanceof Array) {
                var numberOfNodes = child.length;
                if (child.length == 0)
                {
                    newJsonNode = this.createChildNode({}, this.nodeType, childKey);
                    this.appendChild(newJsonNode);
                }
                else
                {
                    for (var index = 0; index < numberOfNodes; index++) {
                        subChild = child[index];
                        newJsonNode = this.createChildNode(subChild, this.nodeType, childKey);
                        this.appendChild(newJsonNode);
                    }
                }

            }
            else if (typeof child == "object") {
                newJsonNode = this.createChildNode(child, this.nodeType, childKey);
                this.appendChild(newJsonNode);
            }
        }
    },
    createChildNode: function(xmlNode, nodeType, nodeName)
    {
        return new YAHOO.rapidjs.data.RapidJsonNode(this.mainDocument, xmlNode, nodeName, this.indexingAttributes);
    }
});

YAHOO.rapidjs.data.RapidXmlDocument.nextNumber = 0;