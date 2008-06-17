YAHOO.rapidjs.riadmin.DBConnectorDlg = function(config, gridWindow, errorDialog){
	YAHOO.rapidjs.riadmin.DBConnectorDlg.superclass.constructor.call(this, config, gridWindow, errorDialog);
};

YAHOO.extendX(YAHOO.rapidjs.riadmin.DBConnectorDlg, YAHOO.rapidjs.riadmin.ConnectorDialog, {
	
	renderConnector: function(){
		var dh = YAHOO.ext.DomHelper;
		this.connwrap = dh.append(this.wrap.dom, {tag:'div'});
		var detailView = dh.append(this.connwrap, {tag:'div', cls:'rc-db-detailwrp', 
					html:'<table><tbody><tr><td><div class="rc-formtext">DatabaseConnectionName:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
						'<tr><td><div class="riadmin-formtext">PollingInterval:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
						'<tr><td><div class="riadmin-formtext">NewRecordIdentifier:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
						'<tr><td><div class="riadmin-formtext">SQL:</div></td><td><div class="rc-db-sqlwrp"/></td></tr><tbody><table>'});
						
		var inputWrappers = detailView.getElementsByTagName('div');
		this.connNameComb = dh.append(inputWrappers[1], {tag:'select', cls:'riadmin-combobox'});
		this.pollIntervalInput = dh.append(inputWrappers[3], {tag:'input', cls:'riadmin-input'});
		this.newRecordInput = dh.append(inputWrappers[5], {tag:'input', cls:'riadmin-input'});
		this.sqlInput = dh.append(inputWrappers[7], {tag:'textarea', cls:'rapid-textarea'});
		this.sqlInput.rows = 5;
		this.dialog.addTabListener(this.sqlInput, this.saveButton.el.dom);
		this.dialog.addHelp({url:help_ri_admin_conn_db_dlg});
	}, 
	
	getExtraPostData: function(){
		var connectionName = this.connNameComb.options[this.connNameComb.selectedIndex].value;
		var pollingInterval = this.pollIntervalInput.value;
		var sql = this.sqlInput.value;
		var newRecordIdentifier = this.newRecordInput.value;
		var postData = new Array();
		
		postData[postData.length] = 'DatabaseConnectionName=' + encodeURIComponent(connectionName);
		postData[postData.length] = 'PollingInterval=' + pollingInterval;
		postData[postData.length] = 'Sql=' + encodeURIComponent(sql);
		if(newRecordIdentifier && newRecordIdentifier != '')
			postData[postData.length] = 'NewRecordIdentifier=' + encodeURIComponent(newRecordIdentifier);
		return postData.join('&');
	},
	
	getConnectorAddUrl: function()
	{
		return '/RapidConnector/AdapterManager/addDatabaseConnector';
	},
	
	getConnectorUpdateUrl: function()
	{
		return '/RapidConnector/AdapterManager/updateDatabaseConnector';
	},
	populateExtraInputsForUpdate: function(adapterDetailsNode)
	{
		this.pollIntervalInput.value = adapterDetailsNode.getElementsByTagName('PollingInterval')[0].getAttribute('Value');			
		this.sqlInput.value = adapterDetailsNode.getElementsByTagName('Sql')[0].getAttribute('Value');	
		if(adapterDetailsNode.getElementsByTagName('NewRecordIdentifier') && adapterDetailsNode.getElementsByTagName('NewRecordIdentifier').length != 0){		
			this.newRecordInput.value = adapterDetailsNode.getElementsByTagName('NewRecordIdentifier')[0].getAttribute('Value');
		}
		var dbConnName = adapterDetailsNode.getElementsByTagName('DatabaseConnectionName')[0].getAttribute('Value');
		var datasources = adapterDetailsNode.getElementsByTagName('Datasource');
		SelectUtils.addOption(this.connNameComb, '', '');
		for(var index=0; index<datasources.length; index++) {
			var datasource = datasources[index].getAttribute('Name');
			SelectUtils.addOption(this.connNameComb, datasource, datasource);
		}	
		SelectUtils.selectTheValue(this.connNameComb, dbConnName, 0);		
	},
	clearExtraFormInputs: function()
	{
		SelectUtils.clear(this.connNameComb);
		this.sqlInput.value = '';
		this.newRecordInput.value = '';
		this.pollIntervalInput.value = '';
	}, 
	
	populateFieldsForAdd: function(node){
		var datasources = node.getElementsByTagName('Datasource');
		SelectUtils.addOption(this.connNameComb, '', '');
		for(var index=0; index<datasources.length; index++) {
			var datasource = datasources[index].getAttribute('Name');
			SelectUtils.addOption(this.connNameComb, datasource, datasource);
		}	
		SelectUtils.selectTheValue(this.connNameComb, '', 0);
	}
});