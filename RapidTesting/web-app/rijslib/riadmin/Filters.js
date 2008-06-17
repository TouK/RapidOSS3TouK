YAHOO.rapidjs.riadmin.Filters = function(errorDialog){
	var gridConfig = {
		content:[	{header:'Actions',type:"Action", width:'50',actions:[
						{handler:this.handleUpdate,scope:this,image:'../images/layout/pencil.png', tooltip:'Update'},
						{handler:this.handleRemove,scope:this,image:'../images/layout/cross.png', tooltip:'Remove'}]},
					{header:'Filter Name',width:'250',attribute:'Name'}, 
					{header:'Filter Expression',width:'400',attribute:'FilterExpression'}
				], 
		url:'/RapidInsight/Filter/getFilterInfo',
		rootTag:'Filters', 
		pollInterval:60, 
		id:'filterGrid', 
		contentPath:'Filter', 
		nodeId: 'Name', 
		title:'Rapid Insight Filters'
		
	};
	
	var crudConfig = {
		crudTitle: 'Filters',
		addButtonText: 'Add Filter',
		addIconCssClass: 'riadmin-filter-add'
	};
	
	YAHOO.rapidjs.riadmin.Filters.superclass.constructor.call(this, gridConfig, crudConfig, errorDialog);
	new YAHOO.rapidjs.component.layout.HelpTool(this.gLayout, "center", this.gridWindow, {url:help_ri_admin_filters_tab});
};

YAHOO.extendX(YAHOO.rapidjs.riadmin.Filters, YAHOO.rapidjs.riadmin.BasicCrud, {
	
	createDialog: function(gridWindow, errorDialog){
		return new YAHOO.rapidjs.component.dialogs.AddFilterDialog(gridWindow, errorDialog);
	},
	
	getRemoveUrl: function(node)
	{
		var filterName = node.getAttribute('Name');
		return '/RapidInsight/Filter/delete?FilterName=' + encodeURIComponent(filterName);
	}
});


YAHOO.rapidjs.component.dialogs.AddFilterDialog = function(gridWindow, errorDialog){
	
	YAHOO.rapidjs.component.dialogs.AddFilterDialog.superclass.constructor.call(this, gridWindow, 'Filter', 580, 230, errorDialog);
};


YAHOO.extendX(YAHOO.rapidjs.component.dialogs.AddFilterDialog, YAHOO.rapidjs.riadmin.BasicCrudDialog, {
	
	populateFieldsForUpdate: function(node)
	{
		this.nameInput.readOnly = true;
		var filterName = node.getAttribute('Name');
		this.nameInput.value = filterName;
		var exp = node.getAttribute('FilterExpression');
		this.expressionInput.value = exp;
	},
	
	clear: function()
	{
		this.nameInput.readOnly = false;
		this.nameInput.value = '';
		this.expressionInput.value = '';
	},
	
	handleSave: function()
	{
		var filterName = encodeURIComponent(this.nameInput.value);
		var exp = encodeURIComponent(this.expressionInput.value);
		this.request('/RapidInsight/Filter/create?FilterName=' + filterName + '&Expression=' + exp, this.saveSuccess);
	}, 
	render : function(){
		var dh = YAHOO.ext.DomHelper;
		var detailView = dh.append(this.container, {tag:'div', cls:'riadmin-userdlg-detailwr', 
					html:'<table><tr><td><div class="riadmin-fname-label">Filter Name:</div></td><td><div class="riadmin-fname-inputwrap"/></td></tr>' +
						'<tr><td><div class="riadmin-fexp-label">Filter Expression:</div></td><td><div class="riadmin-fexp-wrap"/></td></tr><table>'});
						
		var inputWrappers = detailView.getElementsByTagName('div');
		this.nameInput = dh.append(inputWrappers[1], {tag:'input', type:'text', cls:'riadmin-fname-input'});
		this.expressionInput = dh.append(inputWrappers[3], {tag:'textarea', cls:'riadmin-fexp-text'});
		this.expressionInput.cols = 30;
		
		this.dialog.addTabListener(this.cancelButton.el.dom, this.nameInput);
		this.dialog.addTabListener(this.expressionInput, this.saveButton.el.dom);
		this.dialog.addHelp({url:help_ri_admin_filters_add_dlg});
	}, 
	
	setDefaultInput : function(){
		this.dialog.defaultInput = this.nameInput;
	}
	
});