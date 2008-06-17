YAHOO.rapidjs.component.menu.MenuAction = function(funcRef, scope){
	this.funcRef = funcRef;
	this.scope = scope;
};
YAHOO.rapidjs.component.menu.MenuAction.prototype = {
	execute : function(component, menuItem, data, node){
		this.funcRef.call(this.scope || window, menuItem, data, node);
	}
};

YAHOO.rapidjs.component.menu.NumberOfActions = 0;
YAHOO.rapidjs.component.menu.BasicMenuAction = function(config){
	YAHOO.ext.util.Config.apply(this, config, {name:"AutomaticCreatedMenuActionName_"+YAHOO.rapidjs.component.menu.NumberOfActions});
	YAHOO.rapidjs.component.menu.NumberOfActions++;
	YAHOO.rapidjs.Actions[this.name] = this;
	if(config.timeout){
		this.timeout = config.timeout * 1000;
	}
	else{
		this.timeout = 30000;
	}
};

YAHOO.rapidjs.component.menu.BasicMenuAction.prototype = {
	execute : function(component, menuItem, data, node){
	}, 
	getPostData : function(data, menuItem){
		var postData = "";
		if(menuItem){
			postData = postData + 'MenuLabel=' + encodeURIComponent(menuItem.value) + '&';
		}
		if(this.fixedParams){
			for(var fParam in this.fixedParams) {
				postData = postData + fParam + "=" + encodeURIComponent(this.fixedParams[fParam])+"&";
			}
		}
		if(this.dynamicParams){
			for(var paramName in this.dynamicParams) {
				var attribute = this.dynamicParams[paramName];
				postData = postData + paramName + "=" + encodeURIComponent(data[attribute])+"&";
			}
		}
		if(postData != "")
		{
			postData = postData.substring(0, postData.length-1);
		}
		return postData
	}
};

YAHOO.rapidjs.component.menu.scriptActionDefined = false;
YAHOO.rapidjs.component.menu.ScriptMenuAction = function(config){
	if(YAHOO.rapidjs.component.menu.scriptActionDefined == false){
		this.createResultDialog();
	}
	YAHOO.rapidjs.component.menu.scriptActionDefined = true;
	YAHOO.rapidjs.component.menu.ScriptMenuAction.superclass.constructor.call(this, config);
};

YAHOO.extendX(YAHOO.rapidjs.component.menu.ScriptMenuAction, YAHOO.rapidjs.component.menu.BasicMenuAction, {
	
	
	enableComponent: function(component, enable)
	{
		this._enableComponent(component, enable);
		for(var index=0; index<YAHOO.rapidjs.Links.length; index++) {
			if(YAHOO.rapidjs.Links[index].to == component)
			{
				this._enableComponent(YAHOO.rapidjs.Links[index].from, enable);
			}
			else if(YAHOO.rapidjs.Links[index].from == component)
			{
				this._enableComponent(YAHOO.rapidjs.Links[index].to, enable);
			}
		}
	},
	_enableComponent: function(component, enable)
	{
		var blockingDiv = null;
		var layoutPanel = YAHOO.rapidjs.DomUtils.findParent(component.panel.el.dom, "ylayout-panel");
		if(layoutPanel)
		{
			blockingDiv = YAHOO.rapidjs.DomUtils.findChild(layoutPanel, "ydlg-mask");
			if(!blockingDiv)
			{
				blockingDiv = document.createElement("div");
				layoutPanel.appendChild(blockingDiv);
				YAHOO.util.Dom.addClass(blockingDiv, "ydlg-mask");
				YAHOO.util.Dom.setStyle(blockingDiv, "display", "none");
				YAHOO.util.Dom.setStyle(blockingDiv, "backgroundColor","#FFFFFF");
				YAHOO.util.Dom.setStyle(blockingDiv, "opacity","0");
				YAHOO.util.Dom.setStyle(blockingDiv, "width","100%");
				YAHOO.util.Dom.setStyle(blockingDiv, "height","100%");
			}
			if(enable == false)
			{
				YAHOO.util.Dom.setStyle(blockingDiv, "cursor","wait");
				YAHOO.util.Dom.setStyle(blockingDiv, "display", "block");
			}
			else
			{
				YAHOO.util.Dom.setStyle(blockingDiv, "cursor","");
				YAHOO.util.Dom.setStyle(blockingDiv, "display", "none");
			}
		}
	},
	execute : function(component, menuItem, data, node){
		this.enableComponent(component, false);
		var postData = this.getPostData(data, menuItem);
		var callback = {
			success: this.processSuccess,
			failure: this.processFailure,
			scope: this,
			argument: [component],
			timeout: this.timeout
		};
		YAHOO.util.Connect.asyncRequest('POST', this.url, callback, postData);
	}, 
	
	processSuccess : function(response){
		YAHOO.rapidjs.ServerStatus.refreshState(true);
		if(YAHOO.rapidjs.Connect.isAuthenticated(response) == true){
			this.enableComponent(response.argument[0], true);
			YAHOO.rapidjs.component.menu.ActionResultTextArea.value = response.responseText;
			var dialog = YAHOO.rapidjs.component.menu.menuActionDialog;
			if(dialog.isVisible() != true){
				dialog.show();
			}
		}
		else{
			window.location = "login.html?page=" + window.location.pathname;
		}
	}, 
	
	processFailure : function(response){
		this.enableComponent(response.argument[0], true);
	}, 
	
	createResultDialog: function(){
		var dialogContainer = YAHOO.ext.DomHelper.append(document.body, {tag:'div'});
		YAHOO.rapidjs.component.menu.menuActionDialog = new YAHOO.ext.LayoutDialog(dialogContainer, { 
            modal: false,
            width:500,
            height:200,
            shadow:true,
            minWidth:100,
            minHeight:100,
            syncHeightBeforeShow: true,
            title: 'Action Result',
            resizable: true,
            center:{
                autoScroll:true
            }
    	});
    	var container = YAHOO.ext.DomHelper.append(document.body, {tag:'div', style:"width:100%;height:100%;overflow:hidden;"});
    	YAHOO.rapidjs.component.menu.ActionResultTextArea = YAHOO.ext.DomHelper.append(container, {tag:'textarea', cls:"rapid-textarea"});
    	YAHOO.rapidjs.component.menu.ActionResultTextArea.readOnly=true;
    	var layout = YAHOO.rapidjs.component.menu.menuActionDialog.getLayout();
	    YAHOO.rapidjs.component.menu.menuActionDialog.beginUpdate();
	    layout.add('center',new YAHOO.rapidjs.component.layout.RapidPanel(container));
	    YAHOO.rapidjs.component.menu.menuActionDialog.endUpdate();
	}
});
YAHOO.rapidjs.component.menu.MergeMenuAction = function(config){
	YAHOO.rapidjs.component.menu.MergeMenuAction.superclass.constructor.call(this, config);
};

YAHOO.extendX(YAHOO.rapidjs.component.menu.MergeMenuAction, YAHOO.rapidjs.component.menu.ScriptMenuAction, {
	processSuccess : function(response){
		if(YAHOO.rapidjs.Connect.isAuthenticated(response) == true){
			this.enableComponent(response.argument[0], true);
			if(response.argument[0]&& response.argument[0].processData)
			{
				response.argument[0].processSuccess(response, true, this.removeAttribute);
			}
		}
		else{
			window.location = "login.html?page=" + window.location.pathname;
		}
	}, 
	
	processFailure : function(response){
		this.enableComponent(response.argument[0], true);
	}
	
	
});


YAHOO.rapidjs.component.menu.WindowMenuAction = function(config){
	YAHOO.rapidjs.component.menu.WindowMenuAction.superclass.constructor.call(this, config);
};
YAHOO.extendX(YAHOO.rapidjs.component.menu.WindowMenuAction, YAHOO.rapidjs.component.menu.BasicMenuAction, {
	execute : function(component, menuItem, data, node){
		var params = {};
		if(menuItem){
			params['MenuLabel'] = menuItem.value;
		}
		if(this.fixedParams){
			for(var fParam in this.fixedParams) {
				params[fParam] = this.fixedParams[fParam];
			}
		}
		if(this.dynamicParams){
			for(var paramName in this.dynamicParams) {
				var attribute = this.dynamicParams[paramName];
				params[paramName] = data[attribute];
			}
		}
		YAHOO.rapidjs.Components[this.windowId].setContextMenuContent(data, params, this.dynamicTitleAttribute);
	}

});
YAHOO.rapidjs.component.menu.LinkMenuAction = function(config){
	YAHOO.rapidjs.component.menu.LinkMenuAction.superclass.constructor.call(this, config);
};
YAHOO.extendX(YAHOO.rapidjs.component.menu.LinkMenuAction, YAHOO.rapidjs.component.menu.BasicMenuAction, {
	execute : function(component, menuItem, data, node){
		var postData = this.getPostData(data, menuItem);
		var url = this.url;
		if(this.url.indexOf('?') < 0){
			if(postData != ""){
				url = url + '?' + postData;
			}
		}
		else{
			if(postData != ""){
				url = url + '&' + postData;
			}
		}
		window.open(url, '_blank');
	}
});
