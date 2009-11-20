function Requester(){
}

Requester.prototype.init= function(caller, successCallback, failureCallback, abortCallback)
{
    this.lastRequestedUrl = null;
    this.caller = caller;
    this.successCallback = successCallback;
    this.failureCallback = failureCallback;
    this.abortCallback = abortCallback;
    if(this.abortCallback != null)
    {
        this.abortCallback = this.abortCallback.createDelegate(this.caller);
    }
};
Requester.prototype.doRequest= function (url, params, argument)
{
    this.lastRequestedUrl = url;
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
        postData = postData + paramName + "=" + encodeURIComponent(params[paramName])+"&";
    }
    if(postData != "")
    {
        postData = postData.substring(0, postData.length-1);
    }

    var callback = {
        success: this.successCallback,
        failure: this.failureCallback,
        scope: this.caller,
        cache:false,
        argument : argument
    };
    this.lastConnection = YAHOO.util.Connect.asyncRequest('POST',url , callback, postData);
};
Requester.prototype.abort= function()
{
    if(this.lastConnection){
        var callStatus = YAHOO.util.Connect.isCallInProgress(this.lastConnection);
        if(callStatus == true){
            YAHOO.util.Connect.abort(this.lastConnection);
            this.lastConnection = null;
            if(this.abortCallback != null)
            {
                this.abortCallback(this.lastConnection);
            }
        }
    }
};
