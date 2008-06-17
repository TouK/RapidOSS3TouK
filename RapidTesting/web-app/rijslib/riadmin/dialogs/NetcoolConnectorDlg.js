YAHOO.rapidjs.riadmin.NetcoolConnectorDlg = function(config, gridWindow, errorDialog){
	YAHOO.rapidjs.riadmin.NetcoolConnectorDlg.superclass.constructor.call(this, config, gridWindow, errorDialog);
};

YAHOO.extendX(YAHOO.rapidjs.riadmin.NetcoolConnectorDlg, YAHOO.rapidjs.riadmin.ConnectorDialog, {
	
	getExtraPostData: function(){
		var postData = new Array();
		var connectionName = this.connNameComb.options[this.connNameComb.selectedIndex].value;
		var pollingInterval = this.pollIntervalInput.value;
		
		postData[postData.length] = 'DatabaseConnectionName=' + connectionName;
		postData[postData.length] = 'PollingInterval=' + pollingInterval;
		postData[postData.length] = 'MonitoredAttributes=' + this.getMonitoredAttributes();
		
		return postData.join('&');
	},
	
	getConnectorAddUrl: function()
	{
		return '/RapidConnector/AdapterManager/addNetcoolConnector';
	},
	
	getConnectorUpdateUrl: function()
	{
		return '/RapidConnector/AdapterManager/updateNetcoolConnector';
	},
	
	handleAddAtt: function(){
		SelectUtils.moveAllSelectedsFromSelectToSelect(this.attSelect, this.monAttSelect);	
	},
	
	handleRemoveAtt: function(){
		SelectUtils.moveAllSelectedsFromSelectToSelect(this.monAttSelect, this.attSelect);	
	},
	
	populateExtraInputsForUpdate: function(adapterDetailsNode)
	{
		var connName = adapterDetailsNode.getElementsByTagName('DatabaseConnectionName')[0].getAttribute('Value');
		var datasources = adapterDetailsNode.getElementsByTagName('Datasource');	
		var pollingInterval = adapterDetailsNode.getElementsByTagName('PollingInterval')[0].getAttribute('Value');
		var monitoredAttributes = adapterDetailsNode.getElementsByTagName('MonitoredAttributes')[0].getAttribute('Value');
		SelectUtils.clear(this.connNameComb);
		SelectUtils.addOption(this.connNameComb, '', '');
		for(var index=0; index<datasources.length; index++) {
			var datasource = datasources[index].getAttribute('Name');
			SelectUtils.addOption(this.connNameComb, datasource, datasource);
		}
		SelectUtils.selectTheValue(this.connNameComb, connName, 0);
		this.pollIntervalInput.value = pollingInterval;
		var monAttributesArray = monitoredAttributes.split(',');
		var nOfMonAtts = monAttributesArray.length;
		for(var index=0; index<nOfMonAtts; index++) {
			var monAtt = monAttributesArray[index];
			if(monAtt.length > 0){
				SelectUtils.addOption(this.monAttSelect, monAtt, monAtt);	
			}
		}
		this.request('/RapidInsight/ManagedObject/invoke?Script=admin/getNetcoolFields', this.fieldsReceived);
	},
	
	fieldsReceived : function(response){
		this.dialog.hideLoading();
		if(this.checkResponse(response)){
			var fields = response.responseXML.getElementsByTagName('Field');
			var nOfFields = fields.length;
			if(this.openedForUpdate == false){
				for(var index=0; index<nOfFields; index++) {
					var fieldName = fields[index].getAttribute('Name');
					SelectUtils.addOption(this.attSelect, fieldName, fieldName);
				}
			}
			else{
				var monAttsArray = SelectUtils.collectValuesFromSelect(this.monAttSelect);
				for(var i=0; i<nOfFields; i++) {
					var fieldName = fields[i].getAttribute('Name');
					var found = false;
					for(var j = 0 ; j < monAttsArray.length ; j++)
					{
						var oldMonAtt = monAttsArray[j];
						if(oldMonAtt == fieldName)
						{
							found = true;
							break;
						}
					}
					if(found == false)
					{
						SelectUtils.addOption(this.attSelect, fieldName, fieldName);
					}
				}
			}	
		}
		
	},

	clearExtraFormInputs: function()
	{
		SelectUtils.clear(this.connNameComb);
		this.pollIntervalInput.value = '';
		SelectUtils.clear(this.monAttSelect);
		SelectUtils.clear(this.attSelect);
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
					html:'<table><tbody><tr><td><div class="rc-formtext">DatabaseConnectionName:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
						'<tr><td><div class="riadmin-formtext">PollingInterval:</div></td><td><div class="riadmin-inputwrp"/></td></tr><tbody><table>'});

		var inputWrappers = detailView.getElementsByTagName('div');
		this.connNameComb = dh.append(inputWrappers[1], {tag:'select', cls:'riadmin-combobox'});
		this.pollIntervalInput = dh.append(inputWrappers[3], {tag:'input', cls:'riadmin-input'});
		
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
		
		this.dialog.addTabListener(this.monAttSelect, this.saveButton.el.dom);
		this.dialog.addHelp({url:help_ri_admin_conn_netcool_dlg});
	}, 
	
	populateFieldsForAdd: function(node){
		var datasources = node.getElementsByTagName('Datasource');
		SelectUtils.addOption(this.connNameComb, '', '');
		for(var index=0; index<datasources.length; index++) {
			var datasource = datasources[index].getAttribute('Name');
			SelectUtils.addOption(this.connNameComb, datasource, datasource);
		}
		SelectUtils.selectTheValue(this.connNameComb, '', 0);
		this.request('/RapidInsight/ManagedObject/invoke?Script=admin/getNetcoolFields', this.fieldsReceived);
	}
});