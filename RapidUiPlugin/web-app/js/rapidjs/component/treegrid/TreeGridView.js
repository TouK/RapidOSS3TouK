YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.treegrid');
YAHOO.rapidjs.component.treegrid.TreeGridView = function(container,config){
	this.container = container;
	this.columns = null;
	this.headers = [];
	this.rootNode = null;
	this.rootImages - null;
	this.contentPath = null;
	this.expandedNodes = [];
	this.events = {
		'selectionchanged' : new YAHOO.util.CustomEvent('selectionchanged'), 
		'contextmenuclicked' : new YAHOO.util.CustomEvent('contextmenuclicked')
	};
	YAHOO.ext.util.Config.apply(this, config);
	this.renderTask = new YAHOO.ext.util.DelayedTask(this.renderRows, this);
	this.sortState = {header:null, direction:null};
	this.isSortingDisabled = false;
	this.selectedNode = null;
};

YAHOO.rapidjs.component.treegrid.TreeGridView.prototype = {
	render:function(){
		var dh = YAHOO.ext.DomHelper;
		this.wrapper = dh.append(this.container, {tag: 'div', cls:'r-tree'});
		this.header = dh.append(this.wrapper, {tag: 'div', cls:'r-tree-header', 
			html:'<div><table cellspacing="0" cellpadding="0" class="r-tree-headertable"><tbody>' +
				'<tr class="r-tree-headerrow"></tr></tbody></table></div>'});
		this.headerInnerDiv = this.header.firstChild;
		var headerRow = this.header.getElementsByTagName('tr')[0];

    	var numberOfCols = this.columns.length;   
    	var sortColIndex;
    	for(var index=0; index<numberOfCols; index++) {
    		var colLabel = this.columns[index].colLabel;
    		var colWidth = this.columns[index].width;
	    	var headerCell = new YAHOO.rapidjs.component.treegrid.TreeHeaderCell(this,headerRow, colLabel, colWidth, index);
	    	this.headers[index] = headerCell;
	    	this.headers[index].events["resize"].subscribe(this.onColumnSplitterMoved, this, true);
	    	this.headers[index].events["click"].subscribe(this.headerClicked, this, true);
	    	if(this.columns[index]['sortBy'] == true){
				sortColIndex = index;
	    	}
    	}
    	if(sortColIndex != null && this.columns[sortColIndex]){
    		this.sortState['header'] = this.headers[sortColIndex];
    		this.sortState['direction'] = 'ASC';
    		this.headers[sortColIndex].updateSortState('ASC');
    	}
    	this.refreshHeaderWidth();
    	this.body = dh.append(this.wrapper, {tag: 'div', cls:'r-tree-body'}, true);
    	this.treeBody = dh.append(this.body.dom, {tag: 'div'}, true);
    	this.bufferPos = dh.append(this.treeBody.dom, {tag:'div'}, true);
    	this.bufferView = dh.append(this.treeBody.dom, {tag:'div'}, true);
    	this.bufferView.rows = [];
    	
    	YAHOO.util.Event.addListener(this.body.dom, 'scroll', this.handleScroll, this, true);     
    	YAHOO.util.Event.addListener(this.treeBody.dom, 'click', this.handleClick, this, true);     
    	YAHOO.util.Event.addListener(this.treeBody.dom, 'contextmenu', this.handleRightClick, this, true);     
    	 
    	
    	if(this.tooltip == true){
    		YAHOO.util.Event.addListener(this.treeBody.dom, 'mouseover', this.updateTooltip, this, true);  
    		Tooltip.add(this.treeBody.dom, '');
    	}  
	},
	
	onColumnSplitterMoved:function(colIndex, newSize){
		var rows = this.bufferView.rows;
		this.columns[colIndex]['width'] = newSize;
		for(var index=0; index<rows.length; index++) {
			var cells = rows[index].cells;
			this.setCellWidth(cells[colIndex], newSize);
		}
	 	this.refreshHeaderWidth();
	 	this.handleScroll();
	}, 
	
	refreshHeaderWidth: function()
	{
		var numberOfHeaders = this.headers.length;
		var totalWidth = 0;
		for(var index=0; index<numberOfHeaders; index++) {
			totalWidth += this.headers[index].width;
		}
		this.headerInnerDiv.style.width = (totalWidth+1000)+"px";
	},
	
	resize: function(width, height)
	{
		this.body.setHeight(height - this.header.offsetHeight);
		this.handleScroll();
	},
	
	handleScroll: function(){
		var scrollLeft = this.body.dom.scrollLeft;
		this.header.scrollLeft = scrollLeft;
		if(this.prevScrollLeft == scrollLeft)
		{
			this.renderTask.delay(50);
		}
		this.prevScrollLeft = scrollLeft;
	}, 
	
	handleData : function(data, expandAll){
		if(this.rootNode){
			this.rootNode.destroy();
		}
		this.rootNode = new YAHOO.rapidjs.component.treegrid.TreeRootNode(data, this.contentPath);
		if(expandAll == true){
			this.expandAll();
		}
		else{
			this.expandNode(this.rootNode, null, -1);	
		}
		
	},     
	
	expandAll : function(){
		this._expandNodes(this.rootNode, true);
		this.updateExpandedNodes(this.rootNode, -1, true);
		this.updateBodyHeight();
	}, 
	
	_expandNodes : function(treeNode, isExpanded){
		treeNode.isExpanded = isExpanded;
		var childNodes = treeNode.childNodes;
		for(var index=0; index<childNodes.length; index++) {
			var childNode = childNodes[index];
			childNode.isExpanded = isExpanded;
			this._expandNodes(childNode, isExpanded);
		}
	},
	
	selectionChanged: function(row, triggerEvent)
	{
		var treeNode = this.expandedNodes[row.rowIndex];
		if(this.selectedRow){
			if(row != this.selectedRow){
				YAHOO.util.Dom.replaceClass(this.selectedRow, 'r-tree-rowselected', 'r-tree-rowunselected');
				YAHOO.util.Dom.replaceClass(row, 'r-tree-rowunselected', 'r-tree-rowselected');
				this.selectedNode = treeNode;
				if(triggerEvent == true){
					this.events["selectionchanged"].fireDirect(treeNode);	
				}
			}
		}
		else{
			YAHOO.util.Dom.replaceClass(row, 'r-tree-rowunselected', 'r-tree-rowselected');
			this.selectedNode = treeNode;
			if(triggerEvent == true){
				this.events["selectionchanged"].fireDirect(treeNode);	
			}
		}
		this.selectedRow = row;	
	}, 
	
	expandNode : function(treeNode, treeRow, expandedRowIndex){
		this.updateIcon(treeRow, true);
		treeNode.isExpanded = true;
		this.updateExpandedNodes(treeNode, expandedRowIndex, true);
		this.updateBodyHeight();
	}, 
	
	collapseNode : function(treeNode, treeRow, collapsedRowIndex){
		if(treeNode.childNodes.length != 0){
			this.updateIcon(treeRow, false);
			this.updateExpandedNodes(treeNode, collapsedRowIndex, false);
			treeNode.isExpanded = false;
			this.updateBodyHeight();	
		}
	}, 
	
	updateExpandedNodes : function(treeNode, rowIndex, isExpand){
		if(isExpand == true){
			var populateArray = [];
			var willBeSorted = false;
			var sortColumnIndex = null;
			var sortDirection = this.sortState['direction'];
			if(this.sortState['header']){
				willBeSorted = true;
				sortColumnIndex = this.sortState['header'].colIndex;
			}
			this._getNewlyExpandedNodes(treeNode, populateArray, willBeSorted, sortColumnIndex, sortDirection);
			var firstPart = this.expandedNodes.slice(0, rowIndex + 1);
			if(rowIndex != this.expandedNodes.length - 1){
				var lastPart = this.expandedNodes.slice(rowIndex + 1);
				this.expandedNodes = firstPart.concat(populateArray, lastPart);
			}
			else{
				this.expandedNodes = firstPart.concat(populateArray);
			}
			
		}
		else{
			var collapseEndIndex = this._getNewlyCollapsedNodeIndex(treeNode, rowIndex);
			this.expandedNodes.splice(rowIndex + 1, collapseEndIndex - rowIndex);
		}
	},
	
	_getNewlyExpandedNodes: function(treeNode, populateArray, willBeSorted, sortColumnIndex, sortDirection){
		if(treeNode.isExpanded == true){
			var childNodes = treeNode.childNodes;
			if(willBeSorted == true){
				this.sort(childNodes, sortColumnIndex, sortDirection);
			}
			var nOfChildren = childNodes.length;
			for(var index=0; index<nOfChildren; index++) {
				var childNode = childNodes[index];
				if(childNode.childNodes.length == 0){childNode.isExpanded = true;}
				populateArray.push(childNode);
				this._getNewlyExpandedNodes(childNode, populateArray, willBeSorted, sortColumnIndex, sortDirection);
			}	
		}	
	}, 
	_getNewlyCollapsedNodeIndex: function(treeNode, rowIndex){
		var lastIndex = rowIndex;
		if(treeNode.isExpanded == true){
			var childNodes = treeNode.childNodes;
			var nOfChildren = childNodes.length;
			for(var index=0; index<nOfChildren; index++) {
				lastIndex = lastIndex + 1
				var childNode = childNodes[index];
				lastIndex = this._getNewlyCollapsedNodeIndex(childNode, lastIndex);
			}	
		}	
		return lastIndex;
	}, 
	updateIcon : function(treeRow, isExpanded){
		if(treeRow && treeRow.icon){
			if(isExpanded == true)
			{
				YAHOO.util.Dom.replaceClass(treeRow.icon, 'r-tree-treerowicon-collapsed', 'r-tree-treerowicon-expanded');	
			}
			else
			{
				YAHOO.util.Dom.replaceClass(treeRow.icon, 'r-tree-treerowicon-expanded', 'r-tree-treerowicon-collapsed');
			}	
		}
	},
	
	updateBodyHeight : function(){
		this.treeBody.setHeight(this.expandedNodes.length * this.getRowHeight());
		this.renderTask.delay(50);
	}, 
	getRowHeight : function(){
		if(!this.rowHeight){
            var rule = YAHOO.ext.util.CSS.getRule('.r-tree-treerow');
        	if(rule && rule.style.height){
        	    this.rowHeight = parseInt(rule.style.height, 10);
        	}else{
        	    this.rowHeight = 25;
        	}
        }
        return this.rowHeight;
	}, 
	
	renderRows : function(){
		var scrollTop = this.body.dom.scrollTop;
		var rowStartIndex = Math.floor(scrollTop/this.getRowHeight());
		var interval = Math.floor(this.body.getHeight() / this.getRowHeight());
		interval = interval + 2;
		var nOfExpandedNodes = this.expandedNodes.length;
		if(nOfExpandedNodes < rowStartIndex + interval)
		{
			rowStartIndex = nOfExpandedNodes - interval;
		}
		if(rowStartIndex < 0 )
		{
			rowStartIndex = 0;
			interval = nOfExpandedNodes;
		}
		if(interval > this.bufferView.rows.length)
		{
			this.createEmptyRows(interval - this.bufferView.rows.length);
		}
		else if(interval < this.bufferView.rows.length)
		{
			while(this.bufferView.rows.length > interval) {
				var row = this.bufferView.rows[this.bufferView.rows.length-1];
				for(var cellIndex=0; cellIndex<row.cells.length; cellIndex++) {
					var cell = row.cells[cellIndex];
					cell.body = null;
					cell.wrapper.value = null;
					cell.wrapper = null;
					cell.rootImage = null;
					row.cells[cellIndex] = null;
				}
				row.cells = null;
				row.icon = null;
				row.rowIndex = null;
				if(this.selectedRow == row){
					this.selectedRow = null;
				}
				this.bufferView.dom.removeChild(row);
				this.bufferView.rows.splice(this.bufferView.rows.length-1,1);
			}
		}
		
		if(rowStartIndex == 0)
		{
			YAHOO.util.Dom.setStyle(this.bufferPos.dom,'display', 'none');
		}
		else
		{
			YAHOO.util.Dom.setStyle(this.bufferPos.dom,'display', 'block');
		}
		
		this.bufferPos.setHeight(rowStartIndex*this.getRowHeight());
		this.bufferView.setHeight(interval*this.getRowHeight());
	
		for(var rowIndex = 0; rowIndex < interval; rowIndex++){
			var row = this.bufferView.rows[rowIndex];
			YAHOO.util.Dom.setStyle(row, 'display', 'block');
			var realRowIndex = rowStartIndex + rowIndex;
			row.rowIndex = realRowIndex;
			this.renderRow(row);
			var treeNode = this.expandedNodes[realRowIndex];
			if(treeNode == this.selectedNode){
				if(row != this.selectedRow){
					if(this.selectedRow){
						YAHOO.util.Dom.replaceClass(this.selectedRow, 'r-tree-rowselected', 'r-tree-rowunselected');	
					}
					YAHOO.util.Dom.replaceClass(row, 'r-tree-rowunselected', 'r-tree-rowselected');
					this.selectedRow = row;
				}
				else{
					if(!YAHOO.util.Dom.hasClass(row, 'r-tree-rowselected')){
						YAHOO.util.Dom.replaceClass(row, 'r-tree-rowunselected', 'r-tree-rowselected');
					}
				}
			}
			else{
				if(YAHOO.util.Dom.hasClass(row, 'r-tree-rowselected')){
					YAHOO.util.Dom.replaceClass(row, 'r-tree-rowselected', 'r-tree-rowunselected');
				}
			}
        }
	}, 
	createEmptyRows: function(rowCount)
    {
    	if(this.columns){
    		var dh = YAHOO.ext.DomHelper;
	    	var colCount = this.columns.length;
	        for(var rowIndex = 0; rowIndex < rowCount; rowIndex++){
	        	var rowCells = [];
	        	var row = dh.append(this.bufferView.dom, {tag:'div', cls:'r-tree-treerow', 
	        			html:'<table cellspacing="0" cellpadding="0" style="height:100%;"><tbody><tr></tr></tbody></table>'});
	        	YAHOO.util.Dom.addClass(row, 'r-tree-rowunselected');
	        	var tr = row.getElementsByTagName('tr')[0];
	        	if(colCount > 0){
	        		var firstTd = document.createElement('td');
	        		tr.appendChild(firstTd);
	        		firstTd.className = 'r-tree-treecell';
	        		var firstCellBody, rootImageEl;
	        		if(this.rootImages){
	        			firstCellBody = dh.append(firstTd, {tag:'div', cls:'r-tree-treecell-content', 
							html:'<table style="position:relative;"><tbody><tr><td><div class="r-tree-treerowicon-collapsed"></div></td>' +
							'<td><div class="r-tree-rootimage"></div></td><td class="r-tree-firstcell"></td></tr></tbody></table>'});
						rootImageEl = YAHOO.util.Dom.getElementsByClassName('r-tree-rootimage', 'div', firstCellBody)[0];
						YAHOO.util.Dom.setStyle(rootImageEl, 'display', 'none');
	        		}
	        		else{
	        			firstCellBody = dh.append(firstTd, {tag:'div', cls:'r-tree-treecell-content', 
							html:'<table style="position:relative;"><tbody><tr><td><div class="r-tree-treerowicon-collapsed"></div></td>' +
							'<td class="r-tree-firstcell"></td></tr></tbody></table>'});
	        		}
					
					var firstCell = {wrapper:firstTd, body:firstCellBody, rootImage:rootImageEl};
					this.setCellWidth(firstCell,this.columns[0]['width']);
					rowCells.push(firstCell);
					row.icon = firstCellBody.getElementsByTagName('div')[0];
					for(var cellindex=1; cellindex<colCount; cellindex++) {
						var td = document.createElement('td');
						tr.appendChild(td);
						td.className = 'r-tree-treecell';
						var cell = null;
						if(this.columns[cellindex].type == 'image' || this.columns[cellindex].type == 'Image'){
							var cellBody = dh.append(td, {tag:'div', cls:'r-tree-treecell-imagecontent'});
							cell = {wrapper:td, body:cellBody};
						}
						else{
							var cellBody = dh.append(td, {tag:'div', cls:'r-tree-treecell-content'});
							cell = {wrapper:td, body:cellBody};
						}
						this.setCellWidth(cell,this.columns[cellindex]['width']);
						rowCells.push(cell);
					}
	        	}
	        	row.cells = rowCells;
	            this.bufferView.rows[this.bufferView.rows.length] = row;
		        YAHOO.util.Dom.setStyle(row, 'display', 'none');
	        }	
    	}
    	
    }, 
    
    setCellWidth: function(cell, width){
    	YAHOO.util.Dom.setStyle(cell['body'], 'width', width+ 'px');
		YAHOO.util.Dom.setStyle(cell['wrapper'], 'width', (width*1 -2) + 'px');
    },
    
    renderRow : function(row){
    	if(row.cells && row.cells.length > 0){
    		var treeNode = this.expandedNodes[row.rowIndex];
    		var dataNode = treeNode.xmlData;
    		var firstCell = row.cells[0];
    		var cellBody = firstCell['body'];
    		var labelEl;
    		if(this.rootImages){
    			this.setRootImage(firstCell['rootImage'], dataNode, treeNode.isExpanded);
    			labelEl = cellBody.getElementsByTagName('td')[2];
    		}
    		else{
    			labelEl = cellBody.getElementsByTagName('td')[1];
    		}
    		var value = dataNode.getAttribute(this.columns[0]['attributeName']);
    		if(!value){value = '-';}
    		labelEl.innerHTML = value;
    		firstCell['wrapper'].value = value;
    		YAHOO.util.Dom.setStyle(cellBody.firstChild, 'left', (treeNode.level * 20) + 'px');
    		
    		this.updateIcon(row, treeNode.isExpanded);
    		this.setCellWidth(firstCell,this.columns[0]['width']);
    		for(var colIndex = 1; colIndex < row.cells.length; colIndex++){
	            var columnConfig = this.columns[colIndex];
	            var cell = row.cells[colIndex];
	            if(columnConfig.type=="Image" || columnConfig.type=="image")
	            {
	            	this.setImageSource(columnConfig, dataNode, cell.body);
	            }
	            else
	            {
	               var cellValue = dataNode.getAttribute(columnConfig['attributeName']) || '-';
	               cell.wrapper.value = cellValue;
		           cell.body.innerHTML = cellValue;
	            }
	            this.setCellWidth(cell,columnConfig['width']);
        	}
    	}
    }, 
    
    setImageSource: function(columnConfig, dataNode, htmlEl){
    	var expressionsArray = columnConfig['images'];
		for(var i = 0 ; i < expressionsArray.length ; i++)
		{
			var currentExpressionStr = expressionsArray[i]['visible'];
			var data = dataNode.getAttributes();
			var evaluationResult = eval(currentExpressionStr);
			if(evaluationResult == true)
			{
				var imageSrc = expressionsArray[i]['src'];
				htmlEl.style.backgroundImage = 'url("' + imageSrc + '")';
				htmlEl.style.backgroundPosition = columnConfig['align'] || 'left';
			}
		}
    }, 
    setRootImage: function(imageEl, dataNode, isExpanded){
    	var expressionsArray = this.rootImages;
		for(var i = 0 ; i < expressionsArray.length ; i++)
		{
			var currentExpressionStr = expressionsArray[i]['visible'];
			var data = dataNode.getAttributes();
			var evaluationResult = eval(currentExpressionStr);
			if(evaluationResult == true)
			{
				YAHOO.util.Dom.setStyle(imageEl, 'display', '');
				var imageSrc;
				if(isExpanded == true){
					imageSrc = expressionsArray[i]['expanded'];
				}
				else{
					imageSrc = expressionsArray[i]['collapsed'];
				}
				imageEl.style.background = 'url("' + imageSrc + '") no-repeat center';
				break;
			}
			else{
				YAHOO.util.Dom.setStyle(imageEl, 'display', 'none');
			}
		}
    }, 
    
    handleClick: function(e){
    	var target = YAHOO.util.Event.getTarget(e);
    	var row = this.getRowFromChild(target);
    	if(row){
    		if(YAHOO.util.Dom.hasClass(target, "r-tree-treerowicon-collapsed"))
    		{
    			this.expandNode(this.expandedNodes[row.rowIndex], row, row.rowIndex);
    		}
    		else if(YAHOO.util.Dom.hasClass(target, "r-tree-treerowicon-expanded")){
    			this.collapseNode(this.expandedNodes[row.rowIndex], row, row.rowIndex);
    		}
    		else{
    			this.selectionChanged(row, true);
    		}
    	}
    }, 
    handleRightClick : function(event){
    	var target = YAHOO.util.Event.getTarget(event);
    	var row = this.getRowFromChild(target);
    	if(row){
    		var treeNode = this.expandedNodes[row.rowIndex];
    		this.selectionChanged(row, true);
    		this.events["contextmenuclicked"].fireDirect(event,treeNode.xmlData);
    	}
	},
	
	updateTooltip : function(event){
		var target = YAHOO.util.Event.getTarget(event);
		var cell = this.getCellFromChild(target);
        if(cell){
        	if(YAHOO.util.Dom.hasClass(cell.firstChild, 'r-tree-treecell-imagecontent')){
        		Tooltip.update(this.treeBody.dom, '');
        	}
        	else{
        		Tooltip.update(this.treeBody.dom, cell.value);
        	}
        }
        else{
        	Tooltip.update(this.treeBody.dom, '');
        }   	
    }, 
    

    getRowFromChild : function(childEl){
        return YAHOO.rapidjs.DomUtils.getElementFromChild(childEl, 'r-tree-treerow');
    },

    getCellFromChild : function(childEl){
        return YAHOO.rapidjs.DomUtils.getElementFromChild(childEl, 'r-tree-treecell');
    },
    
    refreshData: function(){
    	this.expandedNodes = [];
    	if(this.rootNode){
    		this._refreshData(this.rootNode, true);
    	}
    	this.updateBodyHeight();
    },
    
    _refreshData : function(treeNode, populateExpandedNodes){
    	var childNodes = treeNode.childNodes;
    	if(this.sortState['header']){
    		this.sort(childNodes, this.sortState['header'].colIndex, this.sortState['direction']);
    	}
    	var tempNodes = [];
		var nOfChildren = childNodes.length;
		for(var index=0; index<childNodes.length; index++) {
			var childNode = childNodes[index];
			if(populateExpandedNodes == true && treeNode.isExpanded == true){
				if(childNode.isRemoved == false){
					this.expandedNodes.push(childNode);	
					if(childNode.childNodes.length == 0){childNode.isExpanded = true;}	
					
				}
			}
			if(childNode.isRemoved == true){
				if(this.selectedNode == childNode){
					this.selectedNode = null;
				}
				childNode.destroy();
			}
			else{
				tempNodes.push(childNode);
				if(treeNode.isExpanded == false){
					this._refreshData(childNode, false);
				}
				else{
					this._refreshData(childNode, populateExpandedNodes);
				}
				
			}
		}
		treeNode.childNodes = tempNodes;
		if(tempNodes.length == 0){
			treeNode.isExpanded = true;
		}
    }, 
    
    clear: function(){
    	if(this.rootNode && this.rootNode.xmlData){
    		var currentChildren = this.rootNode.xmlData.childNodes();
    		while(currentChildren.length > 0){
    			var childNode = currentChildren[0];
				this.rootNode.xmlData.removeChild(childNode);
    		}
    	}
    	this.refreshData();
    }, 
    
    sort:function(arrayToBeSorted, columnIndex,direction){
        var dsc = (direction && direction.toUpperCase() == 'DESC');
        var attribute = this.columns[columnIndex]['attributeName'];
        var sortType = this.columns[columnIndex]['sortType'];
        if(!sortType){
	        sortType = YAHOO.rapidjs.component.tree.sortTypes.none;
	    }
        
    	var fn = function(node1, node2){	
            var v1 = sortType(node1.xmlData.getAttribute(attribute));
            var v2 = sortType(node2.xmlData.getAttribute(attribute));
            if(v1 < v2)
    			return dsc ? +1 : -1;
    		else if(v1 > v2)
    			return dsc ? -1 : +1;
    		else if(v1 == v2)
    		{
    			if(node1.indexInParent < node2.indexInParent){
	    			return -1;
    			}
	    		else{
    				return +1;
	    		}
    		}
    	    return 0;
        };
	    arrayToBeSorted.sort(fn);
	    for(var index=0; index<arrayToBeSorted.length; index++) {
	    	var treeNode = arrayToBeSorted[index];
	    	treeNode.indexInParent = index;
	    }
    }, 
    
    headerClicked: function(header, direction){
    	var lastClicked = this.sortState['header'];
    	this.sortState['header'] = header;
    	this.sortState['direction'] = direction;
    	this.expandedNodes = [];
    	this.expandNode(this.rootNode, null, -1);
    	if(lastClicked && lastClicked != header){
    		lastClicked.updateSortState(null);	
    	}
    	header.updateSortState(direction);
    }
	
};

YAHOO.rapidjs.component.tree.sortTypes = {
    none : function(s) {
    	return s;
    },

    asUCString : function(s) {
    	return String(s).toUpperCase();
    },
    
    asDate : function(s) {
        if(s instanceof Date){
            return s.getTime();
        }
    	var date =  Date.parse(String(s));
    	if(isNaN(date)){
    		return new Date().setTime(0)
    	}
    	else{
    		return date;
    	}
    },
    
    asFloat : function(s) {
    	var val = parseFloat(String(s).replace(/,/g, ''));
        if(isNaN(val)) val = 0;
    	return val;
    },
    
    asInt : function(s) {
        var val = parseInt(String(s).replace(/,/g, ''));
        if(isNaN(val)) val = 0;
    	return val;
    }
};






