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
YAHOO.rapidjs.component.layout.ServerStatusTool = function()
{
	this.state = 'available';
	this.refreshState(this.state);
};

YAHOO.rapidjs.component.layout.ServerStatusTool.prototype = 
{
	render: function(parentElement)
	{
		this.wrapper = document.createElement("td");
		this.wrapper.innerHTML = '<img src="../images/layout/network-offline.png"/>';
		YAHOO.util.Dom.setStyle(this.wrapper, 'display', 'none');
		parentElement.appendChild(this.wrapper);
	},
	
	refreshState: function(isServerAvailable){
		if(isServerAvailable == true && this.state=='unavailable'){
			if(this.wrapper){
				YAHOO.util.Dom.setStyle(this.wrapper, 'display', 'none');
			}
			this.state = 'available';
		}
		else if(isServerAvailable == false && this.state == 'available'){
			if(this.wrapper){
				YAHOO.util.Dom.setStyle(this.wrapper, 'display', '');
			}
			this.state = 'unavailable';
		}
	}
}
