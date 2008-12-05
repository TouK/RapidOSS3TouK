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
YAHOO.rapidjs.component.list.BasicList = function(container, config){
	this.container = container;
	this.config = config;
	this.events = {
		'selectionchanged' : new YAHOO.util.CustomEvent('selectionchanged'), 
		'contextmenu' : new YAHOO.util.CustomEvent('contextmenu')
	};
	this.render();
};

YAHOO.rapidjs.component.list.BasicList.prototype = {
	render: function(){
		
	},
	
	loadData : function(data){
		if(!this.rootListItem){
			this.rootListItem = this.constructRootListItem(data);
			this.rootListItem.events['listitemclicked'].subscribe(this.listItemClicked, this, true);
			this.rootListItem.events['contextmenu'].subscribe(this.contextMenuClicked, this, true);	
		}
	}, 
	
	constructRootListItem : function(data){
		
	}, 
	
	listItemClicked : function(event, listItem){
		
	}, 
	
	contextMenuClicked: function(event, listItem){
		
	}
};