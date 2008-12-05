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
YAHOO.rapidjs.component.list.ListItem= function(xmlData, container, parentItem, config,indexAtt, appendUp){
	YAHOO.rapidjs.component.list.ListItem.superclass.constructor.call(this, xmlData);
	this.container = container;
	this.config = config;
	this.parentItem = parentItem;
	this.index = xmlData.getAttribute(indexAtt);
	this.appendUp = appendUp;
	this.events = {
		'listitemclicked': new YAHOO.util.CustomEvent('listitemclicked'), 
		'contextmenu': new YAHOO.util.CustomEvent('contextmenu')
	};
	this.render();
};

YAHOO.extendX(YAHOO.rapidjs.component.list.ListItem, YAHOO.rapidjs.component.RapidElement, {
	render: function(){
	},
	
	fireClick : function(event){
		this.events['listitemclicked'].fireDirect(event, this);	
	},
	fireContextMenu : function(event){
		this.events['contextmenu'].fireDirect(event, this);	
	}, 
	
	dataDestroyed: function(){
		this.destroy();	
	},
	
	destroy: function(){
		delete this.parentItem.listItems[this.index];
		if(this.parentItem.selectedItem == this){
			this.parentItem.selectedItem = null;
		}
		this.parentItem = null;
		this.purgeElements();
		for(var eventName in this.events) {
			var event = this.events[eventName];
			event.unsubscribeAll();
		}
		this.getEl().remove();
		this.container = null;
	},
	
	purgeElements : function(){
		
	}, 
	
	getEl : function(){
		
	}
});


YAHOO.rapidjs.component.list.RootListItem = function(xmlData, container, config){
	YAHOO.rapidjs.component.list.RootListItem.superclass.constructor.call(this, xmlData, container, null, config);
	this.listItems = {};
	this.selectedItem = null;
	var children = xmlData.getElementsByTagName(this.config.contentPath);
	for(var index=0; index<children.length; index++) {
		var childNode = children[index];
        this.createListItem(childNode);
	}
};

YAHOO.extendX(YAHOO.rapidjs.component.list.RootListItem, YAHOO.rapidjs.component.list.ListItem, {
	childAdded : function(newChild){
		this.createListItem(newChild);
	}, 
	
	fireListItemClicked: function(event, listItem){
		this.handleSelection(listItem);
		this.events['listitemclicked'].fireDirect(event, listItem);
	}, 
	
	fireContextMenu: function(event, listItem){
		this.handleSelection(listItem);
		this.events['contextmenu'].fireDirect(event, listItem);
	}, 
	
	render : function(){
	},
	handleSelection : function(listItem){
		if(listItem != this.selectedItem){
			if(!this.selectedItem){
				this.selectedItem = listItem;
				listItem.getEl().addClass('r-list-item-selected');
			}
			else{
				this.selectedItem.getEl().removeClass('r-list-item-selected');
				listItem.getEl().addClass('r-list-item-selected');
				this.selectedItem = listItem;
			}	
		}
	}, 
	
	createListItem : function(xmlData){
		var listItem = this.constructListItem(xmlData);
		this.listItems[listItem.index] = listItem;
		listItem.events['listitemclicked'].subscribe(this.fireListItemClicked, this, true);
		listItem.events['contextmenu'].subscribe(this.fireContextMenu, this, true);
	}, 
	
	constructListItem : function(xmlData){
	}, 
	
	destroy: function(){
		for(var listItemIndex in this.listItems) {
			var listItem = this.listItems[listItemIndex];
			listItem.destroy();
		}
		for(var eventName in this.events) {
			var event = this.events[eventName];
			event.unsubscribeAll();
		}
		this.xmlData = null;
		this.container = null;
		this.config = null;
		this.listItems = null;
	}, 
	
	mergeStarted: function(){
	}, 
	mergeFinished: function(){
	}
	
});