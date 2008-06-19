YAHOO.rapidjs.component.dialogs.ComponentConfigurationDialog = function(id, configurationTool, config){
	config["center"]={
        tabPosition: 'top',
        alwaysShowTabs: true
    };
    config["autoScroll"] = true;
    config["autoTabs"] = true;
    config["modal"] = false;
    config["minWidth"] = 400;
    config["title"] = "Window Configuration";
    config["minHeight"] = 300;
	YAHOO.rapidjs.component.dialogs.ComponentConfigurationDialog.superclass.constructor.call(this,id, config);
	this.configurationTool = configurationTool;
	this.addButton('Cancel', this.hide, this);
	this.addButton('Save', this.saveAndHide, this);
	this.subPanels = [];
	this.addListener("show", this.refreshTabStates, this, true);
};

YAHOO.extendX(YAHOO.rapidjs.component.dialogs.ComponentConfigurationDialog, YAHOO.ext.LayoutDialog, {
	addPanel: function(panel)
	{
	    this.beginUpdate();
	    this.layout.add('center', panel);
	    this.subPanels[this.subPanels.length] = panel;
	    this.endUpdate();
	},
	saveAndHide: function()
	{
		for(var index=0; index<this.subPanels.length; index++) {
			if(this.subPanels[index] && this.subPanels[index].save)
			{
				this.subPanels[index].save();
			}
		}
		this.hide();
		
		this.configurationTool.save();
	},
	
	refreshTabStates: function()
	{
		for(var index=0; index<this.subPanels.length; index++) {
			if(this.subPanels[index] && this.subPanels[index].refresh)
			{
				this.subPanels[index].refresh();
			}
		}
	},
	
	show: function()
	{
		this.refreshTabStates();
		YAHOO.rapidjs.component.dialogs.ComponentConfigurationDialog.superclass.show.call(this);
	}
});


YAHOO.rapidjs.component.dialogs.PollingConfigurationPanel = function(component, pollingTool){
	this.component = component;
	this.pollingTool = pollingTool;
	var dh = YAHOO.ext.DomHelper;
	var tmp = dh.append(document.body, {tag:'div'});
	YAHOO.rapidjs.component.dialogs.PollingConfigurationPanel.superclass.constructor.call(this,tmp, {title:"AutoRefresh",fitToFrame:true,autoCreate:true,autoScroll:true});
	this.render();
};
YAHOO.extendX(YAHOO.rapidjs.component.dialogs.PollingConfigurationPanel, YAHOO.rapidjs.component.layout.RapidPanel, {
	render: function()
	{
		var dh = YAHOO.ext.DomHelper;
		if(this.component.getPollingInterval)
		{
			var currentPollingInterval = this.component.getPollingInterval();
		
			this.checkLabelDiv = dh.append(this.el.dom, {tag:'div', cls:'ri-comp-conf-formelement', html:'Enable AutoRefresh? '});
			this.pollCheck = dh.append(this.checkLabelDiv, {tag:'input', type:'checkbox'});
			YAHOO.util.Event.addListener(this.pollCheck, 'click', this.handleCheck, this,true);
			
			if (!currentPollingInterval || currentPollingInterval <= 0) 
			{
				this.pollCheck.checked = false;
			} 
			else 
			{
				this.pollCheck.checked = true;
			}
			
			this.inputLabelDiv = dh.append(this.el.dom, {tag:'div', cls:'ri-comp-conf-formelement', html:'Refresh rate in seconds:'});
			this.pollingIntervalInput = dh.append(this.inputLabelDiv, {tag:'input', type:'text', cls:'ri-comp-conf-input'});
			this.refresh();	
		}
	},
	save: function()
	{
		if(this.component.getPollingInterval)
		{
			if (this.pollCheck.checked) 
			{
				if(this.component.setPollingInterval)
				{
					if(this.pollingIntervalInput.value * 1 < 0)
					{
						this.pollingIntervalInput.value = 0;
					}
					this.component.setPollingInterval(this.pollingIntervalInput.value * 1);
				}
			} 
			else 
			{
				this.component.setPollingInterval(0);
			}
			this.pollingTool.refreshState();	
		}
		
	},
	refresh: function()
	{
		if(this.component.getPollingInterval)
		{
			var currentPollingInterval = this.component.getPollingInterval();
			if (!currentPollingInterval || currentPollingInterval <= 0) 
			{
				this.pollCheck.checked = false;
			} 
			else 
			{
				this.pollCheck.checked = true;
			}
			this.handleCheck();
			this.pollingIntervalInput.value = this.component.getPollingInterval();	
		}
		
	},
	handleCheck: function()
	{
		if (this.pollCheck.checked) 
		{
			this.pollingIntervalInput.style.display = "";
			this.inputLabelDiv.style.display = "";
		}
		else
		{
			this.inputLabelDiv.style.display = "none";	
			this.pollingIntervalInput.style.display = "none";			
		}
	}
});



