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
YAHOO.rapidjs.component.menu.ContextMenu = function(isSub, parentMenuItem){
	this.menuItems = new Array();
	this.isSub = isSub;
	this.parentMenuItem = parentMenuItem;
	this.fixedItem = null;
	this.isOpen = false;
	this.el = YAHOO.ext.DomHelper.append(document.body, {tag: 'div', cls:'rapid-context-menu', style:'visibility:hidden'}, true);
	var table = document.createElement('table');
	YAHOO.util.Dom.addClass(table, 'rapid-context-menu-table');
	table.cellSpacing = "0px";
	table.cellPadding = "0px";
	table.border = 0;
	this.body = document.createElement('tbody');
	table.appendChild(this.body);
	this.el.dom.appendChild(table);
	YAHOO.util.Event.addListener(document, 'mousedown', this.handleDocMouseDown, this, true); 
	YAHOO.util.Event.addListener(this.el.dom, 'mousedown', this.handleMouseDown, this, true); 
};

YAHOO.rapidjs.component.menu.ContextMenu.prototype = {
	addMenuItem: function(menuLabel, action, exp, evalFunction){
		var menuItem = new YAHOO.rapidjs.component.menu.MenuItem(this.body, menuLabel, action, exp, evalFunction, this);
		this.menuItems.push(menuItem);
		return menuItem;
	}, 
	
	addSplitter: function(){
		return YAHOO.ext.DomHelper.append(this.el.dom, {tag:'div', style:'border-bottom: 1px solid #e0e0d0;'});
	}, 
	
	getXToDraw: function(x, deviation){
		var offsetWidth = this.el.dom.offsetWidth;
		var viewPortWidth = YAHOO.util.Dom.getViewportWidth();
		var scrollX = document.documentElement.scrollLeft || document.body.scrollLeft;
		var leftConstraint = scrollX + 10;
		var rightConstraint = scrollX + viewPortWidth - offsetWidth - 10;
		if (x < leftConstraint) {
			x = leftConstraint;
		} else if (x > rightConstraint) {
			if(deviation){
				x = x - deviation;
			}
			else{
				x = rightConstraint;
			}
			
		}
		return x;
	}, 
	
	getYToDraw: function(y, deviation){
		var offsetHeight = this.el.dom.offsetHeight;
		var viewPortHeight = YAHOO.util.Dom.getViewportHeight();
		var scrollY = document.documentElement.scrollTop || document.body.scrollTop;
		var topConstraint = scrollY + 10;
		var bottomConstraint = scrollY + viewPortHeight - offsetHeight - 10;
		if (y < topConstraint) {
			y = topConstraint;
		} else if (y > bottomConstraint) {
			if(deviation){
				y = y - deviation;
			}
			else{
				y = bottomConstraint;
			}
		}
		return y;
	}, 
	
	show: function(component, data, node, x, y, xDeviation, yDeviation){
		var numberOfMenuItem = 0;
		for(var index=0; index<this.menuItems.length; index++) {
			var menuItem = this.menuItems[index];
			if(menuItem.evaluate(component, data, node) == true){
				menuItem.setVisible(true);
				numberOfMenuItem ++;
			}
			else{
				menuItem.setVisible(false);
			}
		}
		if(numberOfMenuItem > 0){
			var xToDraw = this.getXToDraw(x, xDeviation);
			var yToDraw = this.getYToDraw(y, yDeviation);
			this.el.setX(xToDraw);
			this.el.setY(yToDraw);
			YAHOO.util.Dom.setStyle(this.el.dom, 'visibility', 'visible');
			this.isOpen = true;
		}
	}, 
	
	contextMenuClicked: function(component, event, node, windowId){
		var Event = YAHOO.util.Event;
		var pageX = Event.getPageX(event);
		var pageY = Event.getPageY(event);
		this.hide();
		var data;
		if(node){
			data = node.getAttributes();
			data['TagName'] = node.nodeName;
			data['WindowId'] = windowId;
		}
		else{
			data = {};
		}
		this.show(component, data, node, pageX, pageY);
		Event.stopEvent(event);
	}, 
	
	handleMouseDown: function(event){
		YAHOO.util.Event.stopPropagation(event);
	}, 
	handleDocMouseDown: function(event){
		if(this.isOpen == true && this.isSub != true){
			this.hide();
		}
	}, 
	
	hide: function(){
		YAHOO.util.Dom.setStyle(this.el.dom, 'visibility', 'hidden');
		this.isOpen = false;
		this.fixedItem = null;
		for(var index=0; index<this.menuItems.length; index++) {
			var menuItem = this.menuItems[index];
			menuItem.hideSubMenu();
		}
	}, 
	
	hideSubMenus : function(menuItem){
		for(var index=0; index<this.menuItems.length; index++) {
			var currentMenuItem = this.menuItems[index];
			if(currentMenuItem != menuItem){
				currentMenuItem.hideSubMenu();
			}
		}
	},
	
	destroy: function(){
		while(this.menuItems.length > 0){
			var menuItem = this.menuItems.pop();
			menuItem.destroy();
		}
		YAHOO.util.Event.purgeElement(this.el.dom, false);
		document.body.removeChild(this.el.dom);
	}
};


YAHOO.rapidjs.component.menu.DynamicContextMenu = function(tagName, att, action, isSub, parentMenuItem){
	YAHOO.rapidjs.component.menu.DynamicContextMenu.superclass.constructor.call(this, isSub, parentMenuItem);
	this.tagName = tagName;
	this.att = att;
	this.action = action;
};

YAHOO.extendX(YAHOO.rapidjs.component.menu.DynamicContextMenu, YAHOO.rapidjs.component.menu.ContextMenu, {
	show: function(component, data, node, x, y, xDeviation, yDeviation){
		var nOfMenuItems = this.menuItems.length;
		for(var index=0; index<nOfMenuItems; index++) {
			var menuItem = this.menuItems.pop();
			menuItem.destroy();
		}
		var items = node.getElementsByTagName(this.tagName);
		for(var index=0; index<items.length; index++) {
			var item = items[index];
			var menuLabel = item.getAttribute(this.att);
			var menuItem = this.addMenuItem(menuLabel, this.action, true);
			menuItem.currentData = data;
			menuItem.currentNode = node;
			menuItem.component = component;
		}
		if(items.length > 0){
			var xToDraw = this.getXToDraw(x, xDeviation);
			var yToDraw = this.getYToDraw(y, yDeviation);
			this.el.setX(xToDraw);
			this.el.setY(yToDraw);
			YAHOO.util.Dom.setStyle(this.el.dom, 'visibility', 'visible');
			this.isOpen = true;
		}
	} 
});
