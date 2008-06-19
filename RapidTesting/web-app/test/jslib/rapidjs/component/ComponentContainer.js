YAHOO.rapidjs.component.ComponentContainer = function(container, config){
	this.id = config.id;
	this.container = getEl(container).dom;
	this.config = config;
	this.linkedComponents = {};
	this.url = config.url;
	this.title = config.title;
	this.dynamicTitle = config.dynamicTitle;
	this.titlePrefix = config.titlePrefix;
	this.titleSuffixAttribute = config.titleSuffixAttribute;
	this.isLinked = false;
	this.events = {
		'contextmenuclicked': new YAHOO.util.CustomEvent('contextmenuclicked'),
		'loadstatechanged' :new YAHOO.util.CustomEvent('loadstatechanged'),
	    'erroroccurred' : new YAHOO.util.CustomEvent('erroroccurred')
	};
	YAHOO.rapidjs.Components[this.id] = this;
};

YAHOO.rapidjs.component.ComponentContainer.prototype = 
{
	addLinkedComponent: function(component, parameters, dynamicParameters, dynamicTitleAtt)
	{
		if(this.linkedComponents[component.id] == null)
		{
			YAHOO.rapidjs.Links[YAHOO.rapidjs.Links.length] = {from:this,to:component};
			this.linkedComponents[component.id] = [component, parameters, dynamicParameters, dynamicTitleAtt];	
			component.setLinked(true);
		}
		
	},
	removeLinkedComponent: function(component)
	{
		delete this.linkedComponents[component.id];	
	},
	
	sendOutputs: function(contentNode)
	{
		for(var componentId in  this.linkedComponents) {
			var component = this.linkedComponents[componentId][0];
			var fparams = this.linkedComponents[componentId][1];
			var dparams = this.linkedComponents[componentId][2];
			var dynamicTitleAtt = this.linkedComponents[componentId][3];
			var params = {};
			for(var fParam in fparams) {
				params[fParam] = fparams[fParam];
			}
			for(var dParam in dparams) {
				params[dParam] = contentNode.getAttribute(dparams[dParam]);
			}
			component.setFocusedContent(contentNode, params, dynamicTitleAtt);
		}
	},
	
	setFocusedContent: function(focusedContent, parameters, dynamicTitleAtt, dialogCall)
	{
		this.clearData();
		if(!dialogCall && this.dynamicTitle == true){
			var title = this.titlePrefix;
			var titleAtt = focusedContent.getAttribute(dynamicTitleAtt);
			title = title + " " + titleAtt;
			this.panel.setTitle(title)
		}
		
	},
	setContextMenuContent: function(focusedContent, parameters, dynamicTitleAtt, dialogCall)
	{
		this.clearData();
		if(!dialogCall && this.dynamicTitle == true){
			var title = this.titlePrefix;
			var titleAtt = focusedContent[dynamicTitleAtt];
			title = title + " " + titleAtt;
			this.panel.setTitle(title)
		}
	}, 
	
	clearData: function(){
	}, 
	
	handleContextMenu : function(event, node){
		this.events['contextmenuclicked'].fireDirect(this, event, node, this.id);
	}, 
	
	subscribeToPanel: function(){
		this.panel.events['visible'].subscribe(this.handleVisible, this, true);
		this.panel.events['unvisible'].subscribe(this.handleUnvisible, this, true);
	}, 
	
	handleVisible : function(){
	}, 
	
	handleUnvisible: function(){
	}, 
	
	setLinked : function(isLinked){
		this.isLinked = isLinked;
	}, 
	saveConfiguration: function(confStrToSave)
	{
		var callback = {
			success: this.configSaveSuccess,
			failure: this.configSaveFailure,
			timeout: 30000,
			scope: this
		};
		
		var url = "/RapidManager/Client/saveComponentConfiguration?ComponentId=" + this.id + "&ConfigurationString=";
		url += confStrToSave;
		YAHOO.util.Connect.asyncRequest('GET',url , callback, null);
	},
	
	configSaveSuccess: function(o){
		if(o.responseText.indexOf('Successful') > -1)
		{
			this.events["erroroccurred"].fireDirect(this, false, '');
		}
		else
		{
			this.component.events["erroroccurred"].fireDirect(this, true, "Configuration change is applied. However, it can not be saved to the server for some reason. Response from the server was: " + o.responseText);
		}
		
	},
	
	configSaveFailure: function(o){
		this.events["erroroccurred"].fireDirect(this, true, "Configuration change is applied. However, it can not be saved to the server since server failed to handle the request.");
	},
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	//////////////////////////////////// LOAD CONFIGURATION /////////////////////////////////////////////////////
	loadConfiguration: function(){
		/*var callback = {
			success: this.configLoadSuccess,
			failure: this.configLoadFailure,
			scope: this
		};
		var url = "/RapidManager/Client/loadComponentConfiguration?ComponentId=" + this.id;
		YAHOO.util.Connect.asyncRequest('GET',url , callback);        */
	},
	
	configLoadSuccess: function(o){
		/*if(o.responseText.indexOf('Configuration') > -1)
		{
			var configurationNodes = o.responseXML.getElementsByTagName("Configuration");
			if(configurationNodes.length == 0)
			{
				return;
			}
			var attributes = configurationNodes[0].attributes;
			for(var i = 0 ; i < attributes.length ; i++)
			{
				var strToEval = attributes[i].value;
				
				if(strToEval == "")
				{
					continue;
				}
				
				try
				{
					eval('this.' + strToEval);
				}
				catch(e)
				{
					this.events["erroroccurred"].fireDirect(this, true, this.id + ": Error occurred while loading configuration. Saved configuration can not be parsed! Erronous data was: " + strToEval);
				}
			}
			this.events["erroroccurred"].fireDirect(this, false, '');
		}
		else
		{
			this.events["erroroccurred"].fireDirect(this, true, "Configuration can not be loaded.Response from the server was: " + o.responseText);
		}
		this.configurationLoaded();    */
		
	},
	
	configLoadFailure: function(o){
		this.events["erroroccurred"].fireDirect(this, true, "Configuration can not be loaded from the server since the server did not respond.");
		this.configurationLoaded();
	}, 
	
	configurationLoaded: function(){
		
	}
	
};



