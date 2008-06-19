YAHOO.rapidjs.component.grid.RapidXmlDataModel = function(config)
{
	YAHOO.rapidjs.component.grid.RapidXmlDataModel.superclass.constructor.call(this, config);
	this.fieldIndexes = {};
	var fields = this.schema.fields;
	var numberOfFields = fields.length;
	for(var index=0; index<numberOfFields; index++) {
		this.fieldIndexes[fields[index]]= index;
	}
	this.isSortingDisabled = false;
	this.rootRow = null;
};

YAHOO.extendX(YAHOO.rapidjs.component.grid.RapidXmlDataModel, YAHOO.ext.grid.XMLDataModel, {
	setRootNode: function(rootNode)
	{
		this.rootRow = new YAHOO.rapidjs.component.grid.GridRootNode(rootNode,  this);
	},
	removeRows: function(startIndex, endIndex, removeFromData){
        endIndex = endIndex || startIndex;
        if(!(removeFromData == false))
        this.data.splice(startIndex, endIndex-startIndex+1);
        this.fireRowsDeleted(startIndex, endIndex);
    },
	getNamedValue: function(node, name, defaultValue)
	{
		if(!node || !name){
    		return defaultValue;
    	}
		var attr = node.getAttribute(name);
		if(attr == null)
		{
			return defaultValue;
		}
		return attr;
	},
    loadData: function(doc, callback, keepExisting, insertIndex){
    	this.isSortingDisabled = true;
    	YAHOO.rapidjs.component.grid.RapidXmlDataModel.superclass.loadData.call(this, doc, callback, keepExisting, insertIndex);
    	var numberOfRows = this.data.length;
       	for(var index=0; index<numberOfRows; index++) {
       		var dataNode = this.data[index];
       		if(dataNode.dummyRow == null)
       		{
       			dataNode.dummyRow = new YAHOO.rapidjs.component.grid.GridRow(dataNode.node, this.fieldIndexes, this);
       		}
       		dataNode.dummyRow.index = index;
       		
       	}
       	this.isSortingDisabled = false;
    },
    addRow: function(id, cellValues){
        var res = YAHOO.rapidjs.component.grid.RapidXmlDataModel.superclass.addRow.call(this, id, cellValues);
        if(res >= 0)
        {
	        cellValues.dummyRow = new YAHOO.rapidjs.component.grid.GridRow(cellValues.node, this.fieldIndexes, this);
	        cellValues.dummyRow.index = this.data.length;
        }
        return res;
    },
    
    insertRow: function(index, id, cellValues){
        var res = YAHOO.rapidjs.component.grid.RapidXmlDataModel.superclass.insertRow.call(this, index, id, cellValues);
        if(res  >= 0)
        {
	        cellValues.dummyRow = new YAHOO.rapidjs.component.grid.GridRow(cellValues.node, this.fieldIndexes, this);
	        cellValues.dummyRow.index = this.data.length;
        }
        return res;
    },
    
    sort: function(sortInfo, columnIndex, direction, suppressEvent){
       if(this.isSortingDisabled == false){
       	   // store these so we can maintain sorting when we load new data
	        this.sortInfo = sortInfo;
	        this.sortColumn = columnIndex;
	        this.sortDir = direction;
	        
	        var dsc = (direction && direction.toUpperCase() == 'DESC');
	        var sortType = null;
	        if(sortInfo != null){
	            if(typeof sortInfo == 'function'){
	                sortType = sortInfo;
	            }else if(typeof sortInfo == 'object'){
	                sortType = sortInfo.getSortType(columnIndex);;
	            }
	        }
	        var fn = function(cells, cells2){
	            var v1 = sortType ? sortType(cells[columnIndex], cells) : cells[columnIndex];
	            var v2 = sortType ? sortType(cells2[columnIndex], cells2) : cells2[columnIndex];
	            if(v1 < v2)
	    			return dsc ? +1 : -1;
	    		else if(v1 > v2)
	    			return dsc ? -1 : +1;
	    		else if(v1 == v2)
	    		{
	    			if(cells.dummyRow.index < cells2.dummyRow.index)
	    				return -1;
	    			else if(cells.dummyRow.index > cells2.dummyRow.index)
	    				return +1;
	    		}
	    	    return 0;
	        };
	        this.data.sort(fn);
	       var numberOfRows = this.data.length;
	       for(var index=0; index<numberOfRows; index++) {
	       		this.data[index].dummyRow.index = index;
	       }
	       if(!suppressEvent){
	           this.fireRowsSorted(columnIndex, direction);
	       }
       }
       
    },
    
     setNamedValue: function(node, name, value){
     },
     
     setValueAt: function(value, rowIndex, colIndex){
     	YAHOO.rapidjs.component.grid.RapidXmlDataModel.superclass.setValueAt.call(this, value, rowIndex, colIndex);
     	if(colIndex == this.sortColumn)
     	{
     		this.applySort();
     	}
     },
     
     purgeRemovedData: function()
     {
     	var newData = [];
     	var newIndex = 0;
     	for(var index=0;index<this.data.length; index++) {
     		if(this.data[index].dummyRow.isRemoved == true)
     		{
     			this.removeRows(index, index, false);
     		}
     		else
     		{
     			this.data[index].dummyRow.index = newIndex;
     			newData[newIndex] = this.data[index];
     			newIndex++;
     		}
     	}
     	this.data = newData;
     },
     
     applySort: function(suppressEvent){
    	if(this.isSortingDisabled == false){
    		YAHOO.rapidjs.component.grid.RapidXmlDataModel.superclass.applySort.call(this, suppressEvent);
    	}
     },
     createNode: function(xmlDoc, id, colData){
        return colData.node;
    }
     
});