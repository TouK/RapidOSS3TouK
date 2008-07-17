YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.action');
YAHOO.rapidjs.component.action.RequestAction = function(config)
{
    this.url = config.url;
    this.allowMultipleRequests = config.allowMultipleRequests;
    this.events = {
        'success' : new YAHOO.util.CustomEvent('success'),
        'failure' : new YAHOO.util.CustomEvent('failure'),
        'timeout' : new YAHOO.util.CustomEvent('timeout')
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
		this.lastConnection = YAHOO.util.Connect.asyncRequest('POST', this.url, callback, postData);
    },
    processSuccess: function(response, arguments)
    {
        this.events['success'].fireDirect(response, arguments);
    },
    processFailure: function(response, arguments)
    {
        this.events['failure'].fireDirect(response, arguments);
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
    }
};