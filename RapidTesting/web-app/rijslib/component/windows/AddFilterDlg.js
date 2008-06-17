YAHOO.rapidjs.component.windows.AddFilterDlg = function(filterTree, errorDlg){
	this.filterTree = filterTree;
	this.errorDlg = errorDlg;
	var config = {
		modal: true,
	    width:440,
	    height:340,
	    shadow:true,
	    minWidth:100,
	    minHeight:100,
	    syncHeightBeforeShow: true,
	    resizable: false,
	    title: 'Add Filters', 
	    center:{
	        autoScroll:true
    }};
    var dh = YAHOO.ext.DomHelper;
    this.dialog = new YAHOO.ext.LayoutDialog(dh.append(document.body, {tag:'div'}), config);
	this.cancelButton = this.dialog.addButton('Cancel', this.hide, this);
	this.saveButton = this.dialog.addButton('Save', this.handleSave, this);
	this.okButton = this.dialog.addButton('OK', this.hide, this);
	var layout = this.dialog.layout;
	layout.beginUpdate();
	this.container = dh.append(document.body, {tag:'div'});
	layout.add('center', new YAHOO.rapidjs.component.layout.RapidPanel(this.container));
	layout.endUpdate();
	
	this.dialog.addTabListener(this.saveButton.el.dom, this.cancelButton.el.dom);
	this.dialog.addKeyListener(13, function(){if(this.cancelButton.isFocused == false && this.saveButton.isFocused == false){this.handleSave();}}, this);
	this.dialog.addKeyListener(27, function(){this.hide();}, this);
	this.dialog.addHelp({url:help_ri_add_filter_dlg});
	this.dialog.on('hide', this._clear, this, true);
	this.render();
	this.mode = 0;
};

YAHOO.rapidjs.component.windows.AddFilterDlg.prototype = {
	hide: function(){
		this.dialog.hide();
	}, 
	controlVisiblity: function()
	{
		if(this.mode == 2)
		{
			this.nameInput.readOnly = true;
			this.descInput.readOnly = true;
			this.expInput.readOnly = true;
			this.viewInput.disabled = true;
			this.groupInput.disabled = true;
			this.saveButton.setVisible(false);
			this.cancelButton.setVisible(false);
			this.okButton.setVisible(true);	
		}
		else
		{
			if(this.mode == 1)
			{
				this.nameInput.readOnly = true;
			}
			else
			{
				this.nameInput.readOnly = false;
			}
			this.descInput.readOnly = false;
			this.expInput.readOnly = false;
			this.groupInput.disabled = false;
			this.viewInput.disabled = false;
			this.saveButton.setVisible(true);
			this.cancelButton.setVisible(true);
			this.okButton.setVisible(false);
		}
	},
	show: function(animTarget, mode, currentNode){
		this.mode = mode;
		this.currentNode = currentNode;
		this.loadGroups();
		this.controlVisiblity();
		if(this.mode == 1){
			this.populateFields();
			this.dialog.setTitle('Update ' + this.currentNode.getAttribute('Name'))
		}
		else if(this.mode == 0){
			this.dialog.setTitle('Add Filter');
		}
		else if(this.mode == 2){
			this.populateFields();
			this.dialog.setTitle('View Filter');
		}
		else if(this.mode == 3){
			this.populateFields();
			this.dialog.setTitle('Add Filter');
		}
		this.dialog.show(animTarget);
		this.request('/RapidInsight/View/list', this.getViewsSuccess);
	}, 
	
	showForAddFilterToGroup : function(animTarget, groupName){
		this.mode = 0;
		this.controlVisiblity();
		this.currentNode = null;
		SelectUtils.addOption(this.groupInput, groupName, groupName);
		SelectUtils.selectTheValue(this.groupInput, groupName, 1);
		this.groupInput.disabled = true;
		this.dialog.setTitle('Add Filter');
		this.dialog.show(animTarget);
		this.request('/RapidInsight/View/list', this.getViewsSuccess);
	}, 
	
	loadGroups: function(){
		if(this.filterTree.rootNode){
			var groupNodes = this.filterTree.rootNode.childNodes();
			var nOfGroupNodes = groupNodes.length;
			for(var index=0; index<nOfGroupNodes; index++) {
				var groupNode = groupNodes[index];
				var groupName = groupNode.getAttribute('Name');
				if(groupName != 'DefaultFilters')
				{
					SelectUtils.addOption(this.groupInput, groupName, groupName);
				}
			}
		}
	}, 
	
	
	populateFields : function(){
		var node = this.currentNode;
		this.nameInput.value = node.getAttribute('Name');
		var groupName = node.getAttribute('Group');
		SelectUtils.selectTheValue(this.groupInput, groupName, 0);
		this.descInput.value = node.getAttribute('Description');
		this.expInput.value = node.getAttribute('Expression');
	},
	
	getViewsSuccess : function(response){
		var views = response.responseXML.getElementsByTagName('View');
		SelectUtils.clear(this.viewInput);
		SelectUtils.addOption(this.viewInput, 'default', '');
		var nOfViews = views.length;
		for(var index=0; index<nOfViews; index++) {
			var viewName = views[index].getAttribute('Name');
			SelectUtils.addOption(this.viewInput, viewName, viewName);
		}
		if(this.mode == 1){
			var filterView = this.currentNode.getAttribute('View');
			SelectUtils.selectTheValue(this.viewInput, filterView, 0);
		}
	}, 
	
	_clear: function(){
		this.clear.defer(50, this);
	}, 
	
	clear : function(){
		this.nameInput.value = '';
		SelectUtils.clear(this.groupInput);
		SelectUtils.addOption(this.groupInput, '', '');
		SelectUtils.clear(this.viewInput);
		this.descInput.value = '';
		this.expInput.value = '';
		this.mode = 0;
		this.controlVisiblity();
	},
	
	render : function(){
		var dh = YAHOO.ext.DomHelper;
		var wrp = dh.append(this.container, {tag:'div', cls:'r-ftree-gdlg-wrp'});
		var nameView = dh.append(wrp, {tag:'div', 
			html:'<table><tbody><tr><td><div class="r-ftree-dlg-text">Filter Name:</div></td><td><div class="r-ftree-dlg-inputwrp"></div></td></tr>' +
					'<tr><td><div class="r-ftree-dlg-text">Group Name:</div></td><td><div class="r-ftree-dlg-inputwrp"></div></td></tr>' +
					'<tr><td><div class="r-ftree-dlg-text">View:</div></td><td><div class="r-ftree-dlg-inputwrp"></div></td></tr>' +
					'<tr><td><div class="r-ftree-dlg-text">Description:</div></td><td><div class="r-ftree-dlg-textareawrp"></div></td></tr>' +
					'<tr><td><div class="r-ftree-dlg-text">Expression:</div></td><td><div class="r-ftree-dlg-textareawrp"></div></td></tr></tbody></table>'});
		var inputWrappers = nameView.getElementsByTagName('div');
		this.nameInput = dh.append(inputWrappers[1], {tag:'input', cls:'r-ftree-dlg-input'});
		this.dialog.defaultInput = this.nameInput;
		this.groupInput = dh.append(inputWrappers[3], {tag:'select', cls:'r-ftree-dlg-input'});
		SelectUtils.addOption(this.groupInput, '', '');
		this.viewInput = dh.append(inputWrappers[5], {tag:'select', cls:'r-ftree-dlg-input'});
		this.descInput = dh.append(inputWrappers[7], {tag:'textarea', cls:'rapid-textarea'});
		this.expInput = dh.append(inputWrappers[9], {tag:'textarea', cls:'rapid-textarea'});
	}, 
	
	request : function(url, successDelegate){
		var callback = {
			success : successDelegate, 
			failure : this.processFailure,
			scope: this
		};
		YAHOO.util.Connect.asyncRequest('GET', url, callback);
	}, 
	
	handleSave : function(){
		var url;
		if(this.mode == 0 || this.mode == 3){
			url = '/RapidInsight/UserFilter/add?'
		}
		else{
			url= '/RapidInsight/UserFilter/update?'
		}
		var filterName = encodeURIComponent(this.nameInput.value);
		var groupName = encodeURIComponent(this.groupInput.options[this.groupInput.selectedIndex].value);
		var view = encodeURIComponent(this.viewInput.options[this.viewInput.selectedIndex].value);
		var desc = encodeURIComponent(this.descInput.value);
		var exp = encodeURIComponent(this.expInput.value);
		url = url + 'Name=' + filterName + '&Expression=' + exp + '&Description=' + desc + '&Group=' + groupName + '&View=' + view;
		this.request(url, this.saveSuccess);
		
	}, 
	
	saveSuccess : function(response){
		if(this.checkResponse(response)){
			if(this.mode == 1){
				var updateSuccessDelegate = this.filterTree.filterUpdated.createDelegate(this.filterTree, [this.currentNode]);
				this.filterTree.poll(updateSuccessDelegate);
			}
			else{
				this.filterTree.poll();
			}
			this.hide();	
		}
	}, 
	removeSuccess : function(response){
		if(this.checkResponse(response)){
			this.filterTree.poll();
		}
	}, 
	
	removeFilter: function(filterName){
		this.request('/RapidInsight/UserFilter/remove?Name=' + encodeURIComponent(filterName), this.removeSuccess);
	}, 
	
	setFilterTree : function(filterTree){
		this.filterTree = filterTree;
	}, 
	
	processFailure: function(response){
		var st = response.status;
		if(st == -1){
			this.errorDlg.setErrorText('Request received a timeout');
		}
		else if(st == 404){
			this.errorDlg.setErrorText('Specified url cannot be found');
		}
		else if(st == 0){
			this.errorDlg.setErrorText('Server is not available');
			YAHOO.rapidjs.ServerStatus.refreshState(false);
		}
		this.errorDlg.show();
	}, 
	
	checkResponse: function(response){
		if(YAHOO.rapidjs.Connect.containsError(response) == true)
		{
			this.errorDlg.setErrorText(YAHOO.rapidjs.Connect.getErrorMessages(response.responseXML));
			this.errorDlg.show();
			return null;
		}
		else if(YAHOO.rapidjs.Connect.isAuthenticated(response) == false)
		{
			window.location = "login.html?page=" + window.location.pathname;
			return null;
		}
		return true;
	}
};