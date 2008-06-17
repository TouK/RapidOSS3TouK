YAHOO.rapidjs.component.windows.AddFilterGroupDlg = function(filterTree, errorDlg){
	this.filterTree = filterTree;
	this.errorDlg = errorDlg;
	var config = {
		modal: true,
	    width:320,
	    height:121,
	    shadow:true,
	    minWidth:100,
	    minHeight:100,
	    syncHeightBeforeShow: true,
	    resizable: false,
	    title: 'Add Filter Group', 
	    center:{
	        autoScroll:true
    }};
    var dh = YAHOO.ext.DomHelper;
    this.dialog = new YAHOO.ext.LayoutDialog(dh.append(document.body, {tag:'div'}), config);
	this.cancelButton = this.dialog.addButton('Cancel', this.hide, this);
	this.saveButton = this.dialog.addButton('Save', this.handleSave, this);
	var layout = this.dialog.layout;
	layout.beginUpdate();
	this.container = dh.append(document.body, {tag:'div'});
	layout.add('center', new YAHOO.rapidjs.component.layout.RapidPanel(this.container));
	layout.endUpdate();
	
	this.dialog.addTabListener(this.saveButton.el.dom, this.cancelButton.el.dom);
	this.dialog.addKeyListener(13, function(){if(this.cancelButton.isFocused == false && this.saveButton.isFocused == false){this.handleSave();}}, this);
	this.dialog.addKeyListener(27, function(){this.hide();}, this);
	this.dialog.on('hide', this._clear, this, true);
	this.render();
};

YAHOO.rapidjs.component.windows.AddFilterGroupDlg.prototype = {
	hide: function(){
		this.dialog.hide();
	}, 
	
	show: function(animTarget){
		this.dialog.show(animTarget);
	}, 
	
	_clear: function(){
		this.clear.defer(50, this);
	}, 
	
	clear : function(){
		this.nameInput.value = '';
	},
	
	render : function(){
		var dh = YAHOO.ext.DomHelper;
		var wrp = dh.append(this.container, {tag:'div', cls:'r-ftree-gdlg-wrp'});
		var nameView = dh.append(wrp, {tag:'div', 
			html:'<table><tbody><tr><td><div class="r-ftree-dlg-text">Group Name:</div></td>' +
					'<td><div class="r-ftree-dlg-inputwrp"><input class="r-ftree-dlg-input"></input></div></td></tr></tbody></table>'});
		this.nameInput = YAHOO.util.Dom.getElementsByClassName('r-ftree-dlg-input', 'input', nameView)[0];
		this.dialog.defaultInput = this.nameInput;
	}, 
	
	handleSave : function(){
		var groupName = encodeURIComponent(this.nameInput.value);
		var url = '/RapidInsight/FilterGroup/add?Name=' + groupName;
		this.request(url, this.saveSuccess);
	}, 
	
	request : function(url, successDelegate){
		var callback = {
			success : successDelegate, 
			failure : this.processFailure,
			scope: this
		};
		YAHOO.util.Connect.asyncRequest('GET', url, callback);
	}, 
	
	saveSuccess : function(response){
		if(this.checkResponse(response)){
			this.filterTree.poll();
			this.hide();	
		}
	}, 
	removeSuccess : function(response){
		if(this.checkResponse(response)){
			this.filterTree.poll();
		}
	}, 
	
	removeGroup : function(groupName){
		var url = '/RapidInsight/FilterGroup/remove?Name=' + encodeURIComponent(groupName);
		this.request(url, this.removeSuccess);
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