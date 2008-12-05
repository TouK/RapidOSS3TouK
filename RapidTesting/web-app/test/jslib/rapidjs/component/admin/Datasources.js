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
YAHOO.rapidjs.admin.Datasources = function(errorDialog){
	var gridConfig = {
		content:[	{header:'Actions',type:"Action", width:'50',actions:[
						{handler:this.handleUpdate,scope:this,image:'../images/layout/pencil.png', tooltip:'Update'},
						{handler:this.handleRemove,scope:this,image:'../images/layout/cross.png', tooltip:'Remove'}]},
					{header:'Name',width:'250',attribute:'Name'}, 
					{header:'Type',width:'400',attribute:'Type'}
				], 
		url:'/RapidManager/Datasource/get',
		rootTag:'Datasources', 
		pollInterval:60, 
		id:'datasourceGrid', 
		contentPath:'Datasource', 
		nodeId: 'Name', 
		title:''
		
	};
	var crudConfig = {crudTitle: 'Datasources'};
	YAHOO.rapidjs.admin.Datasources.superclass.constructor.call(this, gridConfig, crudConfig, errorDialog);
	new YAHOO.rapidjs.component.layout.HelpTool(this.gLayout, "center", this.gridWindow, {url:this.getHelpUrl()});
};

YAHOO.extendX(YAHOO.rapidjs.admin.Datasources, YAHOO.rapidjs.admin.BasicAdmin, {
	newDatabase: function(){
		this.basicAdminDialog.show(BasicAdminDialog.ADD, 'Database');
		this.basicAdminDialog.setTitle('New Database Datasource');
	}, 
	newIC: function(){
		this.basicAdminDialog.show(BasicAdminDialog.ADD, 'Smarts');
		this.basicAdminDialog.setTitle('New Smarts Datasource');
	}, 
	newRS: function(){
		this.basicAdminDialog.show(BasicAdminDialog.ADD, 'RapidSuite');
		this.basicAdminDialog.setTitle('New RapidSuite Datasource');
	}, 
	createButtons: function(gLayout){
		var dh = YAHOO.ext.DomHelper;
		var buttonContainer = dh.append(document.body, {tag:'div'});
		gLayout.add('west', new YAHOO.rapidjs.component.layout.RapidPanel(buttonContainer, {fitToFrame:true, title:'Actions'}));
		gLayout.regions['west'].collapsedEl.addClass('r-admin-actioncollapsed');
		
		new YAHOO.rapidjs.component.ToolbarButton(dh.append(buttonContainer, {tag:'div', cls:'rapid-btnwrp'}), 
	    	{text:'New Database Datasource', className:'r-admin-data-newdb', scope:this, click:this.newDatabase});
		new YAHOO.rapidjs.component.ToolbarButton(dh.append(buttonContainer, {tag:'div', cls:'rapid-btnwrp'}), 
	    	{text:"New Smarts Datasource", className:'r-admin-data-newsmarts', scope:this, click:this.newIC});
		new YAHOO.rapidjs.component.ToolbarButton(dh.append(buttonContainer, {tag:'div', cls:'rapid-btnwrp'}), 
	    	{text:"New RapidSuite Datasource", className:'r-admin-data-newrs', scope:this, click:this.newRS});
	}, 
	
	handleUpdate: function(node){
		var type = node.getAttribute('Type');
		this.basicAdminDialog.setTitle('Update ' + type + ' Datasource');
		this.basicAdminDialog.populateFieldsForUpdate(node);
		this.basicAdminDialog.show(BasicAdminDialog.UPDATE, type);
	},
	handleClone: function(node){
		var type = node.getAttribute('Type');
		this.basicAdminDialog.setTitle('New ' + type + ' Datasource');
		this.basicAdminDialog.populateFieldsForClone(node);
		this.basicAdminDialog.show(BasicAdminDialog.CLONE, type);
	},
	createContextMenu: function(){
		this.menu = new YAHOO.rapidjs.component.menu.ContextMenu();
		var MenuAction = YAHOO.rapidjs.component.menu.MenuAction;
		this.menu.addMenuItem('Update', new MenuAction(this.updateItem, this), true);
		this.menu.addMenuItem('Clone', new MenuAction(this.cloneItem, this), true);
		this.menu.addMenuItem('Remove', new MenuAction(this.removeItem, this), true);
	},
	getRemoveUrl: function(node)
	{
		var type = node.getAttribute('Type');
		var datasourceName = node.getAttribute('Name');
		if(type == 'Smarts'){
			return '/RapidManager/Datasource/removeSmarts?Name='+ encodeURIComponent(datasourceName);
		}else if(type == 'Database'){
			return '/RapidManager/Datasource/removeDatabase?Name='+ encodeURIComponent(datasourceName);
		}
		else{
			return '/RapidManager/Datasource/removeRapidSuite?Name='+ encodeURIComponent(datasourceName);
		}
	}, 
	getHelpUrl : function(){
		alert("Override getHelpUrl method");
	}
});

YAHOO.rapidjs.admin.DatasourceDialog = function(gridWindow, errorDialog){
	
	YAHOO.rapidjs.admin.DatasourceDialog.superclass.constructor.call(this, gridWindow, 'Datasource', 352, 243, errorDialog);
};


YAHOO.extendX(YAHOO.rapidjs.admin.DatasourceDialog, YAHOO.rapidjs.admin.BasicAdminDialog, {
	
	show: function(mode, type){
		this.mode = mode;
		if(this.mode == BasicAdminDialog.ADD){
			this.clear();
		}
		this.type = type;
		if(type == 'Database'){
			this.dialog.helpWindow.rapidWindow.url = this.dbDlgHelp;
			YAHOO.util.Dom.setStyle(this.dbView, 'display', '');
			YAHOO.util.Dom.setStyle(this.icView, 'display', 'none');
			YAHOO.util.Dom.setStyle(this.riView, 'display', 'none');
		}
		else if (type == 'Smarts'){
			this.dialog.helpWindow.rapidWindow.url = this.icDlgHelp;
			YAHOO.util.Dom.setStyle(this.riView, 'display', 'none');
			YAHOO.util.Dom.setStyle(this.dbView, 'display', 'none');
			YAHOO.util.Dom.setStyle(this.icView, 'display', '');
		}
		else{
			this.dialog.helpWindow.rapidWindow.url = this.rsDlgHelp;
			YAHOO.util.Dom.setStyle(this.dbView, 'display', 'none');
			YAHOO.util.Dom.setStyle(this.icView, 'display', 'none');
			YAHOO.util.Dom.setStyle(this.riView, 'display', '');
		}
		this.autoHeight();
		this.dialog.show();
	}, 
	populateFieldsForUpdate: function(node){
		this.dataNameInput.readOnly = true;
		this.populateFieldsFromExistingNode(node);
	},
	populateFieldsForClone: function(node){
		this.populateFieldsFromExistingNode(node);
	},
	populateFieldsFromExistingNode: function(node)
	{
		var type = node.getAttribute('Type');
		var datasourceName = node.getAttribute('Name');
		var interval = node.getAttribute('ReconnectInterval');
		var username = node.getAttribute('Username');
		var password = node.getAttribute('Password');
		this.dataNameInput.value = datasourceName;
		this.intervalInput.value = interval;
		if(type == 'Database'){
			var driver = node.getAttribute('Driver');
			var url = node.getAttribute('Url');
			this.driverInput.value = driver;
			this.urlInput.value = url;
			this.dbUserNameInput.value = username;
			this.dbPwdInput.value = password;
		}
		else if(type == 'Smarts'){
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
		this.portInput.value = this.getDefaultPort();
		this.riUserNameInput.value = 'rsadmin';
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
		else if(this.type == 'Smarts'){
			var broker = encodeURIComponent(this.brokerInput.value);
			var domain = encodeURIComponent(this.domainInput.value);
			var domainType = encodeURIComponent(this.domainTypeInput.options[this.domainTypeInput.selectedIndex].value);
			var username = encodeURIComponent(this.icUserNameInput.value);
			var password = encodeURIComponent(this.icPwdInput.value);
			this.request('/RapidManager/Datasource/addSmarts?Name=' + 
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
	
	render: function(){
		this.icDlgHelp = this.getSmartsDlgHelp();
		this.dbDlgHelp = this.getDatabaseDlgHelp();
		this.rsDlgHelp = this.getRapidSuiteDlgHelp();
		var dh = YAHOO.ext.DomHelper;
		var wrapper = dh.append(this.container, {tag:'div', cls:'r-admin-dlg-wrp'});
		var nameView = dh.append(wrapper, {tag:'div', 
			html:'<table class="r-admin-form-table"><tbody><tr><td><div class="r-admin-dlg-txt">Datasource Name:</div></td><td><div class="r-admin-inputwrp"/></td></tr>' +
				'<tr><td><div class="r-admin-dlg-txt">Reconnect Interval:</div></td><td><div class="r-admin-inputwrp"/></td></tr></tbody></table>'});
		var nameInWrps = nameView.getElementsByTagName('div');
		this.dataNameInput = dh.append(nameInWrps[1], {tag:'input', cls:'r-admin-input'});
		this.intervalInput = dh.append(nameInWrps[3], {tag:'input', cls:'r-admin-input'});
		
		this.icView = dh.append(wrapper, {tag:'div',
			html:'<table class="r-admin-form-table"><tbody><tr><td><div class="r-admin-dlg-txt">Broker Name:</div></td><td><div class="r-admin-inputwrp"/></td></tr>' +
				'<tr><td><div class="r-admin-dlg-txt">Domain Name:</div></td><td><div class="r-admin-inputwrp"/></td></tr>' +
				'<tr><td><div class="r-admin-dlg-txt">Domain Type:</div></td><td><div class="r-admin-inputwrp"/></td></tr>' +
				'<tr><td><div class="r-admin-dlg-txt">User Name:</div></td><td><div class="r-admin-inputwrp"/></td></tr>' +
				'<tr><td><div class="r-admin-dlg-txt">Password:</div></td><td><div class="r-admin-inputwrp"/></td></tr></tbody></table>'});
		var icInputWrps = this.icView.getElementsByTagName('div');
		this.brokerInput = dh.append(icInputWrps[1], {tag:'input', cls:'r-admin-input'});
		this.domainInput = dh.append(icInputWrps[3], {tag:'input', cls:'r-admin-input'});
		var domainTypes = {'AM':'AM', 'OI':'OI', 'SAM':'SAM', 'MPLS':'MPLS', 'BGP':'BGP', 'OSPF':'OSPF', 'SDH':'SDH'};
		this.domainTypeInput = dh.append(icInputWrps[5], {tag:'select', cls:'r-admin-input'});
		SelectUtils.populateSelect(this.domainTypeInput, domainTypes);
		this.icUserNameInput = dh.append(icInputWrps[7], {tag:'input', cls:'r-admin-input'});
		this.icPwdInput = dh.append(icInputWrps[9], {tag:'input', cls:'r-admin-input', type:'password'});
		
		this.dbView = dh.append(wrapper, {tag:'div',
			html:'<table class="r-admin-form-table"><tbody><tr><td><div class="r-admin-dlg-txt">Database Driver:</div></td><td><div class="r-admin-inputwrp"/></td></tr>' +
				'<tr><td><div class="r-admin-dlg-txt">Database Url:</div></td><td><div class="r-admin-inputwrp"/></td></tr>' +
				'<tr><td><div class="r-admin-dlg-txt">User Name:</div></td><td><div class="r-admin-inputwrp"/></td></tr>' +
				'<tr><td><div class="r-admin-dlg-txt">Password:</div></td><td><div class="r-admin-inputwrp"/></td></tr></tbody></table>'});
		var dbInputWrps = this.dbView.getElementsByTagName('div');
		this.driverInput = dh.append(dbInputWrps[1], {tag:'input', cls:'r-admin-input'});
		this.urlInput = dh.append(dbInputWrps[3], {tag:'input', cls:'r-admin-input'});
		this.dbUserNameInput = dh.append(dbInputWrps[5], {tag:'input', cls:'r-admin-input'});
		this.dbPwdInput = dh.append(dbInputWrps[7], {tag:'input', cls:'r-admin-input', type:'password'});
		
		this.riView = dh.append(wrapper, {tag:'div',  
			html:'<table class="r-admin-form-table"><tbody><tr><td><div class="r-admin-dlg-txt">Host:</div></td><td><div class="r-admin-inputwrp"/></td></tr>' +
				'<tr><td><div class="r-admin-dlg-txt">Port:</div></td><td><div class="r-admin-inputwrp"/></td></tr>' +
				'<tr><td><div class="r-admin-dlg-txt">User Name:</div></td><td><div class="r-admin-inputwrp"/></td></tr>' +
				'<tr><td><div class="r-admin-dlg-txt">Password:</div></td><td><div class="r-admin-inputwrp"/></td></tr></tbody></table>'});
		var riInputWrps = this.riView.getElementsByTagName('div');
		this.hostInput = dh.append(riInputWrps[1], {tag:'input', cls:'r-admin-input'});
		this.portInput = dh.append(riInputWrps[3], {tag:'input', cls:'r-admin-input'});
		this.riUserNameInput = dh.append(riInputWrps[5], {tag:'input', cls:'r-admin-input'});
		this.riPwdInput = dh.append(riInputWrps[7], {tag:'input', cls:'r-admin-input', type:'password'});
		
		this.dialog.addTabListener(this.cancelButton.el.dom, this.dataNameInput);
		this.dialog.addTabListener(this.icPwdInput, this.saveButton.el.dom);
		this.dialog.addTabListener(this.dbPwdInput, this.saveButton.el.dom);
		this.dialog.addTabListener(this.riPwdInput, this.saveButton.el.dom);
		this.dialog.addHelp({url:this.icDlgHelp});
	}, 
	
	setDefaultInput : function(){
		this.dialog.defaultInput = this.dataNameInput;
	}, 
	getSmartsDlgHelp: function(){
		alert('Override getSmartsDlgHelp');
	},
	getDatabaseDlgHelp: function(){
		alert('Override getDatabaseDlgHelp');
	}, 
	getRapidSuiteDlgHelp: function(){
		alert('Override getRapidSuiteDlgHelp');
	}, 
	getDefaultPort : function(){
		alert('Override getDefaultPort');
	}
});