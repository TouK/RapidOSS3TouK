YAHOO.rapidjs.riadmin.TopoConnectorDlg = function(config, gridWindow, errorDialog){
	YAHOO.rapidjs.riadmin.TopoConnectorDlg.superclass.constructor.call(this, config, gridWindow, errorDialog);
};

YAHOO.extendX(YAHOO.rapidjs.riadmin.TopoConnectorDlg, YAHOO.rapidjs.riadmin.ConnectorDialog, {
	
	getExtraPostData: function(){
		var connectionName = this.connNameComb.options[this.connNameComb.selectedIndex].value;
		var mode = this.modeComb.options[this.modeComb.selectedIndex].value;
		var pollingInterval = this.pollIntervalInput.value;
		var topoClassesArr = new Array();
		var i = 0;
		var classCrudRows = this.classCrud.getRows();
		for(var classCrudRow in classCrudRows)
		{
			if(i > 0)
			{
				topoClassesArr[topoClassesArr.length] = ';;';
			}
			var node = classCrudRows[classCrudRow].node;
			topoClassesArr[topoClassesArr.length] = node.getAttribute('TopologyClass') + "::" + node.getAttribute('MonitoredAttributes');
			i++;
		}
		var topoClassesStr = topoClassesArr.join('');
		var postData = new Array();
		postData[postData.length] = 'InChargeConnectionName=' + connectionName;
		postData[postData.length] = 'Mode=' + mode;
		postData[postData.length] = 'PollingInterval=' + pollingInterval;
		postData[postData.length] = 'TopologyClasses=' + topoClassesStr;
		return postData.join('&');
	},
	
	getConnectorAddUrl: function()
	{
		return '/RapidConnector/AdapterManager/addTopologyConnector';
	},
	
	getConnectorUpdateUrl: function()
	{
		return '/RapidConnector/AdapterManager/updateTopologyConnector';
	},
	
	populateExtraInputsForUpdate: function(adapterDetailsNode)
	{
		var connName = adapterDetailsNode.getElementsByTagName('InChargeConnectionName')[0].getAttribute('Value');
		var datasources = adapterDetailsNode.getElementsByTagName('Datasource');
		SelectUtils.addOption(this.connNameComb, '', '');
		for(var index=0; index<datasources.length; index++) {
			var datasource = datasources[index].getAttribute('Name');
			SelectUtils.addOption(this.connNameComb, datasource, datasource);
		}
		SelectUtils.selectTheValue(this.connNameComb, connName, 0);
		var mode = adapterDetailsNode.getElementsByTagName('Mode')[0].getAttribute('Value');
		SelectUtils.selectTheValue(this.modeComb, mode, 0);
		if(mode == "Polling")
		{
			this.pollIntervalInput.value = adapterDetailsNode.getElementsByTagName('PollingInterval')[0].getAttribute('Value');			
		}
		this.modeChanged();
		
		var topologyClasses = adapterDetailsNode.getElementsByTagName('TopologyClass');
		for(var i = 0 ; i < topologyClasses.length ; i++)
		{
			var topologyClass = topologyClasses[i].getAttribute('Name');
			var monitoredAttributes = topologyClasses[i].getAttribute('MonitoredAttributes');
			var monitoredClassNode = new YAHOO.rapidjs.data.RapidXmlNode(null, null, 1, null);
			monitoredClassNode.attributes['TopologyClass'] = topologyClass;
			monitoredClassNode.attributes['MonitoredAttributes'] = monitoredAttributes;
			this.classCrud.addItem(topologyClass, monitoredClassNode);
		}
	},
	
	clearExtraFormInputs: function()
	{
		SelectUtils.clear(this.connNameComb);
		this.modeComb.selectedIndex = 0;
		this.pollIntervalInput.value = '';
		this.classCrud.removeAll();
		this.classDialog.clear();
		this.modeChanged();
	},
	
	handleTopoClassUpdateClicked: function(crudRow){
		var className = crudRow.node.getAttribute('TopologyClass');
		var monAtts = crudRow.node.getAttribute('MonitoredAttributes');
		this.classDialog.show(className, monAtts, crudRow);
	},
	
	handleTopoClassDeleteClicked:function(crudRow){
		if(confirm('Delete ' + crudRow.name + '?'))
		{
			this.classCrud.removeItem(crudRow);
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
	
	handleAddClass: function(){
		this.classDialog.show('','');
	},
	
	renderConnector: function(){
		var dh = YAHOO.ext.DomHelper;
		this.connwrap = dh.append(this.wrap.dom, {tag:'div', cls:'rc-topo-detailwrp'});
		var detailView = dh.append(this.connwrap, {tag:'div', 
					html:'<table><tbody><tr><td><div class="riadmin-formtext">InChargeConnectionName:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
						'<tr><td><div class="riadmin-formtext">Mode:</div></td><td><div class="riadmin-inputwrp"/></td></tr><tbody><table>'});
						
		var inputWrappers = detailView.getElementsByTagName('div');
		this.connNameComb = dh.append(inputWrappers[1], {tag:'select', cls:'riadmin-combobox'});
		this.modeComb = dh.append(inputWrappers[3], {tag:'select', cls:'riadmin-combobox', html:'<option value="Listening">Listening</option><option value="Polling">Polling</option>'});
		
		this.pollingModeView = dh.append(this.connwrap, {tag:'div',
					html:'<table><tbody><tr><td><div class="riadmin-formtext">PollingInterval:</div></td><td><div class="riadmin-inputwrp"/></td></tr><tbody><table>'});
		
		var pollModeWrappers = this.pollingModeView.getElementsByTagName('div');
		this.pollIntervalInput = dh.append(pollModeWrappers[1], {tag:'input', cls:'riadmin-input'});
		YAHOO.util.Dom.setStyle(this.pollingModeView, 'display', 'none');
		
		var classView = dh.append(this.connwrap, {tag:'div', cls:'rc-topo-classwrp'});
		var buttonWrapper = dh.append(classView, {tag:'div', cls:'rc-topo-addclswrp'});
		var topologyClasses = dh.append(classView, {tag:'div', cls:'rc-topo-clsgridwrp'});
		this.addClassButton = new YAHOO.ext.Button(buttonWrapper, {handler: this.handleAddClass, scope: this,text: 'Add Topology Class',minWidth: 150});
		this.classCrud = new YAHOO.rapidjs.component.crud.Crud(topologyClasses, {headerLabel:'Topology Class'});
		
		this.classCrud.events['rowupdated'].subscribe(this.handleTopoClassUpdateClicked, this, true);
		this.classCrud.events['rowdeleted'].subscribe(this.handleTopoClassDeleteClicked, this, true);
		YAHOO.util.Event.addListener(this.modeComb, 'change', this.modeChanged, this, true);
		this.dialog.addTabListener(this.addClassButton.el.dom, this.saveButton.el.dom);
		
		this.classDialog = new YAHOO.rapidjs.riadmin.TopologyClassDialog(this);
		this.dialog.addHelp({url:help_ri_admin_conn_topo_dlg});
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

YAHOO.rapidjs.riadmin.TopologyClassDialog = function(topoConnectorDialog){
	this.topoConnectorDialog = topoConnectorDialog;
	this.crudRowBeingUpdated = null;
	var dh = YAHOO.ext.DomHelper;
	var config = {
		modal: false,
	    width:350,
	    height:200,
	    shadow:true,
	    minWidth:100,
	    minHeight:100,
	    syncHeightBeforeShow: true,
	    resizable: false,
	    title: 'Add Topology Class', 
	    center:{
	        autoScroll:true
    }};
    this.dialog = new YAHOO.ext.LayoutDialog(dh.append(document.body, {tag:'div'}), config);
	var cancelButton = this.dialog.addButton('Cancel', this.hide, this);
	this.saveButton = this.dialog.addButton('OK', this.handleSave, this);
	this.dialog.setDefaultButton(this.saveButton);
	var layout = this.dialog.layout;
	layout.beginUpdate();
	var container = dh.append(document.body, {tag:'div'});
	layout.add('center', new YAHOO.rapidjs.component.layout.RapidPanel(container));
	layout.endUpdate();
	
	var detailView = dh.append(container, {tag:'div', cls:'rc-topo-dlgwrp', 
			html:'<table><tbody><tr><td><div class="rc-topo-dlgformtext">Topology Class:</div></td><td><div class="riadmin-inputwrp"/></td></tr>' +
				'<tr><td><div class="rc-topo-dlgformtext">Monitored Attributes:</div></td><td><div class="riadmin-inputwrp"/></td></tr><tbody><table>'});
				
	var inputWrappers = detailView.getElementsByTagName('div');
	this.classNameInput = dh.append(inputWrappers[1], {tag:'input', cls:'riadmin-input'});
	this.monAttInput = dh.append(inputWrappers[3], {tag:'textarea'});
	this.monAttInput.rows = 4;
	this.monAttInput.cols = 31;
	this.dialog.addHelp({url:help_ri_admin_conn_topo_dlg_add_topology_class});
};

YAHOO.rapidjs.riadmin.TopologyClassDialog.prototype = {
	handleSave: function(){
		var className = this.classNameInput.value;
		if(className != '')
		{
			var monitoredAttributes = this.monAttInput.value;
			monitoredAttributes = monitoredAttributes.replace(/,/g, ' ');
			monitoredAttributes= monitoredAttributes.replace(/\n/g, ' ');
			var monitoredClassNode = new YAHOO.rapidjs.data.RapidXmlNode(null, null, 1, null);
			monitoredClassNode.attributes['TopologyClass'] = className;
			monitoredClassNode.attributes['MonitoredAttributes'] = monitoredAttributes;
			if(this.crudRowBeingUpdated)//update
			{
				this.crudRowBeingUpdated.node = monitoredClassNode;
			}
			else
			{
				this.topoConnectorDialog.classCrud.addItem(className, monitoredClassNode);
			}
			this.hide();	
		}
	}, 
	
	hide: function(){
		this.dialog.hide();
	}, 
	
	show: function(className, monitoredAttributes, crudRow)
	{
		if(crudRow)//update
		{
			this.dialog.setTitle("Update Topology Class");
			this.classNameInput.disabled = true;
			this.crudRowBeingUpdated = crudRow;
		}
		else
		{
			this.dialog.setTitle("Add Topology Class");
			this.classNameInput.disabled = false;
			this.crudRowBeingUpdated = null;
		}
		this.classNameInput.value = className;
		this.monAttInput.value = monitoredAttributes;
		this.dialog.show();
	},
	
	clear: function()
	{
		this.classNameInput.value = '';
		this.monAttInput.value = '';
	}
};