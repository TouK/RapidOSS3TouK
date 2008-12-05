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
YAHOO.rapidjs.admin.BasicAdmin = function(gridConfig, crudConfig, errorDialog){
	this.identifierAttributeName = gridConfig.nodeId;
	var crudTitle = crudConfig['crudTitle'];
	var addButtonText = crudConfig['addButtonText'];
	this.addButtonText = addButtonText;
	this.addIconCssClass = crudConfig['addIconCssClass'];
	var dh = YAHOO.ext.DomHelper;
	var gLayoutContainer = dh.append(document.body, {tag:'div'});
	this.gLayout = new YAHOO.ext.BorderLayout(gLayoutContainer, {
	    center: {titlebar:true}, 
	    west:{initialSize: 200,titlebar: true,collapsible: true,animate: true,
                autoScroll:false,useShim:true,cmargins: {top:0,bottom:2,right:2,left:2}}
	});
	
	this.gLayout.beginUpdate();
	var gridContainer = dh.append(document.body, {tag:'div'});
	this.gridWindow = new YAHOO.rapidjs.component.windows.GridWindow(gridContainer, gridConfig);
	YAHOO.rapidjs.ToolsUtil.createDefaultTools(this.gLayout, 'center', this.gridWindow);
	this.gLayout.add('center', this.gridWindow.panel);
	this.gridWindow.panel.setTitle(crudTitle); 
	this.createButtons(this.gLayout);
    this.gLayout.endUpdate();
    
	this.panel = new YAHOO.rapidjs.component.layout.NestedLayoutPanel(this.gLayout, {title:crudTitle});
	this.basicAdminDialog = this.createDialog(this.gridWindow, errorDialog);
	this.createContextMenu();
	this.gridWindow.events['contextmenuclicked'].subscribe(this.menu.contextMenuClicked, this.menu, true);
};

YAHOO.rapidjs.admin.BasicAdmin.prototype = {
	handleAdd: function(){
		this.basicAdminDialog.show(BasicAdminDialog.ADD);
	},
	
	removeItem: function(menuItem, data, node){
		this.menu.hide();
		this.handleRemove(node);
	},
	
	handleRemove: function(node)
	{
		var nameOfTheItem = node.getAttribute(this.identifierAttributeName);
		if(confirm("Remove " + nameOfTheItem + "?"))
		{
			this.basicAdminDialog.request(this.getRemoveUrl(node), this.removeSuccess, this);
		}
	},
	
	removeSuccess: function(response)
	{
		this.basicAdminDialog.dialog.hideLoading();
		YAHOO.rapidjs.ServerStatus.refreshState(true);
		if(this.checkResponse(response))
		{
			this.gridWindow.poll();	
		}
	},
	
	handleUpdate: function(node){
		this.basicAdminDialog.populateFieldsForUpdate(node);
		this.basicAdminDialog.show(BasicAdminDialog.UPDATE);
	},
	handleClone: function(node){
		this.basicAdminDialog.populateFieldsForClone(node);
		this.basicAdminDialog.show(BasicAdminDialog.CLONE);
	},
	
	updateItem: function(menuItem, data, node){
		this.handleUpdate(node);
	},
	cloneItem: function(menuItem, data, node){
		this.handleClone(node);
	},
	
	selectionChange: function(selectionModel, selectedRows, selectedRowIds){
		if(selectedRows.length > 0){
			this.removeButton.enable();
			this.updateButton.enable();
			this.selectedRow = selectedRows[0];
		}
		else{
			this.removeButton.disable();
			this.updateButton.disable();
		}
	},
	
	createContextMenu: function(){
		this.menu = new YAHOO.rapidjs.component.menu.ContextMenu();
		var MenuAction = YAHOO.rapidjs.component.menu.MenuAction;
		this.menu.addMenuItem('Update', new MenuAction(this.updateItem, this), true);
		this.menu.addMenuItem('Remove', new MenuAction(this.removeItem, this), true);
		this.menu.addMenuItem('Clone', new MenuAction(this.cloneItem, this), true);
	},
	
	//override this method to create custom buttons
	createButtons: function(gLayout)
	{
		var dh = YAHOO.ext.DomHelper;
		var buttonContainer = dh.append(document.body, {tag:'div'});
		gLayout.add('west', new YAHOO.rapidjs.component.layout.RapidPanel(buttonContainer, {fitToFrame:true, title:'Actions'}));
		gLayout.regions['west'].collapsedEl.addClass('r-admin-actioncollapsed');
		
		new YAHOO.rapidjs.component.ToolbarButton(dh.append(buttonContainer, {tag:'div', cls:'rapid-btnwrp'}), 
	    	{text:this.addButtonText, className:this.addIconCssClass, scope:this, click:this.handleAdd});
	},
	
	checkResponse: function(response)
	{
		return this.basicAdminDialog.checkResponse(response);
	},
	
	
	////////////////////////// ABSTRACT METHODS ////////////////////////////////////////
	createDialog: function(gridWindow, errorDialog){
		alert("Extenders of BasicAdmin should override createDialog function!!");
		// override this method to create a custom dialog for the crud. 
		// This function should return a BasicAdminDialog instance.
	},
	
	getRemoveUrl: function(node){
		alert("Extenders of BasicAdmin should override getRemoveUrl function!!");
		// override this method to cosntruct the url to remove an item.
	}
};

YAHOO.rapidjs.admin.BasicAdminDialog = function(gridWindow, dialogTitle, dialogWidth, dialogHeight, errorDialog){
	this.gridWindow = gridWindow;
	this.errorDialog = errorDialog;
	this.dialogTitleSuffix = dialogTitle;
	var config = {
		modal: true,
	    width:dialogWidth,
	    height:dialogHeight,
	    shadow:true,
	    minWidth:100,
	    minHeight:100,
	    syncHeightBeforeShow: true,
	    resizable: false,
	    title: dialogTitle, 
	    center:{
	        autoScroll:true
    }};
    var dh = YAHOO.ext.DomHelper;
    this.dialog = new YAHOO.ext.LayoutDialog(dh.append(document.body, {tag:'div'}), config);
	this.cancelButton = this.dialog.addButton('Cancel', this.hide, this);
	this.saveButton = this.dialog.addButton('OK', this.handleSave, this);
	var layout = this.dialog.layout;
	layout.beginUpdate();
	this.container = dh.append(document.body, {tag:'div'});
	layout.add('center', new YAHOO.rapidjs.component.layout.RapidPanel(this.container));
	layout.endUpdate();
	
	this.dialog.addTabListener(this.saveButton.el.dom, this.cancelButton.el.dom);
	this.dialog.addKeyListener(13, function(){if(this.cancelButton.isFocused == false && this.saveButton.isFocused == false){this.handleSave();}}, this);
	this.dialog.addKeyListener(27, function(){this.hide();}, this);
	this.dialog.on('hide', this._clear, this, true);
	
	this.mode = null;
	this.render();
	this.setDefaultInput();
	this.dialog.addLoading();
};

YAHOO.rapidjs.admin.BasicAdminDialog.prototype = {
	
	request: function(url, successDelegate, scopeOwner){
		var callback={
			success: successDelegate, 
			failure: this.processFailure, 
			scope: !scopeOwner ? this : scopeOwner
		};
		
		YAHOO.util.Connect.asyncRequest('GET', url, callback);
		this.dialog.showLoading();
	},
	doPostRequest: function(url, postData, successDelegate, scopeOwner){
		var callback={
			success: successDelegate, 
			failure: this.processFailure, 
			scope: !scopeOwner ? this : scopeOwner
		};
		
		YAHOO.util.Connect.asyncRequest('POST', url, callback, postData);
		this.dialog.showLoading();
	},
	
	processFailure: function(response)
	{
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
		this.saveButton.enable();
		this.cancelButton.enable();
		this.dialog.hideLoading();
	},
	
	saveSuccess: function(response)
	{
		YAHOO.rapidjs.ServerStatus.refreshState(true);
		if(this.checkResponse(response))
		{
			if(this.dialog.isVisible()){
				this.hide();				
			}
			this.gridWindow.poll();
		}
		this.saveButton.enable();
		this.cancelButton.enable();
		this.dialog.hideLoading();
	},
	
	checkResponse: function(response)
	{
		if(YAHOO.rapidjs.Connect.containsError(response) == true)
		{
			this.errorDialog.setErrorText(YAHOO.rapidjs.Connect.getErrorMessages(response.responseXML));
			this.errorDialog.show();
			return null;
		}
		else if(YAHOO.rapidjs.Connect.isAuthenticated(response) == false)
		{
			window.location = "login.html?page=" + window.location.pathname;
			return null;
		}
		return true;
	},
	
	hide: function()
	{
		this.dialog.hideLoading();
		this.dialog.hide();
	},
	
	show: function(mode){
		this.mode = mode;
		var title = '';
		if(this.mode == BasicAdminDialog.ADD)
		{
			title = 'New ' + this.dialogTitleSuffix;
			this.clear();
			this.populateFieldsForAdd();
		}
		else if(this.mode == BasicAdminDialog.UPDATE)
		{
			title = 'Update ' + this.dialogTitleSuffix;
		}
		else{
			title = 'New ' + this.dialogTitleSuffix;
		}
		this.dialog.setTitle(title);
		this.dialog.show();
	},
		
	//////////////////////////////////////////// ABSTRACT METHODS //////////////////////////////////////////////
	//override this method to create form
	render : function(){
		alert('render should be overriden');
	}, 
	
	//override this method for the required input field to be focused when the dialog is shown;
	setDefaultInput: function(){
		alert('setDefaultInput should be overriden');
	}, 
	
	
	//override this method to populate the data of the fields of the form from the selected node of the grid.
	populateFieldsForUpdate: function(node)
	{
		alert("populateFields method in BasicAdminDialog must be overriden.");
	},
	//override this method to populate the data of the fields of the form from the selected node of the grid.
	populateFieldsForClone: function(node)
	{
		alert("populateFieldsForClone method in BasicAdminDialog must be overriden.");
	},
	
	//override this method to populate some form fields before add
	populateFieldsForAdd: function()
	{
		
	},
	
	_clear: function(){
		this.clear.defer(50, this);
	}, 
	//override this method to customize how the fields in the form is cleared.
	clear: function()
	{
		alert("clear method in BasicAdminDialog must be overriden.");
	},
	
	//override this method to do custom save request that is constructed by using the data from the form fields.
	handleSave: function(){
		alert("handleSave method in BasicAdminDialog must be overriden.");
	}, 
	autoHeight: function(){
		var containerEl = getEl(this.container);
		var height = containerEl.getHeight()+ this.dialog.getHeaderFooterHeight();
        var bm = this.dialog.body.getMargins();
        var borders = this.dialog.layout.getEl().getBorderWidth('tb') + this.dialog.layout.regions['center'].getEl().getBorderWidth('tb');
        height = height + bm.top + bm.bottom + borders;
        this.dialog.resizeTo(this.dialog.size.width, height);
	}, 
	setTitle: function(text){
		this.dialog.setTitle(text);
	}
}
YAHOO.rapidjs.admin.BasicAdminDialog.ADD = 0;
YAHOO.rapidjs.admin.BasicAdminDialog.UPDATE = 1;
YAHOO.rapidjs.admin.BasicAdminDialog.CLONE = 2;
var BasicAdminDialog = YAHOO.rapidjs.admin.BasicAdminDialog;
