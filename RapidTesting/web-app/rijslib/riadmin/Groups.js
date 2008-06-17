YAHOO.rapidjs.riadmin.Groups = function(errorDialog){
	var gridConfig = {
		content:[	{header:'Actions',type:"Action", width:'50',actions:[
						{handler:this.handleUpdate,scope:this,image:'../images/layout/pencil.png', tooltip:'Update'},
						{handler:this.handleRemove,scope:this,image:'../images/layout/cross.png', tooltip:'Remove'}]},
					{header:'Group Name',width:'385',attribute:'Name'}
				], 
		url:'/RapidInsight/Group/getInfo',
		rootTag:'Groups', 
		pollInterval:60, 
		id:'groupsGrid', 
		contentPath:'Group', 
		nodeId: 'Name', 
		buttons:{
			create:{text:'Add Group', minWidth:50},
			update:{text:'Update', minWidth:50},
			remove:{text:'Remove', minWidth:50}
			}
	};
	
	var crudConfig = {
		crudTitle: 'Groups',
		addButtonText: 'Add Group',
		addIconCssClass: 'riadmin-group-add'
	};
	
	YAHOO.rapidjs.riadmin.Groups.superclass.constructor.call(this, gridConfig, crudConfig, errorDialog);
	new YAHOO.rapidjs.component.layout.HelpTool(this.gLayout, "center", this.gridWindow, {url:help_ri_admin_groups_tab});
};

YAHOO.extendX(YAHOO.rapidjs.riadmin.Groups, YAHOO.rapidjs.riadmin.BasicCrud, {
	
	createDialog: function(gridWindow, errorDialog){
		return new YAHOO.rapidjs.riadmin.GroupDialog(this.gridWindow, errorDialog);
	},
	
	getRemoveUrl: function(node){
		var groupToBeRemoved = node.getAttribute('Name');
		return '/RapidManager/Group/delete?Name=' + encodeURIComponent(groupToBeRemoved);	
	}
});

YAHOO.rapidjs.riadmin.GroupDialog = function(gridWindow, errorDialog){
	YAHOO.rapidjs.riadmin.GroupDialog.superclass.constructor.call(this, gridWindow, 'Group', 530, 530, errorDialog);
};

YAHOO.extendX(YAHOO.rapidjs.riadmin.GroupDialog, YAHOO.rapidjs.riadmin.BasicCrudDialog, {
	handleSave: function(){
		var groupName = this.nameInput.value;
		var role = this.roleComb.options[this.roleComb.selectedIndex].value;
		var segmentExp = this.expressionText.value;
		var usersDelimited = new Array();
		for(var i = 0 ; i < this.groupUsers.options.length ; i++)
		{
			usersDelimited[i] = this.groupUsers.options[i].value;
		}
		var usersJoined = usersDelimited.join('::');
		var url = '/RapidInsight/ManagedObject/invoke?Operation=admin_scripts/saveGroupsData&IsUpdating=' + this.isUpdating + '&GroupName=' + groupName + '&Role=' + role + '&SegmentationExpression=' + encodeURIComponent(segmentExp) + '&Users=' + usersJoined;
		this.request(url, this.saveSuccess);
	},
	
	clear: function(){
		this.nameInput.readOnly = false;
		this.roleComb.selectedIndex = 0;
		this.nameInput.value = '';
		this.expressionText.value = '';
		this.availableUsers.innerHTML = '';
		this.groupUsers.innerHTML = '';
	}, 
	
	handleAddUser: function(){
		SelectUtils.moveAllSelectedsFromSelectToSelect(this.availableUsers, this.groupUsers);
	},
	
	handleRemoveUser: function(){
		SelectUtils.moveAllSelectedsFromSelectToSelect(this.groupUsers, this.availableUsers);
	},
	
	populateFieldsForUpdate: function(node){
		this.nameInput.readOnly = true;
		var groupName = node.getAttribute('Name');
		var roleName = node.getAttribute('Role');
		this.roleComb.selectedIndex = this.getIndexFromApplicationRole(roleName);
		var segExp = node.getAttribute('SegmentExpression');
		this.nameInput.value = groupName;
		this.expressionText.value = segExp;
		var url = '/RapidInsight/ManagedObject/invoke?Operation=admin_scripts/getGroupDetails&GroupName=' + encodeURIComponent(groupName);
		this.request(url, this.getGroupDetailsSuccess);
	},
	
	populateFieldsForAdd: function()
	{
		this.request('/RapidManager/User/getUsers', this.getUsersSuccess);
	},
	
	getIndexFromApplicationRole: function(appRole)
	{
		if(!appRole)
		{
			return 0;
		}
		else if(appRole == "rioperator")
		{
			return 0;
		}
		else if(appRole == "riviewer")
		{
			return 1;
		}
		else
		{
			return 0;
		}
	},

	getUsersSuccess: function(response)
	{
		this.dialog.hideLoading();
		if(this.checkResponse(response))
		{
			this.populateOtherUsers(response.responseXML.getElementsByTagName('User'));
		}
	},
	getGroupDetailsSuccess: function(response)
	{
		this.dialog.hideLoading();
		if(this.checkResponse(response))
		{
			var xml = response.responseXML;
			var groupsUsers = xml.getElementsByTagName("GroupsUsers")[0].getElementsByTagName("User");
			this.populateGroupsUsers(groupsUsers);
			var otherUsers = xml.getElementsByTagName("OtherUsers")[0].getElementsByTagName("User");
			this.populateOtherUsers(otherUsers);
		}
	},

	populateUsersForSelect: function(users, select)
	{
		for(var index = 0; index < users.length; index++) {
			var userName = users[index].getAttribute('Login');
			opt = document.createElement('option');
			opt.setAttribute('class','riadmin-option');
			opt.setAttribute('value',userName);
			opt.setAttribute('title',userName);
			opt.innerHTML = userName;
			select.appendChild(opt);
		}
	},
	
	populateOtherUsers: function(otherUsers){
		this.populateUsersForSelect(otherUsers, this.availableUsers);
	}, 
	
	populateGroupsUsers: function(groupsUsers){
		this.populateUsersForSelect(groupsUsers, this.groupUsers);
	}, 
	
	render : function(){
		var dh = YAHOO.ext.DomHelper;
		var detailView = dh.append(this.container, {tag:'div', cls:'riadmin-groups-detailwr', 
					html:'<table><tbody><tr><td><div class="riadmin-formtext">Name:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
						'<tr><td><div class="riadmin-formtext">Role:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
						'<tr><td><div class="riadmin-textarealbl">Filter:</div></td><td><div class="riadmin-grpdlg-filterwr"/></td></tr></tbody></table>'});
						
		var inputWrappers = detailView.getElementsByTagName('div');
		this.nameInput = dh.append(inputWrappers[1], {tag:'input', cls:'riadmin-input'});
		this.roleComb = dh.append(inputWrappers[3], {tag:'select', cls:'riadmin-input', 
					html:'<option value="rioperator">rioperator</option><option value="riviewer">riviewer</option>'});
		this.expressionText = dh.append(inputWrappers[5], {tag:'textarea', cls:'rapid-textarea'});
		this.expressionText.cols = 30;
		
		var userView = dh.append(this.container, {tag:'div', cls:'riadmin-groups-userwr', 
					html:'<table><tbody><tr><td><div>Available Users</div></td><td></td><td><div>Group\'s Users</div></td></tr>' +
						'<tr><td><div class="riadmin-selectwrp"/></td>' +
						'<td><div class="riadmin-dlg-buttonswrp"/></td>' +
						'<td><div class="riadmin-selectwrp"/></td></tr></tbody></table>'});
		var usersComps = userView.getElementsByTagName('div');
		this.availableUsers = dh.append(usersComps[2], {tag:'select', size:'15', cls:'riadmin-select'});
		this.availableUsers.multiple = true;
		this.groupUsers = dh.append(usersComps[4], {tag:'select', size:'15', cls:'riadmin-select'});
		this.groupUsers.multiple = true;
		var userButtonWrapper = dh.append(usersComps[3], {tag:'div', 
			html:'<table><tbody><tr><td><div class="riadmin-dlg-bwrap"/></td></tr>' +
				'<tr><td><div class="riadmin-dlg-bwrap"/></td></tr></tbody></table>'});
		
		var userButtons = userButtonWrapper.getElementsByTagName('div');
		this.addUserButton = new YAHOO.ext.Button(userButtons[0], {handler: this.handleAddUser, scope: this,text: '>',minWidth: 30});
		this.removeUserButton =  new YAHOO.ext.Button(userButtons[1], {handler: this.handleRemoveUser, scope: this,text: '<',minWidth: 30});
		this.dialog.addTabListener(this.cancelButton.el.dom, this.nameInput);
		this.dialog.addTabListener(this.groupUsers, this.saveButton.el.dom);
		this.dialog.addHelp({url:help_ri_admin_groups_add_dlg});
	}, 
	
	setDefaultInput : function(){
		this.dialog.defaultInput = this.nameInput;
	}
});