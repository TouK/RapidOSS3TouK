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