YAHOO.rapidjs.riadmin.NtfConnectorDlg = function(config, gridWindow, errorDialog){
	YAHOO.rapidjs.riadmin.NtfConnectorDlg.superclass.constructor.call(this, config, gridWindow, errorDialog);
	this.notificationAttributeNames = [
											"Acknowledged",
											"Active",
											"Category",
											"Certainty",
											"ClassDisplayName",
											"ClassName",
											"ElementClassName",
											"ElementName",
											"EventDisplayName",
											"EventName",
											"EventState",
											"EventText",
											"EventType",
											"FirstNotifiedAt",
											"Impact",
											"InMaintenance",
											"InstanceDisplayName",
											"InstanceName",
											"IsProblem",
											"IsRoot",
											"LastChangedAt",
											"LastClearedAt",
											"LastNotifiedAt",
											"Name",
											"OccurrenceCount",
											"Owner",
											"Severity",
											"SourceDomainName",
											"SourceEventType",
											"TroubleTicketID",
											"UserDefined1",
											"UserDefined2",
											"UserDefined3",
											"UserDefined4",
											"UserDefined5",
											"UserDefined6",
											"UserDefined7",
											"UserDefined8",
											"UserDefined9",
											"UserDefined10"
	];
};

YAHOO.extendX(YAHOO.rapidjs.riadmin.NtfConnectorDlg, YAHOO.rapidjs.riadmin.ConnectorDialog, {
	
	getExtraPostData: function(){
		var postData = new Array();
		postData[postData.length] = 'InChargeConnectionName=' + this.getInChargeConnectionName();
		postData[postData.length] = 'NotificationList=' + this.getNotificationList();
		var mode = this.getMode();
		postData[postData.length] = 'Mode=' + mode;
		if(mode == 'Polling')
		{
			postData[postData.length] = 'PollingInterval=' + this.getPollingInterval();
		}
		postData[postData.length] = 'NamePattern=' + this.getNamePattern();
		postData[postData.length] = 'MonitoredAttributes=' + this.getMonitoredAttributes();
		return postData.join('&');
	},
	
	getConnectorAddUrl: function()
	{
		return '/RapidConnector/AdapterManager/addNotificationConnector';
	},
	
	getConnectorUpdateUrl: function()
	{
		return '/RapidConnector/AdapterManager/updateNotificationConnector';
	},
	
	handleAddAtt: function(){
		SelectUtils.moveAllSelectedsFromSelectToSelect(this.attSelect, this.monAttSelect);	
	},
	
	handleRemoveAtt: function(){
		SelectUtils.moveAllSelectedsFromSelectToSelect(this.monAttSelect, this.attSelect);	
	},
	
	populateExtraInputsForUpdate: function(adapterDetailsNode)
	{
		var connName = adapterDetailsNode.getElementsByTagName('InChargeConnectionName')[0].getAttribute('Value');
		var datasources = adapterDetailsNode.getElementsByTagName('Datasource');
		var notificationList = adapterDetailsNode.getElementsByTagName('NotificationList')[0].getAttribute('Value');
		var mode = adapterDetailsNode.getElementsByTagName('Mode')[0].getAttribute('Value');
		var pollingInterval = '';
		var namePattern = '';
		if(mode == 'Polling')
		{
			pollingInterval = adapterDetailsNode.getElementsByTagName('PollingInterval')[0].getAttribute('Value');
			namePattern = adapterDetailsNode.getElementsByTagName('NamePattern')[0].getAttribute('Value');
		}
		var monitoredAttributes = adapterDetailsNode.getElementsByTagName('MonitoredAttributes')[0].getAttribute('Value');
		
		this.populateInputs(connName, datasources, notificationList, mode, pollingInterval, namePattern, monitoredAttributes);
	},
	
	clearExtraFormInputs: function()
	{
		this.populateInputs('', [], 'ALL_NOTIFICATIONS', 'Polling', '0', '.*', '');
	},
	
	populateInputs: function(connName, datasources, notList, mode, pollInterval, namePattern, monAttsCommaDelimited)
	{
		SelectUtils.clear(this.connNameComb);
		SelectUtils.addOption(this.connNameComb, '', '');
		for(var index=0; index<datasources.length; index++) {
			var datasource = datasources[index].getAttribute('Name');
			SelectUtils.addOption(this.connNameComb, datasource, datasource);
		}
		SelectUtils.selectTheValue(this.connNameComb, connName, 0);
		this.ntfListInput.value = notList;
		SelectUtils.selectTheValue(this.modeComb, mode, 0);
		this.modeChanged();
		this.pollIntervalInput.value = pollInterval;
		this.namePatternInput.value = namePattern;
		this.populateAttributesAndMonitoredAttributes(monAttsCommaDelimited.split(','));
	},
	
	populateAttributesAndMonitoredAttributes: function(oldMonitoredAttributesArray)
	{
		SelectUtils.clear(this.monAttSelect);
		SelectUtils.clear(this.attSelect);
		for(var i = 0 ; i < this.notificationAttributeNames.length ; i++)
		{
			var monAtt = this.notificationAttributeNames[i];
			var found = false;
			for(var j = 0 ; j < oldMonitoredAttributesArray.length ; j++)
			{
				var oldMonAtt = oldMonitoredAttributesArray[j];
				if(oldMonAtt == monAtt)	//
				{
					if(oldMonAtt.length > 0){
						SelectUtils.addOption(this.monAttSelect, monAtt, monAtt);	
					}
					found = true;
					break;
				}
			}
			if(found == false)
			{
				SelectUtils.addOption(this.attSelect, monAtt, monAtt);
			}
		}
	},
	
	modeChanged: function(){
		var mode = this.modeComb.options[this.modeComb.selectedIndex].text; 
		if(mode == 'Listening'){
			YAHOO.util.Dom.setStyle(this.pollingModeView, 'display', 'none');
		}
		else{
			YAHOO.util.Dom.setStyle(this.pollingModeView, 'display', '');
		}
		this.autoHeight();
	},
	
	getInChargeConnectionName: function()
	{
		return this.connNameComb.options[this.connNameComb.selectedIndex].value;
	},
	
	getNotificationList: function()
	{
		return this.ntfListInput.value;
	},
	
	getMode: function()
	{
		if(this.modeComb.selectedIndex > -1)
		{
			return this.modeComb.options[this.modeComb.selectedIndex].value;
		}
		return null;
	},
	
	getPollingInterval: function()
	{
		return this.pollIntervalInput.value;
	},
	
	getNamePattern: function()
	{
		return this.namePatternInput.value;
	},
	
	//return comma delimited list of monitored attributes
	getMonitoredAttributes: function()
	{
		var monAttsArray = SelectUtils.collectValuesFromSelect(this.monAttSelect);
		return monAttsArray.join(',');
	},
	
	renderConnector: function(){
		var dh = YAHOO.ext.DomHelper;
		this.connwrap = dh.append(this.wrap.dom, {tag:'div', cls:'rc-ntf-detailwrp'});
		var detailView = dh.append(this.connwrap, {tag:'div',
					html:'<table><tbody><tr><td><div class="riadmin-formtext">InChargeConnectionName:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
						'<tr><td><div class="riadmin-formtext">NotificationList:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
						'<tr><td><div class="riadmin-formtext">Mode:</div></td><td><div class="riadmin-inputwrp"/></td></tr><tbody><table>'});
						
		var inputWrappers = detailView.getElementsByTagName('div');
		this.connNameComb = dh.append(inputWrappers[1], {tag:'select', cls:'riadmin-combobox'});
		this.ntfListInput = dh.append(inputWrappers[3], {tag:'input', cls:'riadmin-input'});
		this.ntfListInput.value = "ALL_NOTIFICATIONS";
		this.modeComb = dh.append(inputWrappers[5], {tag:'select', cls:'riadmin-combobox', html:'<option value="Listening">Listening</option><option value="Polling">Polling</option>'});
		
		this.pollingModeView = dh.append(this.connwrap, {tag:'div', 
					html:'<table><tbody><tr><td><div class="riadmin-formtext">PollingInterval:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
						'<tr><td><div class="riadmin-formtext">NamePattern:</div></td><td><div class="riadmin-inputwrp"/></td></tr><tbody><table>'});
		
		var pollModeWrappers = this.pollingModeView.getElementsByTagName('div');
		this.pollIntervalInput = dh.append(pollModeWrappers[1], {tag:'input', cls:'riadmin-input'});
		this.namePatternInput = dh.append(pollModeWrappers[3], {tag:'input', cls:'riadmin-input'});
		YAHOO.util.Dom.setStyle(this.pollingModeView, 'display', 'none');
		
		var attsView = dh.append(this.connwrap, {tag:'div',
				html:'<table><tbody><tr><td><div>Select Attribute</div></td><td></td><td><div>Monitored Attributes</div></td></tr>' +
					'<tr><td><div class="rc-ntf-attselectwrp"/></td>' +
					'<td><div class="rc-ntf-attbtnswrp"/></td>' +
					'<td><div class="rc-ntf-monattwrp"/></td></tr></tbody></table>'});
		var attsComps = attsView.getElementsByTagName('div');
		this.attSelect = dh.append(attsComps[2], {tag:'select', size:'15', cls:'rc-ntf-attselect', multiple:'true'});
				
		this.monAttSelect = dh.append(attsComps[4], {tag:'select', size:'15', cls:'rc-ntf-monattselect', multiple:'true'});
		var attButtonWrapper = dh.append(attsComps[3], {tag:'div', 
			html:'<table><tbody><tr><td><div class="rc-ntf-attbtnwrp"/></td></tr>' +
				'<tr><td><div class="rc-ntf-attbtnwrp"/></td></tr></tbody></table>'});
		
		var attButtons = attButtonWrapper.getElementsByTagName('div');
		this.addAttButton = new YAHOO.ext.Button(attButtons[0], {handler: this.handleAddAtt, scope: this,text: '>',minWidth: 30});
		this.removeAttButton =  new YAHOO.ext.Button(attButtons[1], {handler: this.handleRemoveAtt, scope: this,text: '<',minWidth: 30});
		
		YAHOO.util.Event.addListener(this.modeComb, 'change', this.modeChanged, this, true);
		this.dialog.addTabListener(this.monAttSelect, this.saveButton.el.dom);
		this.dialog.addHelp({url:help_ri_admin_conn_ntf_dlg});
	}, 
	
	populateFieldsForAdd: function(node){
		var datasources = node.getElementsByTagName('Datasource');
		SelectUtils.clear(this.connNameComb);
		SelectUtils.addOption(this.connNameComb, '', '');
		for(var index=0; index<datasources.length; index++) {
			var datasource = datasources[index].getAttribute('Name');
			SelectUtils.addOption(this.connNameComb, datasource, datasource);
		}
		SelectUtils.selectTheValue(this.connNameComb, '', 0);
	}
});