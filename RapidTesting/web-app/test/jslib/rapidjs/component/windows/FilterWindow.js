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
YAHOO.rapidjs.component.windows.FilterWindow = function(container, config){
	YAHOO.rapidjs.component.windows.FilterWindow.superclass.constructor.call(this,container, config);
	YAHOO.ext.util.Config.apply(this, config);
	this.selectedClass = 'selected';
	this.addFilterDialog = new YAHOO.rapidjs.component.windows.AddFilterDialog(this);
	this.panel = new YAHOO.rapidjs.component.layout.FilterPanel(this, {title:this.title, fitToFrame:true});
	this.subscribeToPanel();
	this.render();
	this.dFilters = [];
	this.addDefaultFilters();
	this.loadConfiguration();

};

YAHOO.extendX(YAHOO.rapidjs.component.windows.FilterWindow, YAHOO.rapidjs.component.ComponentContainer, {
	render: function()
	{
		var dh = YAHOO.ext.DomHelper;
		this.wrapper = dh.append(this.container, {tag:'div', cls:'r-filter-wrp'}); 
		this.header = dh.append(this.wrapper, {tag: 'div', cls:'r-filter-header'}, true);
		new YAHOO.rapidjs.component.ToolbarButton(this.header.dom,{text:'Add Filter', className:'r-filter-addbutton', scope:this, click:this.handleAdd});
		this.bodywrp = dh.append(this.wrapper, {tag: 'div', cls:'r-filter-bodywrp'}, true);
		this.body = dh.append(this.bodywrp.dom, {tag: 'div', cls:'r-filter-body'});
	}, 
	
	getFilter: function(filterName){
		for(var index in this.filters) {
			var filter = this.filters[index];
			if(filter.name == filterName){
				return filter;
			}
		}
		return null;
	}, 
	
	selectFilter : function(filter){
		if(this.lastSelectedFilter){
			YAHOO.util.Dom.removeClass(this.lastSelectedFilter.el.dom, this.selectedClass);
		}
		YAHOO.util.Dom.addClass(filter.el.dom, this.selectedClass);
		this.lastSelectedFilter = filter;
		this.sendOutputs(filter.node);
	}, 
	
	addDefaultFilters: function(){
		if(this.defaultFilters){
			for(var index=0; index<this.defaultFilters.length; index++) {
				var filterConfig = this.defaultFilters[index];
				this.dFilters[index] = new YAHOO.rapidjs.component.windows.DefaultFilter(this, filterConfig.name, filterConfig.expression, '', filterConfig.selected);
				this.dFilters[index].events['rowupdated'].subscribe(this.handleUpdate, this, true);
				this.dFilters[index].setWidth(this.width);
				this.dFilters[index].events['rowclicked'].subscribe(this.selectFilter, this, true);
			}
		}
	},
	
	addFilter: function(filterName, filterExpression){
		var filter = this.getFilter(filterName);
		if(!filter){
			var index = new Date().getTime();
			filter = new YAHOO.rapidjs.component.windows.Filter(this, filterName, filterExpression, index);
			filter.setWidth(this.width);
			this.filters[index] = filter;
			filter.events['rowclicked'].subscribe(this.selectFilter, this, true);
			filter.events['rowupdated'].subscribe(this.handleUpdate, this, true);
			filter.events['rowdeleted'].subscribe(this.handleRemove, this, true);
			
		}
		else{
			filter.setExp(filterExpression);
		}
		this.selectFilter(filter);
		var confString = this.getConfString();
		this.saveConfiguration(confString);
	}, 
	
	updateFilter: function(filterName, filterExpression){
		var filter = this.getFilter(filterName);
		if(filter){
			filter.setExp(filterExpression);
			this.selectFilter(filter);
			var confString = this.getConfString();
			this.saveConfiguration(confString);	
		}
	}, 
	
	
	getConfString: function(){
		var confString = 'filters={';
		var filtersString = new Array();
		for(var index in this.filters) {
			var filter = this.filters[index];
			filtersString[filtersString.length] = encodeURIComponent(index) + ":{name:'" + encodeURIComponent(filter.name)+ "',expression:'"+ encodeURIComponent(filter.exp) + "'}";
		}
		return confString + filtersString.join(',') + '};';
		
	},
	
	handleAdd: function(){
		var x = this.header.getX();
		var y = this.header.getY();
		this.addFilterDialog.show(x, y, false);
	}, 
	handleUpdate: function(filter){
		var x = filter.el.getX();
		var y = filter.el.getY();
		this.addFilterDialog.populateFieldsForUpdate(filter);
		this.addFilterDialog.show(x, y, true);
	}, 
	
	handleRemove: function(filter){
		if(confirm("Remove " + filter.name + "?"))
		{
			if(this.lastSelectedFilter == filter){
				this.lastSelectedFilter = null;
			}
			filter.destroy();
			delete this.filters[filter.index];
			var confString = this.getConfString();
			this.saveConfiguration(confString);
		}
	},
	
	configurationLoaded: function(){
		if(!this.filters){
			this.filters = {};
		}
		for(var index in this.filters) {
			var filterConfig = this.filters[index];
			this.filters[index] = new YAHOO.rapidjs.component.windows.Filter(this, filterConfig['name'], filterConfig['expression'], index);
			this.filters[index].setWidth(this.width);
			this.filters[index].events['rowclicked'].subscribe(this.selectFilter, this, true);
			this.filters[index].events['rowupdated'].subscribe(this.handleUpdate, this, true);
			this.filters[index].events['rowdeleted'].subscribe(this.handleRemove, this, true);
		}
	}, 
	
	resize : function(width, height){
		this.width = width;
		this.bodywrp.setHeight(height - this.header.getHeight());
		for(var index=0; index<this.dFilters.length; index++) {
			this.dFilters[index].setWidth(width);
		}
		for(var filterIndex in this.filters) {
			var filter = this.filters[filterIndex];
			if(filter.setWidth){
				filter.setWidth(width);
			}
		}
	}, 
	
	handleVisible: function(){
		if(!this.pageLoaded){
			this.pageLoaded = true;
			for(var index=0; index<this.dFilters.length; index++) {
				if(this.dFilters[index].isSelected == true){
					this.selectFilter(this.dFilters[index]);
					return;
				}
			}
		}
	}
	
});

YAHOO.rapidjs.component.windows.Filter = function(filterWindow, name, exp, index){
	this.name = name;
	this.exp = exp;
	this.index = index;
	this.filterWindow = filterWindow;
	this.node = new YAHOO.rapidjs.data.RapidXmlNode(null, null, 1, 'Filter');
	this.node.attributes['Name'] = this.name;
	this.node.attributes['Expression'] = this.exp;
	this.wrapper = this.filterWindow.body;
	this.events = {
		'rowclicked' : new YAHOO.util.CustomEvent('rowclicked'),
		'rowdeleted' : new YAHOO.util.CustomEvent('rowdeleted'), 
		'rowupdated' : new YAHOO.util.CustomEvent('rowupdated')
	};
	this.render();
};

YAHOO.rapidjs.component.windows.Filter.prototype = {
	render: function(rowHeight){
		this.el = YAHOO.ext.DomHelper.append(this.wrapper, {tag:'div', cls:'r-filter-filter', 
			html: '<span class="r-filter-filterlbl">' + this.name + '</span>' +
					'<div class="r-filter-filtertools"><div class="r-filter-filterupdate" title="Update"></div><div class="r-filter-filterremove" title="Remove"></div></div>'}, true);
		YAHOO.util.Event.addListener(this.el.dom, 'click', this.fireClick, this, true);
		
		var comps = this.el.dom.getElementsByTagName('div');
		YAHOO.util.Event.addListener(comps[1], 'click', this.fireRowUpdated, this, true);
		YAHOO.util.Event.addListener(comps[2], 'click', this.fireRowDeleted, this, true);
		this.labelEl = getEl(this.el.dom.getElementsByTagName('span')[0]);
		this.updateEl = getEl(comps[1]);
		this.removeEl = getEl(comps[1]);
		this.updateEl.addClassOnOver('r-filter-buttonover');
		this.removeEl.addClassOnOver('r-filter-buttonover');
		this.el.addClassOnOver('r-filter-filterover');
	}, 
	
	destroy: function(){
		YAHOO.util.Event.purgeElement(this.el.dom, true);
		for(var event in this.events) {
			this.events[event].unsubscribeAll();
		}
		this.wrapper.removeChild(this.el.dom);
		delete YAHOO.ext.Element.cache[this.el.dom.id];
		delete YAHOO.ext.Element.cache[this.updateEl.dom.id];
		delete YAHOO.ext.Element.cache[this.removeEl.dom.id];
		delete YAHOO.ext.Element.cache[this.labelEl.dom.id];
		this.updateEl = null;
		this.removeEl = null;
		this.labelEl = null;
		this.wrapper = null;
		this.node = null;
	}, 
	
	fireClick: function(event){
		this.events['rowclicked'].fireDirect(this);	
	}, 
	fireRowDeleted: function(event){
		YAHOO.util.Event.stopEvent(event);
		this.events['rowdeleted'].fireDirect(this);
	}, 
	fireRowUpdated: function(event){
		YAHOO.util.Event.stopEvent(event);
		this.events['rowupdated'].fireDirect(this);
	}, 
	
	setExp: function(exp){
		this.exp = exp;
		this.node.attributes['Expression'] = exp;
	}, 
	
	setWidth: function(width){
		this.labelEl.setWidth(width - (this.updateEl.getWidth() + this.updateEl.getPadding('lr')+this.removeEl.getWidth()+ this.removeEl.getPadding('lr')));
	}
};


YAHOO.rapidjs.component.windows.DefaultFilter = function(filterWindow, name, exp, index, isSelected){
	this.isDefault = true;
	this.isSelected = isSelected;
	YAHOO.rapidjs.component.windows.DefaultFilter.superclass.constructor.call(this, filterWindow, name, exp, index);
};
YAHOO.extendX(YAHOO.rapidjs.component.windows.DefaultFilter, YAHOO.rapidjs.component.windows.Filter, {
	render: function(){
		this.el = YAHOO.ext.DomHelper.append(this.wrapper, {tag:'div', cls:'r-filter-filter', 
			html: '<span class="r-filter-filterlbl">' + this.name + '</span>' +
					'<div class="r-filter-filtertools"><div class="r-filter-filterupdate" title="View"></div></div>'}, true);
		YAHOO.util.Event.addListener(this.el.dom, 'click', this.fireClick, this, true);
		var comps = this.el.dom.getElementsByTagName('div');
		YAHOO.util.Event.addListener(comps[1], 'click', this.fireRowUpdated, this, true);
		this.updateEl = getEl(comps[1]);
		this.labelEl = getEl(this.el.dom.getElementsByTagName('span')[0]);
		this.el.addClassOnOver('r-filter-filterover');
	}, 
	
	setWidth: function(width){
		this.labelEl.setWidth(width - (this.updateEl.getWidth() + this.updateEl.getPadding('lr')));
	}
})



YAHOO.rapidjs.component.windows.AddFilterDialog = function(filterWindow){
	this.filterWindow = filterWindow;
	var config = {
		modal: false,
	    width:463,
	    height:269,
	    shadow:true,
	    minWidth:100,
	    minHeight:100,
	    syncHeightBeforeShow: true,
	    resizable: false,
	    title: 'Add Filter', 
	    center:{
	        autoScroll:true
    }};
    var dh = YAHOO.ext.DomHelper;
    this.dialog = new YAHOO.ext.LayoutDialog(dh.append(document.body, {tag:'div'}), config);
	this.cancelButton = this.dialog.addButton('Cancel', this.hide, this);
	this.saveButton = this.dialog.addButton('OK', this.handleSave, this);
	var layout = this.dialog.layout;
	layout.beginUpdate();
	this.container = dh.append(document.body, {tag:'div'});
	layout.add('center', new YAHOO.rapidjs.component.layout.RapidPanel(this.container));
	layout.endUpdate();
	
	this.dialog.addTabListener(this.saveButton.el.dom, this.cancelButton.el.dom);
	this.dialog.addKeyListener(13, function(){if(this.cancelButton.isFocused == false && this.saveButton.isFocused == false){this.handleSave();}}, this);
	this.dialog.addKeyListener(27, function(){this.hide();}, this);
	this.dialog.on('hide', this.clear, this, true);
	
	var wrapper = dh.append(this.container, {tag:'div', cls:'r-filter-dlgwrp'});
	var inputView = dh.append(wrapper, {tag:'div', 
		html:'<table><tbody><tr><td><div class="r-filter-formtext">Filter Name:</div></td><td><div class="r-filter-inputwrp"/></td></tr>' +
				'<tr><td><div class="r-filter-formtext">Filter Expression:</div></td><td><div class="r-filter-dlg-expwrp"/></td></tr></tbody></table>'});
	var inputWrappers = inputView.getElementsByTagName('div');
	this.nameInput = dh.append(inputWrappers[1], {tag:'input', cls:'r-filter-input'});
	this.expInput = dh.append(inputWrappers[3], {tag:'textarea', cls:'r-filter-textarea'});
	this.expInput.cols = 30;
	
	this.dialog.addTabListener(this.cancelButton.el.dom, this.nameInput);
	this.dialog.addTabListener(this.expInput, this.saveButton.el.dom);
};

YAHOO.rapidjs.component.windows.AddFilterDialog.prototype = {
	
	show: function(x, y, isUpdating){
		this.dialog.moveTo(x, y);
		this.updating = isUpdating;
		if(this.updating == true){
			
		}
		else{
			this.setTitle('Add Filter');
		}
		this.dialog.show();
	}, 
	
	hide: function(){
		this.dialog.hide();
	}, 
	
	setTitle: function(text){
		this.dialog.setTitle(text);
	}, 
	
	clear: function(){
		this.nameInput.readOnly = false;
		this.expInput.readOnly = false;
		this.nameInput.value = '';
		this.expInput.value = '';
		this.saveButton.enable();
	}, 
	
	handleSave: function(){
		if(this.expInput.readOnly == false){
			var filterName = this.nameInput.value;
			var filterExp = this.expInput.value;
			if(this.isUpdating == true){
				
				this.filterWindow.updateFilter(filterName, filterExp);
			}
			else{
				this.filterWindow.addFilter(filterName, filterExp);
			}	
		}
		this.hide();
	},  
	populateFieldsForUpdate: function(filter){
		var title = 'Update ' + filter.name;
		this.nameInput.readOnly = true;
		this.nameInput.value= filter.name;
		this.expInput.value = filter.exp;
		if(filter.isDefault == true){
			this.expInput.readOnly = true;
			this.saveButton.disable();
			title = filter.name;
		}
		this.setTitle(title);
	}
};

YAHOO.rapidjs.component.layout.FilterPanel = function(filterWindow, config){
	this.filterWindow = filterWindow;
	YAHOO.rapidjs.component.layout.FilterPanel.superclass.constructor.call(this, this.filterWindow.container, config);
};
YAHOO.extendX(YAHOO.rapidjs.component.layout.FilterPanel , YAHOO.rapidjs.component.layout.RapidPanel, {
	setSize: function(width, height)
	{
		this.filterWindow.resize(width, height);
		YAHOO.rapidjs.component.layout.FilterPanel.superclass.setSize.call(this, width, height)
	}
});

	