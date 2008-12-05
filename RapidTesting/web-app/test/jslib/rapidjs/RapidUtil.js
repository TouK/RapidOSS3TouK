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

YAHOO.rapidjs.ServerStatus = new YAHOO.rapidjs.component.layout.ServerStatusTool();

YAHOO.rapidjs.Links = [];
YAHOO.rapidjs.Actions = {};
YAHOO.rapidjs.Components = {};
String.prototype.trim = function() {
	a = this.replace(/^\s+/, '');
	return a.replace(/\s+$/, '');
};
YAHOO.rapidjs.ArrayUtils = new function()
{
	this.remove = function(arrayP, index)
	{
		if(index >= arrayP.length || index < 0) return;
		var lastIndex = arrayP.length - 1;
		arrayP[index] = arrayP[lastIndex];
		arrayP.splice(lastIndex, 1);
	};
}();
YAHOO.rapidjs.Connect = new function()
{
	this.containsError = function(response)
	{
		try
		{
			if(response.responseXML)
			{
				var errors = response.responseXML.getElementsByTagName("Errors");
				if(!errors) 
				{
					return false;	
				}
				if(errors.length != null)
				{
					return errors.length > 0;	
				}
				else
				{
					return errors != null;	
				}
			}
			else
			{
				//if response.responseXML is undefined, than we have no errors since RI sends errors in XML.
				return false;
			}
		}
		catch(e)
		{
			return response.responseText.indexOf("Errors") >= 0
		}
		 
	};
	this.isAuthenticated = function(response){
		try
		{
			if(response.responseXML)
			{
				return response.responseXML.getElementsByTagName("Authenticate").length == 0;
			}
			else
			{
				//if response.responseXML is undefined, than we have no errors since RM sends auth error in XML.
				return true;
			}
		}
		catch(e)
		{
			return response.responseText.indexOf("Authenticate") < 0
		}
	};
	
	this.getErrorMessages = function(xmlDoc){
		var messages = xmlDoc.getElementsByTagName('Message');
		var message = '';
		for(var index=0; index<messages.length; index++) {
			if(messages[index].firstChild){
				message = message + messages[index].firstChild.nodeValue + '\n';		
			}
			
		}
		return message;
	};
	this.getSuccessMessage = function(xmlDoc){
		var success = xmlDoc.getElementsByTagName('Successful');
		if(success && success.length > 0 && success[0].firstChild)
		{
			return success[0].firstChild.nodeValue;
		}
		else
		{
			return "";
		}
	};
}();

YAHOO.rapidjs.ToolsUtil = new function()
{
	this.createDefaultTools = function(layout, layoutRegion, component)
	{
		new YAHOO.rapidjs.component.layout.LoadingTool(layout, layoutRegion, component);
		new YAHOO.rapidjs.component.layout.PollingTool(layout, layoutRegion, component);
		new YAHOO.rapidjs.component.layout.ErrorTool(layout, layoutRegion, component);
	};
}();

YAHOO.rapidjs.DomUtils = new function()
{
	this.findParent = function(element, className){
		element = element.parentNode;
		while(element)
		{
			if(YAHOO.util.Dom.hasClass(element, className))
			{
				return element;
			}
			element = element.parentNode;
		}
		return null;
	};
	this.findChild = function(element, className){
		var childNodes = element.childNodes;
		for(var index=0; index<childNodes.length; index++) {
			if(YAHOO.util.Dom.hasClass(childNodes[index], className))
			{
				return childNodes[index];
			}
		}
		return null;
	};
	this.getElementFromChild = function(childEl, parentClass){
        if(!childEl || (YAHOO.util.Dom.hasClass(childEl, parentClass))){
		    return childEl;
	    }
	    var p = childEl.parentNode;
	    var b = document.body;
	    while(p && p != b){
            if(YAHOO.util.Dom.hasClass(p, parentClass)){
            	return p;
            }
            p = p.parentNode;
        }
	    return null;
    };
}();

YAHOO.rapidjs.CursorManager = new function(){
	this.processes = new Array();
	this.idle = function(){
		this.processes.pop();
		if(this.processes.length == 0){
			document.body.style.cursor = '';
		}
	};
	this.busy = function(){
		this.processes.push('');
		document.body.style.cursor = 'wait';
	};
}();

YAHOO.ext.LayoutManager.prototype.isVisible = true;
YAHOO.ext.LayoutManager.prototype.setVisibleState = function(isVisible){
	this.isVisible = isVisible;
	for(var reg in this.regions) {
		var region = this.regions[reg];
		region.setVisibleState(isVisible);
	}
};
YAHOO.ext.LayoutManager.prototype.setUpdatingState = function(isUpdating){
	for(var reg in this.regions) {
		var region = this.regions[reg];
		region.setUpdatingState(isUpdating);
	}
};
YAHOO.ext.LayoutManager.prototype.beginUpdate = function(){
	 this.updating = true;
	 this.setUpdatingState(this.updating);
};
YAHOO.ext.LayoutManager.prototype.endUpdate = function(noLayout){
  	 this.updating = false;
  	 this.setUpdatingState(this.updating);
     if(!noLayout){
        this.layout();
     }  
};

YAHOO.ext.LayoutRegion.prototype.isLayoutUpdating = false;
YAHOO.ext.LayoutRegion.prototype.setVisibleState = function(isVisible){
	if(this.activePanel){
		this.activePanel.setVisibleState(isVisible);
	}
};

YAHOO.ext.LayoutRegion.prototype.setUpdatingState = function(isLayoutUpdating){
	this.isLayoutUpdating = isLayoutUpdating;
	this.panels.each(this.setPanelsUpdatingState, this);
};
YAHOO.ext.LayoutRegion.prototype.setPanelsUpdatingState = function(panel){
	panel.setUpdatingState(this.isLayoutUpdating);
};

YAHOO.ext.LayoutRegion.prototype.add = function(panel){
	if(arguments.length > 1){
        for(var i = 0, len = arguments.length; i < len; i++) {
        	this.add(arguments[i]);
        }
        return null;
    }
    if(this.hasPanel(panel)){
        this.showPanel(panel);
        return panel;
    }
    panel.setUpdatingState(this.isLayoutUpdating);
    panel.setRegion(this);
    this.panels.add(panel);
    if(this.panels.getCount() == 1 && !this.config.alwaysShowTabs){
        this.bodyEl.dom.appendChild(panel.getEl().dom);
        this.setActivePanel(panel);
        this.fireEvent('paneladded', this, panel);
        return panel;
    }
    if(!this.tabs){
        this.initTabs();
    }else{
        this.initPanelAsTab(panel);
    }
    this.tabs.activate(panel.getEl().id);
    this.fireEvent('paneladded', this, panel);
    return panel;
};

YAHOO.ext.LayoutRegion.prototype.initTabs = function(){
        this.bodyEl.setStyle('overflow', 'hidden');
        var ts = new YAHOO.rapidjs.component.layout.RapidTabPanel(this.bodyEl.dom, this.bottomTabs);
        this.tabs = ts;
        ts.resizeTabs = this.config.resizeTabs === true;
        ts.minTabWidth = this.config.minTabWidth || 40;
        ts.maxTabWidth = this.config.maxTabWidth || 250;
        ts.preferredTabWidth = this.config.preferredTabWidth || 150;
        ts.monitorResize = false;
        ts.bodyEl.setStyle('overflow', this.config.autoScroll ? 'auto' : 'hidden');
        this.panels.each(this.initPanelAsTab, this);
};

YAHOO.rapidjs.component.ToolbarButton = function(container, config){
	YAHOO.rapidjs.component.ToolbarButton.superclass.constructor.call(this, config);
	this.init(container);
};
YAHOO.extendX(YAHOO.rapidjs.component.ToolbarButton, YAHOO.ext.ToolbarButton, {});

YAHOO.ext.BasicDialog.prototype.showEl = function(){
        this.proxy.hide();
        this.el.setXY(this.xy);
        this.el.show();
        this.adjustAssets(true);
        this.toFront();
        if(this.defaultButton){
            this.defaultButton.focus();
        }
        this.fireEvent('show', this);
        
        //For FF input cursor bug
        YAHOO.util.Dom.setStyle(this.el.dom, 'overflow', 'auto');
        if(this.defaultInput){
        	this.defaultInput.focus();
        }

};
YAHOO.ext.BasicDialog.prototype.hideEl = function(callback){
        this.proxy.hide();
        if(this.modal){
            this.mask.hide();
            YAHOO.util.Dom.removeClass(document.body, 'masked');
        }
        this.fireEvent('hide', this);
        if(typeof callback == 'function'){
            callback();
        }
        //For FF input cursor bug
        YAHOO.util.Dom.setStyle(this.el.dom, 'overflow', 'hidden');

};
YAHOO.ext.BasicDialog.prototype.addHelp = function(config){
	if(!this.helpTool){
		if(!this.tools){
			this.tools = YAHOO.ext.DomHelper.append(this.el.dom, {tag:'div', cls:'ydlg-tools'});
		}
		this.helpTool = YAHOO.ext.DomHelper.append(this.tools, {tag: 'div', cls: 'ydlg-help', title:'Help', 
            children: [{tag: 'div', cls: 'ydlg-help-inner', html: '&#160;'}]}, true);
    	this.helpTool.addClassOnOver('ydlg-help-over');
    	
    	var help = new YAHOO.rapidjs.component.windows.HtmlWindow(YAHOO.ext.DomHelper.append(document.body, {tag:'div'}), config);
		this.helpWindow = new YAHOO.rapidjs.component.PopUpWindow(help, {modal:false,shadow:true,width:600,height:450,minWidth:200,minHeight:200,title:'Help'});
		this.helpTool.on('click', this.showHelp, this, true);
	}
};
YAHOO.ext.BasicDialog.prototype.addLoading = function(){
	if(!this.loading){
		if(!this.tools){
			this.tools = YAHOO.ext.DomHelper.append(this.el.dom, {tag:'div', cls:'ydlg-tools'});
		}
		this.loading = YAHOO.ext.DomHelper.append(this.tools, {tag: 'div', cls: 'ydlg-loading'});
		YAHOO.util.Dom.setStyle(this.loading, 'display', 'none');
	}
};
YAHOO.ext.BasicDialog.prototype.showLoading = function(){
	if(this.loading){
		YAHOO.util.Dom.setStyle(this.loading, 'display', '');
	}
};
YAHOO.ext.BasicDialog.prototype.hideLoading = function(){
	if(this.loading){
		YAHOO.util.Dom.setStyle(this.loading, 'display', 'none');
	}
};

YAHOO.ext.BasicDialog.prototype.showHelp = function(config){
	this.helpWindow.show();
};
YAHOO.ext.BasicDialog.prototype.addTabListener = function(el, next){
	var handler = function(event, nextEl){
		var keyCode = event.keyCode;
		if(keyCode == 9){
			try {
				nextEl.focus();
			} catch (e) {
			}
			YAHOO.util.Event.stopEvent(event);
		}
		
	};
	YAHOO.util.Event.addListener(el, 'keydown', handler, next, this);
};

YAHOO.ext.Button.prototype.render = function(renderTo){
	this.isFocused = false;
	var btn;
        if(!this.dhconfig){
            if(!YAHOO.ext.Button.buttonTemplate){
                // hideous table template
                YAHOO.ext.Button.buttonTemplate = new YAHOO.ext.DomHelper.Template('<a href="#" class="ybtn-focus"><table border="0" cellpadding="0" cellspacing="0" class="ybtn-wrap"><tbody><tr><td class="ybtn-left">&#160;</td><td class="ybtn-center" unselectable="on">{0}</td><td class="ybtn-right">&#160;</td></tr></tbody></table></a>');
            }
            btn = YAHOO.ext.Button.buttonTemplate.append(
               getEl(renderTo).dom, [this.text], true);
            this.tbl = getEl(btn.dom.firstChild, true);
        }else{
            btn = YAHOO.ext.DomHelper.append(this.footer.dom, this.dhconfig, true);
        }
        this.el = btn;
        this.autoWidth();
        btn.addClass('ybtn');
        btn.mon('click', this.onClick, this, true);
        btn.on('mouseover', this.onMouseOver, this, true);
        btn.on('mouseout', this.onMouseOut, this, true);
        btn.on('mousedown', this.onMouseDown, this, true);
        btn.on('mouseup', this.onMouseUp, this, true);
        btn.on('focus', function(){this.isFocused = true;}, this, true);
        btn.on('blur', function(){this.isFocused = false;}, this, true);
};

YAHOO.ext.grid.DefaultColumnModel.sortTypes.asDate = function(s){
	if(s instanceof Date){
        return s.getTime();
    }
	var date =  Date.parse(String(s));
	if(isNaN(date)){
		return new Date().setTime(0)
	}
	else{
		return date;
	}
};

YAHOO.rapidjs.component.Button = function(container, config){
    YAHOO.ext.util.Config.apply(this, config);
    this.init(container);
};

YAHOO.rapidjs.component.Button.prototype = {
    /** @private */
    init : function(appendTo){
        var element = document.createElement('span');
        element.className = 'r-button';
        if(this.id){
            element.id = this.id;
        }
        this.setDisabled(this.disabled === true);
        var inner = document.createElement('span');
        inner.className = 'r-button-inner ' + this.className;
        inner.unselectable = 'on';
        if(this.tooltip){
            element.setAttribute('title', this.tooltip);
        }
        if(this.style){
           YAHOO.ext.DomHelper.applyStyles(inner, this.style);
        } 
        element.appendChild(inner);
        appendTo.appendChild(element);
        this.el = getEl(element, true);
        this.el.unselectable();
        inner.innerHTML = (this.text ? this.text : '&#160;');
        this.inner = inner;
        this.el.mon('click', this.onClick, this, true);    
        this.el.mon('mouseover', this.onMouseOver, this, true);    
        this.el.mon('mouseout', this.onMouseOut, this, true);
        this.el.mon('mousedown', this.onMouseDown, this, true);
        this.el.mon('mouseup', this.onMouseUp, this, true);
    },
    
    /**
     * Sets this buttons click handler
     * @param {Function} click The function to call when the button is clicked
     * @param {Object} scope (optional) Scope for the function passed above
     */
    setHandler : function(click, scope){
        this.click = click;
        this.scope = scope;  
    },
    
    /**
     * Set this buttons text
     * @param {String} text
     */
    setText : function(text){
        this.inner.innerHTML = text;    
    },
    
    /**
     * Set this buttons tooltip text
     * @param {String} text
     */
    setTooltip : function(text){
        this.el.dom.title = text;    
    },
    
    /**
     * Show this button
     */
    show: function(){
        this.el.dom.parentNode.style.display = '';
    },
    
    /**
     * Hide this button
     */
    hide: function(){
        this.el.dom.parentNode.style.display = 'none';  
    },
    
    /**
     * Disable this button
     */
    disable : function(){
        this.disabled = true;
        if(this.el){
            this.el.addClass('r-button-disabled');
        }
    },
    
    /**
     * Enable this button
     */
    enable : function(){
        this.disabled = false;
        if(this.el){
            this.el.removeClass('r-button-disabled');
        }
    },
    
    /**
     * Returns true if this button is disabled.
     * @return {Boolean}
     */
    isDisabled : function(){
        return this.disabled === true;
    },
    
    setDisabled : function(disabled){
        if(disabled){
            this.disable();
        }else{
            this.enable();
        }
    },
    
    /** @private */
    onClick : function(){
        if(!this.disabled && this.click){
            this.click.call(this.scope || window, this);
        }
    },
    
    /** @private */
    onMouseOver : function(){
        if(!this.disabled){
            this.el.addClass('r-button-over');
            if(this.mouseover){
                this.mouseover.call(this.scope || window, this);
            }
        }
    },
    
    /** @private */
    onMouseOut : function(){
        this.el.removeClass('r-button-over');
        if(!this.disabled){
            if(this.mouseout){
                this.mouseout.call(this.scope || window, this);
            }
        }
    }, 
    /** @private */
    onMouseDown : function(){
        if(!this.disabled){
        	this.el.removeClass('r-button-over');
            this.el.addClass('r-button-down');
        }
    },
    
    /** @private */
    onMouseUp : function(){
        if(!this.disabled){
            this.el.removeClass('r-button-down');
            this.el.addClass('r-button-over');
        }
    }
};
