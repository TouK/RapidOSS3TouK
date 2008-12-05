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