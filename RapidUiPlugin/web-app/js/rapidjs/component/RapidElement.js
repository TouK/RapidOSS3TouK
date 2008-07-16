YAHOO.namespace('rapidjs', 'rapidjs.component');
YAHOO.rapidjs.component.RapidElement= function(xmlData)
{
	this.xmlData = xmlData;
	this.registerToxmlDataEvents();
};

YAHOO.rapidjs.component.RapidElement.prototype=
{
	registerToxmlDataEvents: function()
	{
		this.xmlData.subscribe(this);
	},
	
	childAdded : function(newChild){},
	childAddedBefore : function(newChild, refChild){},
	childRemoved : function(oldChild){},
	dataChanged : function(attributeName, attributeValue){},
	batchDataChanged : function(){},
	dataDestroyed : function(){}, 
	mergeStarted: function(){},
	mergeFinished: function(){}
	
};