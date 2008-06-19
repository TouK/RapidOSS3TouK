YAHOO.rapidjs.component.grid.RapidEditorSelectionModel = function(){
    YAHOO.rapidjs.component.grid.RapidEditorSelectionModel.superclass.constructor.call(this);
    this.clicksToActivateCell = 1;
    this.events['cellactivate'] = new YAHOO.util.CustomEvent('cellactivate');
};

YAHOO.extendX(YAHOO.rapidjs.component.grid.RapidEditorSelectionModel, YAHOO.rapidjs.component.grid.RapidSelectionModel, {
	initEvents: function(){
		this.grid.addListener("cellclick", this.onCellClick, this, true);
	    this.grid.addListener("celldblclick", this.onCellDblClick, this, true);
	    this.grid.addListener("keydown", this.keyDown, this, true);
	}, 
	
	onCellClick : function(grid, rowIndex, colIndex, e){
		YAHOO.rapidjs.component.grid.RapidEditorSelectionModel.superclass.rowClick.call(this, grid, rowIndex, e);
	    if(this.clicksToActivateCell == 1){
	        var row = this.grid.getRow(rowIndex);
	        var cell = row.childNodes[colIndex];
	        if(cell){
	            this.activate(row, cell);
	        }
	    } 
	}, 
	activate : function(row, cell){
	    this.fireEvent('cellactivate', this, row, cell);
	    this.grid.doEdit(row, cell);
	}, 
	
	onCellDblClick : function(grid, rowIndex, colIndex, e){
		YAHOO.rapidjs.component.grid.RapidEditorSelectionModel.superclass.rowClick.call(this, grid, rowIndex, e);
	    if(this.clicksToActivateCell == 2){
	        var row = this.grid.getRow(rowIndex);
	        var cell = row.childNodes[colIndex];
	        if(cell){
	            this.activate(row, cell);
	        }
	    }
	}, 
	
	focusRow : function(row, selected){
		
	},
	
	getEditorCellAfter : function(cell, spanRows){
	    var g = this.grid;
	    var next = g.getCellAfter(cell);
	    while(next && !g.colModel.isCellEditable(next.columnIndex)){
	        next = g.getCellAfter(next);
	    }
	    if(!next && spanRows){
	        var row = g.getRowAfter(g.getRowFromChild(cell));
	        if(row){
	            next = g.getFirstCell(row);
	            if(!g.colModel.isCellEditable(next.columnIndex)){
	                next = this.getEditorCellAfter(next);
	            }
	        }
	    }
	    return next;
	}, 
	
	getEditorCellBefore : function(cell, spanRows){
	    var g = this.grid;
	    var prev = g.getCellBefore(cell);
	    while(prev && !g.colModel.isCellEditable(prev.columnIndex)){
	        prev = g.getCellBefore(prev);
	    }
	    if(!prev && spanRows){
	        var row = g.getRowBefore(g.getRowFromChild(cell));
	        if(row){
	            prev = g.getLastCell(row);
	            if(!g.colModel.isCellEditable(prev.columnIndex)){
	               prev = this.getEditorCellBefore(prev);
	            }
	        }
	    }
	    return prev;
	}, 
	
	allowArrowNav : function(e){
    	return (!this.disableArrowNavigation && (!this.controlForArrowNavigation || e.ctrlKey));
	}, 
	
	keyDown : function(e){
	    var g = this.grid, cm = g.colModel, cell = g.getEditingCell();
	    if(!cell) return;
	    var newCell;
	    switch(e.browserEvent.keyCode){
	         case e.TAB:
	             if(e.shiftKey){
	                 newCell = this.getEditorCellBefore(cell, true);
	             }else{
	                 newCell = this.getEditorCellAfter(cell, true);
	             }
	             e.preventDefault();
	         break;
	         case e.DOWN:
	             if(this.allowArrowNav(e)){
	                 var next = g.getRowAfter(g.getRowFromChild(cell));
	                 if(next){
	                     newCell = next.childNodes[cell.columnIndex];
	                 }
	             }
	         break;
	         case e.UP:
	             if(this.allowArrowNav(e)){
	                 var prev = g.getRowBefore(g.getRowFromChild(cell));
	                 if(prev){
	                     newCell = prev.childNodes[cell.columnIndex];
	                 }
	             }
	         break;
	         case e.RETURN:
	             if(e.shiftKey){
	                 var prev = g.getRowBefore(g.getRowFromChild(cell));
	                 if(prev){
	                     newCell = prev.childNodes[cell.columnIndex];
	                 }
	             }else{
	                 var next = g.getRowAfter(g.getRowFromChild(cell));
	                 if(next){
	                     newCell = next.childNodes[cell.columnIndex];
	                 }
	             }
	         break;
	         case e.RIGHT:
	             if(this.allowArrowNav(e)){
	                 newCell = this.getEditorCellAfter(cell);
	             }
	         break;
	         case e.LEFT:
	             if(this.allowArrowNav(e)){
	                 newCell = this.getEditorCellBefore(cell);
	             }
	         break;
	    };
	    if(newCell){
	        this.activate(g.getRowFromChild(newCell), newCell);
	        e.stopEvent();
	    }
	}
	
});

YAHOO.rapidjs.component.grid.RapidEditorSelectionModel.prototype.disableArrowNavigation = false;
YAHOO.rapidjs.component.grid.RapidEditorSelectionModel.prototype.controlForArrowNavigation = false;
