YAHOO.rapidjs.component.layout.UserTool = function(layout, regionName)
{
	this.layout = layout;
	this.region = layout.getRegion(regionName);
	this.userDialog = new YAHOO.rapidjs.component.layout.UserDialog();
	this.userName = null;
	this.requestUser();
};

YAHOO.rapidjs.component.layout.UserTool.prototype = 
{
	render: function()
	{
		var tr = this.region.getTabs().toolsArea;
		var wrapper = YAHOO.ext.DomHelper.insertBefore(tr.firstChild.nextSibling.firstChild, 
			{tag:'div', cls:'rapid-usertool-wrp', html:this.userName, title:'Set Password'}, true);
		YAHOO.util.Event.addListener(wrapper.dom, 'click', this.toolClicked, this, true);
		wrapper.addClassOnOver('rapid-usertool-visited');
	}, 
	
	requestUser: function(){
		
	}, 
	
	processSuccess: function(response){
		YAHOO.rapidjs.ServerStatus.refreshState(true);
		var userName = response.responseXML.firstChild.getAttribute('Name');
		if(userName){
			this.userName = userName;
			this.render();
		}
	}, 
	
	processFailure: function(response){
		if(response.status == 0){
			YAHOO.rapidjs.ServerStatus.refreshState(false);
		}
	}, 
	
	toolClicked: function(){
		this.userDialog.show(this.userName);
	}
}

YAHOO.rapidjs.component.layout.UserDialog = function(){
	var dh = YAHOO.ext.DomHelper;
	var config = {modal: false,
	    width:422,height:196,shadow:true,
	    syncHeightBeforeShow: true,resizable: false, title: 'Set Password',
	    center:{}};
    this.dialog = new YAHOO.ext.LayoutDialog(dh.append(document.body, {tag:'div'}), config);
	this.cancelButton = this.dialog.addButton('Cancel', this.hide, this);
	this.saveButton = this.dialog.addButton('Save', this.handleSave, this);
	var layout = this.dialog.layout;
	layout.beginUpdate();
	this.wrap = dh.append(document.body, {tag:'div'}, true);
	layout.add('center', new YAHOO.rapidjs.component.layout.RapidPanel(this.wrap));
	layout.endUpdate();
	
	var detailView = dh.append(this.wrap.dom, {tag:'div', cls:'rapid-userdlg-detailwrp', 
				html:'<table><tbody><tr><td><div class="rapid-formtext">User Name:</div></td><td><div class="rapid-inputwrp"/></td></tr>' +
					'<tr><td><div class="rapid-formtext">Old Password:</div></td><td><div class="rapid-inputwrp"/></td></tr>' +
					'<tr><td><div class="rapid-formtext">New Password:</div></td><td><div class="rapid-inputwrp"/></td></tr>' +
					'<tr><td><div class="rapid-formtext">Confirm Password:</div></td><td><div class="rapid-inputwrp"/></td></tr><tbody><table>'});
					
	var inputWrappers = detailView.getElementsByTagName('div');
	this.usernameInput = dh.append(inputWrappers[1], {tag:'input', cls:'rapid-input', readonly: 'true'});
	this.oldPassInput = dh.append(inputWrappers[3], {tag:'input', cls:'rapid-input', type:'password'});
	this.passInput = dh.append(inputWrappers[5], {tag:'input', cls:'rapid-input', type:'password'});
	this.confirmInput = dh.append(inputWrappers[7], {tag:'input', cls:'rapid-input', type:'password'});
	this.dialog.addTabListener(this.cancelButton.el.dom, this.usernameInput);
	this.dialog.addTabListener(this.saveButton.el.dom, this.cancelButton.el.dom);
	this.dialog.addTabListener(this.confirmInput, this.saveButton.el.dom);
	this.dialog.addKeyListener(13, function(){if(this.cancelButton.isFocused == false && this.saveButton.isFocused == false){this.handleSave();}}, this);
	this.dialog.addKeyListener(27, function(){this.hide();}, this);
	
	this.errorDialog = new YAHOO.rapidjs.component.dialogs.ErrorDialog();
};

YAHOO.rapidjs.component.layout.UserDialog.prototype = {
	hide: function(){
		this.clear();
		this.dialog.hide();
	}, 
	
	clear: function(){
		this.usernameInput.value = '';
		this.passInput.value = '';
		this.confirmInput.value = '';
		this.oldPassInput.value = '';	
	}, 
	
	handleSave: function(){
		var userName = this.usernameInput.value;
		var newPass1 = this.passInput.value;
		var newPass2 = this.confirmInput.value;
		var oldPass = this.oldPassInput.value;
		var callback = {
			success: this.saveSuccess, 
			failure: this.saveFailure,
			scope: this, 
			timeout: 30000
		};
		var url = '/RapidManager/User/update';
		
		var postData = 'UserName=' + encodeURIComponent(userName) + '&OldPassword=' + encodeURIComponent(oldPass) + 
			'&NewPassword1=' + encodeURIComponent(newPass1) + '&NewPassword2=' + encodeURIComponent(newPass2);
		YAHOO.util.Connect.asyncRequest('POST', url, callback, postData);
	}, 
	
	show: function(userName){
		this.usernameInput.value = userName;
		this.dialog.show();
	}, 
	
	setTitle: function(text){
		this.dialog.setTitle(text);
	}, 
	
	saveSuccess: function(response){
		YAHOO.rapidjs.ServerStatus.refreshState(true);
		if(this.checkResponse(response)){
			this.hide();
		}
	}, 
	checkResponse: function(response)
	{
		if(response.responseText.indexOf("<Errors>") > -1)
		{
			this.errorDialog.setErrorText(response.responseText);
			this.errorDialog.show();
			return null;
		}
		else if(response.responseText.indexOf("<Authenticate>") > -1)
		{
			window.location = "login.html?page=" + window.location.pathname;
			return null;
		}
		return true;
	},
	
	saveFailure: function(response){
		var st = response.status;
		if(st == -1){
			this.errorDialog.setErrorText('Request received a timeout');
		}
		else if(st == 404){
			this.errorDialog.setErrorText('Specified url cannot be found');
		}
		else if(st == 0){
			this.errorDialog.setErrorText('Server is not available');
			YAHOO.rapidjs.ServerStatus.refreshState(false);
		}
		this.errorDialog.show();
	}
};