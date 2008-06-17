YAHOO.rapidjs.component.menu.MenuItem = function(wrapperEl, menuLabel, action, exp, evalFunction, parentMenu){
	this.parentMenu = parentMenu;
	this.selectedClass = 'rapid-context-menu-item-selected';
	this.unselectedClass = 'rapid-context-menu-item-unselected';
	this.wrapper = wrapperEl;
	this.action = action;
	this.exp = exp;
	this.evalFunction = evalFunction;
	this.value = menuLabel;
	var tr = document.createElement('tr');
	YAHOO.util.Dom.addClass(tr, this.unselectedClass);
	var td = document.createElement('td');
	this.labelEl = document.createElement('div');
	YAHOO.util.Dom.addClass(this.labelEl, 'rapid-context-menu-text');
	this.labelEl.innerHTML = this.value;
	td.appendChild(this.labelEl);
	tr.appendChild(td);
	td = document.createElement('td');
	this.arrow = document.createElement('div');
	YAHOO.util.Dom.addClass(this.arrow, 'rapid-submenu-arrow');
	td.appendChild(this.arrow);
	tr.appendChild(td);
	this.wrapper.appendChild(tr);
	this.el = getEl(tr);
	this.showSubMenuTask = new YAHOO.ext.util.DelayedTask(this.showSubMenu, this);
	this.hideSubMenuTask = new YAHOO.ext.util.DelayedTask(this.hideSubMenu, this);
	this.subMenu = null;
	this.currentData = null;
	this.currentNode = null;
	YAHOO.util.Dom.setStyle(this.arrow, 'display', 'none');
	YAHOO.util.Event.addListener(this.el.dom, 'mouseover', this.handleMouseOver, this, true);
	YAHOO.util.Event.addListener(this.el.dom, 'mouseout', this.handleMouseOut, this, true);
	YAHOO.util.Event.addListener(this.el.dom, 'mousedown', this.menuItemClicked, this, true);
	this.events = {
		'menuitemclicked' : new YAHOO.util.CustomEvent('menuitemclicked')
	}
};

YAHOO.rapidjs.component.menu.MenuItem.prototype = {
	evaluate : function(component, data, node){
		this.currentData = data;
		this.currentNode = node;
		this.component = component;
		if(this.evalFunction){
			return this.evalFunction.call(window, this, this.currentData, this.currentNode);
		}
		else{
			return eval(this.exp);
		}
	}, 
	
	execute : function(){
		this.action.execute(this.component, this, this.currentData, this.currentNode);
	}, 
	addSubMenuItem : function(menuLabel, action, exp, evalFunction){
		if(this.subMenu){
			this.subMenu.addMenuItem(menuLabel, action, exp, evalFunction, this.subMenu);
		}
		else{
			this.subMenu = new YAHOO.rapidjs.component.menu.ContextMenu(true, this);
			YAHOO.util.Dom.setStyle(this.arrow, 'display', '');
			YAHOO.util.Event.addListener(this.subMenu.el.dom, 'mouseover', this.subMenuMouseOver, this, true);
			YAHOO.util.Event.addListener(this.subMenu.el.dom, 'mouseout', this.subMenuMouseOut, this, true);
			this.subMenu.addMenuItem(menuLabel, action, exp, evalFunction, this.subMenu);
		}
	}, 
	
	addDynamicSubMenu: function(tagName, att, action){
		if(!this.subMenu){
			this.subMenu = new YAHOO.rapidjs.component.menu.DynamicContextMenu(tagName, att, action, true, this);
			YAHOO.util.Dom.setStyle(this.arrow, 'display', '');
			YAHOO.util.Event.addListener(this.subMenu.el.dom, 'mouseover', this.subMenuMouseOver, this, true);
			YAHOO.util.Event.addListener(this.subMenu.el.dom, 'mouseout', this.subMenuMouseOut, this, true);
		}
	}, 
	
	
	menuItemClicked: function(){
		if(this.subMenu){
			this.subMenuFixed = true;
			YAHOO.util.Dom.replaceClass(this.el.dom, this.unselectedClass, this.selectedClass);
			this.parentMenu.fixedItem = this;
			this.parentMenu.hideSubMenus(this);
			this.showSubMenuTask.delay(1);
		}
		else{
			this.hideAll();
			this.execute();
		}
	}, 
	
	handleMouseOver: function(){
		var fixedItem = this.parentMenu.fixedItem;
		if(!fixedItem || fixedItem == this){
			YAHOO.util.Dom.replaceClass(this.el.dom, this.unselectedClass, this.selectedClass);
			this.showSubMenuTask.delay(300);
			this.hideSubMenuTask.cancel();	
		}
	}, 
	
	handleMouseOut: function(){
		if(this.subMenuFixed != true){
			YAHOO.util.Dom.replaceClass(this.el.dom, this.selectedClass, this.unselectedClass);
			this.hideSubMenuTask.delay(300);
			this.showSubMenuTask.cancel();	
		}
	}, 
	
	subMenuMouseOver : function(){
		YAHOO.util.Dom.replaceClass(this.el.dom, this.unselectedClass, this.selectedClass);
		this.hideSubMenuTask.cancel();
	}, 
	
	subMenuMouseOut: function(){
		if(this.subMenuFixed != true){
			YAHOO.util.Dom.replaceClass(this.el.dom, this.selectedClass, this.unselectedClass);
			this.hideSubMenuTask.delay(300);	
		}
	}, 
	
	setVisible: function(isVisible){
		if(isVisible)
		{
			YAHOO.util.Dom.setStyle(this.el.dom, 'display', '');
		}
		else
		{
			YAHOO.util.Dom.setStyle(this.el.dom, 'display', 'none');
		}
	}, 
	
	showSubMenu : function(){
		if(this.subMenu){
			var x = this.el.getX() + this.el.dom.offsetWidth - 5;
			var y = this.el.getY() - 2;
			var xDeviation = this.el.dom.offsetWidth + this.subMenu.el.getWidth() - 10;
			this.subMenu.show(this.component, this.currentData, this.currentNode, x, y, xDeviation);
		}
	}, 
	
	hideSubMenu: function(){
		if(this.subMenu){
			this.subMenuFixed = false;
			YAHOO.util.Dom.replaceClass(this.el.dom, this.selectedClass, this.unselectedClass);
			if(this.parentMenu.fixedItem == this){
				this.parentMenu.fixedItem = null;
			}
			this.subMenu.hide();
		}
	},
	
	hideAll: function(){
		var menu = this.parentMenu;
		while(menu.isSub == true){
			menu = menu.parentMenuItem.parentMenu;
		}
		menu.hide();
	}, 
	
	purgeAll : function(){
		for(var event in this.events) {
			this.events[event].unsubscribeAll();
		}
	}, 
	
	destroy: function(){
		YAHOO.util.Event.purgeElement(this.el.dom, true);
		this.purgeAll();
		this.wrapper.removeChild(this.el.dom);
		this.parentMenu = null;
	}
};