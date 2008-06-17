YAHOO.rapidjs.riadmin.Users = function(errorDialog){
	var dh = YAHOO.ext.DomHelper;
	var gridConfig = {
		content:[	{header:'Actions',type:"Action", width:'50',actions:[
						{handler:this.handleUpdate,scope:this,image:'../images/layout/pencil.png', tooltip:'Update'},
						{handler:this.handleRemove,scope:this,image:'../images/layout/cross.png', tooltip:'Remove'}]},
					{header:'User',width:'385',attribute:'Login'}
				], 
		url:'/RapidManager/User/getUsers',
		rootTag:'Users', 
		pollInterval:60, 
		id:'usersGrid', 
		contentPath:'User', 
		nodeId: 'Login'
	};
	
	var crudConfig = {
		crudTitle: 'Users',
		addButtonText: 'Add User',
		addIconCssClass: 'riadmin-user-add'
	};
	
	YAHOO.rapidjs.riadmin.Users.superclass.constructor.call(this, gridConfig, crudConfig, errorDialog);
	new YAHOO.rapidjs.component.layout.HelpTool(this.gLayout, "center", this.gridWindow, {url:help_ri_admin_users_tab});
};

YAHOO.extendX(YAHOO.rapidjs.riadmin.Users, YAHOO.rapidjs.riadmin.BasicCrud, {
	createDialog: function(gridWindow, errorDialog){
		return new YAHOO.rapidjs.riadmin.UserDialog(gridWindow, errorDialog);
	},
	
	getRemoveUrl: function(node)
	{
		var userToBeRemoved = node.getAttribute('Login');
		return '/RapidManager/User/delete?UserName=' + encodeURIComponent(userToBeRemoved);
	}
});


YAHOO.rapidjs.riadmin.UserDialog = function(gridWindow, errorDialog){
	YAHOO.rapidjs.riadmin.UserDialog.superclass.constructor.call(this, gridWindow, 'User', 530, 530, errorDialog);
};

YAHOO.extendX(YAHOO.rapidjs.riadmin.UserDialog, YAHOO.rapidjs.riadmin.BasicCrudDialog, {
	populateFieldsForUpdate: function(node)
	{
		this.nameInput.readOnly = true;
		var userName = node.getAttribute('Login');
		this.nameInput.value = userName;
		var url = '/RapidInsight/ManagedObject/invoke?Operation=admin_scripts/getUserDetails&UserName=' + encodeURIComponent(userName);
		this.request(url, this.getUserDetailsSuccess);
	},
	
	populateFieldsForAdd: function()
	{
		this.request('/RapidInsight/Group/getInfo', this.getGroupsSuccess);
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
			var xml = response.responseXML;
			this.populateUsersGroups(xml.getElementsByTagName("UsersGroups")[0].getElementsByTagName('Group'));
			this.populateOtherGroups(xml.getElementsByTagName("OtherGroups")[0].getElementsByTagName('Group'));	
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
			opt.setAttribute('class','riadmin-option');
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
	},
	
	handleSave: function()
	{
		var userName = this.nameInput.value;
		var password1 = this.passwordInput.value;
		var password2 = this.confirmInput.value;
		if(password1 != password2)
		{
			alert("Passwords are not same!");
		}
		else
		{
			var groupsDelimited = new Array();
			for(var i = 0 ; i < this.userGroups.options.length ; i++)
			{
				groupsDelimited[i] = this.userGroups.options[i].value;
			}
			var groupsJoined = groupsDelimited.join('::');
			if(groupsJoined.length == 0)
			{
				alert("Users should be assigned to at least one group!");
			}
			else
			{
				var url = '/RapidInsight/ManagedObject/invoke';
				var postData = 'Operation=admin_scripts/saveUsersData&IsUpdating=' + this.isUpdating + '&UserName=' + userName + "&Password=" + password1 + "&Groups=" + groupsJoined;
				this.doPostRequest(url, postData, this.saveSuccess);					
			}
		}
	},
	
	handleAddGroup: function(){
		SelectUtils.moveAllSelectedsFromSelectToSelect(this.availableGroups, this.userGroups);
	}, 
	
	handleRemoveGroup: function(){
		SelectUtils.moveAllSelectedsFromSelectToSelect(this.userGroups, this.availableGroups);
	}, 
	
	render : function(){
		var dh = YAHOO.ext.DomHelper;
		var detailView = dh.append(this.container, {tag:'div', cls:'riadmin-userdlg-detailwr', 
					html:'<table><tbody><tr><td><div class="riadmin-formtext">Name:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
						'<tr><td><div class="riadmin-formtext">Password:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
						'<tr><td><div class="riadmin-formtext">Confirm Password:</div></td><td><div class="riadmin-inputwrp"/></td></tr><tbody><table>'});
						
		var inputWrappers = detailView.getElementsByTagName('div');
		this.nameInput = dh.append(inputWrappers[1], {tag:'input', cls:'riadmin-input'});
		this.passwordInput = dh.append(inputWrappers[3], {tag:'input', cls:'riadmin-input', type:'password'});
		this.confirmInput = dh.append(inputWrappers[5], {tag:'input', cls:'riadmin-input', type:'password'});
		
		
		var groupView = dh.append(this.container, {tag:'div', cls:'riadmin-userdlg-groupwr', 
					html:'<table><tbody><tr><td><div>Available Groups</div></td><td></td><td><div>User\'s Groups</div></td></tr>' +
						'<tr><td><div class="riadmin-selectwrp"/></td>' +
						'<td><div class="riadmin-dlg-buttonswrp"/></td>' +
						'<td><div class="riadmin-selectwrp"/></td></tr></tbody></table>'});
		var groupComps = groupView.getElementsByTagName('div');
		this.availableGroups = dh.append(groupComps[2], {tag:'select', size:'15', cls:'riadmin-select'});
		this.availableGroups.multiple = true;
		this.userGroups = dh.append(groupComps[4], {tag:'select', size:'15', cls:'riadmin-select'});
		this.userGroups.multiple = true;
		var userButtonWrapper = dh.append(groupComps[3], {tag:'div', 
			html:'<table><tbody><tr><td><div class="riadmin-dlg-bwrap"/></td></tr>' +
				'<tr><td><div class="riadmin-dlg-bwrap"/></td></tr></tbody></table>'});
		
		var groupButtons = userButtonWrapper.getElementsByTagName('div');
		this.addGroupButton = new YAHOO.ext.Button(groupButtons[0], {handler: this.handleAddGroup, scope: this,text: '>',minWidth: 30});
		this.removeGroupButton =  new YAHOO.ext.Button(groupButtons[1], {handler: this.handleRemoveGroup, scope: this,text: '<',minWidth: 30});
		
		this.dialog.addTabListener(this.cancelButton.el.dom, this.nameInput);
		this.dialog.addTabListener(this.userGroups, this.saveButton.el.dom);
		this.dialog.addHelp({url:help_ri_admin_users_add_dlg});
	}, 
	
	setDefaultInput : function(){
		this.dialog.defaultInput = this.nameInput;
	}
});