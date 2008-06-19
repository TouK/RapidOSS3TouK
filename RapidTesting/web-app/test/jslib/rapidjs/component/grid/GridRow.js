YAHOO.rapidjs.component.grid.GridRow = function(xmlData, fieldIndexes, dataModel)
{
	YAHOO.rapidjs.component.grid.GridRow.superclass.constructor.call(this, xmlData);
	this.index = -1;
	this.fieldIndexes = fieldIndexes;
	this.dataModel = dataModel;
	this.isRemoved = false;
};

YAHOO.extendX(YAHOO.rapidjs.component.grid.GridRow, YAHOO.rapidjs.component.RapidElement, 
{
	dataDestroyed: function(){
		this.isRemoved = true;
		this.dataModel = null;
	},
	dataChanged: function(attributeName, attributeValue)
	{
		var colIndex = this.fieldIndexes[attributeName];
		if(colIndex != null)
		{
			this.dataModel.setValueAt(attributeValue, this.index, colIndex)
		}
	}
});

YAHOO.rapidjs.component.grid.GridRootNode = function(xmlData, dataModel)
{
	YAHOO.rapidjs.component.grid.GridRootNode.superclass.constructor.call(this, xmlData);
	this.dataModel = dataModel;
};

YAHOO.extendX(YAHOO.rapidjs.component.grid.GridRootNode, YAHOO.rapidjs.component.RapidElement, 
{
	childAdded : function(newChild)
	{
		if(newChild.nodeName != this.dataModel.schema.tagName)
			return;
		var colData = [];
    	colData.node = newChild;
		var fields = this.dataModel.schema.fields;
        for(var j = 0; j < fields.length; j++) {
            var val = this.dataModel.getNamedValue(newChild, fields[j], "");
            if(this.dataModel.preprocessors[j]){
                val = this.dataModel.preprocessors[j](val);
            }
            colData.push(val);
        }
		this.dataModel.addRow(null, colData);
	}
});