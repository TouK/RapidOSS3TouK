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
YAHOO.rapidjs.component.grid.RapidGridView = function()
{
	YAHOO.rapidjs.component.grid.RapidGridView.superclass.constructor.call(this);
	this.linkClickedEvent = new YAHOO.util.CustomEvent('linkclicked');
	this.IsRendered = false;
	this.beginingIndex = 0;
	this.currentInterval = 0;
	this.scrollRenderingTask = new YAHOO.ext.util.DelayedTask(this._renderRows, this);
};

YAHOO.extendX(YAHOO.rapidjs.component.grid.RapidGridView, YAHOO.ext.grid.GridView, {
	render : function(){
		var returnVal = YAHOO.rapidjs.component.grid.RapidGridView.superclass.render.call(this);
		this.contextMenuClicked = new YAHOO.util.CustomEvent("contextMenuClicked");
		YAHOO.util.Event.addListener(this.pwrap.dom, "contextmenu", this.handleContextMenu, this, true);
		YAHOO.util.Event.addListener(this.pwrap.dom, "click", this.handleToolClick, this, true);
		this.IsRendered = true;
		this.createEmptyMiddleDiv();
		return returnVal;
    },
    addTooltip: function(){
    	this.tooltip = true;
    	YAHOO.util.Event.addListener(this.pwrap.dom,'mouseover',this.updateTooltip, this, true);
    	Tooltip.add(this.pwrap.dom, '');
    	
    },
    
    updateTooltip : function(event){
		var target = YAHOO.util.Event.getTarget(event);
		var cell = this.grid.getCellFromChild(target);
        if(cell){
        	var row = this.grid.getRowFromChild(target);
            Tooltip.update(this.pwrap.dom, this.grid.dataModel.getValueAt(row.rowIndex, cell.columnIndex));
        }
        else{
        	Tooltip.update(this.pwrap.dom, '');
        }   	
    }, 
    
    createEmptyMiddleDiv: function()
    {
    	var bt = this.getBodyTable();
		this.divStart = document.createElement("div");
		this.divMiddle = document.createElement("div");
		this.divMiddle.rows = [];
		bt.appendChild(this.divStart);
		bt.appendChild(this.divMiddle);
        this.divMiddle.style.overflow = "hidden";
        this.divMiddle.style.height = "0px";
        this.divStart.style.height = "0px";
        this.divStart.style.display = "none";
    },
    
    createTool : function(parentEl, className){
        var btn = document.createElement('div');
        YAHOO.util.Dom.addClass(btn, 'ylayout-tools-button');
        var btnInner = document.createElement('div');
        btnInner.innerHTML = '&#160;';
        YAHOO.util.Dom.addClass(btnInner, 'ylayout-tools-button-inner');
        if(className)
        YAHOO.util.Dom.addClass(btnInner, className);
        YAHOO.util.Event.addListener(btn, "mouseover", function(){
        	YAHOO.util.Dom.addClass(this, 'ylayout-tools-button-over');
        }, btn, true);
        YAHOO.util.Event.addListener(btn, "mouseout", function(){
            YAHOO.util.Dom.removeClass(this, 'ylayout-tools-button-over');
        }, btn, true);
        btn.appendChild(btnInner);
        parentEl.appendChild(btn);
        return btn;
    },
    on : function(eventName, handler, scope, override){
        YAHOO.util.Event.addListener(this.dom, eventName, handler, scope || this, true);
        return this;
    },
    addClassOnOver : function(el, className){
        
        return this;
    },
    
    createEmptyRows: function(rowCount)
    {
    	var colCount = this.grid.colModel.getColumnCount();
        for(var rowIndex = 0; rowIndex < rowCount; rowIndex++){
        	var row = document.createElement('span');
            row.className = 'ygrid-row';
            this.divMiddle.appendChild(row);
            this.divMiddle.rows[this.divMiddle.rows.length] = row;
	        for(var colIndex = 0; colIndex < colCount; colIndex++){
	            var td = document.createElement('span');
	            td.className = 'ygrid-col ygrid-col-' + colIndex + (colIndex == colCount-1 ? ' ygrid-col-last' : '');
	            td.columnIndex = colIndex;
	            td.tabIndex = 0;
	            var span = document.createElement('span');
	            span.className = 'ygrid-cell-text';
	            if(this.grid.colModel.config[colIndex].type=="Image" || this.grid.colModel.config[colIndex].type=="image")
            	{
            		YAHOO.util.Dom.setStyle(span, "padding", "0px 0px");
            		YAHOO.util.Dom.setStyle(span, "width", "100%");
            		YAHOO.util.Dom.setStyle(span, "height", "100%");
		            var imageEl = YAHOO.ext.DomHelper.append(span, {tag:'div', cls:'ygrid-cell-image'});
					span.appendChild(imageEl);
            	}
            	else if(this.grid.colModel.config[colIndex].type=="Link" || this.grid.colModel.config[colIndex].type=="Link")
            	{
            		var innerSpan = document.createElement('span');
            		innerSpan.type = "link";
            		YAHOO.util.Dom.addClass(innerSpan,"ygrid-link");
            		innerSpan.action = this.grid.colModel.config[colIndex].action;
            		span.appendChild(innerSpan);
            	}
            	else if(this.grid.colModel.config[colIndex].type=="Action" || this.grid.colModel.config[colIndex].type=="action")
            	{
            		var tableEl = document.createElement('table');
            		tableEl.cellPadding= "0";
            		tableEl.cellSpacing= "0";
            		tableEl.border= "0";
            		var tableBody = document.createElement('tbody');
            		var tableRow = document.createElement('tr');
            		YAHOO.util.Dom.setStyle(span, "padding", "0px 0px");
            		span.appendChild(tableEl);
            		tableEl.appendChild(tableBody);
            		tableBody.appendChild(tableRow);
            		
            		var actions = this.grid.colModel.config[colIndex].actions;
            		if(actions)
            		{
	            		for(var index=0; index<actions.length; index++) {
	            			var tableCol = document.createElement('td');
	            			tableRow.appendChild(tableCol);
	            			var imageEl = this.createTool(tableCol, 'ygrid-row-actionimage');
	            			imageEl.actionFunction = actions[index]["handler"];
	            			imageEl.actionScope = actions[index]["scope"];
	            			imageEl.type = "action";
	            			imageEl.firstChild.type = "action";
	            			imageEl.firstChild.actionFunction = actions[index]["handler"];
	            			imageEl.firstChild.actionScope = actions[index]["scope"];
				            imageEl.firstChild.style.background = "url('"+actions[index]["image"]+"') no-repeat right 0px";
				            if(actions[index]['tooltip']){
				            	imageEl.setAttribute('title', actions[index]['tooltip']);
				            }
							tableCol.appendChild(imageEl);
//							YAHOO.util.Event.addListener(imageEl, "click", this.handleToolClick, this, true);
	            		}
            		}
            	}
	            
	            td.appendChild(span);
	            row.appendChild(td);
	            row.style.display = "none";
	        }
        }
    },
    
    
    handleToolClick: function(e)
    {
    	
    	var target = YAHOO.util.Event.getTarget(e);
    	if(target.type == "action" && target.actionFunction)
    	{
    		var row = this.grid.getRowFromChild(target);
    		if(row){
    			target.actionFunction.call(target.actionScope || window, this.grid.dataModel.data[row.rowIndex].node);
    		}
    	}
    	else if(target.type == "link")
    	{
    		var row = this.grid.getRowFromChild(target);
    		if(row){
    			var node = this.grid.dataModel.data[row.rowIndex].node;
    			if(target.action)
    			{
    				this.linkClickedEvent.fireDirect(target.action, node.getAttributes(), node);
    			}
    		}
    	}
    },
    
    getViewRowIndex: function(rowIndex)
    {
    	var index = this.beginingIndex;
		var interval = this.currentInterval;
		if(rowIndex >= index + interval)
			return -1;
		else
		{
			return rowIndex - index;
		}
    },
    
    getRow: function(index)
    {
    	return this.divMiddle.rows[index - this.beginingIndex];
    },
    
    handleContextMenu : function(event){
    	var x = YAHOO.util.Event.getPageX(event);
    	
    	var target = YAHOO.util.Event.getTarget(event);
        var row = this.grid.getRowFromChild(target);
        if(row){
        	if(this.grid.selModel){
	        	this.grid.selModel.selectRow(row.rowIndex, false);
	        }
	        var node = this.grid.dataModel.data[row.rowIndex].dummyRow.xmlData;
	        this.fireContextMenuClicked(event, node);
        }
    }, 
    
    handleSort : function(dataModel, sortColumnIndex, sortDir, noRefresh){
    	YAHOO.rapidjs.component.grid.RapidGridView.superclass.handleSort.call(this, dataModel, sortColumnIndex, sortDir, noRefresh);
    	this.handleScroll();
    },
    
    focusRow : function(row){
        if(typeof row == 'number'){
            row = this.grid.dataModel.data[row];
        }
        if(!row) return;
        this._ensureVisible(row);
//        this.lastFocusedRow = row;
    },

    _ensureVisible : function(row){
        if(typeof row == 'number'){
            row = this.grid.dataModel.data[row];
        }
        if(!row) return;
    	var left = this.wrap.scrollLeft;
    	var rowTop = row.dummyRow.index * this.getRowHeight();
        var rowBottom = rowTop + this.getRowHeight();
        var clientTop = parseInt(this.wrap.scrollTop, 10); // parseInt for safari bug
        var clientBottom = clientTop + this.wrap.clientHeight;
        if(rowTop < clientTop){
        	this.wrap.scrollTop = rowTop;
        }else if(rowBottom > clientBottom){
            this.wrap.scrollTop = rowBottom-this.wrap.clientHeight;
        }
        this.wrap.scrollLeft = left;
        this.handleScroll();
    },
    
    fireContextMenuClicked: function(event, node){
    	this.contextMenuClicked.fireDirect(event, node);
    },
    
    fireScroll: function(scrollLeft, scrollTop){
		
		this.onScroll.fireDirect(this.grid, scrollLeft, scrollTop);
		//this._insertRows(this.grid.dataModel, index, lastRow);
		if(this.prevScrollLeft == scrollLeft)
		{
			this.scrollRenderingTask.cancel();
			this.scrollRenderingTask.delay(50, this._renderRows, this, [this.grid.dataModel, scrollTop]);
//			this._renderRows(this.grid.dataModel, scrollTop);
		}
		this.prevScrollLeft = scrollLeft;
	},
	
	_renderRows: function(dataModel, scrollTop)
	{
		if(!this.divMiddle || !this.divStart) return;
		var index = Math.floor(scrollTop / this.getRowHeight());
		var interval = this.wrap.clientHeight/this.getRowHeight();
		interval = Math.floor(interval + 2);
		if(dataModel.getRowCount() < index + interval)
		{
			index = dataModel.getRowCount() - interval;
		}
		if(index < 0 )
		{
			index = 0;
			interval = dataModel.getRowCount();
		}
		
		if(interval > this.divMiddle.rows.length)
		{
			this.createEmptyRows(interval - this.divMiddle.rows.length);
		}
		else if(interval < this.divMiddle.rows.length)
		{
			while(this.divMiddle.rows.length > interval) {
//				YAHOO.util.Event.purgeElement(this.divMiddle.rows[this.divMiddle.rows.length-1], true);
				this.divMiddle.removeChild(this.divMiddle.rows[this.divMiddle.rows.length-1]);
				YAHOO.rapidjs.ArrayUtils.remove(this.divMiddle.rows, this.divMiddle.rows.length-1);
			}
		}
		
		if(index == 0)
		{
			this.divStart.style.display = "none";
		}
		else
		{
			this.divStart.style.display = "block";
		}
		this.currentInterval = interval;
		this.beginingIndex = index;
		var height = index * this.getRowHeight();
		this.divStart.style.height = (height)+"px";
		this.divMiddle.style.height = (interval*this.getRowHeight())+"px";

		var renderers = this.getColumnRenderers();
        var dindexes = this.getDataIndexes();
        var colCount = this.grid.colModel.getColumnCount();
		
		for(var rowIndex = 0; rowIndex < interval; rowIndex++){
			var row = this.divMiddle.rows[rowIndex];
			row.style.display = "block";
			var realRowIndex = index+ rowIndex;
			this.renderRow(dataModel, row, realRowIndex, colCount, renderers, dindexes);
			row.rowIndex = realRowIndex;
			if(this.grid.selModel)
			if(dataModel.data[realRowIndex].selected == true){
	        	this.grid.selModel.selectRow(dataModel.data[realRowIndex], false);
	        }
	        else
	        {
	        	this.grid.selModel.deselectRow(dataModel.data[realRowIndex]);
	        }
        }
	},
	
    updateRowIndexes: function(firstRow, lastRow){
    },

	insertRows: function(dataModel, firstRow, lastRow){
	},
	
	getImageSource: function(colModel, xmlData, colIndex)
	{
    	var expressionsArray = colModel.config[colIndex]['images'];
    	for(var i = 0 ; i < expressionsArray.length ; i++)
		{
			var currentExpressionStr = expressionsArray[i]['visible'];
			var data = xmlData.getAttributes();
			var evaluationResult = eval(currentExpressionStr);
			if(evaluationResult == true)
			{
				return expressionsArray[i]['src'];
			}
		}
		return null;
	},
	getBackgroundColor: function(xmlData)
	{
    	var expressionsArray = this.grid.rowColors;
    	if(expressionsArray)
    	for(var i = 0 ; i < expressionsArray.length ; i++)
		{
			var currentExpressionStr = expressionsArray[i]['visible'];
			var data = xmlData.getAttributes();
			var evaluationResult = eval(currentExpressionStr);
			if(evaluationResult == true)
			{
				return [expressionsArray[i]['color'], expressionsArray[i]['textColor']];
			}
		}
		
		return null;
	},
	
	renderRow : function(dataModel, row, rowIndex, colCount, renderers, dindexes){
		var color = this.getBackgroundColor(dataModel.data[rowIndex].node);
		if(color)
			row.style.backgroundColor = color[0];
        for(var colIndex = 0; colIndex < colCount; colIndex++){
        	var td = row.childNodes[colIndex];
            var span = row.childNodes[colIndex].firstChild;
            var val = renderers[colIndex](dataModel.getValueAt(rowIndex, dindexes[colIndex]), rowIndex, colIndex, td);
            var columnConfig = this.grid.colModel.config[colIndex];
            if(columnConfig.type=="Image" || columnConfig.type=="image")
            {
            	var imageEl = span.firstChild;
            	var src = this.getImageSource(this.grid.colModel, dataModel.data[rowIndex].node, colIndex);
            	if(src)
            	{
            		imageEl.style.backgroundImage = 'url(\'' + src + '\')';
            		imageEl.style.backgroundPosition = 	columnConfig.align || 'left';
            		
            	}
            	else{
            		imageEl.style.backgroundImage = '';
            	}
            }
            else if(columnConfig.type=="Action" || columnConfig.type=="action")
            {
            	var actions = columnConfig.actions;
            	var images = span.getElementsByTagName('td');
        		if(actions)
        		{
            		for(var index=0; index<actions.length; index++) {
            			var currentExpressionStr = actions[index]['visible'];
            			if(currentExpressionStr){
            				var data = dataModel.data[rowIndex].node.getAttributes();
							var evaluationResult = eval(currentExpressionStr);
							if(evaluationResult == false)
							{
								YAHOO.util.Dom.setStyle(images[index], 'display', 'none');
							}
							else
							{
								YAHOO.util.Dom.setStyle(images[index], 'display', '');
							}
            			}
            		}
        		}
            }
            else
            {
            	if(columnConfig.type=="Link" || columnConfig.type=="link")
	            {
	            	span = span.firstChild;
	            }
	            if(typeof val == 'undefined' || val === '') val = '&#160;';
	            span.innerHTML = val;
	            if(color)
	            span.style.color = color[1];
            }
        }
    },
   
    
    deleteRows: function(dataModel, firstRow, lastRow){
        // first make sure they are deselected
        this.grid.selModel.deselectRange(firstRow, lastRow);
    },
    
    updateRows: function(dataModel, firstRow, lastRow){
        
    },
    
    
    renderRows: function(dataModel){
        this.grid.stopEditing();
        if(this.grid.selModel){
            this.grid.selModel.clearSelections();
        }
    },
    
    updateCell: function(dataModel, rowIndex, dataIndex){
        var viewRowIndex = this.getViewRowIndex(rowIndex);
        if(viewRowIndex >= 0)
        {
        	var row = this.divMiddle.rows[viewRowIndex];
        	var renderers = this.getColumnRenderers();
	        var dindexes = this.getDataIndexes();
	        var colCount = this.grid.colModel.getColumnCount();
        	this.renderRow(dataModel, row, rowIndex, colCount, renderers, dindexes);
        }
    },
    
    calcColumnWidth: function(colIndex, maxRowsToMeasure){
        var maxWidth = 0;
        var bt = this.getBodyTable();
        var rows = this.divMiddle.rows;
        var stopIndex = Math.min(maxRowsToMeasure || rows.length, rows.length);
        if(this.grid.autoSizeHeaders){
            var h = this.headers[colIndex];
            var curWidth = h.style.width;
            h.style.width = this.grid.minColumnWidth+'px';
            maxWidth = Math.max(maxWidth, h.scrollWidth);
            h.style.width = curWidth;
        }
        for(var i = 0; i < stopIndex; i++){
        	if(rows[i])
        	{
	            var cell = rows[i].childNodes[colIndex].firstChild;
	            maxWidth = Math.max(maxWidth, cell.scrollWidth);
        	}
        }
        return maxWidth + /*margin for error in IE*/ 5;
    },
    updateWrapHeight : function(){
    	YAHOO.rapidjs.component.grid.RapidGridView.superclass.updateWrapHeight.call(this);
    	try
    	{
    		this.handleScroll();
    	}
    	catch(e)
    	{
    	}
    }
    
});