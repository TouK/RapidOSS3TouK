YAHOO.rapidjs.component.windows.ViewBuilder = function(){
	this.errDlg = new YAHOO.rapidjs.component.dialogs.ErrorDialog();
	var config = {
		modal: true,
	    width:617,
	    height:500,
	    shadow:true,
	    minWidth:100,
	    minHeight:100,
	    syncHeightBeforeShow: true,
	    resizable: false,
	    title: 'View Builder', 
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
	this.isUpdating = null;
	this.render();
	this.columnsConfig = {};
};

YAHOO.rapidjs.component.windows.ViewBuilder.prototype = {
	
	handleSave: function(){
		var parameters = [];
		var url;
		if(this.isUpdating == true){
			url = '/RapidInsight/View/update';
		}else{
			url = '/RapidInsight/View/add'
		}
		parameters.push('Name=' + encodeURIComponent(this.nameInput.value));
		var defaultSortColumn = this.defaultSortInput.options[this.defaultSortInput.selectedIndex].value;
		parameters.push('DefaultSortColumn=' + encodeURIComponent(defaultSortColumn));
		var colList = this.colList;
		var nOfColumns = colList.options.length;
		for(var index=0; index<nOfColumns; index++) {
			var fieldName = colList.options[index].value;
			var colConfig = this.columnsConfig[fieldName];
			var colName = 'Column' + (index + 1);
			parameters.push(colName + 'AttributeName=' + encodeURIComponent(fieldName));
			parameters.push(colName + 'Header=' + encodeURIComponent(colConfig['header']));
			parameters.push(colName + 'Width=' + encodeURIComponent(colConfig['width']));
		}
		this.postRequest(url, parameters.join('&'), this.saveSuccess);
	}, 
	saveSuccess: function(response){
		if(this.checkResponse(response)){
			var currentView = this.nameInput.value;
			this.dialog.hide();
			if(this.isUpdating == false){
				MultiGridData.getViews(currentView, 1);
			}
			else{
				MultiGridData.getViews(currentView, 2);
			}
		}
	},
	
	removeView : function(view){
		this.removedView = view;
		var url = '/RapidInsight/View/remove?Name=' + encodeURIComponent(view);
		this.request(url, this.removeSuccess);
	}, 
	removeSuccess : function(response){
		if(this.checkResponse(response)){
			MultiGridData.getViews(this.removedView, 3);
		}
	}, 
	hide: function(){
		this.dialog.hide();
	}, 
	
	show: function(animTarget, isUpdating, currentNode){
		this.isUpdating = isUpdating;
		this.currentNode = currentNode;
		this.dialog.show(animTarget);
		if(!this.availableFields){
			this.request('/RapidInsight/ManagedObject/invoke?Script=getViewFields', this.getFieldsSuccess);
		}
		else{
			if(this.isUpdating == true){
				this.populateFieldsForUpdate();
			}
			this.loadAvailableFields(this.isUpdating);
		}
	}, 
	
	getFieldsSuccess : function(response){
		if(this.checkResponse(response)){
			this.availableFields = response.responseXML.getElementsByTagName('Field');
			if(this.isUpdating == true){
				this.populateFieldsForUpdate();
			}
			this.loadAvailableFields(this.isUpdating);
		}
	},
	
	populateFieldsForUpdate: function(){
		var viewName = this.currentNode.getAttribute('Name');
		this.nameInput.value = viewName;
		this.nameInput.readOnly = true;
		var defaultSortColumn = this.currentNode.getAttribute('DefaultSortColumn');
		var colNodes = this.currentNode.getElementsByTagName('Column');
		var nOfColumns = colNodes.length;
		var orderedArray = new Array(nOfColumns);
		for(var index=0; index<nOfColumns; index++) {
			var colNode = colNodes[index];
			var colOrder = parseInt(colNode.getAttribute('Order'), 10);
			var att = colNode.getAttribute('AttributeName');
			var header = colNode.getAttribute('Header');
			var width = colNode.getAttribute('Width');
			this.columnsConfig[att] = {header:header, width:width};
			orderedArray[colOrder-1] = att;
		}
		for(var index=0; index<nOfColumns; index++) {
			var attName = orderedArray[index];
			SelectUtils.addOption(this.colList, attName, attName);
			SelectUtils.addOption(this.defaultSortInput, attName, attName);
		}
		SelectUtils.selectTheValue(this.defaultSortInput, defaultSortColumn, 0);
	}, 
	loadAvailableFields : function(isUpdating){
		var nOfAvailableFields = this.availableFields.length;
		for(var index=0; index<nOfAvailableFields; index++) {
			var field = this.availableFields[index];
			var fieldName = field.getAttribute('Name');
			if(isUpdating == false || !this.columnsConfig[fieldName]){
				SelectUtils.addOption(this.allFields, fieldName, fieldName);
			}
		}
		this.handleButtons();
	}, 
	request : function(url, successDelegate){
		var callback={
			success: successDelegate, 
			failure: this.processFailure, 
			scope:  this
		};
		return YAHOO.util.Connect.asyncRequest('GET', url, callback);
	},
	postRequest : function(url, postData, successDelegate){
		var callback={
			success: successDelegate, 
			failure: this.processFailure, 
			scope:  this
		};
		return YAHOO.util.Connect.asyncRequest('POST', url, callback, postData);
	},
	processFailure: function(response){
		var st = response.status;
		if(st == -1){
			this.errDlg.setErrorText('Request received a timeout');
		}
		else if(st == 404){
			this.errDlg.setErrorText('Specified url cannot be found');
		}
		else if(st == 0){
			this.errDlg.setErrorText('Server is not available');
			YAHOO.rapidjs.ServerStatus.refreshState(false);
		}
		this.errDlg.show();
	},
	checkResponse : function(response)
	{
		if(YAHOO.rapidjs.Connect.containsError(response) == true)
		{
			this.errDlg.setErrorText(YAHOO.rapidjs.Connect.getErrorMessages(response.responseXML));
			this.errDlg.show();
			return null;
		}
		else if(YAHOO.rapidjs.Connect.isAuthenticated(response) == false)
		{
			window.location = "login.html?page=" + window.location.pathname;
			return null;
		}
		return true;
	},
	
	_clear: function(){
		this.clear.defer(50, this);
	},
	clear: function(){
		SelectUtils.clear(this.allFields);
		SelectUtils.clear(this.colList);
		SelectUtils.clear(this.defaultSortInput);
		SelectUtils.addOption(this.defaultSortInput, '', '');
		this.nameInput.value = '';
		this.nameInput.readOnly = false;
		this.attInput.readOnly = false;
		this.headerInput.readOnly = false;
		this.colWidthInput.readOnly = false;
		this.attInput.value = '';
		this.headerInput.value = '';
		this.colWidthInput.value = '100';
		this.disableAll();
		this.columnsConfig = {};
	}, 
	
	render: function(){
		var dh = YAHOO.ext.DomHelper;
		var wrp = dh.append(this.container, {tag:'div', cls:'r-mgrid-view-wrp'});
		var nameView = dh.append(wrp, {tag:'div', 
			html:'<table><tbody><tr><td><div class="r-mgrid-view-text">View Name:</div></td>' +
					'<td><div class="r-mgrid-view-inputwrp"><input class="r-mgrid-view-input"></input></div></td></tr></tbody></table>'});
		this.nameInput = YAHOO.util.Dom.getElementsByClassName('r-mgrid-view-input', 'input', nameView)[0];
		this.dialog.defaultInput = this.nameInput;
		
		var columnView = dh.append(wrp, {tag:'div', 
			html:'<table><tbody>' +
					'<tr><td><div class="r-mgrid-view-text">Available Fields:</div></td><td></td><td><div class="r-mgrid-view-text">Grid Columns:</div></td><td></td></tr>' +
					'<tr>' +
						'<td><div class="r-mgrid-view-fields"><select class="r-mgrid-view-fieldlist" size="23"></select></div></td>' +
						'<td valign="top"><div class="r-mgrid-view-buttons"><div class="r-mgrid-view-btnwrp"></div><div class="r-mgrid-view-btnwrp"></div>' +
							'<div class="r-mgrid-view-btnwrp"></div><div class="r-mgrid-view-btnwrp"></div></div>' +
						'</td>' +
						'<td><div class="r-mgrid-view-gridcols"><select class="r-mgrid-view-collist" size="10"></select></div>' +
							'<div class="r-mgrid-view-text">Field Name:</div>' +
							'<div class="r-mgrid-view-inputwrp"><input class="r-mgrid-view-input"></input></div>' +
							'<div class="r-mgrid-view-text">Column Title:</div>' +
							'<div class="r-mgrid-view-inputwrp"><input class="r-mgrid-view-input"></input></div>' +
							'<div class="r-mgrid-view-text">Column Width:</div>' +
							'<div class="r-mgrid-view-inputwrp"><input class="r-mgrid-view-input"></input></div>' +
							'<div class="r-mgrid-view-text">Default Sort Column:</div>' +
							'<div class="r-mgrid-view-inputwrp"><select class="r-mgrid-view-input"><option value=""></option></select></div>' +
						'</td>' +
						'<td valign="top"><div class="r-mgrid-view-buttons"><div class="r-mgrid-view-btnwrp"></div><div class="r-mgrid-view-btnwrp"></div>' +
							'<div class="r-mgrid-view-btnwrp"></div><div class="r-mgrid-view-btnwrp"></div></div>' +
						'</td>' +
					'</tr>' +
				'</tbody></table>'});
		this.allFields = YAHOO.util.Dom.getElementsByClassName('r-mgrid-view-fieldlist', 'select', columnView)[0];
		this.allFields.multiple = true;
		YAHOO.util.Event.addListener(this.allFields, 'change', this.handleButtons, this, true);
		YAHOO.util.Event.addListener(this.allFields, 'dblclick', this.handleFieldDblClick, this, true);
		this.colList = YAHOO.util.Dom.getElementsByClassName('r-mgrid-view-collist', 'select', columnView)[0];
		this.colList.multiple = true;
		YAHOO.util.Event.addListener(this.colList, 'change', this.handleColumnSelect, this, true);
		YAHOO.util.Event.addListener(this.colList, 'dblclick', this.handleColumnDblClick, this, true);
		var inputs = YAHOO.util.Dom.getElementsByClassName('r-mgrid-view-input', 'input', columnView);
		this.attInput = inputs[0];
		this.attInput.readOnly = true;
		this.headerInput = inputs[1];
		YAHOO.util.Event.addListener(this.headerInput, 'keyup', this.headerChanged, this, true);
		this.colWidthInput = inputs[2];
		YAHOO.util.Event.addListener(this.colWidthInput, 'keyup', this.widthChanged, this, true);
		this.defaultSortInput = YAHOO.util.Dom.getElementsByClassName('r-mgrid-view-input', 'select', columnView)[0];
		
		var buttonWrappers = YAHOO.util.Dom.getElementsByClassName('r-mgrid-view-btnwrp', 'div', columnView);
		this.addButton = new YAHOO.ext.Button(buttonWrappers[0],{handler: this.handleAdd,scope: this,text: 'Add >>',minWidth: 100});
		this.addAllButton = new YAHOO.ext.Button(buttonWrappers[1],{handler: this.handleAddAll,scope: this,text: 'Add All >>',minWidth: 100});
		this.removeButton = new YAHOO.ext.Button(buttonWrappers[2],{handler: this.handleRemove,scope: this,text: '<< Remove',minWidth: 100});
		this.removeAllButton = new YAHOO.ext.Button(buttonWrappers[3],{handler: this.handleRemoveAll,scope: this,text: '<< Remove All',minWidth: 100});
		this.topButton = new YAHOO.ext.Button(buttonWrappers[4],{handler: this.handleTop,scope: this,text: 'Top',minWidth: 100});
		this.upButton = new YAHOO.ext.Button(buttonWrappers[5],{handler: this.handleUp,scope: this,text: 'Up',minWidth: 100});
		this.downButton = new YAHOO.ext.Button(buttonWrappers[6],{handler: this.handleDown,scope: this,text: 'Down',minWidth: 100});
		this.bottomButton = new YAHOO.ext.Button(buttonWrappers[7],{handler: this.handleBottom,scope: this,text: 'Bottom',minWidth: 100});
		this.disableAll();
	}, 
	
	handleColumnSelect: function(){
		if(this.checkMultipleSelection(this.colList)){
			this.disableColumnInputs();
			this.handleButtons(true);
		}
		else{
			this.fillColumnInputs();
			this.handleButtons();	
		}
		
	}, 
	
	checkMultipleSelection : function(select){
		var selectedCount = 0;
		var options = select.options;
		for(var index=0; index<options.length; index++) {
			var option = options[index];
			if(option.selected == true){
				selectedCount ++;
				if (selectedCount > 1) {
					return true;
				} 
			}
		}
		return false;
	},
	
	fillColumnInputs : function(){
		var selectedIndex = this.colList.selectedIndex;
		if(selectedIndex == -1){
			this.attInput.value = '';
			this.headerInput.value = '';
			this.colWidthInput.value = '100';
		}
		else{
			var fieldName = this.colList.options[selectedIndex].value;
			var colConfig = this.columnsConfig[fieldName];
			this.attInput.value = fieldName;
			this.headerInput.value = colConfig['header'];
			this.colWidthInput.value = colConfig['width'];
		}
		this.attInput.readOnly = false;
		this.headerInput.readOnly = false;
		this.colWidthInput.readOnly = false;
	}, 
	
	disableColumnInputs : function(){
		this.attInput.value = '';
		this.attInput.readOnly = true;
		this.headerInput.value = '';
		this.headerInput.readOnly = true;
		this.colWidthInput.value = '100';
		this.colWidthInput.readOnly = true;
	},
	headerChanged: function(){
		if(this.colList.selectedIndex != -1){
			var header = this.headerInput.value;
			var fieldName = this.colList.options[this.colList.selectedIndex].value;
			this.columnsConfig[fieldName]['header'] = header;
		}
	},
	widthChanged: function(){
		if(this.colList.selectedIndex != -1){
			var width = this.colWidthInput.value;
			var fieldName = this.colList.options[this.colList.selectedIndex].value;
			this.columnsConfig[fieldName]['width'] = width;
		}
	},
	
	handleAdd: function(){
		var selectedIndexes = SelectUtils.collectSelectedIndicesFromSelect(this.allFields);
		for(var index=selectedIndexes.length-1; index >= 0; index--) {
			var selectedIndex = selectedIndexes[index];
			var fieldName = this.allFields.options[selectedIndex].value;
			SelectUtils.moveFromSelectToSelect(selectedIndex, this.allFields, this.colList);
			SelectUtils.addOption(this.defaultSortInput, fieldName, fieldName);
			this.columnsConfig[fieldName] = {header:fieldName, width:'100'};
		}
		this.handleColumnSelect();
	}, 
	handleFieldDblClick: function(event){
		var selectedIndex = this.allFields.selectedIndex;
		var fieldName = this.allFields.options[selectedIndex].value;
		SelectUtils.moveFromSelectToSelect(selectedIndex, this.allFields, this.colList);
		SelectUtils.addOption(this.defaultSortInput, fieldName, fieldName);
		this.columnsConfig[fieldName] = {header:fieldName, width:'100'};
		this.handleColumnSelect();
	},
	handleAddAll: function(){
		var allFields = this.allFields;
		var nOfFields = allFields.options.length;
		for(var index=nOfFields-1; index >= 0; index--) {
			var fieldName = allFields[index].value;
			SelectUtils.moveFromSelectToSelect(index, this.allFields, this.colList);
			SelectUtils.addOption(this.defaultSortInput, fieldName, fieldName);
			this.columnsConfig[fieldName] = {header:fieldName, width:'100'};
		}
		this.handleColumnSelect();
	}, 
	handleRemove: function(){
		var selectedIndexes = SelectUtils.collectSelectedIndicesFromSelect(this.colList);
		for(var index=selectedIndexes.length-1; index >= 0; index--) {
			var selectedIndex = selectedIndexes[index];
			var fieldName = this.colList.options[selectedIndex].value;
			SelectUtils.moveFromSelectToSelect(selectedIndex, this.colList, this.allFields);
			SelectUtils.remove(this.defaultSortInput, fieldName);
			delete this.columnsConfig[fieldName];
		}
		this.handleColumnSelect();
	}, 
	handleColumnDblClick: function(event){
		var selectedIndex = this.colList.selectedIndex;
		var fieldName = this.colList.options[selectedIndex].value;
		SelectUtils.moveFromSelectToSelect(selectedIndex, this.colList, this.allFields);
		SelectUtils.remove(this.defaultSortInput, fieldName);
		delete this.columnsConfig[fieldName];
		this.handleColumnSelect();
	},
	handleRemoveAll: function(){
		var colList = this.colList;
		var nOfFields = colList.options.length;
		for(var index=nOfFields-1; index >= 0; index--) {
			var fieldName = colList[index].value;
			SelectUtils.moveFromSelectToSelect(index, this.colList, this.allFields);
			SelectUtils.remove(this.defaultSortInput, fieldName);
			delete this.columnsConfig[fieldName];
		}
		this.handleColumnSelect();
	}, 
	handleTop: function(){
		var colList = this.colList
		var selectedIndex = colList.selectedIndex;
		var fieldName = colList.options[selectedIndex].value;
		colList.remove(selectedIndex);
		SelectUtils.addOptionBefore(colList, fieldName, fieldName, 0);
		SelectUtils.selectTheValue(colList, fieldName, 0);
		this.handleColumnSelect();
	}, 
	handleUp: function(){
		var colList = this.colList
		var selectedIndex = colList.selectedIndex;
		var fieldName = colList.options[selectedIndex].value;
		colList.remove(selectedIndex);
		SelectUtils.addOptionBefore(colList, fieldName, fieldName, selectedIndex - 1);
		SelectUtils.selectTheValue(colList, fieldName, selectedIndex - 1);
		this.handleColumnSelect();
	}, 
	handleDown: function(){
		var colList = this.colList
		var selectedIndex = colList.selectedIndex;
		var fieldName = colList.options[selectedIndex].value;
		if(selectedIndex == colList.options.length - 2){
			colList.remove(selectedIndex);
			SelectUtils.addOption(colList, fieldName, fieldName);
			SelectUtils.selectTheValue(colList, fieldName, colList.options.length - 1);
		}
		else{
			colList.remove(selectedIndex);
			SelectUtils.addOptionBefore(colList, fieldName, fieldName, selectedIndex + 1);
			SelectUtils.selectTheValue(colList, fieldName, selectedIndex + 1);
		}
		this.handleColumnSelect();
	}, 
	handleBottom: function(){
		var colList = this.colList
		var selectedIndex = colList.selectedIndex;
		var fieldName = colList.options[selectedIndex].value;
		colList.remove(selectedIndex);
		SelectUtils.addOption(colList, fieldName, fieldName);
		SelectUtils.selectTheValue(colList, fieldName, colList.options.length - 1);
		this.handleColumnSelect();
	},
	
	disableAll : function(){
		this.addButton.disable();
		this.addAllButton.disable();
		this.removeButton.disable();
		this.removeAllButton.disable();
		this.topButton.disable();
		this.upButton.disable();
		this.downButton.disable();
		this.bottomButton.disable();
	},
	
	handleButtons: function(colListMultipleSelected){
		if(this.availableFields.length > 0){
			this.addAllButton.enable();
			if(this.allFields.selectedIndex != -1){
				this.addButton.enable();
			}
			else{
				this.addButton.disable();
			}
		}
		else{
			this.addAllButton.disable();
			this.addButton.disable();
		}
		if(this.colList.options.length > 0){
			this.removeAllButton.enable();
			if(this.colList.selectedIndex != -1){
				this.removeButton.enable();
				if(this.colList.options.length > 1){
					if(colListMultipleSelected){
						this.topButton.disable();
						this.upButton.disable();
						this.downButton.disable();
						this.bottomButton.disable();
					}
					else{
						if(this.colList.selectedIndex == 0){
							this.topButton.disable();
							this.upButton.disable();
							this.downButton.enable();
							this.bottomButton.enable();
						}
						else if(this.colList.selectedIndex == this.colList.options.length-1){
							this.topButton.enable();
							this.upButton.enable();
							this.downButton.disable();
							this.bottomButton.disable();
						}
						else{
							this.topButton.enable();
							this.upButton.enable();
							this.downButton.enable();
							this.bottomButton.enable();
						}	
					}
					
				}
				else{
					this.topButton.disable();
					this.upButton.disable();
					this.downButton.disable();
					this.bottomButton.disable();
				}
			}
			else{
				this.removeButton.disable();
				this.topButton.disable();
				this.upButton.disable();
				this.downButton.disable();
				this.bottomButton.disable();
			}
		}
		else{
			this.removeAllButton.disable();
			this.removeButton.disable();
			this.topButton.disable();
			this.upButton.disable();
			this.downButton.disable();
			this.bottomButton.disable();
		}
		
	}
};