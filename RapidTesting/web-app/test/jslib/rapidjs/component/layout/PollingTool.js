YAHOO.rapidjs.component.layout.PollingTool = function(layout, regionName,component)
{
	YAHOO.rapidjs.component.layout.PollingTool.superclass.constructor.call(this, layout, regionName,component);
	
	this.pollingStyle = 'ri-layout-polling';
	this.nonpollingStyle = 'ri-layout-nonpolling';
	
	var dialogDiv = document.createElement("div");
	document.body.appendChild(dialogDiv);
	
	this.confDialog = new YAHOO.rapidjs.component.dialogs.ComponentConfigurationDialog(dialogDiv, this,{width:400,height:300});
	this.pollConfPanel =  new YAHOO.rapidjs.component.dialogs.PollingConfigurationPanel(this.component, this);
	this.confDialog.addPanel(this.pollConfPanel);
	this.loadConfiguration();
};

YAHOO.extendX(YAHOO.rapidjs.component.layout.PollingTool, YAHOO.rapidjs.component.layout.LayoutTool, {
	
	getToolName: function()
	{
		return 'PollingTool';
	},
	
	performAction: function()
	{
		this.confDialog.show();
	},
	
	configurationLoaded: function()
	{
		this.refreshState();
	},
	
	refreshState: function()
	{
		if(this.component.getPollingInterval && this.component.getPollingInterval() > 0)
		{
			if(this.toolInnerComp.hasClass(this.nonpollingStyle) == true)
			{
				this.toolInnerComp.replaceClass(this.nonpollingStyle, this.pollingStyle);
			}
			else
			{
				this.toolInnerComp.addClass(this.pollingStyle);
			}
			this.toolComp.dom.setAttribute('title', 'AutoRefresh enabled - Click to configure auto-refresh');
		}
		else
		{
			if(this.toolInnerComp.hasClass(this.pollingStyle) == true)
			{
				this.toolInnerComp.replaceClass(this.pollingStyle, this.nonpollingStyle);
			}
			else
			{
				this.toolInnerComp.addClass(this.nonpollingStyle);
			}
			this.toolComp.dom.setAttribute('title', 'AutoRefresh disabled - Click to configure auto-refresh');
		}
	},
	
	save: function()
	{
		var confStr = "component.pollInterval = " + this.component.pollInterval + ";";
		YAHOO.rapidjs.component.layout.PollingTool.superclass.saveConfiguration.call(this, confStr);
		if(this.component.poll)
		{
			this.component.poll();
		}
	}
});