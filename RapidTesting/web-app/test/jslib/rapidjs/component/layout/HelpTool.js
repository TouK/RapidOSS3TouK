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
YAHOO.rapidjs.component.layout.HelpTool = function(layout, regionName, component, config)
{
	YAHOO.rapidjs.component.layout.HelpTool.superclass.constructor.call(this, layout, regionName,component, config);
	
	this.toolComp.dom.setAttribute('title', this.getToolName());
	this.toolInnerComp.addClass("layout-helptool");
	
	var dialogDiv = document.createElement("div");
	document.body.appendChild(dialogDiv);
	this.helpWindow = new YAHOO.rapidjs.component.windows.HtmlWindow(dialogDiv, config);
	this.popupWindow = new YAHOO.rapidjs.component.PopUpWindow(this.helpWindow, {modal:false,shadow:true,width:600,height:450,minWidth:600,minHeight:450,title:this.getToolName()});
};

YAHOO.extendX(YAHOO.rapidjs.component.layout.HelpTool, YAHOO.rapidjs.component.layout.LayoutTool, {
	
	getToolName: function()
	{
		return 'Help';
	},
	
	performAction: function()
	{
		this.popupWindow.show();
	},
	render: function()
	{
		var wrapper = document.createElement("td");
		wrapper.innerHTML = '<a href="javascript:logout();" class="whitelink">Logout</a>';
		this.region.getTabs().toolsArea.appendChild(wrapper);
	}
});