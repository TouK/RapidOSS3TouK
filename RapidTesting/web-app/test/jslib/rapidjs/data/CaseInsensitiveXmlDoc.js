YAHOO.rapidjs.data.CaseInsensitiveXmlDoc = function(response, indexingAttributes){
	YAHOO.rapidjs.data.CaseInsensitiveXmlDoc.superclass.constructor.call(this, response, indexingAttributes);
};

YAHOO.extendX(YAHOO.rapidjs.data.CaseInsensitiveXmlDoc, YAHOO.rapidjs.data.RapidXmlDocument, {
	createIndexes : function(){
	 	this.globalIndexes = {};
		if(this.indexingAttributes){
			var numberOfIndexingAttr = this.indexingAttributes.length;
			for(var index=0; index< numberOfIndexingAttr; index++) {
				this.globalIndexes[this.indexingAttributes[index]] =  new YAHOO.rapidjs.data.CaseInsensitiveMap();
			}
		}
	 }, 
	 createRootNode: function(){
		this.rootNode = YAHOO.rapidjs.data.NodeFactory.getRootNode(this, true);
	 },
	 findAllObjects: function(attributeName, attributeValue, tagName)
	 {
	 	var indexes = this.globalIndexes[attributeName];
		if(indexes != null)
		{
			var objects =  indexes.get(attributeValue);
			var returnedObjects = [];
			if(objects)
			{
				for(var index=0; index<objects.length; index++) {
					if(objects[index].nodeName == tagName)
					{
						returnedObjects[returnedObjects.length] = objects[index];
					}
				}
			}
			return returnedObjects;
		}
		return null;
	 }
});

YAHOO.rapidjs.data.CaseInsensitiveNode = function(mainDocument, xmlNode, nodeType, nodeName, indexingAttributes){
	YAHOO.rapidjs.data.CaseInsensitiveNode.superclass.constructor.call(this, mainDocument, xmlNode, nodeType, nodeName, indexingAttributes);
};
YAHOO.extendX(YAHOO.rapidjs.data.CaseInsensitiveNode, YAHOO.rapidjs.data.Node, {
	createIndexes: function(){
		this.indexes = {};
		if(this.indexingAttributes)
		{
			var numberOfIndexingAttr = this.indexingAttributes.length;
			for(var index=0; index< numberOfIndexingAttr; index++) {
				this.indexes[this.indexingAttributes[index]] =  new YAHOO.rapidjs.data.CaseInsensitiveMap();
			}
		}
	},
	
	findChildNode: function(attributeName, attributeValue, tagName)
	 {
	 	var indexMap = this.indexes[attributeName];
	 	if(indexMap)
	 	{
	 		var objects = indexMap.get(attributeValue);	
	 		var returnedObjects = [];
			if(objects)
			{
				for(var index=0; index<objects.length; index++) {
					if(objects[index].nodeName == tagName)
					{
						returnedObjects[returnedObjects.length] = objects[index];
					}
				}
			}
			return returnedObjects;
	 	}
		return null;
	 },
	
	addObjectToIndex: function(indexMap, attributeName, attributeValue, obj)
	 {
	 	var array = indexMap[attributeName].get(attributeValue);
		if(array == null)
		{
			array = [];
			indexMap[attributeName].put(attributeValue, array);
		}
		array[array.length] = obj;
	 },
	 removeIndexes: function(obj)
	 {
	 	if(this.indexingAttributes && this.indexes){
	 		var numberOfIndexingAttr = this.indexingAttributes.length;
			for(var index=0; index< numberOfIndexingAttr; index++) {
				var attributeName = this.indexingAttributes[index];
				var attributeValue = obj.getAttribute(attributeName) ;
				if(!attributeValue)
				{
					attributeValue = "undefined";
				}
				var indexMap = this.indexes[attributeName];
				this.findAndRemoveObject(indexMap.get(attributeValue), obj);
				if(indexMap.get(attributeValue) && indexMap.get(attributeValue).length == 0)
				{
					indexMap.remove(attributeValue);
				}
				if(this.mainDocument)
				{
					var globalIndexMap = this.mainDocument.globalIndexes[attributeName];
					this.findAndRemoveObject(globalIndexMap.get(attributeValue), obj);
					if(globalIndexMap.get(attributeValue) && globalIndexMap.get(attributeValue).length == 0)
					{
						globalIndexMap.remove(attributeValue);
					}
				}
			}
	 	}
	 	
	 }
});

YAHOO.rapidjs.data.CaseInsensitiveXmlNode = function(mainDocument, xmlNode, nodeType, nodeName, indexingAttributes){
	YAHOO.rapidjs.data.CaseInsensitiveXmlNode.superclass.constructor.call(this, mainDocument, xmlNode, nodeType, nodeName, indexingAttributes);
};

YAHOO.extendX(YAHOO.rapidjs.data.CaseInsensitiveXmlNode, YAHOO.rapidjs.data.CaseInsensitiveNode, {
	createChildNode: function(xmlNode, nodeType, nodeName)
	 {
	 	return new YAHOO.rapidjs.data.CaseInsensitiveXmlNode(this.mainDocument, xmlNode, nodeType, nodeName, this.indexingAttributes);
	 },
	 createChildNodes: function(xmlNode)
	 {
		this.children = [];
		this.attributes={};
		if(xmlNode != null)
		{
			this.createAttributes(xmlNode);
			var xmlNodeChildren = xmlNode.childNodes;
			if(xmlNodeChildren)
			{
				var numberOfChildNodes = xmlNodeChildren.length;
			 	if(numberOfChildNodes > 0)
			 	{
				 	var prevNode = null;
				 	for(var index=0; index<numberOfChildNodes; index++) {  
				 		var childNode = xmlNodeChildren[index];
				 		if(childNode.nodeType == 1)
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
YAHOO.rapidjs.data.CaseInsensitiveJsonNode = function(mainDocument, xmlNode, nodeType, nodeName, indexingAttributes){
	YAHOO.rapidjs.data.CaseInsensitiveJsonNode.superclass.constructor.call(this, mainDocument, xmlNode, nodeType, nodeName, indexingAttributes);
};

YAHOO.extendX(YAHOO.rapidjs.data.CaseInsensitiveJsonNode, YAHOO.rapidjs.data.CaseInsensitiveNode, {
	createChildNodes: function(xmlNode)
	 {
	 	this.attributes={};
	 	this.children = [];
	 	var childKey = null;
	 	var subChild = null;
	 	var newJsonNode = null;
	 	for(childKey in xmlNode) {
 			var child = xmlNode[childKey];
 			if(typeof child == "string"){
	 			this.attributes[childKey] = child;
	 		}
 			else if(child instanceof Array){
		 		var numberOfNodes = child.length;
		 		if(child.length == 0)
		 		{
		 			newJsonNode = this.createChildNode({}, this.nodeType, childKey);
			 		this.appendChild(newJsonNode);
		 		}
		 		else
		 		{
			 		for(var index=0; index<numberOfNodes; index++) {
			 			subChild = child[index];
			 			newJsonNode = this.createChildNode(subChild, this.nodeType, childKey);
			 			this.appendChild(newJsonNode);
			 		}
		 		}
		 		
	 		}
	 		else if(typeof child == "object"){
	 			newJsonNode = this.createChildNode(child, this.nodeType, childKey);
	 			this.appendChild(newJsonNode);
		 	}
 		}
	 },
	 createChildNode: function(xmlNode, nodeType, nodeName)
	 {
	 	return new YAHOO.rapidjs.data.CaseInsensitiveJsonNode(this.mainDocument, xmlNode, nodeName, this.indexingAttributes);
	 }
});
