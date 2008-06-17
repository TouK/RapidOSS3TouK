YAHOO.rapidjs.riadmin.ConnectorDialog = function(conf, gridWindow, errorDialog){
	this.errorDialog = errorDialog;
	this.dataModelOfGrid = gridWindow.dm;
	this.gridWindow = gridWindow;
	this.indexOfTheNameColumn = this.dataModelOfGrid.fieldIndexes['Name'];
	this.openedForUpdate = false;
	var dh = YAHOO.ext.DomHelper;
	var config = {modal: false,
	    width:conf.width,height:conf.height,shadow:true,
	    syncHeightBeforeShow: true,resizable: false, title:conf.title, 
	    center:{}};
    this.dialog = new YAHOO.ext.LayoutDialog(dh.append(document.body, {tag:'div'}), config);
	this.cancelButton = this.dialog.addButton('Cancel', this.hide, this);
	this.saveButton = this.dialog.addButton('Save', this.handleSave, this);
	var layout = this.dialog.layout;
	layout.beginUpdate();
	this.wrap = dh.append(document.body, {tag:'div'}, true);
	layout.add('center', new YAHOO.rapidjs.component.layout.RapidPanel(this.wrap));
	layout.endUpdate();
	var detailView = dh.append(this.wrap.dom, {tag:'div', cls:'riadmin-conndlg-detailwr', 
				html:'<table><tbody><tr><td><div class="riadmin-formtext">Connector Name:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
					'<tr><td><div class="riadmin-formtext">Script:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
					'<tr><td><div class="riadmin-formtext">Init Script:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
					'<tr><td><div class="riadmin-formtext">Log Level:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
					'<tr><td><div class="riadmin-formtext">Start Type:</div></td><td><div class="riadmin-inputwrp"/></td></tr><tbody><table>'});
					
	var inputWrappers = detailView.getElementsByTagName('div');
	this.nameInput = dh.append(inputWrappers[1], {tag:'input', cls:'riadmin-input'});
	this.dialog.defaultInput = this.nameInput;
	this.scriptInput = dh.append(inputWrappers[3], {tag:'input', cls:'riadmin-input'});
	this.initScriptInput = dh.append(inputWrappers[5], {tag:'input', cls:'riadmin-input'});
	this.logLevelComb = dh.append(inputWrappers[7], {tag:'select', cls:'riadmin-combobox', 
		html:'<option value="ALL">ALL</option><option value="DEBUG">DEBUG</option><option value="INFO">INFO</option>' +
			'<option value="WARN" selected="true">WARN</option><option value="ERROR">ERROR</option><option value="FATAL">FATAL</option><option value="OFF">OFF</option>'});
	this.startTypeComb = dh.append(inputWrappers[9], {tag:'select', cls:'riadmin-combobox', 
		html:'<option value="manual">manual</option><option value="automatic">automatic</option>' +
			'<option value="conditioned">conditioned</option><option value="scheduled">scheduled</option>'});
			
	this.conditionedView = dh.append(this.wrap.dom, {tag:'div', cls:'riadmin-conndlg-conwrp', 
			html:'<table><tbody><tr><td><div class="riadmin-intext" style="font-style:italic;color:#0837DD">Start connector when</div></td><td></td></tr>' +
				'<tr><td><div class="riadmin-intext">Connector Name:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
				'<tr><td><div class="riadmin-intext" style="font-style:italic;color:#0837DD">transitions to</div></td><td></td></tr>' +
				'<tr><td><div class="riadmin-intext">Status:</div></td><td><div class="riadmin-inputwrp"/></td></tr><tbody><table>'});
	var condWrappers = this.conditionedView.getElementsByTagName('div');
	this.condAptNameComb = dh.append(condWrappers[2], {tag:'select', cls:'riadmin-combobox'});
	this.condStatusComb = dh.append(condWrappers[5], {tag:'select', cls:'riadmin-combobox', 
		html:'<option value="R">Running</option><option value="SE">Start Error</option><option value="TS">Terminated Successfully</option><option value="TWE">Terminated With Exception</option>'});
	
	this.scheduledView = dh.append(this.wrap.dom, {tag:'div', cls:'riadmin-conndlg-shedwrp',
			html:'<table><tbody><tr><td><div class="riadmin-intext">Starting:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
				'<tr><td><div class="riadmin-intext">Frequency:</div></td><td><div class="riadmin-inputwrp"/></td></tr><tbody><table>'});
	var schedWrappers = this.scheduledView.getElementsByTagName('div');
	this.schedStartingInput = dh.append(schedWrappers[1], {tag:'input', cls:'riadmin-input'});
	this.schedFrequencyInput = dh.append(schedWrappers[3], {tag:'input', cls:'riadmin-input'});
	
	YAHOO.util.Dom.setStyle(this.conditionedView, 'display', 'none');
	YAHOO.util.Dom.setStyle(this.scheduledView, 'display', 'none');
	YAHOO.util.Event.addListener(this.startTypeComb, 'change', this.startTypeChanged, this, true);
	this.renderConnector();
	this.autoHeight();
	
	this.dialog.addTabListener(this.cancelButton.el.dom, this.nameInput);
	this.dialog.addTabListener(this.saveButton.el.dom, this.cancelButton.el.dom);
	this.dialog.addKeyListener(13, function(){if(this.cancelButton.isFocused == false && this.saveButton.isFocused == false){this.handleSave();}}, this);
	this.dialog.addKeyListener(27, function(){this.hide();}, this);
	this.dialog.addLoading();
};

YAHOO.rapidjs.riadmin.ConnectorDialog.prototype = {
	
	hide:function(){
		this.dialog.hide();
	}, 
	
	show: function(isUpdating, node)
	{	
		this.clear();
		if(isUpdating)
		{
			this.openedForUpdate = true;
			this.populateInputsForUpdate(node);
		}
		else
		{
			this.openedForUpdate = false;
			this.populateFieldsForAdd(node);
		}
		this.dialog.show();
	},
	
	populateInputsForUpdate: function(adapterDetailsNode)
	{
		this.nameInput.disabled = true;
		this.nameInput.value = adapterDetailsNode.getAttribute('Name');
		this.scriptInput.value = adapterDetailsNode.getAttribute('Script');
		this.initScriptInput.value = adapterDetailsNode.getAttribute('InitScript');
		SelectUtils.selectTheValue(this.logLevelComb, adapterDetailsNode.getAttribute('LogLevel'), 3);
		var startType = adapterDetailsNode.getAttribute('StartType');
		SelectUtils.selectTheValue(this.startTypeComb, startType, 0);
		this.startTypeChanged();
		if(startType == 'conditioned')
		{
			var adapterNode = adapterDetailsNode.getElementsByTagName('Adapter')[0];
			var dependsToConnectorName = adapterNode.getAttribute('Name');
			var dependsToConnectorState = adapterNode.getAttribute('State');
			SelectUtils.selectTheValue(this.condAptNameComb, dependsToConnectorName, 0);
			SelectUtils.selectTheValue(this.condStatusComb, dependsToConnectorState, 0);
		}
		else if(startType == 'scheduled')
		{
			var scheduleNode = adapterDetailsNode.getElementsByTagName('Schedule')[0];
			var starting = scheduleNode.getAttribute('Starting');
			var frequency = scheduleNode.getAttribute('Frequency');
			this.schedStartingInput.value = starting;
			this.schedFrequencyInput.value = frequency;
		}
		this.populateExtraInputsForUpdate(adapterDetailsNode);
	},
	
	populateExtraInputsForUpdate: function(adapterDetailsNode)
	{
		alert("Override populateExtraInputsForUpdate method please");
	},
	
	populateFieldsForAdd: function(node){
		alert("Override populateFieldsForAdd method please");
	},
	
	clear: function()
	{
		this.nameInput.disabled = false;
		this.nameInput.value = '';
		this.scriptInput.value = '';
		this.initScriptInput.value = '';
		this.logLevelComb.selectedIndex = 3;
		this.startTypeComb.selectedIndex = 0;
		this.startTypeChanged();
		this.condAptNameComb.selectedIndex = 0;
		this.condStatusComb.selectedIndex = 0;
		this.schedStartingInput.value = '';
		this.schedFrequencyInput.value = '';
		this.clearExtraFormInputs();
	},
	
	clearExtraFormInputs: function()
	{
		alert("Override clearExtraFormInputs of ConnectorDialog please");
	},
	
	populateAdapterNames: function()
	{
		SelectUtils.clear(this.condAptNameComb);
		var rowCount = this.dataModelOfGrid.getRowCount();
		for(var i = 0 ; i < rowCount ; i++)
		{
			var adapterName = this.dataModelOfGrid.getValueAt(i, this.indexOfTheNameColumn);
			if(adapterName != this.nameInput.value)
			{
				SelectUtils.addOption(this.condAptNameComb, adapterName, adapterName);
			}
		}
	},
	
	handleSave: function(){
		var connectorName = this.nameInput.value;
		var script = this.scriptInput.value;
		var initScript = this.initScriptInput.value;
		var logLevel = this.logLevelComb.options[this.logLevelComb.selectedIndex].value;
		var startType = this.startTypeComb.options[this.startTypeComb.selectedIndex].value;
		
		var dependsToConnectorName = "";
		var dependsToConnectorStatus = "";
		if(this.condAptNameComb.selectedIndex > -1)
		{
			dependsToConnectorName = this.condAptNameComb.options[this.condAptNameComb.selectedIndex].value;	
		}
		if(this.condStatusComb.selectedIndex > -1)
		{
			dependsToConnectorStatus = this.condStatusComb.options[this.condStatusComb.selectedIndex].value;	
		}
		var scheduleStarting = this.schedStartingInput.value;
		var period = this.schedFrequencyInput.value;
		
		var postData = new Array();
		postData[postData.length] = 'ConnectorName=' + connectorName;
		if(script.length > 0)
		{
			postData[postData.length] = 'Script=' + script;	
		}
		if(initScript.length > 0)
		{
			postData[postData.length] = 'InitScript=' + initScript;	
		}
		postData[postData.length] = 'LogLevel=' + logLevel;
		postData[postData.length] = 'StartType=' + startType;
		postData[postData.length] = 'DependsToConnectorName=' + dependsToConnectorName;
		postData[postData.length] = 'DependsToConnectorStatus=' + dependsToConnectorStatus;
		postData[postData.length] = 'ScheduleStarting=' + scheduleStarting;
		postData[postData.length] = 'SchedulePeriod=' + period;
		postData[postData.length] = this.getExtraPostData();
		
		var url = '';
		if(this.openedForUpdate == false)
		{
			url = this.getConnectorAddUrl(); 
		}
		else
		{
			url = this.getConnectorUpdateUrl();
		}
		this.postRequest(url, postData.join('&'), this.saveSuccess);
	},
	
	saveSuccess: function(response)
	{
		this.dialog.hideLoading();
		if(this.checkResponse(response))
		{
			this.hide();
			this.gridWindow.poll()	
		}
	},
	
	getConnectorAddUrl: function()
	{
		//should return the url to add a new connector
		alert("Override getConnectorAddUrl method of ConnectorDialog please");
	},
	
	getConnectorUpdateUrl: function()
	{
		//should return the url to update an existing connector
		alert("Override getConnectorUpdateUrl method of ConnectorDialog please");
	},
	
	getExtraPostData: function()
	{
		//should return some post data like a=b&c=d
		alert("Override getExtraPostData method of ConnectorDialog please");
	},
	
	setTitle: function(text){
		this.dialog.setTitle(text);
	},
	
	startTypeChanged: function(){
		var startType = this.startTypeComb.options[this.startTypeComb.selectedIndex].text; 
		if(startType == 'conditioned'){
			YAHOO.util.Dom.setStyle(this.conditionedView, 'display', '');
			YAHOO.util.Dom.setStyle(this.scheduledView, 'display', 'none');
			this.populateAdapterNames();
		}
		else if(startType == 'scheduled'){
			YAHOO.util.Dom.setStyle(this.scheduledView, 'display', '');
			YAHOO.util.Dom.setStyle(this.conditionedView, 'display', 'none');
		}
		else{
			YAHOO.util.Dom.setStyle(this.conditionedView, 'display', 'none');
			YAHOO.util.Dom.setStyle(this.scheduledView, 'display', 'none');
		}
		this.autoHeight();
	}, 
	
	autoHeight: function(){
		var height = this.wrap.getHeight()+ this.dialog.getHeaderFooterHeight();
        var bm = this.dialog.body.getMargins();
        var borders = this.dialog.layout.getEl().getBorderWidth('tb') + this.dialog.layout.regions['center'].getEl().getBorderWidth('tb');
        height = height + bm.top + bm.bottom + borders;
        this.dialog.resizeTo(this.dialog.size.width, height);
	},
	
	request : function(url, successDelegate){
		var callback = {
			success: successDelegate || this.processSuccess, 
			failure: this.processFailure,
			timeout: 30000,
			scope: this			
		};
		YAHOO.util.Connect.asyncRequest('GET',url , callback);
		this.dialog.showLoading();
	},
	
	postRequest: function(url, postData, successDelegate)
	{
		var sDelegate = this.processSuccess
		if(successDelegate)
		{
			sDelegate = successDelegate;
		}

		var callback = {
			success: sDelegate, 
			failure: this.processFailure,
			timeout: 30000,
			scope: this			
		};
		YAHOO.util.Connect.asyncRequest('POST',url , callback, postData);
		this.dialog.showLoading();
	},
	
	processSuccess: function(response)
	{
		YAHOO.rapidjs.ServerStatus.refreshState(true);
		this.dialog.hideLoading();
		this.checkResponse(response);
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
	
	processFailure: function(response)
	{
		this.dialog.hideLoading();
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
}
