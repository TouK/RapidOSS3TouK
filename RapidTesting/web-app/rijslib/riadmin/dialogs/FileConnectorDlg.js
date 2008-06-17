YAHOO.rapidjs.riadmin.FileConnectorDlg = function(config, gridWindow, errorDialog){
	YAHOO.rapidjs.riadmin.FileConnectorDlg.superclass.constructor.call(this, config, gridWindow, errorDialog);
};

YAHOO.extendX(YAHOO.rapidjs.riadmin.FileConnectorDlg, YAHOO.rapidjs.riadmin.ConnectorDialog, {
	terminateChanged:function(){
		if(this.terminateInput.checked == true){
			this.tailModeInput.disabled = true;
		}
		else{
			this.tailModeInput.disabled = false;
		}
	}, 
	renderConnector: function(){
		var dh = YAHOO.ext.DomHelper;
		this.connwrap = dh.append(this.wrap.dom, {tag:'div', cls:'rc-file-detailwrp'});
		var detailView = dh.append(this.connwrap, {tag:'div',
					html:'<table><tbody><tr><td><div class="riadmin-formtext">FieldDelimiter:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
						'<tr><td><div class="riadmin-formtext">InputFileName:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
						'<tr><td><div class="riadmin-formtext">PollingInterval:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
						'<tr><td><div class="riadmin-formtext">StringInput:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
						'<tr><td><div class="riadmin-formtext">EndOfEntry:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
						'<tr><td><div class="riadmin-formtext">QuoteCharacter:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
						'<tr><td><div class="riadmin-formtext">CommentRegExp:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
						'<tr><td><div class="riadmin-formtext">TerminateAtEndOfFile:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
						'<tr><td><div class="riadmin-formtext">TailMode:</div></td><td><div class="riadmin-inputwrp"/></td></tr><tbody><table>'});
						
		var inputWrappers = detailView.getElementsByTagName('div');
		this.fieldDelInput = dh.append(inputWrappers[1], {tag:'input', cls:'riadmin-input'});
		this.inputFileInput = dh.append(inputWrappers[3], {tag:'input', cls:'riadmin-input'});
		this.pollIntervalInput = dh.append(inputWrappers[5], {tag:'input', cls:'riadmin-input'});
		this.stringInComb = dh.append(inputWrappers[7], {tag:'select', cls:'riadmin-combobox', 
			html:'<option value="DelimitedInput">DelimitedInput</option><option value="NameValuePairInput">NameValuePairInput</option>'});
		this.endOfEntryInput = dh.append(inputWrappers[9], {tag:'input', cls:'riadmin-input'});
		this.quoteCharInput = dh.append(inputWrappers[11], {tag:'input', cls:'riadmin-input'});
		this.commentRegExpInput = dh.append(inputWrappers[13], {tag:'input', cls:'riadmin-input'});
		this.terminateInput = dh.append(inputWrappers[15], {tag:'input', cls:'riadmin-checkbox', type:'checkbox', checked:'true'});
		this.tailModeInput = dh.append(inputWrappers[17], {tag:'input', cls:'riadmin-checkbox', type:'checkbox', disabled:'true'});
		YAHOO.util.Event.addListener(this.terminateInput, 'click', this.terminateChanged, this, true);
		this.dialog.addTabListener(this.commentRegExpInput, this.saveButton.el.dom);
		this.dialog.addHelp({url:help_ri_admin_conn_file_dlg});
	}, 
	
	getExtraPostData: function(){
		var fieldDelimeter = this.fieldDelInput.value;
		var inputFileName = this.inputFileInput.value;
		var pollingInterval = this.pollIntervalInput.value;
		var stringInput = this.stringInComb.options[this.stringInComb.selectedIndex].value;
		var endOfEntry = this.endOfEntryInput.value;
		var quoteChar = this.quoteCharInput.value;
		var commentRegExp = this.commentRegExpInput.value;
		var terminate = this.terminateInput.checked ? 'true': 'false';
		var tailMode = this.tailModeInput.checked ? 'true': 'false';
		var postData = new Array();
		
		postData[postData.length] = 'FieldDelimiter=' + encodeURIComponent(fieldDelimeter);
		postData[postData.length] = 'InputFileName=' + encodeURIComponent(inputFileName);
		postData[postData.length] = 'PollingInterval=' + encodeURIComponent(pollingInterval);
		postData[postData.length] = 'StringInput=' + encodeURIComponent(stringInput);
		postData[postData.length] = 'EndOfEntry=' + encodeURIComponent(endOfEntry);
		postData[postData.length] = 'QuoteCharacter=' + encodeURIComponent(quoteChar);
		postData[postData.length] = 'CommentRegExp=' + encodeURIComponent(commentRegExp);
		postData[postData.length] = 'TerminateAtEndOfFile=' + encodeURIComponent(terminate);
		postData[postData.length] = 'TailMode=' + encodeURIComponent(tailMode);
		return postData.join('&');
	},
	
	getConnectorAddUrl: function()
	{
		return '/RapidConnector/AdapterManager/addFileConnector';
	},
	
	getConnectorUpdateUrl: function()
	{
		return '/RapidConnector/AdapterManager/updateFileConnector';
	},
	populateExtraInputsForUpdate: function(adapterDetailsNode)
	{
		this.fieldDelInput.value = adapterDetailsNode.getElementsByTagName('FieldDelimiter')[0].getAttribute('Value');
		this.pollIntervalInput.value = adapterDetailsNode.getElementsByTagName('PollingInterval')[0].getAttribute('Value');			
		this.endOfEntryInput.value = adapterDetailsNode.getElementsByTagName('EndOfEntry')[0].getAttribute('Value');	
		this.commentRegExpInput.value = adapterDetailsNode.getElementsByTagName('CommentRegExp')[0].getAttribute('Value');	
		this.quoteCharInput.value = adapterDetailsNode.getElementsByTagName('QuoteCharacter')[0].getAttribute('Value');	
		this.inputFileInput.value = adapterDetailsNode.getElementsByTagName('InputFileName')[0].getAttribute('Value');	
		var stringInput = adapterDetailsNode.getElementsByTagName('StringInput')[0].getAttribute('Value');
		SelectUtils.selectTheValue(this.stringInComb, stringInput, 0);
		var terminate = adapterDetailsNode.getElementsByTagName('TerminateAtEndOfFile')[0].getAttribute('Value');
		if(terminate == "true"){
			this.terminateInput.checked = true;
		}
		else{
			this.terminateInput.checked = false;
			this.tailModeInput.disabled = false;
			var tailMode = adapterDetailsNode.getElementsByTagName('TailMode')[0].getAttribute('Value');
			if(tailMode == 'true'){
				this.tailModeInput.checked = true;
			}
			else{
				this.tailModeInput.checked = false;
			}
		}
	},
	clearExtraFormInputs: function()
	{
		this.fieldDelInput.value = ',';
		this.inputFileInput.value = '';
		this.endOfEntryInput.value = '';
		this.pollIntervalInput.value = '1';
		this.quoteCharInput.value = '';
		this.commentRegExpInput.value = '';
		this.terminateInput.checked = true;
		this.tailModeInput.disabled = true;
		this.tailModeInput.checked = false;
		this.stringInComb.selectedIndex = 0;
	}, 
	
	populateFieldsForAdd: function(node){
		
	}
});