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
YAHOO.namespace('rapidjs');
YAHOO.rapidjs.Requester = function(successFunctionToCall, failureFunctionToCall, scope, timeout) {
    this.scope = scope;
    this.timeout = timeout;
    this.lastConnection = null;
    if(scope != null)
    {
        this.successFunctionToCall = successFunctionToCall.createDelegate(scope)
        this.failureFunctionToCall = failureFunctionToCall.createDelegate(scope)
    }
    else
    {
        this.successFunctionToCall = successFunctionToCall
        this.failureFunctionToCall = failureFunctionToCall
    }
}
YAHOO.rapidjs.Requester.prototype = {
    isRequesting: function()
    {
        return  this.lastConnection != null && YAHOO.util.Connect.isCallInProgress(this.lastConnection) == true
    },
    processSuccess : function(response) {
        if (this.isRequesting()) {
            return;
        }
        YAHOO.rapidjs.ErrorManager.serverUp();
        var containsErrors = false;
        try
        {

            if (YAHOO.rapidjs.Connect.containsError(response) == false)
            {
                this.successFunctionToCall(response);
            }
            else
            {
                containsErrors = true;
                this.handleErrors(response);
            }

        }
        catch(e)
        {
        }
        var callback = response.argument[0];
        if (typeof callback == 'function') {
            callback = callback.createDelegate(this.scope, [containsErrors], true)
            callback(response);
        }
    },
    handleErrors: function(response)
    {
        var errors = YAHOO.rapidjs.Connect.getErrorMessages(response.responseXML);
        YAHOO.rapidjs.ErrorManager.errorOccurred(this.scope, errors);
        this.failureFunctionToCall(errors, null);
    },
    processFailure : function(response)
    {
        if (this.isRequesting()) {
            return;
        }
        var st = response.status;
        if (st == 0) {
            YAHOO.rapidjs.ErrorManager.serverDown();
        }
        else{
            var errors = [];
            var statusCodes = [];
            if (st == -1) {
                errors = ['Request received timeout.'];
                statusCodes = [-1]
            }
            else if (st == 404) {
                errors = ['Specified url cannot be found.']
                statusCodes = [404]
            }
            else if (st == 500) {
                errors = ["Internal Server Error. Please see the log files."]
                statusCodes = [500]
            }
            this.failureFunctionToCall(errors, statusCodes);
            YAHOO.rapidjs.ErrorManager.errorOccurred(this.scope, errors);
        }
    },
    doRequest: function(url, params, callback)
    {
        this.abort();
        var urlAndParams = parseURL(url)
        var paramsArray = this.getParamsArray(params, urlAndParams.params)
        var cb = {
            success: this.processSuccess,
            failure: this.processFailure,
            timeout: this.timeout,
            scope: this,
            cache:false,
            argument : [callback]
        };
        var tmpUrl = urlAndParams.url + "?" + paramsArray.join("&");
        this.lastConnection = YAHOO.util.Connect.asyncRequest('GET', tmpUrl, cb, null);
    },
    getParamsArray: function(requestParams, urlParams){
        if (requestParams == null)
        {
            requestParams = {};
        }
        var paramsArray = [];
        
        for (var param in requestParams) {
            paramsArray[paramsArray.length] = param + "=" + encodeURIComponent(requestParams[param])
        }


        for (var param in urlParams) {
            if(requestParams[param] == null)
            {
                paramsArray[paramsArray.length] = param + "=" + urlParams[param]
            }
        }


        return paramsArray;
    },
    doGetRequest : function(url, params, callback)
    {
        this.doRequest(url, params, callback);
    },
    doPostRequest : function(url, params, callback)
    {
        this.abort();
        var urlAndParams = parseURL(url)
        var paramsArray = this.getParamsArray(params, urlAndParams.params)
        var cb = {
            success: this.processSuccess,
            failure: this.processFailure,
            timeout: this.timeout,
            scope: this,
            cache:false,
            argument : [callback]
        };

        this.lastConnection = YAHOO.util.Connect.asyncRequest('POST', urlAndParams.url, cb, paramsArray.join("&"));
    },

    abort: function()
    {
        if (this.lastConnection) {
            var callStatus = YAHOO.util.Connect.isCallInProgress(this.lastConnection);
            if (callStatus == true) {
                YAHOO.util.Connect.abort(this.lastConnection);
                this.lastConnection = null;
            }
        }
    }
}