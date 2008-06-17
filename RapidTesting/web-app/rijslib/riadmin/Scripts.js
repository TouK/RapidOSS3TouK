YAHOO.rapidjs.riadmin.Scripts = function(errorDialog){
	var gridConfig = {
		content:[	{header:'Actions',type:"Action", width:'50',actions:[
						{handler:this.handleUpdate,scope:this,image:'../images/layout/pencil.png', tooltip:'Update', visible:'data["Type"] == "Periodic"'},
						{handler:this.handleRemove,scope:this,image:'../images/layout/cross.png', tooltip:'Remove'}]},
					{header:'Path',width:'250',attribute:'Path'}, 
					{header:'Type',width:'100',attribute:'Type'}
				], 
		url:'/RapidInsight/ManagedObject/invoke?Script=admin/getScriptData',
		rootTag:'Scripts', 
		pollInterval:60, 
		id:'scripts', 
		contentPath:'Script', 
		nodeId: 'Path', 
		title:'Scripts'
	};
	var crudConfig = {crudTitle: 'Scripts'};
	YAHOO.rapidjs.riadmin.Scripts.superclass.constructor.call(this, gridConfig, crudConfig, errorDialog);
	new YAHOO.rapidjs.component.layout.HelpTool(this.gLayout, "center", this.gridWindow, {url:help_ri_admin_scripts_tab});
	
    this.outputDialog = new YAHOO.rapidjs.component.dialogs.ErrorDialog();
};

YAHOO.extendX(YAHOO.rapidjs.riadmin.Scripts, YAHOO.rapidjs.riadmin.BasicCrud, {
	handleAddPeriodic: function(){
		this.basicCrudDialog.show(BasicCrudDialog.ADD, 'Periodic');
		this.basicCrudDialog.setTitle('New Periodic Script');
	}, 
	handleAddListening: function(){
		this.basicCrudDialog.show(BasicCrudDialog.ADD, 'Listening');
		this.basicCrudDialog.setTitle('New Listening Script');
	}, 
	handleAddOnDemand: function(){
		this.basicCrudDialog.show(BasicCrudDialog.ADD, 'OnDemand');
		this.basicCrudDialog.setTitle('New OnDemand');
	}, 
	createButtons: function(gLayout){
		var dh = YAHOO.ext.DomHelper;
		var buttonContainer = dh.append(document.body, {tag:'div'});
		gLayout.add('west', new YAHOO.rapidjs.component.layout.RapidPanel(buttonContainer, {fitToFrame:true, title:'Actions'}));
		gLayout.regions['west'].collapsedEl.addClass('riadmin-actioncollapsed');
		
		new YAHOO.rapidjs.component.ToolbarButton(dh.append(buttonContainer, {tag:'div', cls:'rapid-btnwrp'}), 
	    	{text:'Add Periodic Script', className:'riadmin-script-add', scope:this, click:this.handleAddPeriodic});
		new YAHOO.rapidjs.component.ToolbarButton(dh.append(buttonContainer, {tag:'div', cls:'rapid-btnwrp'}), 
	    	{text:"Add Listening Script", className:'riadmin-script-add', scope:this, click:this.handleAddListening});
		new YAHOO.rapidjs.component.ToolbarButton(dh.append(buttonContainer, {tag:'div', cls:'rapid-btnwrp'}), 
	    	{text:"Add OnDemand", className:'riadmin-script-add', scope:this, click:this.handleAddOnDemand});
	}, 
	
	createDialog: function(gridWindow, errorDialog){
		return new YAHOO.rapidjs.component.dialogs.ScriptDialog(gridWindow, errorDialog);
	},
	handleUpdate: function(node){
		var type = node.getAttribute('Type');
		this.basicCrudDialog.setTitle('Update ' + type + ' Script');
		this.basicCrudDialog.populateFieldsForUpdate(node);
		this.basicCrudDialog.show(BasicCrudDialog.UPDATE, type);
	},
	createContextMenu: function(){
		this.menu = new YAHOO.rapidjs.component.menu.ContextMenu();
		var MenuAction = YAHOO.rapidjs.component.menu.MenuAction;
		var reloadAction = new YAHOO.rapidjs.component.menu.ScriptMenuAction(
				{
					url:'/RapidInsight/Script/reload', 
					dynamicParams: {'Script':'Path'}
				})
		this.menu.addMenuItem('Reload', reloadAction,  'data["Type"] != "OnDemand"');
		this.menu.addMenuItem('Run', new MenuAction(this.runScript, this),  'data["Type"] == "OnDemand"');
		this.menu.addMenuItem('Update', new MenuAction(this.updateItem, this), 'data["Type"] == "Periodic"');
		this.menu.addMenuItem('Remove', new MenuAction(this.removeItem, this), true);
	},
	runScript: function(menuItem, data, node){
		var scriptName = data['Path'];
		this.currentScriptName = scriptName;
		this.basicCrudDialog.request('/RapidInsight/ManagedObject/invoke?Script=' + encodeURIComponent(scriptName), this.executeScriptSuccess, this);
	},
	
	executeScriptSuccess: function(response)
	{
		this.basicCrudDialog.dialog.hideLoading();
		YAHOO.rapidjs.ServerStatus.refreshState(true);
		if(this.checkResponse(response))
		{
			this.outputDialog.dialog.setTitle('Script result for ' + this.currentScriptName);
			this.outputDialog.setErrorText(response.responseText);
			this.outputDialog.show();
		}
	},
	
	getRemoveUrl: function(node)
	{
		var path = node.getAttribute("Path");
		return '/RapidInsight/Script/remove?Path=' + encodeURIComponent(path);
	}
});

YAHOO.rapidjs.component.dialogs.ScriptDialog = function(gridWindow, errorDialog){
	
	YAHOO.rapidjs.component.dialogs.ScriptDialog.superclass.constructor.call(this, gridWindow, 'Script', 430, 243, errorDialog);
};


YAHOO.extendX(YAHOO.rapidjs.component.dialogs.ScriptDialog, YAHOO.rapidjs.riadmin.BasicCrudDialog, {
	
	show: function(mode, type){
		this.mode = mode;
		if(this.mode == BasicCrudDialog.ADD){
			this.clear();
		}
		this.type = type;
		if(type == 'Listening'){
			this.dialog.helpWindow.rapidWindow.url = this.lScriptDlgHelp;
			YAHOO.util.Dom.setStyle(this.periodView, 'display', 'none');
		}
		else if (type == 'Periodic'){
			this.dialog.helpWindow.rapidWindow.url = this.pScriptDlgHelp;
			YAHOO.util.Dom.setStyle(this.periodView, 'display', '');
		}
		else{
			this.dialog.helpWindow.rapidWindow.url = this.oScriptDlgHelp;
			YAHOO.util.Dom.setStyle(this.periodView, 'display', 'none');
		}
		this.autoHeight();
		this.dialog.show();
	}, 
	populateFieldsForUpdate: function(node)
	{
		var path = node.getAttribute('Path');
		var period = node.getAttribute('Period');
		this.pathInput.readOnly = true;
		this.pathInput.value = path;
		this.periodInput.value = period;
	},
	
	clear: function()
	{
		this.pathInput.value = '';
		this.periodInput.value = '';
		this.pathInput.readOnly = false;
	},
	
	handleSave: function()
	{
		var path = encodeURIComponent(this.pathInput.value);
		var period = encodeURIComponent(this.periodInput.value);
		if(this.mode == BasicCrudDialog.UPDATE){
			this.request('/RapidInsight/Script/update?Path=' + path + '&Period=' + period, this.saveSuccess);
		}
		else{
			if(this.type == 'Listening'){
				this.request('/RapidInsight/Script/add?Type=Listening&Path=' + path, this.saveSuccess);
			}
			else if(this.type == 'Periodic'){
				this.request('/RapidInsight/Script/add?Type=Periodic&Path=' + path + '&Period=' + period, this.saveSuccess);
			}
			else{
				this.request('/RapidInsight/Script/add?Type=OnDemand&Path=' + path, this.saveSuccess);
			}
		}
		
	},
	actionSuccess: function(response)
	{
		YAHOO.rapidjs.ServerStatus.refreshState(true);
		this.checkResponse(response);
	}, 
	render : function(){
		this.lScriptDlgHelp = help_ri_admin_lscript_add_dlg;
		this.pScriptDlgHelp = help_ri_admin_pscript_add_dlg;
		this.oScriptDlgHelp = help_ri_admin_odscript_add_dlg;
		var dh = YAHOO.ext.DomHelper;
		var wrapper = dh.append(this.container, {tag:'div', cls:'riadmin-scriptdlg-wr'});
		var pathView = dh.append(wrapper, {tag:'div', 
			html:'<table><tbody><tr><td><div class="riadmin-formtext">Path:</div></td>' +
				'<td><div class="riadmin-inputwrp"/></td></tr></tbody></table>'});
		var inputWrappers = pathView.getElementsByTagName('div');
		this.pathInput = dh.append(inputWrappers[1], {tag:'input', cls:'riadmin-input'});
		this.periodView = dh.append(wrapper, {tag:'div',
			html:'<table><tbody><tr><td><div class="riadmin-formtext">Period:</div></td>' +
				'<td><div class="riadmin-inputwrp"/></td><td><div>sec.</div></td></tr></tbody></table>'});
		var periodWrappers = this.periodView.getElementsByTagName('div');
		this.periodInput = dh.append(periodWrappers[1], {tag:'input', cls:'riadmin-input'});
		
		this.dialog.addTabListener(this.cancelButton.el.dom, this.pathInput);
		this.dialog.addTabListener(this.periodInput, this.saveButton.el.dom);
		this.dialog.addHelp({url:this.pScriptDlgHelp});
	},
	
	setDefaultInput : function(){
		this.dialog.defaultInput = this.pathInput;
	}
	
});