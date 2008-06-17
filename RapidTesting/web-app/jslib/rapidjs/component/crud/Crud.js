YAHOO.rapidjs.component.crud.Crud = function(container, config){
	this.container = getEl(container);	
	this.rowHeight = 22;
	YAHOO.ext.util.Config.apply(this, config);
	this.rows = {};
	this.rowSize = 0;
	this.render();
	this.idSeed = 0;
	this.selectedClass = 'rapid-crud-rowselected';
	this.lastSelectedRow = null;
	this.events = {
		'contextmenuclicked': new YAHOO.util.CustomEvent('contextmenuclicked'), 
		'rowdblclicked': new YAHOO.util.CustomEvent('rowdblclicked'), 
		'rowupdated': new YAHOO.util.CustomEvent('rowupdated'), 
		'rowdeleted': new YAHOO.util.CustomEvent('rowdeleted')
	};
};

YAHOO.rapidjs.component.crud.Crud.prototype = {
	render: function(){
		var dh = YAHOO.ext.DomHelper;
		this.wrapper = dh.append(this.container.dom, {tag:'div', cls:'rapid-crud-wrap'});
		this.header = dh.append(this.wrapper, {tag:'div', cls:'rapid-crud-header', html:'<div class="rapid-crud-headerlabel">' + this.headerLabel+ '</div>'});
		this.body = dh.append(this.wrapper, {tag:'div', cls:'rapid-crud-body'}, true);
	}, 
	
	//
	// As far as I know, javascript does not support method overloading. So, the second addItem function will disable the first one.
	//
//	addItem: function(node){
//		var name = node.getAttribute(this.contentPath);
//		this._addItem(name, node);
//	},
	 
//	addItem: function(name){
//		this._addItem(name);
//	}, 
	
	addItem:function(name, node){
		var row = new YAHOO.rapidjs.component.crud.CrudRow(this.idSeed, this, name, node, this.rowHeight);
		this.rows[this.idSeed] = row;
		row.events['contextmenuclicked'].subscribe(this.fireContextMenu, this, true);
		row.events['rowclicked'].subscribe(this.handleSelection, this, true);
		row.events['rowupdated'].subscribe(this.fireRowUpdated, this, true);
		row.events['rowdeleted'].subscribe(this.fireRowDeleted, this, true);
		row.events['rowdblclicked'].subscribe(this.fireDblClick, this, true);
		this.rowSize ++;
		this.idSeed++;
		this.updateBodyHeight();
	},
	
	removeItem: function(row){
		if(this.lastSelectedRow == row){
			this.lastSelectedRow = null;
		}
		row.destroy();
		this.rowSize --;
		delete this.rows[row.id];
		this.updateBodyHeight();
	},
	
	getRows: function()
	{
		return this.rows;
	},
	
	removeAll: function(){
		for(var rowIndex in this.rows) {
			this.removeItem(this.rows[rowIndex]);
		}
	}, 
	updateBodyHeight: function(){
		this.body.setHeight(this.rowSize * this.rowHeight);
	} , 
	
	fireContextMenu: function(crudRow){
		this.events['contextmenuclicked'].fireDirect(crudRow);
	}, 
	fireDblClick: function(crudRow){
		this.events['rowdblclicked'].fireDirect(crudRow);
	}, 
	fireRowUpdated: function(crudRow){
		this.events['rowupdated'].fireDirect(crudRow);
	}, 
	fireRowDeleted: function(crudRow){
		this.events['rowdeleted'].fireDirect(crudRow);
	}, 
	handleSelection: function(crudRow){
		if(this.lastSelectedRow){
			YAHOO.util.Dom.removeClass(this.lastSelectedRow.el.dom, this.selectedClass);
		}
		YAHOO.util.Dom.addClass(crudRow.el.dom, this.selectedClass);
		this.lastSelectedRow = crudRow;
	}
};

YAHOO.rapidjs.component.crud.CrudRow = function(id, crud, name, node, rowHeight){
	this.name = name;
	this.crud = crud;
	this.node = node;
	this.wrapper = this.crud.body.dom;
	this.id = id;
	this.events = {
		'rowclicked' : new YAHOO.util.CustomEvent('rowclicked'),
		'rowdblclicked' : new YAHOO.util.CustomEvent('rowdblclicked'),
		'contextmenuclicked' : new YAHOO.util.CustomEvent('contextmenuclicked'), 
		'rowdeleted' : new YAHOO.util.CustomEvent('rowdeleted'), 
		'rowupdated' : new YAHOO.util.CustomEvent('rowupdated')
	};
	this.render(rowHeight);
};

YAHOO.rapidjs.component.crud.CrudRow.prototype = {
	render: function(rowHeight){
		this.el = YAHOO.ext.DomHelper.append(this.wrapper, {tag:'div', cls:'rapid-crud-row', 
			html: '<span class="rapid-crud-label">' + this.name + '</span>' +
					'<div class="rapid-crud-tools"><div class="rapid-crud-update" title="Update"></div><div class="rapid-crud-delete" title="Remove"></div></div>'}, true);
		YAHOO.util.Event.addListener(this.el.dom, 'contextmenu', this.fireContextMenu, this, true);
		YAHOO.util.Event.addListener(this.el.dom, 'click', this.fireClick, this, true);
		YAHOO.util.Event.addListener(this.el.dom, 'dblclick', this.fireDblClick, this, true);
		this.el.setHeight(rowHeight);
		
		var comps = this.el.dom.getElementsByTagName('div');
		YAHOO.util.Event.addListener(comps[1], 'click', this.fireRowUpdated, this, true);
		YAHOO.util.Event.addListener(comps[2], 'click', this.fireRowDeleted, this, true);
	}, 
	
	destroy: function(){
		YAHOO.util.Event.purgeElement(this.el.dom, true);
		for(var event in this.events) {
			this.events[event].unsubscribeAll();
		}
		this.wrapper.removeChild(this.el.dom);
	}, 
	
	fireContextMenu: function(){
		this.events['contextmenuclicked'].fireDirect(this);
	}, 
	fireClick: function(){
		this.events['rowclicked'].fireDirect(this);
	}, 
	fireDblClick: function(){
		this.events['rowdblclicked'].fireDirect(this);
	}, 
	fireRowDeleted: function(){
		this.events['rowdeleted'].fireDirect(this);
	}, 
	fireRowUpdated: function(){
		this.events['rowupdated'].fireDirect(this);
	}
	
};