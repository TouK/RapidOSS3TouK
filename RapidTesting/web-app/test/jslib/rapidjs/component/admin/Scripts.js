YAHOO.rapidjs.admin.Scripts = function(errorDialog){
	var gridConfig = {
		content:[	{header:'Actions',type:"Action", width:'50',actions:[
						{handler:this.handleUpdate,scope:this,image:'../images/layout/pencil.png', tooltip:'Update'},
						{handler:this.handleRemove,scope:this,image:'../images/layout/cross.png', tooltip:'Remove'}]},
					{header:'Path',width:'250',attribute:'Path'}, 
					{header:'Type',width:'100',attribute:'Type'}
				], 
		url:this.getGridUrl(),
		rootTag:'Scripts', 
		pollInterval:60, 
		id:'scripts', 
		contentPath:'Script', 
		nodeId: 'Id', 
		title:'Scripts'
	};
	var crudConfig = {crudTitle: 'Scripts'};
	YAHOO.rapidjs.admin.Scripts.superclass.constructor.call(this, gridConfig, crudConfig, errorDialog);
	new YAHOO.rapidjs.component.layout.HelpTool(this.gLayout, "center", this.gridWindow, {url:this.getHelpUrl()});
	
    this.outputDialog = new YAHOO.rapidjs.component.dialogs.ErrorDialog();
};

YAHOO.extendX(YAHOO.rapidjs.admin.Scripts, YAHOO.rapidjs.admin.BasicAdmin, {
	handleAddPeriodic: function(){
		this.basicAdminDialog.show(BasicAdminDialog.ADD, 'Periodic');
		this.basicAdminDialog.setTitle('New Periodic Script');
	}, 
	handleAddOnDemand: function(){
		this.basicAdminDialog.show(BasicAdminDialog.ADD, 'OnDemand');
		this.basicAdminDialog.setTitle('New OnDemand Script');
	}, 
	createButtons: function(gLayout){
		var dh = YAHOO.ext.DomHelper;
		var buttonContainer = dh.append(document.body, {tag:'div'});
		gLayout.add('west', new YAHOO.rapidjs.component.layout.RapidPanel(buttonContainer, {fitToFrame:true, title:'Actions'}));
		gLayout.regions['west'].collapsedEl.addClass('r-admin-actioncollapsed');
		
		new YAHOO.rapidjs.component.ToolbarButton(dh.append(buttonContainer, {tag:'div', cls:'rapid-btnwrp'}), 
	    	{text:'Add Periodic Script', className:'r-admin-script-add', scope:this, click:this.handleAddPeriodic});
		new YAHOO.rapidjs.component.ToolbarButton(dh.append(buttonContainer, {tag:'div', cls:'rapid-btnwrp'}), 
	    	{text:"Add OnDemand Script", className:'r-admin-script-add', scope:this, click:this.handleAddOnDemand});
	}, 
	
	handleUpdate: function(node){
		var type = node.getAttribute('Type');
		this.basicAdminDialog.setTitle('Update ' + type + ' Script');
		this.basicAdminDialog.populateFieldsForUpdate(node);
		this.basicAdminDialog.show(BasicAdminDialog.UPDATE, type);
	},
	createContextMenu: function(){
		this.menu = new YAHOO.rapidjs.component.menu.ContextMenu();
		var MenuAction = YAHOO.rapidjs.component.menu.MenuAction;
		var reloadAction = new YAHOO.rapidjs.component.menu.ScriptMenuAction(
				{
					url:this.basicAdminDialog.rootUrl + '/Script/reload', 
					dynamicParams: {'Path':'Path','Type':'Type'}
				});
		this.menu.addMenuItem('Reload', reloadAction,  'data["Type"] != "OnDemand"');
		this.menu.addMenuItem('Run', new MenuAction(this.runScript, this),  'data["Type"] == "OnDemand"');
		this.menu.addMenuItem('Update', new MenuAction(this.updateItem, this), 'data["Type"] == "Periodic"');
		this.menu.addMenuItem('Remove', new MenuAction(this.removeItem, this), true);
	},
	runScript: function(menuItem, data, node){
		var scriptName = data['Path'];
		this.currentScriptName = scriptName;
		this.basicAdminDialog.request(this.basicAdminDialog.rootUrl + '/Script/execute?Script=' + encodeURIComponent(scriptName), this.executeScriptSuccess, this);
	},
	
	executeScriptSuccess: function(response)
	{
		this.basicAdminDialog.dialog.hideLoading();
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
		var type = node.getAttribute("Type");
		return this.basicAdminDialog.rootUrl + '/Script/remove?Path=' + encodeURIComponent(path)+'&Type='+encodeURIComponent(type);
	}, 
	
	handleRemove: function(node)
	{
		var nameOfTheItem = node.getAttribute("Path");
		if(confirm("Remove " + nameOfTheItem + "?"))
		{
			this.basicAdminDialog.request(this.getRemoveUrl(node), this.removeSuccess, this);
		}
	},
	
	getHelpUrl : function(){
		alert('Override getHelpUrl');
	},
	getGridUrl : function(){
		alert('Override getGridUrl');
	}
});

YAHOO.rapidjs.admin.ScriptDialog = function(gridWindow, errorDialog){
	YAHOO.rapidjs.admin.ScriptDialog.superclass.constructor.call(this, gridWindow, 'Script', 430, 243, errorDialog);
	this.rootUrl = this.getRootUrl();
};


YAHOO.extendX(YAHOO.rapidjs.admin.ScriptDialog, YAHOO.rapidjs.admin.BasicAdminDialog, {
	
	show: function(mode, type){
		this.mode = mode;
		if(this.mode == BasicAdminDialog.ADD){
			this.clear();
		}
		this.type = type;
		if (type == 'Periodic'){
			this.dialog.helpWindow.rapidWindow.url = this.pScriptDlgHelp;
			YAHOO.util.Dom.setStyle(this.periodView, 'display', '');
			YAHOO.util.Dom.setStyle(this.groupView, 'display', 'none');
		}
		else{
			this.dialog.helpWindow.rapidWindow.url = this.oScriptDlgHelp;
			YAHOO.util.Dom.setStyle(this.periodView, 'display', 'none');
			YAHOO.util.Dom.setStyle(this.groupView, 'display', '');
		}
		this.autoHeight();
		this.dialog.show();
	}, 
	populateFieldsForUpdate: function(node)
	{
		var path = node.getAttribute('Path');
		var period = node.getAttribute('Period');
		var allowedGroups = node.getAttribute('AllowedGroups');
		if(allowedGroups && allowedGroups.length > 0) allowedGroups = allowedGroups+",";
		var allowAll = node.getAttribute('AllowAll');
		this.pathInput.readOnly = true;
		this.pathInput.value = path;
		this.periodInput.value = period;
		this.groupInput.value = allowedGroups;
		this.allowAllInput.checked = allowAll == 'true';
	},
	
	clear: function()
	{
		this.pathInput.value = '';
		this.periodInput.value = '';
		this.periodInput.value = '';
		this.pathInput.readOnly = false;
		this.groupInput.value = '';
		this.allowAllInput.checked = false;
	},
	
	handleSave: function()
	{
		var path = encodeURIComponent(this.pathInput.value);
		var period = encodeURIComponent(this.periodInput.value);
		if(this.mode == BasicAdminDialog.UPDATE){
			if(this.type == 'Periodic')
			{
				this.request(this.rootUrl + '/Script/updatePeriodic?Path=' + path + '&Period=' + period, this.saveSuccess);
			}
			else if(this.type == 'OnDemand')
			{
				var allowAll = this.allowAllInput.checked;
				var groups = this.groupInput.value.trim();
				if(groups.charAt(groups.length-1) == ',')
				{
					groups = groups.substr(0,groups.length-1);
				}
				this.request(this.rootUrl + '/Script/updateOnDemand?Path=' + path + '&AllowedGroups=' + encodeURIComponent(groups)+'&AllowAll='+allowAll, this.saveSuccess);
			}
		}
		else{
			if(this.type == 'Periodic'){
				this.request(this.rootUrl + '/Script/addPeriodic?Path=' + path + '&Period=' + period, this.saveSuccess);
			}
			else{
				var allowAll = this.allowAllInput.checked;
				var groups = this.groupInput.value.trim();
				if(groups.charAt(groups.length-1) == ',')
				{
					groups = groups.substr(0,groups.length-1);
				}
				this.request(this.rootUrl + '/Script/addOnDemand?Path=' + path + '&AllowedGroups=' + encodeURIComponent(groups)+'&AllowAll='+allowAll, this.saveSuccess);
			}
		}
		
	},
	actionSuccess: function(response)
	{
		YAHOO.rapidjs.ServerStatus.refreshState(true);
		this.checkResponse(response);
	}, 
	render : function(){
		this.pScriptDlgHelp = this.getPeriodicScriptDlgHelp();
		this.oScriptDlgHelp = this.getOnDemandScriptDlgHelp();
		var dh = YAHOO.ext.DomHelper;
		var wrapper = dh.append(this.container, {tag:'div', cls:'r-admin-dlg-wrp'});
		var pathView = dh.append(wrapper, {tag:'div', 
			html:'<table class="r-admin-form-table"><tbody><tr><td><div class="r-admin-dlg-txt">Path:</div></td>' +
				'<td><div class="r-admin-inputwrp"/></td></tr></tbody></table>'});
		var inputWrappers = pathView.getElementsByTagName('div');
		this.pathInput = dh.append(inputWrappers[1], {tag:'input', cls:'r-admin-input'});
		this.periodView = dh.append(wrapper, {tag:'div',
			html:'<table class="r-admin-form-table"><tbody><tr><td><div class="r-admin-dlg-txt">Period:</div></td>' +
				'<td><div class="r-admin-inputwrp"/></td><td><div>sec.</div></td></tr></tbody></table>'});
		var periodWrappers = this.periodView.getElementsByTagName('div');
		this.periodInput = dh.append(periodWrappers[1], {tag:'input', cls:'r-admin-input'});
	
		this.groupView = dh.append(wrapper, {tag:'div',
			html:'<table class="r-admin-form-table"><tbody><tr><td><div class="r-admin-dlg-txt">Allowed Groups:</div></td>' +
				'<td><div class="r-admin-inputwrp"/></td></tr><tr><td>Allow All:</td>' +
				'<td><div class="r-admin-inputwrp"/></td></tr></tbody></table>'});	
		var groupWrappers = this.groupView.getElementsByTagName('div');
		this.groupInput = dh.append(groupWrappers[1], {tag:'input', cls:'r-admin-input'});
		this.allowAllInput = dh.append(groupWrappers[2], {tag:'input', type:'checkbox',cls:''});
		
		new YAHOO.rapidjs.component.FormAutoComplete(this.groupInput, 
		{url:this.getGroupsUrl(), contentPath:'Group', queryParam:'Prefix', delimChar:',', suggestCls:'r-admin-script-suggestion'});
		
		this.dialog.addTabListener(this.cancelButton.el.dom, this.pathInput);
		this.dialog.addTabListener(this.periodInput, this.saveButton.el.dom);
		this.dialog.addHelp({url:this.pScriptDlgHelp});
	},
	
	setDefaultInput : function(){
		this.dialog.defaultInput = this.pathInput;
	}, 
	getPeriodicScriptDlgHelp : function(){
		alert('Override getPeriodicScriptDlgHelp');
	},
	getOnDemandScriptDlgHelp : function(){
		alert('Override getOnDemandScriptDlgHelp');
	}, 
	getGroupsUrl : function(){
		alert('Override getGroupsUrl');
	}, 
	getRootUrl : function(){
		alert('Override getRootUrl');
	}
	
});