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
YAHOO.rapidjs.admin.Users = function(errorDialog){
	var gridConfig = {
		content:[	{header:'Actions',type:"Action", width:'50',actions:[
						{handler:this.handleUpdate,scope:this,image:'../images/layout/pencil.png', tooltip:'Update'},
						{handler:this.handleRemove,scope:this,image:'../images/layout/cross.png', tooltip:'Remove'}]},
					{header:'User',width:'385',attribute:'UserName'}
				], 
		url:this.getGridUrl(),
		rootTag:'Users', 
		pollInterval:60, 
		id:'usersGrid', 
		contentPath:'User', 
		nodeId: 'UserName'
	};
	
	var crudConfig = {
		crudTitle: 'Users',
		addButtonText: 'Add User',
		addIconCssClass: 'r-admin-user-add'
	};
	
	YAHOO.rapidjs.admin.Users.superclass.constructor.call(this, gridConfig, crudConfig, errorDialog);
	new YAHOO.rapidjs.component.layout.HelpTool(this.gLayout, "center", this.gridWindow, {url:this.getHelpUrl()});
};

YAHOO.extendX(YAHOO.rapidjs.admin.Users, YAHOO.rapidjs.admin.BasicAdmin, {
	
	getGridUrl : function(){
		alert("Override getGridUrl method");
	}, 
	
	getHelpUrl : function(){
		alert("Override getHelpUrl method");
	}
	
});


YAHOO.rapidjs.admin.UserDialog = function(gridWindow, dialogWidth, dialogHeight, errorDialog){
	YAHOO.rapidjs.admin.UserDialog.superclass.constructor.call(this, gridWindow, 'User', dialogWidth, dialogHeight, errorDialog);
};

YAHOO.extendX(YAHOO.rapidjs.admin.UserDialog, YAHOO.rapidjs.admin.BasicAdminDialog, {
	render: function(){
		var dh = YAHOO.ext.DomHelper;
		var wrapper = dh.append(this.container, {tag:'div', cls:'r-admin-dlg-wrp'});
		
		this.createUserDetailView(wrapper);
		var groupView = dh.append(wrapper, {tag:'div', 
					html:'<table class="r-admin-form-table"><tbody><tr><td><div>Available Groups</div></td><td></td><td><div>User\'s Groups</div></td></tr>' +
						'<tr><td><div class="r-admin-listwrp"/></td>' +
						'<td><div class="r-admin-dlg-buttonswrp"/></td>' +
						'<td><div class="r-admin-listwrp"/></td></tr></tbody></table>'});
		var groupComps = groupView.getElementsByTagName('div');
		this.availableGroups = dh.append(groupComps[2], {tag:'select', size:'15', cls:'r-admin-list'});
		this.availableGroups.multiple = true;
		this.userGroups = dh.append(groupComps[4], {tag:'select', size:'15', cls:'r-admin-list'});
		this.userGroups.multiple = true;
		var userButtonWrapper = dh.append(groupComps[3], {tag:'div', 
			html:'<table><tbody><tr><td><div class="r-admin-dlg-bwrap"/></td></tr>' +
				'<tr><td><div class="r-admin-dlg-bwrap"/></td></tr></tbody></table>'});
		
		var groupButtons = userButtonWrapper.getElementsByTagName('div');
		this.addGroupButton = new YAHOO.ext.Button(groupButtons[0], {handler: this.handleAddGroup, scope: this,text: '>',minWidth: 30});
		this.removeGroupButton =  new YAHOO.ext.Button(groupButtons[1], {handler: this.handleRemoveGroup, scope: this,text: '<',minWidth: 30});
		
		this.dialog.addTabListener(this.cancelButton.el.dom, this.nameInput);
		this.dialog.addTabListener(this.userGroups, this.saveButton.el.dom);
		this.dialog.addHelp({url:this.getDlgHelpUrl()});
	}, 
	
	setDefaultInput : function(){
		this.dialog.defaultInput = this.nameInput;
	}, 
	
	populateFieldsForUpdate: function(node)
	{
		this.populateFieldsFromExistingNode(node);
		this.nameInput.readOnly = true;
	},
	populateFieldsForClone: function(node)
	{
		this.populateFieldsFromExistingNode(node);
	},
	
	populateFieldsFromExistingNode : function(node){
		var userName = node.getAttribute('UserName');
		this.nameInput.value = userName;
		this.populateExtraInputs(node);
		var url = this.getUserDetailsUrl(userName);
		this.request(url, this.getUserDetailsSuccess);
	},
	
	populateFieldsForAdd: function()
	{
		this.request('/RapidManager/Group/getGroups', this.getGroupsSuccess);
	},
	
	getGroupsSuccess: function(response)
	{
		this.dialog.hideLoading();
		if(this.checkResponse(response))
		{
			this.populateOtherGroups(response.responseXML.getElementsByTagName('Group'));	
		}
	},
	getUserDetailsSuccess: function(response)
	{
		this.dialog.hideLoading();
		if(this.checkResponse(response))
		{
			var node = response.responseXML;
			this.populateUsersGroups(node.getElementsByTagName("UsersGroups")[0].getElementsByTagName('Group'));
			this.populateOtherGroups(node.getElementsByTagName("OtherGroups")[0].getElementsByTagName('Group'));
		}
	},
	
	populateUsersGroups: function(groups)
	{
		this.populateGroupsForSelect(groups, this.userGroups);
	},
	
	populateOtherGroups: function(groups)
	{
		this.populateGroupsForSelect(groups, this.availableGroups);
	},
	
	populateGroupsForSelect: function(groups, select)
	{
		for(var index = 0; index < groups.length; index++) {
			var userName = groups[index].getAttribute('Name');
			opt = document.createElement('option');
			opt.setAttribute('value',userName);
			opt.setAttribute('title',userName);
			opt.innerHTML = userName;
			select.appendChild(opt);
		}
	},
	
	clear: function()
	{
		this.nameInput.readOnly = false;
		this.nameInput.value = '';
		this.passwordInput.value = '';
		this.confirmInput.value = '';
		this.availableGroups.innerHTML = '';
		this.userGroups.innerHTML = '';
		this.clearExtraInputs();
	},
	
	handleSave: function()
	{
		var userName = encodeURIComponent(this.nameInput.value);
		var password1 = encodeURIComponent(this.passwordInput.value);
		var password2 = encodeURIComponent(this.confirmInput.value);
		if(password1 != password2)
		{
			alert("Passwords are not same!");
		}
		else
		{
			var params = new Array();
			var groupsDelimited = new Array();
			for(var i = 0 ; i < this.userGroups.options.length ; i++)
			{
				groupsDelimited[i] = this.userGroups.options[i].value;
			}
			var groupsJoined = groupsDelimited.join('::');
			params[params.length] = 'UserName=' + userName;
			params[params.length] = 'Password=' + password1;
			params[params.length] = 'Groups='+ groupsJoined;
			params[params.length] = 'IsUpdating=' + (this.mode == BasicAdminDialog.UPDATE);
			this.addExtraPostData(params);
			var url = this.getSaveUrl();
			this.doPostRequest(url, params.join('&'), this.saveSuccess);	
		}
	},
	
	handleAddGroup: function(){
		SelectUtils.moveAllSelectedsFromSelectToSelect(this.availableGroups, this.userGroups);
	}, 
	
	handleRemoveGroup: function(){
		SelectUtils.moveAllSelectedsFromSelectToSelect(this.userGroups, this.availableGroups);
	}, 
	createUserDetailView : function(wrapper){
		var dh = YAHOO.ext.DomHelper;
		var detailView = dh.append(wrapper, {tag:'div',
					html:'<table class="r-admin-form-table"><tbody><tr><td><div class="r-admin-dlg-txt">Name:</div></td><td><div class="r-admin-inputwrp"/></td></tr>' +
						'<tr><td><div class="r-admin-dlg-txt">Password:</div></td><td><div class="r-admin-inputwrp"/></td></tr>' +
						'<tr><td><div class="r-admin-dlg-txt">Confirm Password:</div></td><td><div class="r-admin-inputwrp"/></td></tr><tbody><table>'});
						
		var inputWrappers = detailView.getElementsByTagName('div');
		this.nameInput = dh.append(inputWrappers[1], {tag:'input', cls:'r-admin-input'});
		this.passwordInput = dh.append(inputWrappers[3], {tag:'input', cls:'r-admin-input', type:'password'});
		this.confirmInput = dh.append(inputWrappers[5], {tag:'input', cls:'r-admin-input', type:'password'});
	},
	getDlgHelpUrl : function(){
		alert("Override getDlgHelpUrl");
	},
	addExtraPostData : function(params){
		alert("Override addExtraPostData");
	},
	getSaveUrl : function(){
		alert("Override getSaveUrl");
	}, 
	populateExtraInputs : function(node){
		alert('Override populateExtraInputs');
	}, 
	clearExtraInputs:function(){
		alert('Override clearExtraInputs');
	}, 
	getUserDetailsUrl: function(userName){
		alert('Override clearExtraInputs');
	}
});