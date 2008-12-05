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
YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.tool');
YAHOO.rapidjs.component.tool.LoadingTool = function(container, component) {
    this.loadingStyle = 'r-tool-loading';
	this.nonloadingStyle = 'r-tool-refresh';
    var config = {tooltip:"Update", className:this.nonloadingStyle};
    YAHOO.rapidjs.component.tool.LoadingTool.superclass.constructor.call(this, container, component,config);
    if(component.events["loadstatechanged"])
	{
		component.events["loadstatechanged"].subscribe(this.refreshState, this, true);
	}
};

YAHOO.lang.extend(YAHOO.rapidjs.component.tool.LoadingTool, YAHOO.rapidjs.component.tool.BasicTool, {
    performAction : function() {
        if (this.component.poll)
        {
            this.component.poll();
        }
    },

    refreshState : function(component, loading) {
        if (loading == true) {
            YAHOO.util.Dom.replaceClass(this.button.inner, this.nonloadingStyle, this.loadingStyle)
        }
        else {
            YAHOO.util.Dom.replaceClass(this.button.inner, this.loadingStyle, this.nonloadingStyle)
        }
    }
});