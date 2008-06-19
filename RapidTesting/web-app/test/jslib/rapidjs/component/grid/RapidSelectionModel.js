YAHOO.rapidjs.component.grid.RapidSelectionModel = function()
{
	this.selectedRows = [];
    this.selectedRowIds = [];
    this.lastSelectedRow = null;
    
    this.onRowSelect = new YAHOO.util.CustomEvent('SelectionTable.rowSelected');
    this.onSelectionChange = new YAHOO.util.CustomEvent('SelectionTable.selectionChanged');
    
    this.events = {
       
	    'selectionchange' : this.onSelectionChange,
	    'rowselect' : this.onRowSelect
    };
    
    this.locked = false;
};

YAHOO.rapidjs.component.grid.RapidSelectionModel.prototype = 
{
    /** @ignore Called by the grid automatically. Do not call directly. */
    init : function(grid){
        this.grid = grid;
        this.initEvents();
    },
    
    /**
     * Lock the selections
     */
    lock : function(){
        this.locked = true;
    },
    
    /**
     * Unlock the selections
     */
    unlock : function(){
        this.locked = false;  
    },
    
    /**
     * Returns true if the selections are locked
     * @return {Boolean}
     */
    isLocked : function(){
        return this.locked;    
    },
    
    /** @ignore */
    initEvents : function(){
        if(this.grid.trackMouseOver){
        	this.grid.addListener("mouseover", this.handleOver, this, true);
        	this.grid.addListener("mouseout", this.handleOut, this, true);
        }
        this.grid.addListener("rowclick", this.rowClick, this, true);
        this.grid.addListener("keydown", this.keyDown, this, true);
    },
    
    fireEvent : YAHOO.ext.util.Observable.prototype.fireEvent,
    on : YAHOO.ext.util.Observable.prototype.on,
    addListener : YAHOO.ext.util.Observable.prototype.addListener,
    delayedListener : YAHOO.ext.util.Observable.prototype.delayedListener,
    removeListener : YAHOO.ext.util.Observable.prototype.removeListener,
    purgeListeners : YAHOO.ext.util.Observable.prototype.purgeListeners,
    bufferedListener : YAHOO.ext.util.Observable.prototype.bufferedListener,
    
    /** @ignore Syncs selectedRows with the correct row by looking it up by id. 
      Used after a sort moves data around. */
    syncSelectionsToIds : function(){
        /*if(this.getCount() > 0){
            var rows = this.selectedRows.concat();
            //this.clearSelections();
            this.selectRows(rows, false);
        }*/
    },
    
    selectRowsById : function(id, keepExisting){
        if (!(id instanceof Array)){
            this.selectRow(id, keepExisting);
            return;
        }
        this.selectRows(id, keepExisting);
    },
    
    
    getCount : function(){
        return this.selectedRows.length;
    },
    
    selectFirstRow : function(){
    	var numberOfRows = this.grid.dataModel.data.length;
        for(var j = 0; j < numberOfRows; j++){
            if(this.isSelectable(this.grid.dataModel.data[j])){
            	this.focusRow(this.grid.dataModel.data[j]);
                this.setRowState(this.grid.dataModel.data[j], true);
                return;
            }
        }
    },
    
    /**
     * Selects the row immediately following the last selected row.
     * @param {<i>Boolean</i>} keepExisting (optional) True to retain existing selections
     */
    selectNext : function(keepExisting){
        if(this.lastSelectedRow){
            for(var j = (this.lastSelectedRow.dummyRow.index+1); j < this.grid.dataModel.data.length; j++){
                var row = this.grid.dataModel.data[j];
                if(this.isSelectable(row)){
                    this.focusRow(row);
                    this.setRowState(row, true, keepExisting);
                    return;
                }
            }
        }
    },
    
    /**
     * Selects the row that precedes the last selected row.
     * @param {<i>Boolean</i>} keepExisting (optional) True to retain existing selections 
     */
    selectPrevious : function(keepExisting){
        if(this.lastSelectedRow){
            for(var j = (this.lastSelectedRow.dummyRow.index-1); j >= 0; j--){
                var row = this.grid.dataModel.data[j];
                if(this.isSelectable(row)){
                    this.focusRow(row);
                    this.setRowState(row, true, keepExisting);
                    return;
                }
            }
        }
    },
    
    /**
     * Returns the selected rows.
     * @return {Array} Array of DOM row elements
     */
    getSelectedRows : function(){
        return this.selectedRows;
    },
    
    /**
     * Returns the selected row ids.
     * @return {Array} Array of String ids
     */
    getSelectedRowIds : function(){
        return this.selectedRowIds;
    },
    
    /**
     * Clears all selections.
     */
    clearSelections : function(currentSelectedNode){
        if(this.isLocked()) return;
        var oldSelections = this.selectedRows.concat();
        for(var j = 0; j < oldSelections.length; j++){
        	if(!currentSelectedNode || currentSelectedNode != oldSelections[j])
            this.setRowState(oldSelections[j], false);
        }
        this.selectedRows = [];
        this.selectedRowIds = [];
    },
    
        
    /**
     * Selects all rows.
     */
    selectAll : function(){
        if(this.isLocked()) return;
        this.selectedRows = [];
        this.selectedRowIds = [];
        for(var j = 0, len = this.grid.dataModel.data.length; j < len; j++){
            this.setRowState(this.grid.dataModel.data[j], true, true);
        }
    },
    
    /**
     * Returns True if there is a selection.
     * @return {Boolean}
     */
    hasSelection : function(){
        return this.selectedRows.length > 0;
    },
    
    /**
     * Returns True if the specified row is selected.
     * @param {HTMLElement} row The row to check
     * @return {Boolean}
     */
    isSelected : function(row){
        return row && row.selected === true;
    },
    
    /**
     * Returns True if the specified row is selectable.
     * @param {HTMLElement} row The row to check
     * @return {Boolean}
     */
    isSelectable : function(row){
        return row && row.selectable != false;
    },
    
    /** @ignore */
    rowClick : function(grid, rowIndex, e){
        if(this.isLocked()) return;
        var row = grid.dataModel.data[rowIndex];
        if(this.isSelectable(row)){
            if(e.shiftKey && this.lastSelectedRow){
                var lastIndex = this.lastSelectedRow.dummyRow.index;
                this.selectRange(this.lastSelectedRow, row, e.ctrlKey);
                this.lastSelectedRow = this.grid.dataModel.data[lastIndex];
            }else{
                this.focusRow(row);
                var rowState = e.ctrlKey ? !this.isSelected(row) : true;
                this.setRowState(row, rowState, e.hasModifier());
            }
        }
    },
    
    /**
     * Deprecated. Tries to focus the row and scroll it into view - Use grid.scrollTo or grid.getView().focusRow() instead.
     * @deprecated
     * @param {HTMLElement} row The row to focus
     */
    focusRow : function(row){
    	this.grid.view.focusRow(row.dummyRow.index);
    },

    /**
     * Selects a row.
     * @param {Number/HTMLElement} row The row or index of the row to select
     * @param {<i>Boolean</i>} keepExisting (optional) True to retain existing selections 
     */
    selectRow : function(row, keepExisting){
        this.setRowState(this.getRow(row), true, keepExisting);
    },
    
    /**
     * Selects multiple rows.
     * @param {Array} rows Array of the rows or indexes of the row to select
     * @param {<i>Boolean</i>} keepExisting (optional) True to retain existing selections 
     */
    selectRows : function(rows, keepExisting){
        if(!keepExisting){
            this.clearSelections();
        }
        for(var i = 0; i < rows.length; i++){
            this.selectRow(rows[i], true);
        }
    },
    
    /**
     * Deselects a row.
     * @param {Number/HTMLElement} row The row or index of the row to deselect
     */
    deselectRow : function(row){
        this.setRowState(this._getSelectedRow(row), false);
    },
    
    /** @ignore */
    getRow : function(row){
        if(typeof row == 'number' || typeof row == 'string'){
            row = this.grid.dataModel.data[row*1];
        }
        return row;
    },
    
    /**
     * Selects a range of rows. All rows in between startRow and endRow are also selected.
     * @param {Number/HTMLElement} startRow The row or index of the first row in the range
     * @param {Number/HTMLElement} endRow The row or index of the last row in the range
     * @param {<i>Boolean</i>} keepExisting (optional) True to retain existing selections 
     */
    selectRange : function(startRow, endRow, keepExisting){
        startRow = this.getRow(startRow);
        endRow = this.getRow(endRow);
        this.setRangeState(startRow, endRow, true, keepExisting);
    },
    
    _getSelectedRow: function(row)
    {
    	if(typeof row == 'number' || typeof row == 'string'){
    		row = row*1;
        	var sr = this.selectedRows;
	        for (var i = 0; i < sr.length; i++) {
	          if (sr[i].dummyRow.index === row){
	              return sr[i];
	          }
	        } 
	        return null;   
        }
        return row;
    },
    /**
     * Deselects a range of rows. All rows in between startRow and endRow are also deselected.
     * @param {Number/HTMLElement} startRow The row or index of the first row in the range
     * @param {Number/HTMLElement} endRow The row or index of the last row in the range
     */
    deselectRange : function(startRow, endRow){
    	if(startRow == endRow)
    	{
    		this.setRowState(this._getSelectedRow(startRow), false, true);
    	}
    	else
    	{
	    	var tempStartRow = this._getSelectedRow(startRow);
	    	var tempEndRow = this._getSelectedRow(endRow);
	    	if(startRow)
	    	{
	    		startRow = tempStartRow.dummyRow.index;
	    	}
	    	if(tempEndRow)
	    	{
	    		endRow = tempEndRow.dummyRow.index;
	    	}
	    	if(endRow == null)
	    	{
	    		endRow = startRow;	
	    	}
	    	startRow = startRow*1;
	    	endRow = endRow*1;
	    	var rows = this.selectedRows.concat();
	    	for(var i=0; i < rows.length; i++)
	    	{
	    		if(rows[i].dummyRow.index >= startRow && rows[i].dummyRow.index <= endRow)
	    		{
	    			this.setRowState(rows[i], false, true);
	    		}
	    	}
    	}
    },
    
    /** @ignore */
    setRowStateFromChild : function(childEl, selected, keepExisting){
        var row = this.grid.getRowFromChild(childEl);
        this.setRowState(row, selected, keepExisting);
    },
    
    /** @ignore */
    setRangeState : function(startRow, endRow, selected, keepExisting){
        if(this.isLocked()) return;
        if(!keepExisting){
            this.clearSelections();
        }
        var curRow = startRow;
        if(curRow && endRow)
        {
	        while(curRow.dummyRow.index != endRow.dummyRow.index){
	            this.setRowState(curRow, selected, true);
	            curRow = (startRow.dummyRow.index < endRow.dummyRow.index ? 
	                        this.grid.getRowAfter(curRow) : this.grid.getRowBefore(curRow))
	        }
	        this.setRowState(endRow, selected, true);
        }
    },
    
    /** @ignore */
    setRowState : function(row, selected, keepExisting){
        if(this.isLocked()) return;
        if(this.isSelectable(row)){
        	var willFireEvent = false;
            if(selected){
                if(!keepExisting){
                    this.clearSelections(row);
                }
                this.setRowClass(this.grid.getRow(row.dummyRow.index), 'selected');
                willFireEvent = !row.selected || row.selected == false;
                row.selected = true;
                this.selectedRows.push(row);
                this.selectedRowIds.push(row.dummyRow.index);
                this.lastSelectedRow = row;
            }else{
            	var renderedRow = this.grid.getRow(row.dummyRow.index);
            	if(renderedRow)
               		this.setRowClass(renderedRow, '');
                willFireEvent = row.selected == true;
                row.selected = false;
                this._removeSelected(row);
            }
            if(willFireEvent == true)
            {
	            this.fireEvent('rowselect', this, row, selected);
	            this.fireEvent('selectionchange', this, this.selectedRows, this.selectedRowIds);
            }
        }
    },

    /** @ignore */
    handleOver : function(e){
        var row = this.grid.getRowFromChild(e.getTarget());
        if(this.isSelectable(this.grid.dataModel.data[row.rowIndex]) && !this.isSelected(this.grid.dataModel.data[row.rowIndex])){
            this.setRowClass(row, 'over');
        }
    },
    
    /** @ignore */
    handleOut : function(e){
        var row = this.grid.getRowFromChild(e.getTarget());
        if(this.isSelectable(this.grid.dataModel.data[row.rowIndex]) && !this.isSelected(this.grid.dataModel.data[row.rowIndex])){
            this.setRowClass(row, '');
        }
    },
    
    /** @ignore */
    keyDown : function(e){
        if(e.browserEvent.keyCode == e.DOWN){
            this.selectNext(e.shiftKey);
            e.preventDefault();
        }else if(e.browserEvent.keyCode == e.UP){
            this.selectPrevious(e.shiftKey);
            e.preventDefault();
        }
    },

    /** @ignore */
    setRowClass : function(row, cssClass){
        if(this.isSelectable(row)){
            if(cssClass == 'selected'){
                YAHOO.util.Dom.removeClass(row, 'ygrid-row-over');
                YAHOO.util.Dom.addClass(row, 'ygrid-row-selected');
            }else if(cssClass == 'over'){
                YAHOO.util.Dom.removeClass(row, 'ygrid-row-selected');
                YAHOO.util.Dom.addClass(row, 'ygrid-row-over');
            }else if(cssClass == ''){
                YAHOO.util.Dom.removeClass(row, 'ygrid-row-selected');
                YAHOO.util.Dom.removeClass(row, 'ygrid-row-over');
            }
        }
    },
    
    /** @ignore */
    _removeSelected : function(row){
        var sr = this.selectedRows;
        for (var i = 0; i < sr.length; i++) {
          if (sr[i] === row){
          	  YAHOO.rapidjs.ArrayUtils.remove(this.selectedRows, i);	
              YAHOO.rapidjs.ArrayUtils.remove(this.selectedRowIds, i);
              return;
          }
        }
    }
    
};