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
YAHOO.rapidjs.component.PollingComponentContainer = function(container, config)
{
	YAHOO.rapidjs.component.PollingComponentContainer.superclass.constructor.call(this,container, config);
	this.pollTask = new YAHOO.ext.util.DelayedTask(this.poll, this);
	
	if(config.extraRequestParameterName)
	{
		this.extraRequestParameterName = config.extraRequestParameterName;
	}
	else
	{
		this.extraRequestParameterName = null;
	}
	this.extraRequestParameterValue = null;
	
	if(config.pollInterval)
	{
		this.setPollingInterval(config.pollInterval*1);
	}
	else
	{
		this.setPollingInterval(0);
	}
	this.params = null;
	this.configureTimeout(config);
};
YAHOO.extendX(YAHOO.rapidjs.component.PollingComponentContainer, YAHOO.rapidjs.component.ComponentContainer, {

	poll : function(callback){
		if(this.isLinked == true){
			if(this.params && this.panel.isVisible == true){
				this.doRequest(this.url, this.params, callback);
			}
		}
		else{
			if(this.panel.isVisible == true){
				this.doRequest(this.url, this.params, callback);
			}
		}
	},
	
	processSuccess : function(response, keepExisting, removeAttribute){
		YAHOO.rapidjs.ServerStatus.refreshState(true);
		if(YAHOO.rapidjs.Connect.isAuthenticated(response) == true){
			try
			{
				if(YAHOO.rapidjs.Connect.containsError(response) == false)
				{
					this.events["erroroccurred"].fireDirect(this, false,'');
					this.processData(response, keepExisting, removeAttribute);
				}
				else
				{
					this.events["erroroccurred"].fireDirect(this, true, response.responseXML);
				}
				
			}
			catch(e)
			{
			}
			this.events["loadstatechanged"].fireDirect(this, false);
			var callback = response.argument[0];
			if(typeof callback =='function'){
				callback();
			}
			if(this.pollInterval > 0 && this.panel.isVisible)
			{
				this.pollTask.delay(this.pollInterval*1000);
			}
		} 
		else{
			window.location = "login.html?page=" + window.location.pathname;
		}
		
	}, 
	
	processData: function(response, keepExisting, removeAttribute)
	{
		alert("extenders of PollingComponentContainer should override processData");
	},
	
	processFailure : function(response)
	{
		if(!this.lastConnection|| YAHOO.util.Connect.isCallInProgress(this.lastConnection) == false){
			
			this.events["loadstatechanged"].fireDirect(this, false);
		}
		var st = response.status;
		if(st == -1){
			this.events["erroroccurred"].fireDirect(this, true, 'Request received a timeout');
		}
		else if(st == 404){
			this.events["erroroccurred"].fireDirect(this, true, 'Specified url cannot be found');
		}
		else if(st == 0){
			this.events["erroroccurred"].fireDirect(this, true, 'Server is not available');
			YAHOO.rapidjs.ServerStatus.refreshState(false);
		}
		if(this.pollInterval > 0 && this.panel.isVisible)
		{
			this.pollTask.delay(this.pollInterval*1000);
		}
	},
	doRequest: function(url, params, c)
	{	
		if(this.lastConnection){
			var callStatus = YAHOO.util.Connect.isCallInProgress(this.lastConnection); 
			if(callStatus == true){
				YAHOO.util.Connect.abort(this.lastConnection);
				this.events["loadstatechanged"].fireDirect(this, false);
				this.lastConnection = null;
			}
		}
		this.pollTask.cancel();
		if(params == null)
		{
			params = {};
		}
		delete params[this.extraRequestParameterName];
		if (this.extraRequestParameterName != null && this.extraRequestParameterName != "" && this.extraRequestParameterName != undefined) {
			if (this.extraRequestParameterValue != null && this.extraRequestParameterValue != "" ) {
				params[this.extraRequestParameterName] = this.extraRequestParameterValue;
			}
		}
		
		var postData = "";
		for(var paramName in params) {
			postData = postData + paramName + "=" + escape(params[paramName])+"&";
		}
		if(postData != "")
		{
			postData = postData.substring(0, postData.length-1);
		}
		
		var callback = {
			success: this.processSuccess,
			failure: this.processFailure,
			timeout: this.timeout,
			scope: this, 
			argument : [c]
		};
		if(postData && postData != "")
		{
			if(url.indexOf("?") >= 0)
			{
				url = url + "&" + postData;
			}
			else
			{
				url = url + "?" + postData;
			}
		}
		this.lastConnection = YAHOO.util.Connect.asyncRequest('GET',url , callback, null);
		this.events["loadstatechanged"].fireDirect(this, true);
	},
	setPollingInterval: function(newPollInterval)
	{
		this.pollTask.cancel();
		this.pollInterval = newPollInterval;
	},
	getPollingInterval: function()
	{
		return this.pollInterval;
	},
	
	setFocusedContent: function(node, params, dynamicTitleAtt, dialogCall)
	{
		YAHOO.rapidjs.component.PollingComponentContainer.superclass.setFocusedContent.call(this,node, params, dynamicTitleAtt, dialogCall);
		this.params = params;
		this.poll();
	}, 
	setContextMenuContent: function(node, params, dynamicTitleAtt, dialogCall)
	{
		YAHOO.rapidjs.component.PollingComponentContainer.superclass.setContextMenuContent.call(this,node, params, dynamicTitleAtt, dialogCall);
		this.params = params;
		this.poll();
	}, 
	
	getRootNode: function(data, responseText){
		var node = data.getRootNode(this.rootTag);
		if(!node){
			this.clearData();
		}
		return node;
	}, 
	handleUnvisible: function(){
		if(this.lastConnection){
			var callStatus = YAHOO.util.Connect.isCallInProgress(this.lastConnection); 
			if(callStatus == true){
				YAHOO.util.Connect.abort(this.lastConnection);
				this.events["loadstatechanged"].fireDirect(this, false);
				this.lastConnection = null;
			}
		}
		this.pollTask.cancel();
	},
	
	handleVisible : function(){
		this.poll();
	}, 
	
	configureTimeout: function(config){
		if(config.timeout){
			this.timeout = config.timeout * 1000;
		}
		else{
			this.timeout = 30000;
		}
	}
	
});