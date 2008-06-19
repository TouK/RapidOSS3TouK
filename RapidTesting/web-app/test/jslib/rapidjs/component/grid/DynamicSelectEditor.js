YAHOO.rapidjs.component.grid.DynamicSelectEditor = function(element, rootTag, contentPath, attribute){
	YAHOO.ext.grid.SelectEditor.superclass.constructor.call(this, element);
	this.rootTag = rootTag;
	this.contentPath = contentPath;
	this.attribute = attribute;
};

YAHOO.extendX(YAHOO.rapidjs.component.grid.DynamicSelectEditor, YAHOO.ext.grid.SelectEditor, {
	startEditing : function(value, row, cell){
        this.originalValue = value;
        this.rowIndex = row.rowIndex;
        var data = this.grid.dataModel.data[this.rowIndex].dummyRow.xmlData;
        this.colIndex = cell.columnIndex;
        this.cell = cell;
        this.setValue(value);
        var cellbox = getEl(cell, true).getBox();
        this.fitToCell(cellbox);
        this.editing = true;
        this.createOptions(data);
        this.show();
    }, 
    
    createOptions : function(node){
    	var selectChildren = this.element.dom.childNodes;
    	for(var index=0; index<selectChildren.length; index++) {
    		var child = selectChildren[index];
    		this.element.dom.removeChild(child);
    	}
    	var roots = node.getElementsByTagName(this.rootTag);
    	if(roots.length > 0){
    		var rootNode = roots[0];
    		var newSelects = rootNode.getElementsByTagName(this.contentPath);
    		for(var index=0; index<newSelects.length; index++) {
    			var value = newSelects[index].getAttribute(this.attribute);
    			YAHOO.ext.DomHelper.append(this.element.dom, {tag:'option', value:value, html:value});
    		}
    	}		
    }
});