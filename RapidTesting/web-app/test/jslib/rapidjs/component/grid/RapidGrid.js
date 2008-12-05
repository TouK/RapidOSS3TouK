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
YAHOO.rapidjs.component.grid.RapidGrid = function(container, dataModel, colModel, selectionModel)
{
	YAHOO.rapidjs.component.grid.RapidGrid.superclass.constructor.call(this, container, dataModel, colModel, selectionModel);
	for(var index=0; index<colModel.getColumnCount(); index++) {
		var colCssName = "#"+this.id + " .ygrid-col-"+index;
		this.addColCss(colCssName);
	}
	
	var colCssName = "#"+this.id + " .ygrid-col-last";
	this.addColCss(colCssName);
	YAHOO.ext.util.CSS.getRules(true);
};

YAHOO.extendX(YAHOO.rapidjs.component.grid.RapidGrid, YAHOO.ext.grid.Grid, {
	addColCss: function(cssName)
	{
		var ds = document.styleSheets;
		if(ds.length > 0)
		{
	        if(ds[0].insertRule)
	        	ds[0].insertRule(cssName + " {padding:0px;} ", 0); 
	        else if(ds[0].addRule)
	        	ds[0].addRule(cssName," {padding:0px;} "); 
		}
	},
	
	setRowColorConfig: function(rowColors)
	{
		this.rowColors = rowColors;
	},
	
	render : function(){
        if((!this.container.dom.offsetHeight || this.container.dom.offsetHeight < 20) 
                || this.container.getStyle('height') == 'auto'){
    	    this.autoHeight = true;   
    	}	       
    	if((!this.container.dom.offsetWidth || this.container.dom.offsetWidth < 20)){
    	    this.autoWidth = true;   
    	}	       
    	if(!this.view){
    	    this.view = new YAHOO.rapidjs.component.grid.RapidGridView();
    	}
    	this.view.init(this);
        this.el = getEl(this.view.render(), true);
        var c = this.container;
        c.mon("click", this.onClick, this, true);
        c.mon("dblclick", this.onDblClick, this, true);
        //c.mon("contextmenu", this.onContextMenu, this, true);
        c.mon("selectstart", this.cancelTextSelection, this, true);
        c.mon("mousedown", this.cancelTextSelection, this, true);
        c.mon("mousedown", this.onMouseDown, this, true);
        c.mon("mouseup", this.onMouseUp, this, true);
        if(this.trackMouseOver){
            this.el.mon("mouseover", this.onMouseOver, this, true);
            this.el.mon("mouseout", this.onMouseOut, this, true);
        }
        c.mon("keypress", this.onKeyPress, this, true);
        c.mon("keydown", this.onKeyDown, this, true);
        this.init();
    },
    
    getRow : function(index){
    	var row = this.view.getRow(index);
    	return row;
    },
	
    getRowsById : function(id){
        var dm = this.dataModel;
        var numberOfRows = dm.data.length;
        if(!(id instanceof Array)){
            for(var i = 0; i < numberOfRows; i++){
                if(dm.getRowId(i) == id){
                    return this.getRow(i);
                }
            }
            return null;
        }
        var found = [];
        var re = "^(?:";
        for(var i = 0; i < id.length; i++){
            re += id[i];
            if(i != id.length-1) re += "|";
        }
        var regex = new RegExp(re + ")$");
        for(var i = 0; i < numberOfRows; i++){
            if(regex.test(dm.getRowId(i))){
                found.push(this.getRow(i));
            }
        }
        return found;
    },
    
    getSelectedRowIndexes : function(){
        var a = [];
        var rows = this.selModel.getSelectedRows();
        for(var i = 0; i < rows.length; i++) {
        	a[i] = rows.index;
        }
        return a;
    },
    
    getRowAfter : function(row){
    	if(typeof row != 'number'){
            row = row.index;
        }
        return this.getRow(row+1);
    },
    
    getRowBefore : function(row){
    	if(typeof row != 'number'){
            row = row.index;
        }
        
        return this.getRow(row-1);
    },
    
    scrollTo : function(row){
//        this.view.ensureVisible(row, true);
    },
    
    startEditing : function(rowIndex, colIndex){
//        var row = this.rows[rowIndex];
//        var cell = row.childNodes[colIndex];
//        this.stopEditing();
//        setTimeout(this.doEdit.createDelegate(this, [row, cell]), 10);
    }, 
    
    destroy: function(){
    	this.dataModel.removeAll();
    	YAHOO.rapidjs.component.grid.RapidGrid.superclass.destroy.call(this);
    	this.dataModel.purgeListeners();
    	this.dataModel = null;
    	this.selModel.purgeListeners();
    	this.selModel = null;
    }
});