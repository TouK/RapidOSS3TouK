/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
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