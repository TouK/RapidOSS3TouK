YAHOO.namespace('rapidjs', 'rapidjs.component');
YAHOO.rapidjs.component.PollingComponentContainer = function(container, config)
{
	YAHOO.rapidjs.component.PollingComponentContainer.superclass.constructor.call(this,container, config);
    this.pollTask = new YAHOO.ext.util.DelayedTask(this.poll, this);
	if(config.pollingInterval)
	{
		this.setPollingInterval(config.pollingInterval*1);
	}
	else
	{
		this.setPollingInterval(0);
	}
	this.params = null;
    this.format = config.format;
    this.rootTag = config.rootTag;
    this.lastConnection = null;
    this.configureTimeout(config);
};
YAHOO.lang.extend(YAHOO.rapidjs.component.PollingComponentContainer, YAHOO.rapidjs.component.ComponentContainer, {

	poll : function(){
        this.doRequest(this.url, this.params);
    },

    processSuccess : function(response){
        YAHOO.rapidjs.ErrorManager.serverUp();
        try
        {

            if(YAHOO.rapidjs.Connect.checkAuthentication(response) == false)
            {
                this.handleAuthenticate();
                return;
            }
            else if(YAHOO.rapidjs.Connect.containsError(response) == false)
            {
                this.events["success"].fireDirect(this);
                this.handleSuccess(response);
            }
            else
            {
                this.handleErrors(response);
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
        if(this.pollingInterval > 0)
        {
            this.pollTask.delay(this.pollingInterval*1000);
        }


    },

    handleSuccess: function(response, keepExisting, removeAttribute)
    {
    },
    handleErrors: function(response)
    {
        this.clearData();
        var errors = YAHOO.rapidjs.Connect.getErrorMessages(response.responseXML);
        this.events["error"].fireDirect(this, errors);
        YAHOO.rapidjs.ErrorManager.errorOccurred(this, errors);
    },
    handleTimeout: function(response)
    {
        this.events["error"].fireDirect(this,  ['Request received timeout.']);
        YAHOO.rapidjs.ErrorManager.errorOccurred(this, ['Request received timeout.']);
    },
    handleUnknownUrl: function(response)
    {
        this.events["error"].fireDirect(this,  ['Specified url cannot be found.']);
        YAHOO.rapidjs.ErrorManager.errorOccurred(this, ['Specified url cannot be found.']);
    },
    handleAuthenticate: function(response)
    {
    },
    processFailure : function(response)
    {
        if(!this.lastConnection|| YAHOO.util.Connect.isCallInProgress(this.lastConnection) == false){

			this.events["loadstatechanged"].fireDirect(this, false);
		}
        var st = response.status;
		if(st == -1){
            this.handleTimeout(response);
        }
		else if(st == 404){
			this.handleUnknownUrl(response);
		}
		else if(st == 0){
			YAHOO.rapidjs.ErrorManager.serverDown();
		}
        if(this.pollingInterval > 0)
        {
            this.pollTask.delay(this.pollingInterval*1000);
        }

    },

    getRootNode: function(data, responseText){
		var node = data.getRootNode(this.rootTag);
		if(!node){
			this.clearData();
		}
		return node;
	},
    doRequest: function(url, params, callback)
    {
        this.abort();

        if(params == null)
        {
            params = {};
        }
        if(this.format)
        {
            params["format"] = this.format;
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
            argument : [callback]
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

    abort: function()
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
    },
    setPollingInterval: function(newPollInterval)
    {
        this.pollTask.cancel();
        this.pollingInterval = newPollInterval;
    },
	getPollingInterval: function()
	{
		return this.pollingInterval;
	},
    configureTimeout: function(config){
		if(config.timeout){
			this.timeout = config.timeout * 1000;
		}
		else{
			this.timeout = 30000;
		}
	},
    clearData: function(){
	}
});