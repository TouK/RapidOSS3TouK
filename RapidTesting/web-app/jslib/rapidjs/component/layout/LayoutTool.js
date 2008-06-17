YAHOO.rapidjs.component.layout.LayoutTool = function(layout, regionName, component, config)
{
	this.config = config;
	this.layout = layout;
	this.component = component;
	this.region = layout.getRegion(regionName);
	this.toolComp = this.region.createTool(this.region.tools.dom, "");
	this.toolInnerComp = this.toolComp.getChildrenByTagName("div")[0];
	this.toolComp.on('click', this.performAction, this, true);
	if(this.component)
	{
		this.id = this.component.id + "_" + this.getToolName();
	}
	else
	{
		this.id = this.getToolName();
	}
};

YAHOO.rapidjs.component.layout.LayoutTool.prototype =
{
	getToolName: function()
	{
		//Abstract method that returns the name of the tool as a string for unique id generation. MUST be overriden!
		alert("getToolName function should be overrided by extenders of LayoutTool class!");
	},
	
	performAction: function()
	{
		//Abstract method which is called when the icon of the tool is clicked.
		//If the tool should do something when the icon is clicked, the extender should override this method.
	},
	
	configurationLoaded: function()
	{
		//Abstract method which is called when the configuration is loaded.
		//If the tool should do something when the configuration loaded, the extender should override this method.
	},
	
	////////////////////////////////////// SAVE CONFIGURATION ///////////////////////////////////////////////////
	saveConfiguration: function(confStrToSave)
	{
		
	},
	
	processSaveSuccess: function(o){
		if(this.component)
		{
			if(o.responseText.indexOf('Successful') > -1)
			{
				this.component.events["erroroccurred"].fireDirect(this, false, '');
			}
			else
			{
				this.component.events["erroroccurred"].fireDirect(this, true, "Configuration change is applied. However, it can not be saved to the server for some reason. Response from the server was: " + o.responseText);
			}
		}
	},
	
	processSaveFailure: function(o){
		if(this.component)
		{
			this.component.events["erroroccurred"].fireDirect(this, true, "Configuration change is applied. However, it can not be saved to the server since server failed to handle the request.");
		}
	},
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	//////////////////////////////////// LOAD CONFIGURATION /////////////////////////////////////////////////////
	loadConfiguration: function(){
		
	},
	
	processLoadSuccess: function(o){
		if(this.component)
		{
			if(o.responseText.indexOf('Configuration') > -1)
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
						with(this)
						{
							eval(strToEval);
						}
					}
					catch(e)
					{
						this.component.events["erroroccurred"].fireDirect(this, true, this.id + ": Error occurred while loading configuration. Saved configuration can not be parsed! Erronous data was: " + strToEval);
					}
				}
				this.component.events["erroroccurred"].fireDirect(this, false, '');
			}
			else
			{
				this.component.events["erroroccurred"].fireDirect(this, true, "Configuration can not be loaded.Response from the server was: " + o.responseText);
			}
			if(this.component.poll && this.component.pollInterval > 0)
			{
				this.component.poll();
			}
			this.configurationLoaded();
		}
	},
	
	processLoadFailure: function(o){
		if(this.component)
		{
			this.component.events["erroroccurred"].fireDirect(this, true, "Configuration can not be loaded from the server since the server did not respond.");
			this.configurationLoaded();
		}
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
};