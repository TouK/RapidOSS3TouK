
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
    this.rootTag = config.rootTag;
    this.lastConnection = null;
    this.configureTimeout(config);
};
YAHOO.lang.extend(YAHOO.rapidjs.component.PollingComponentContainer, YAHOO.rapidjs.component.ComponentContainer, {

	poll : function(){
        this.doRequest(this.url, this.params);
    },

    processSuccess : function(response){
        try
        {
            if(YAHOO.rapidjs.Connect.containsError(response) == false)
            {
                this.handleSuccess(response);
            }
            else
            {
                this.handleFailure(response);
            }

        }
        catch(e)
        {
        }
        if(this.pollingInterval > 0)
        {
            this.pollTask.delay(this.pollingInterval*10000);
        }


    },

    handleSuccess: function(response)
    {
        alert("extenders of PollingComponentContainer should override processData");
    },

    handleFailure: function(response)
    {
        alert("extenders of PollingComponentContainer should override processData");
    },
    handleTimeout: function(response)
    {
        alert("extenders of PollingComponentContainer should override processData");
    },

    processFailure : function(response)
    {
        this.handleFailure(response);
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

    timeout : function(response)
    {
        this.handleTimeout(response);
        if(this.pollingInterval > 0)
        {
            this.pollTask.delay(this.pollingInterval*1000);
        }
    },
    doRequest: function(url, params)
    {
        this.abort();

        if(params == null)
        {
            params = {};
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
            scope: this
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
    },

    abort: function()
    {
        if(this.lastConnection){
            var callStatus = YAHOO.util.Connect.isCallInProgress(this.lastConnection);
            if(callStatus == true){
                YAHOO.util.Connect.abort(this.lastConnection);
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
		return this.datasource.getPollingInterval();
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