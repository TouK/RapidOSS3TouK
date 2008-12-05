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
YAHOO.rapidjs.component.layout.ErrorTool = function(layout, regionName,component)
{
	YAHOO.rapidjs.component.layout.ErrorTool.superclass.constructor.call(this, layout, regionName,component);
	if(component.events["erroroccurred"])
	{
		component.events["erroroccurred"].subscribe(this.refreshState, this, true);
	}
	this.toolInnerComp.addClass('ri-layout-error');
	YAHOO.util.Dom.setStyle(this.toolInnerComp.dom, 'display', 'none');
	this.toolComp.dom.setAttribute('title', 'Errors');
	this.errorDialog = new YAHOO.rapidjs.component.dialogs.ErrorDialog();
    this.state = 'normal';
};

YAHOO.extendX(YAHOO.rapidjs.component.layout.ErrorTool, YAHOO.rapidjs.component.layout.LayoutTool, {
	
	getToolName: function()
	{
		return 'ErrorTool';
	},
	
	refreshState : function(component, error, errorData){
		if(this.state == 'normal' && error == true){
			YAHOO.util.Dom.setStyle(this.toolInnerComp.dom, 'display', '');
			this.state = 'error';
		}
		else if(this.state == 'error' && error == false){
			YAHOO.util.Dom.setStyle(this.toolInnerComp.dom, 'display', 'none');
			this.state = 'normal';
		}
		if(typeof errorData != 'string'){
			this.setErrorText(YAHOO.rapidjs.Connect.getErrorMessages(errorData));
		}
		else{
			this.setErrorText(errorData);
		}
		
	}, 
	
	performAction: function()
	{
		if(this.state == 'error'){
			if(this.errorDialog.isVisible() != true){
				this.errorDialog.show();
			}
		}
	}, 
	
	setErrorText: function(text){
		this.errorDialog.setErrorText(text);
	}
});