YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.action');
YAHOO.rapidjs.component.action.RequestAction = function(config)
{
    this.url = config.url;
    this.allowMultipleRequests = config.allowMultipleRequests;
    this.events = {
        'success' : new YAHOO.util.CustomEvent('success'),
        'error' : new YAHOO.util.CustomEvent('error'),
        'failure' : new YAHOO.util.CustomEvent('failure')
    };
    this.lastConnection = null;
    this.timeout = config.timeout != null?config.timeout:30000;
};
YAHOO.rapidjs.component.action.RequestAction.prototype = {

    getPostData : function(params){
		var postData = "";
		if(params){
			for(var paramName in params) {
				var paramValue = params[paramName];
				postData = postData + paramName + "=" + encodeURIComponent(paramValue)+"&";
			}
		}
		if(postData != "")
		{
			postData = postData.substring(0, postData.length-1);
		}
		return postData
	},
    execute : function( params, arguments){
        if(this.allowMultipleRequests != true)
        {
            this.abort();
        }
        var postData = this.getPostData( params);
		var callback = {
			success:this.processSuccess,
			failure: this.processFailure,
			scope: this,
            argument: arguments,
            timeout: this.timeout
		};
        var tmpUrl = this.url;
        if(postData && postData != "")
        {
            if(tmpUrl.indexOf("?") >= 0)
            {
                tmpUrl = tmpUrl + "&" + postData;
            }
            else
            {
                tmpUrl = tmpUrl + "?" + postData;
            }
        }
        this.lastConnection = YAHOO.util.Connect.asyncRequest('GET', tmpUrl, callback);
    },
    processSuccess: function(response)
    {
        try
        {

            if(YAHOO.rapidjs.Connect.checkAuthentication(response) == false)
            {
                return;
            }
            else if(YAHOO.rapidjs.Connect.containsError(response) == false)
            {
                this.handleSuccess(response);
                this.events['success'].fireDirect(response, response.argument);
            }
            else
            {
                this.handleErrors(response);
                this.events['error'].fireDirect(response, response.argument);
            }
        }
        catch(e)
        {
        }

    },
    processFailure: function(response)
    {
        var st = response.status;
		if(st == -1){
            this.events['failure'].fireDirect(response, response.argument, 'Request received a timeout.');
        }
		else if(st == 404){
			this.events['failure'].fireDirect(response, response.argument, 'Specified url cannot be found.');
		}
		else if(st == 0){
			YAHOO.rapidjs.serverDownEvent.fireDirect(response);
		}

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
    },
    handleSuccess: function(response){},
    handleErrors: function(response){}
};

YAHOO.rapidjs.component.action.MergeAction = function(config){
    YAHOO.rapidjs.component.action.MergeAction.superclass.constructor.call(this, config);
    this.removeAttribute = config.removeAttribute;
};

YAHOO.lang.extend(YAHOO.rapidjs.component.action.MergeAction, YAHOO.rapidjs.component.action.RequestAction, {
    handleSuccess: function(response)
    {
        var componentList = response.argument
        if(componentList && componentList.length){
            for(var i=0; i<componentList.length; i++){
               componentList[i].handleSuccess(response, true, this.removeAttribute) 
            }
        }
    }
});