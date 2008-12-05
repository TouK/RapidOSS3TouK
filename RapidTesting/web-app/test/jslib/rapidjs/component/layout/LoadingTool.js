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
YAHOO.rapidjs.component.layout.LoadingTool = function(layout, regionName,component)
{
	YAHOO.rapidjs.component.layout.LoadingTool.superclass.constructor.call(this, layout, regionName,component);
	if(component.events["loadstatechanged"])
	{
		component.events["loadstatechanged"].subscribe(this.refreshState, this, true);
	}
	this.toolComp.dom.setAttribute('title', 'Update');
	this.loadingStyle = 'ri-layout-loading';
	this.nonloadingStyle = 'layout-refreshtool';
	this.toolInnerComp.addClass(this.nonloadingStyle);
	//YAHOO.util.Event.purgeElement(this.toolComp.dom, false, 'mouseover');
};

YAHOO.extendX(YAHOO.rapidjs.component.layout.LoadingTool, YAHOO.rapidjs.component.layout.LayoutTool, {
	getToolName: function()
	{
		return 'LoadingTool';
	},
	
	refreshState : function(component, loading){
		if(loading == true){
			this.toolInnerComp.replaceClass(this.nonloadingStyle, this.loadingStyle);
		}	
		else{
			this.toolInnerComp.replaceClass(this.loadingStyle, this.nonloadingStyle);
		}
	},
	
	performAction: function()
	{
		if(this.component.poll)
		{
			this.component.poll();
		}
	}
});