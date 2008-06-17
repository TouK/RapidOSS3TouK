YAHOO.rapidjs.riadmin.Datasources = function(errorDialog){
	var gridConfig = {
		content:[	{header:'Actions',type:"Action", width:'50',actions:[
						{handler:this.handleUpdate,scope:this,image:'../images/layout/pencil.png', tooltip:'Update'},
						{handler:this.handleRemove,scope:this,image:'../images/layout/cross.png', tooltip:'Remove', visible:'data["Type"] != "RapidSuite"'}]},
					{header:'Name',width:'250',attribute:'Name'}, 
					{header:'Type',width:'400',attribute:'Type'}
				], 
		url:'/RapidInsight/ManagedObject/invoke?Script=admin/getDatasourceData',
		rootTag:'Datasources', 
		pollInterval:60, 
		id:'datasourceGrid', 
		contentPath:'Datasource', 
		nodeId: 'Name', 
		title:'Rapid Insight Datasources'
		
	};
	var crudConfig = {crudTitle: 'Datasources'};
	YAHOO.rapidjs.riadmin.Datasources.superclass.constructor.call(this, gridConfig, crudConfig, errorDialog);
	new YAHOO.rapidjs.component.layout.HelpTool(this.gLayout, "center", this.gridWindow, {url:help_ri_admin_datasources_tab});
};

YAHOO.extendX(YAHOO.rapidjs.riadmin.Datasources, YAHOO.rapidjs.riadmin.BasicCrud, {
	newDatabase: function(){
		this.basicCrudDialog.show(BasicCrudDialog.ADD, 'Database');
		this.basicCrudDialog.setTitle('New Database Datasource');
	}, 
	newIC: function(){
		this.basicCrudDialog.show(BasicCrudDialog.ADD, 'InCharge');
		this.basicCrudDialog.setTitle('New InCharge Datasource');
	}, 
	createButtons: function(gLayout){
		var dh = YAHOO.ext.DomHelper;
		var buttonContainer = dh.append(document.body, {tag:'div'});
		gLayout.add('west', new YAHOO.rapidjs.component.layout.RapidPanel(buttonContainer, {fitToFrame:true, title:'Actions'}));
		gLayout.regions['west'].collapsedEl.addClass('riadmin-actioncollapsed');
		
		new YAHOO.rapidjs.component.ToolbarButton(dh.append(buttonContainer, {tag:'div', cls:'rapid-btnwrp'}), 
	    	{text:'New Database Datasource', className:'riadmin-data-newdb', scope:this, click:this.newDatabase});
		new YAHOO.rapidjs.component.ToolbarButton(dh.append(buttonContainer, {tag:'div', cls:'rapid-btnwrp'}), 
	    	{text:"New InCharge Datasource", className:'riadmin-data-newic', scope:this, click:this.newIC});
	}, 
	
	createDialog: function(gridWindow, errorDialog){
		return new YAHOO.rapidjs.component.dialogs.DatasourceDialog(gridWindow, errorDialog);
	},
	handleUpdate: function(node){
		var type = node.getAttribute('Type');
		this.basicCrudDialog.setTitle('Update' + type + 'Datasource');
		this.basicCrudDialog.populateFieldsForUpdate(node);
		this.basicCrudDialog.show(BasicCrudDialog.UPDATE, type);
	},
	createContextMenu: function(){
		this.menu = new YAHOO.rapidjs.component.menu.ContextMenu();
		var MenuAction = YAHOO.rapidjs.component.menu.MenuAction;
		this.menu.addMenuItem('Update', new MenuAction(this.updateItem, this), true);
		this.menu.addMenuItem('Remove', new MenuAction(this.removeItem, this), 'data["Type"] != "RapidSuite"');
	},
	getRemoveUrl: function(node)
	{
		var type = node.getAttribute('Type');
		var datasourceName = node.getAttribute('Name');
		if(type == 'InCharge'){
			var domainType = node.getAttribute('DomainType');
			return '/RapidInsight/ManagedObject/invoke?Script=admin/removeInChargeDatasource&Name='+ 
			encodeURIComponent(datasourceName) + '&DomainType=' + encodeURIComponent(domainType);
		}else{
			return '/RapidManager/Datasource/removeDatabase?Name='+ encodeURIComponent(datasourceName);
		}
	}
});

YAHOO.rapidjs.component.dialogs.DatasourceDialog = function(gridWindow, errorDialog){
	
	YAHOO.rapidjs.component.dialogs.DatasourceDialog.superclass.constructor.call(this, gridWindow, 'Datasource', 430, 243, errorDialog);
};


YAHOO.extendX(YAHOO.rapidjs.component.dialogs.DatasourceDialog, YAHOO.rapidjs.riadmin.BasicCrudDialog, {
	
	show: function(mode, type){
		this.mode = mode;
		if(this.mode == BasicCrudDialog.ADD){
			this.clear();
		}
		this.type = type;
		if(type == 'Database'){
			this.dialog.helpWindow.rapidWindow.url = this.dbDlgHelp;
			YAHOO.util.Dom.setStyle(this.dbView, 'display', '');
			YAHOO.util.Dom.setStyle(this.icView, 'display', 'none');
			YAHOO.util.Dom.setStyle(this.riView, 'display', 'none');
		}
		else if (type == 'InCharge'){
			this.dialog.helpWindow.rapidWindow.url = this.icDlgHelp;
			YAHOO.util.Dom.setStyle(this.riView, 'display', 'none');
			YAHOO.util.Dom.setStyle(this.dbView, 'display', 'none');
			YAHOO.util.Dom.setStyle(this.icView, 'display', '');
		}
		else{
			YAHOO.util.Dom.setStyle(this.dbView, 'display', 'none');
			YAHOO.util.Dom.setStyle(this.icView, 'display', 'none');
			YAHOO.util.Dom.setStyle(this.riView, 'display', '');
		}
		this.autoHeight();
		this.dialog.show();
	}, 
	populateFieldsForUpdate: function(node)
	{
		var type = node.getAttribute('Type');
		var datasourceName = node.getAttribute('Name');
		var interval = node.getAttribute('ReconnectInterval');
		var username = node.getAttribute('Username');
		var password = node.getAttribute('Password');
		this.dataNameInput.value = datasourceName;
		this.dataNameInput.readOnly = true;
		this.intervalInput.value = interval;
		if(type == 'Database'){
			var driver = node.getAttribute('Driver');
			var url = node.getAttribute('Url');
			this.driverInput.value = driver;
			this.urlInput.value = url;
			this.dbUserNameInput.value = username;
			this.dbPwdInput.value = password;
		}
		else if(type == 'InCharge'){
			var domain = node.getAttribute('Domain');
			var broker = node.getAttribute('Broker');
			var domainType = node.getAttribute('DomainType');
			SelectUtils.selectTheValue(this.domainTypeInput, domainType, 2);
			this.brokerInput.value = broker;
			this.domainInput.value = domain;
			this.icUserNameInput.value = username;
			this.icPwdInput.value = password;
		}
		else{
			var host = node.getAttribute('Host');
			var port = node.getAttribute('Port');
			this.hostInput.value = host;
			this.portInput.value = port;
			this.riUserNameInput.value = username;
			this.riPwdInput.value = password;
		}
	},
	
	clear: function()
	{
		this.dataNameInput.readOnly = false;
		this.domainTypeInput.selectedIndex = 2;
		this.dataNameInput.value = '';
		this.intervalInput.value = '60';
		this.driverInput.value = 'com.mysql.jdbc.Driver';
		this.urlInput.value = 'jdbc:mysql://localhost/mydatabase';
		this.dbUserNameInput.value = '';
		this.dbPwdInput.value = '';
		this.brokerInput.value = 'localhost:426';
		this.domainInput.value = 'INCHARGE-SA';
		this.icUserNameInput.value = 'admin';
		this.icPwdInput.value = 'changeme';
		this.hostInput.value = 'localhost';
		this.portInput.value = '9191';
		this.riUserNameInput.valu = 'rsadmin';
		this.riPwdInput.value = 'changeme';
	},
	
	handleSave: function()
	{
		var datasourceName = encodeURIComponent(this.dataNameInput.value);
		var interval = encodeURIComponent(this.intervalInput.value);
		if(this.type == 'Database'){
			var driver = encodeURIComponent(this.driverInput.value);
			var url = encodeURIComponent(this.urlInput.value);
			var username = encodeURIComponent(this.dbUserNameInput.value);
			var password = encodeURIComponent(this.dbPwdInput.value);
			this.request('/RapidManager/Datasource/addDatabase?Name=' + datasourceName + '&ReconnectInterval=' + interval + 
			'&Driver=' +driver+ '&Url=' +url+'&Username=' +username+'&Password=' + password, this.saveSuccess);
		}
		else if(this.type == 'InCharge'){
			var broker = encodeURIComponent(this.brokerInput.value);
			var domain = encodeURIComponent(this.domainInput.value);
			var domainType = encodeURIComponent(this.domainTypeInput.options[this.domainTypeInput.selectedIndex].value);
			var username = encodeURIComponent(this.icUserNameInput.value);
			var password = encodeURIComponent(this.icPwdInput.value);
			this.request('/RapidInsight/ManagedObject/invoke?Script=admin/saveInChargeDatasource&Name=' + 
				datasourceName + '&ReconnectInterval=' + interval +'&Broker=' +broker+ '&Domain=' +domain+'&Username=' +username+
				'&Password=' + password +'&DomainType=' + domainType, this.saveSuccess);
		}
		else{
			var host = encodeURIComponent(this.hostInput.value);
			var port = encodeURIComponent(this.portInput.value);
			var username = encodeURIComponent(this.riUserNameInput.value);
			var password = encodeURIComponent(this.riPwdInput.value);
			this.request('/RapidManager/Datasource/addRapidSuite?Name=' + datasourceName + '&ReconnectInterval=' + interval + 
			'&Host=' +host+ '&Port=' +port+'&Username=' +username+'&Password=' + password, this.saveSuccess);
		}
	}, 
	
	render : function(){
		this.icDlgHelp = help_ri_admin_ic_datasource_add_dlg;
		this.dbDlgHelp = help_ri_admin_db_datasource_add_dlg;
		var dh = YAHOO.ext.DomHelper;
		var nameView = dh.append(this.container, {tag:'div', cls:'riadmin-datadlg-namewr', 
			html:'<table><tbody><tr><td><div class="riadmin-formtext">Datasource Name:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
				'<tr><td><div class="riadmin-formtext">Reconnect Interval:</div></td><td><div class="riadmin-inputwrp"/></td></tr></tbody></table>'});
		var nameInWrps = nameView.getElementsByTagName('div');
		this.dataNameInput = dh.append(nameInWrps[1], {tag:'input', cls:'riadmin-input'});
		this.intervalInput = dh.append(nameInWrps[3], {tag:'input', cls:'riadmin-input'});
		
		this.icView = dh.append(this.container, {tag:'div', cls:'riadmin-datadlg-icwr', 
			html:'<table><tbody><tr><td><div class="riadmin-formtext">Broker Name:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
				'<tr><td><div class="riadmin-formtext">Domain Name:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
				'<tr><td><div class="riadmin-formtext">Domain Type:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
				'<tr><td><div class="riadmin-formtext">User Name:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
				'<tr><td><div class="riadmin-formtext">Password:</div></td><td><div class="riadmin-inputwrp"/></td></tr></tbody></table>'});
		var icInputWrps = this.icView.getElementsByTagName('div');
		this.brokerInput = dh.append(icInputWrps[1], {tag:'input', cls:'riadmin-input'});
		this.domainInput = dh.append(icInputWrps[3], {tag:'input', cls:'riadmin-input'});
		var domainTypes = {'AM':'AM', 'OI':'OI', 'SAM':'SAM', 'MPLS':'MPLS', 'BGP':'BGP', 'OSPF':'OSPF', 'SDH':'SDH'};
		this.domainTypeInput = dh.append(icInputWrps[5], {tag:'select', cls:'riadmin-combobox'});
		SelectUtils.populateSelect(this.domainTypeInput, domainTypes);
		this.icUserNameInput = dh.append(icInputWrps[7], {tag:'input', cls:'riadmin-input'});
		this.icPwdInput = dh.append(icInputWrps[9], {tag:'input', cls:'riadmin-input', type:'password'});
		
		this.dbView = dh.append(this.container, {tag:'div', cls:'riadmin-datadlg-dbwr', 
			html:'<table><tbody><tr><td><div class="riadmin-formtext">Database Driver:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
				'<tr><td><div class="riadmin-formtext">Database Url:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
				'<tr><td><div class="riadmin-formtext">User Name:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
				'<tr><td><div class="riadmin-formtext">Password:</div></td><td><div class="riadmin-inputwrp"/></td></tr></tbody></table>'});
		var dbInputWrps = this.dbView.getElementsByTagName('div');
		this.driverInput = dh.append(dbInputWrps[1], {tag:'input', cls:'riadmin-input'});
		this.urlInput = dh.append(dbInputWrps[3], {tag:'input', cls:'riadmin-input'});
		this.dbUserNameInput = dh.append(dbInputWrps[5], {tag:'input', cls:'riadmin-input'});
		this.dbPwdInput = dh.append(dbInputWrps[7], {tag:'input', cls:'riadmin-input', type:'password'});
		
		this.riView = dh.append(this.container, {tag:'div', cls:'riadmin-datadlg-riwr', 
			html:'<table><tbody><tr><td><div class="riadmin-formtext">Host:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
				'<tr><td><div class="riadmin-formtext">Port:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
				'<tr><td><div class="riadmin-formtext">User Name:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
				'<tr><td><div class="riadmin-formtext">Password:</div></td><td><div class="riadmin-inputwrp"/></td></tr></tbody></table>'});
		var riInputWrps = this.riView.getElementsByTagName('div');
		this.hostInput = dh.append(riInputWrps[1], {tag:'input', cls:'riadmin-input'});
		this.portInput = dh.append(riInputWrps[3], {tag:'input', cls:'riadmin-input'});
		this.riUserNameInput = dh.append(riInputWrps[5], {tag:'input', cls:'riadmin-input'});
		this.riPwdInput = dh.append(riInputWrps[7], {tag:'input', cls:'riadmin-input', type:'password'});
		
		this.dialog.addTabListener(this.cancelButton.el.dom, this.dataNameInput);
		this.dialog.addTabListener(this.icPwdInput, this.saveButton.el.dom);
		this.dialog.addTabListener(this.dbPwdInput, this.saveButton.el.dom);
		this.dialog.addTabListener(this.riPwdInput, this.saveButton.el.dom);
		this.dialog.addHelp({url:this.icDlgHelp});
	}, 
	
	setDefaultInput : function(){
		this.dialog.defaultInput = this.dataNameInput;
	}
});