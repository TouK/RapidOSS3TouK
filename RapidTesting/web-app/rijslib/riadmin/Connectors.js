YAHOO.rapidjs.riadmin.Connectors = function(errorDialog){
	var dh = YAHOO.ext.DomHelper;
	var adapterGridConfig = {
		content:[	{header:'Actions',type:"Action", width:'100',actions:[
						{handler:this.handleStart,scope:this,image:'../images/layout/play.png', tooltip:'Start Connector', visible:'data["Status"] != "Running" && data["Status"] != "Initializing"'},
						{handler:void(0),scope:this,image:'../images/layout/inactiveplay.png', tooltip:'', visible:'data["Status"] == "Running" || data["Status"] == "Initializing"'},
						{handler:this.handleStop,scope:this,image:'../images/layout/stop.png', tooltip:'Stop Connector', visible:'data["Status"] == "Running" || data["Status"] == "Initializing"'},
						{handler:void(0),scope:this,image:'../images/layout/inactivestop.png', tooltip:'', visible:'data["Status"] != "Running" && data["Status"] != "Initializing"'},
						{handler:this.handleUpdate,scope:this,image:'../images/layout/pencil.png', tooltip:'Update'},
						{handler:this.handleDelete,scope:this,image:'../images/layout/cross.png', tooltip:'Remove'}]},
					{header:'Name',width:'150',attribute:'Name'}, 
					{header:'Status',width:'100',attribute:'Status'}, 
					{header:'LastUpdate',width:'150',attribute:'LastUpdate'},
					{header:'Location',width:'300',attribute:'PropsFile'},
					{header:'Start Type',width:'75',attribute:'StartType'},
					{header:'Log Level',width:'75',attribute:'LogLevel'}
				], 
				
		 
		url:'/RapidConnector/AdapterManager/getDetailedTopology',
		rootTag:'ManagedObjects', 
		pollInterval:10, 
		id:'adapterGrid', 
		contentPath:'Object', 
		nodeId: 'Name', 
		title:'Configured Connectors', 
		timeout:60
	};
	var cLayoutContainer = dh.append(document.body, {tag:'div'});
	var cLayout = new YAHOO.ext.BorderLayout(cLayoutContainer, {
		south: {minSize: 50,maxSize: 500,initialSize: 150,titlebar: true,collapsible:true, animate:true},
	    center: {titlebar:true}, 
	    west:{initialSize: 200,titlebar: true,collapsible: true,animate: true,
                autoScroll:false,useShim:true,cmargins: {top:0,bottom:2,right:2,left:2}}
	});
	
	cLayout.beginUpdate();
	var consoleContainer = dh.append(document.body, {tag:'div'});
	this.consoleWindow = new YAHOO.rapidjs.component.windows.ConsoleWindow(consoleContainer, {title:'Response Window'});
	cLayout.add('south', this.consoleWindow.panel);
	var buttonContainer = dh.append(document.body, {tag:'div'});
	cLayout.add('west', new YAHOO.rapidjs.component.layout.RapidPanel(buttonContainer, {fitToFrame:true, title:'Actions'}));
	var gridContainer = dh.append(document.body, {tag:'div'});
	this.gridWindow = new YAHOO.rapidjs.component.windows.GridWindow(gridContainer, adapterGridConfig);
    YAHOO.rapidjs.ToolsUtil.createDefaultTools(cLayout, 'center', this.gridWindow);
	cLayout.add('center', this.gridWindow.panel);
    cLayout.endUpdate();
    cLayout.regions['west'].collapsedEl.addClass('riadmin-actioncollapsed');
    
    var newDatabaseReader = new YAHOO.rapidjs.component.ToolbarButton(dh.append(buttonContainer, {tag:'div', cls:'rapid-btnwrp'}), 
    	{text:'New Database Connector', className:'riadmin-conn-newdb', scope:this, click:this.newDatabase});
    var newFileReader = new YAHOO.rapidjs.component.ToolbarButton(dh.append(buttonContainer, {tag:'div', cls:'rapid-btnwrp'}), 
    	{text:'New File Connector', className:'riadmin-conn-newfile', scope:this, click:this.newFile});
    var newNotificationReader = new YAHOO.rapidjs.component.ToolbarButton(dh.append(buttonContainer, {tag:'div', cls:'rapid-btnwrp'}), 
    	{text:'New Notification Connector', className:'riadmin-conn-newntf', scope:this, click:this.newNotification});
    var newTopologyReader = new YAHOO.rapidjs.component.ToolbarButton(dh.append(buttonContainer, {tag:'div', cls:'rapid-btnwrp'}), 
    	{text:'New Topology Connector', className:'riadmin-conn-newtopo', scope:this, click:this.newTopology});
    var newNetcoolReader = new YAHOO.rapidjs.component.ToolbarButton(dh.append(buttonContainer, {tag:'div', cls:'rapid-btnwrp'}), 
    	{text:'New Netcool Connector', className:'riadmin-conn-newNetcool', scope:this, click:this.newNetcool});
    		
    this.panel = new YAHOO.rapidjs.component.layout.NestedLayoutPanel(cLayout, {title: 'Connectors'});
    this.createContextMenu();
	this.gridWindow.events['contextmenuclicked'].subscribe(this.menu.contextMenuClicked, this.menu, true);
	this.fileDlg = new YAHOO.rapidjs.riadmin.FileConnectorDlg({width:430, height:500, title:'New File Connector'}, this.gridWindow, errorDialog);
	this.dbDlg = new YAHOO.rapidjs.riadmin.DBConnectorDlg({width:430, height:480, title:'New Database Connector'}, this.gridWindow, errorDialog);
	this.ntfDlg = new YAHOO.rapidjs.riadmin.NtfConnectorDlg({width:430, height:707, title:'New ICNotification Connector'}, this.gridWindow, errorDialog);
	this.topoDlg = new YAHOO.rapidjs.riadmin.TopoConnectorDlg({width:430, height:530, title:'New ICTopology Connector'}, this.gridWindow, errorDialog);
	this.netcoolDlg = new YAHOO.rapidjs.riadmin.NetcoolConnectorDlg({width:430, height:530, title:'New Netcool Connector'}, this.gridWindow, errorDialog);
	
	new YAHOO.rapidjs.component.layout.HelpTool(cLayout, "center", this.gridWindow, {url:help_ri_admin_connectors_tab});
};

YAHOO.rapidjs.riadmin.Connectors.prototype = {
	
	newDatabase: function(){
		this.dbDlg.dialog.setTitle('New Database Connector');
		this.request('/RapidInsight/ManagedObject/invoke?Script=admin/getDatasources&ReaderType=DatabaseReader', this.addReaderSuccess, [this.dbDlg]);
//		this.dbDlg.show();
	}, 
	newFile: function(){
		this.fileDlg.dialog.setTitle('New File Connector');
		this.fileDlg.show();
	}, 
	newNotification: function(){
		this.ntfDlg.dialog.setTitle('New Smarts Notification Connector');
		this.request('/RapidInsight/ManagedObject/invoke?Script=admin/getDatasources&ReaderType=ICNotificationReader', this.addReaderSuccess, [this.ntfDlg]);
//		this.ntfDlg.show();
	}, 
	newTopology: function(){
		this.request('/RapidInsight/ManagedObject/invoke?Script=admin/getDatasources&ReaderType=ICTopologyReader', this.addReaderSuccess, [this.topoDlg]);
		this.topoDlg.dialog.setTitle('New Smarts Topology Connector');
//		this.topoDlg.show();
	}, 
	
	newNetcool: function(){
		this.request('/RapidInsight/ManagedObject/invoke?Script=admin/getDatasources&ReaderType=NetcoolEventReader', this.addReaderSuccess, [this.netcoolDlg]);
		this.netcoolDlg.dialog.setTitle('New Netcool Connector');
		//this.netcoolDlg.show();
	}, 
	updateConnector: function(menuItem, data, node){
		this.handleUpdate(node);
	},
	
	handleUpdate: function(node){
		var adapterName = node.getAttribute('Name');
		this.request('/RapidInsight/ManagedObject/invoke?Script=admin/getConnectorDetails&AdapterName=' + adapterName, this.updateDataSuccess);
	},
	
	addReaderSuccess: function(response){
		if(this.checkResponse(response))
		{
			response.argument[0].show(false, response.responseXML);
		}
	}, 
	
	updateDataSuccess: function(response)
	{
		if(this.checkResponse(response))
		{
			var xml = response.responseXML;
			var adapterDetailsNode = xml.getElementsByTagName('AdapterDetails')[0];
			var readerType = adapterDetailsNode.getAttribute('ReaderType');
			if(readerType == 'ICTopologyReader')
			{
				this.topoDlg.dialog.setTitle('Update Topology Connector');
				this.topoDlg.show(true, adapterDetailsNode);
			}
			else if(readerType == 'ICNotificationReader')
			{
				this.ntfDlg.dialog.setTitle('Update Notification Connector');
				this.ntfDlg.show(true, adapterDetailsNode);
			}
			else if(readerType == 'DatabaseReader')
			{
				this.dbDlg.dialog.setTitle('Update Database Connector');
				this.dbDlg.show(true, adapterDetailsNode);
			}
			else if(readerType == 'FileReader')
			{
				this.fileDlg.dialog.setTitle('Update File Connector');
				this.fileDlg.show(true, adapterDetailsNode);
			}
			else if(readerType == 'NetcoolEventReader')
			{
				this.netcoolDlg.dialog.setTitle('Update Netcool Connector');
				this.netcoolDlg.show(true, adapterDetailsNode);
			}
		}
	},
	
	removeConnector: function(menuItem, data, node){
		this.handleDelete(node);
	},
	
	handleDelete: function(node){
		var adapterName = node.getAttribute('Name');
		if(confirm('Delete ' + adapterName + '?'))
		{
			this.request('/RapidConnector/Adapter/delete?AdapterName=' + adapterName, this.processSuccess);	
		}
	},
	
	handleStart: function(node){
		var st = node.getAttribute('Status');
		if(st != 'Running' && st!='Initializing'){
			var adapterName = node.getAttribute('Name');
			this.request('/RapidConnector/Adapter/start?AdapterName=' + adapterName, this.processSuccess);
		}
	}, 
	handleStop: function(node){
		var st = node.getAttribute('Status');
		if(st == 'Running' || st=='Initializing'){
			var adapterName = node.getAttribute('Name');
			this.request('/RapidConnector/Adapter/stop?AdapterName=' + adapterName, this.processSuccess);
		}
	}, 
	
	startAdapter: function(menuItem, data, node){
		this.handleStart(node);
	}, 
	stopAdapter: function(menuItem, data, node){
		this.handleStop(node);
	}, 
	setLogLevel: function(menuItem, data, node){
		var adapterName = data['Name'];
		this.request('/RapidConnector/Adapter/setLogLevel?AdapterName=' + adapterName + '&LogLevel=' + menuItem.value, this.processSuccess);
	},  
	reloadConf: function(){
		this.request('/RapidConnector/AdapterManager/reloadConf', this.processSuccess);
	}, 
	
	evaluateReloadLookup: function(menuItem, data, node){
		var transformers = node.getElementsByTagName('LookupTransformers');
		if(transformers.length > 0){
			var transformerNames = transformers[0].getElementsByTagName('Name');
			if(transformerNames.length > 0){
				return true;
			}
			else{
				return false;
			}
		}
		return false;
	}, 
	
	createContextMenu: function(){
		this.menu = new YAHOO.rapidjs.component.menu.ContextMenu();
		var MenuAction = YAHOO.rapidjs.component.menu.MenuAction;
		this.menu.addMenuItem('Start Connector', new MenuAction(this.startAdapter, this), 'data["Status"] != "Running" && data["Status"] != "Initializing"');
		this.menu.addMenuItem('Stop Connector', new MenuAction(this.stopAdapter, this), 'data["Status"] == "Running" || data["Status"] == "Initializing"');
		var logLevelItem = this.menu.addMenuItem('Set Log Level', null, true);
		var setLogLevelAction = new MenuAction(this.setLogLevel, this);
		logLevelItem.addSubMenuItem('ALL', setLogLevelAction, true);
		logLevelItem.addSubMenuItem('DEBUG', setLogLevelAction, true);
		logLevelItem.addSubMenuItem('INFO', setLogLevelAction, true);
		logLevelItem.addSubMenuItem('WARN', setLogLevelAction, true);
		logLevelItem.addSubMenuItem('ERROR', setLogLevelAction, true);
		logLevelItem.addSubMenuItem('FATAL', setLogLevelAction, true);
		logLevelItem.addSubMenuItem('OFF', setLogLevelAction, true);
		this.menu.addSplitter();
		this.menu.addMenuItem('Update', new MenuAction(this.updateConnector, this), 'data["Status"] != "Running" && data["Status"] != "Initializing"');
		this.menu.addMenuItem('Remove', new MenuAction(this.removeConnector, this), 'data["Status"] != "Running" && data["Status"] != "Initializing"');
	}, 
	
	request : function(url, successDelefate, args){
		var callback = {
			success: successDelefate, 
			failure: this.processFailure,
			timeout: 60000,
			scope: this, 
			argument: args			
		};
		YAHOO.util.Connect.asyncRequest('GET',url , callback);
	},
	
	checkResponse: function(response)
	{
		YAHOO.rapidjs.ServerStatus.refreshState(true);
		this.gridWindow.events["erroroccurred"].fireDirect(this, false, '');
		var data = response.responseXML;
		var successful = data.getElementsByTagName('Successful');
		if(successful && successful.length > 0){
			var text = successful[0].firstChild.nodeValue;
			this.consoleWindow.appendText(text);
			return true;
		}
		else{
			if(YAHOO.rapidjs.Connect.containsError(response) == true){
				this.consoleWindow.appendText(YAHOO.rapidjs.Connect.getErrorMessages(response.responseXML));
				return null;
			}
			else{
				if(YAHOO.rapidjs.Connect.isAuthenticated(response) == false){
					window.location = "login.html?page=" + window.location.pathname;
					return null;
				}
				else
				{
					//response is some data
					return true;
				}
			}
		}	
	},
	
	processSuccess: function(response){
		if(this.checkResponse(response))
		{
			this.gridWindow.poll();	
		}
	},
	
	processFailure: function(response){
		var st = response.status;
		if(st == -1){
			this.gridWindow.events["erroroccurred"].fireDirect(this, true, 'Request received a timeout');
		}
		else if(st == 404){
			this.gridWindow.events["erroroccurred"].fireDirect(this, true, 'URL does not exist');
		}
		else if(st == 0){
			YAHOO.rapidjs.ServerStatus.refreshState(false);
			this.gridWindow.events["erroroccurred"].fireDirect(this, true, 'Server is not available');
		}
	}
};
