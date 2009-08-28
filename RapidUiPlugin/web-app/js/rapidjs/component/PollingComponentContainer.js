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
YAHOO.namespace('rapidjs', 'rapidjs.component');
YAHOO.rapidjs.component.PollingComponentContainer = function(container, config)
{
    YAHOO.rapidjs.component.PollingComponentContainer.superclass.constructor.call(this, container, config);
    this.pollTask = new YAHOO.ext.util.DelayedTask(this.poll, this);
    if (config.pollingInterval)
    {
        this.setPollingInterval(config.pollingInterval * 1);
    }
    else
    {
        this.setPollingInterval(0);
    }
    this.params = null;
    this.format = "xml";
    this.rootTag = config.rootTag;
    this.lastConnection = null;
    this.configureTimeout(config);
};
YAHOO.lang.extend(YAHOO.rapidjs.component.PollingComponentContainer, YAHOO.rapidjs.component.ComponentContainer, {

    poll : function() {
        if(this.isVisible()){
            this.doRequest(this.url, this.params);    
        }
    },

    refresh: function(params, title) {
        if (!this.params) {
            this.params = {};
        }
        if (params) {
            for (var param in params) {
                this.params[param] = params[param];
            }
        }
        this.doRequest(this.url, this.params);
        if (title) {
            this.setTitle(title);
        }
    },

    fireSuccessEvent:function()
    {
        this.events["success"].fireDirect(this);
    },
    processSuccess : function(response) {
        YAHOO.rapidjs.ErrorManager.serverUp();
        try
        {

            if (YAHOO.rapidjs.Connect.checkAuthentication(response) == false)
            {
                this.handleAuthenticate();
                return;
            }
            else if (YAHOO.rapidjs.Connect.containsError(response) == false)
            {
                this.fireSuccessEvent();
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
        if (typeof callback == 'function') {
            callback(response);
        }
        if (this.pollingInterval > 0)
        {
            this.pollTask.delay(this.pollingInterval * 1000);
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
        this.events["error"].fireDirect(this, ['Request received timeout.']);
        YAHOO.rapidjs.ErrorManager.errorOccurred(this, ['Request received timeout.']);
    },
    handleInternalServerError: function(response) {
        this.events["error"].fireDirect(this, ["Internal Server Error. Please see the log files."]);
        YAHOO.rapidjs.ErrorManager.errorOccurred(this, ["Internal Server Error. Please see the log files."]);
    },
    handleUnknownUrl: function(response)
    {
        this.events["error"].fireDirect(this, ['Specified url cannot be found.']);
        YAHOO.rapidjs.ErrorManager.errorOccurred(this, ['Specified url cannot be found.']);
    },
    handleAuthenticate: function(response)
    {
    },
    handleServerDown: function(response)
    {
        YAHOO.rapidjs.ErrorManager.serverDown();
    },
    processFailure : function(response)
    {
        if (!this.lastConnection || YAHOO.util.Connect.isCallInProgress(this.lastConnection) == false) {

            this.events["loadstatechanged"].fireDirect(this, false);
        }
        var st = response.status;
        if (st == -1) {
            this.handleTimeout(response);
        }
        else if (st == 404) {
            this.handleUnknownUrl(response);
        }
        else if (st == 500) {
            this.handleInternalServerError(response);
        }
        else if (st == 0) {
            this.handleServerDown(response);
        }
        if (this.pollingInterval > 0)
        {
            this.pollTask.delay(this.pollingInterval * 1000);
        }

    },

    getRootNode: function(data) {
        var node = data.getRootNode(this.rootTag);
        if (!node) {
            this.clearData();
        }
        return node;
    },
    doRequest: function(url, params, callback)
    {
        this.abort();

        if (params == null)
        {
            params = {};
        }
        if (this.format)
        {
            params["format"] = this.format;
        }
        var urlAndParams = parseURL(url)
        var parsedParams = urlAndParams.params;
        for (var param in parsedParams) {
            params[param] = parsedParams[param]
        }
        var paramsArray = [];
        for (var param in params) {
            paramsArray[paramsArray.length] = param + "=" + encodeURIComponent(params[param])
        }

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
        this.events["loadstatechanged"].fireDirect(this, true);
    },
    doGetRequest : function(url, params, callback)
    {
        this.doRequest(url, params, callback);
    },
    doPostRequest : function(url, params, callback)
    {
        this.abort();

        if (params == null)
        {
            params = {};
        }
        if (this.format)
        {
            params["format"] = this.format;
        }
        var urlAndParams = parseURL(url)
        var parsedParams = urlAndParams.params;
        for (var param in parsedParams) {
            params[param] = parsedParams[param]
        }
        var paramsArray = [];
        for (var param in params) {
            paramsArray[paramsArray.length] = param + "=" + encodeURIComponent(params[param])
        }

        var callback = {
            success: this.processSuccess,
            failure: this.processFailure,
            timeout: this.timeout,
            scope: this,
            cache:false,
            argument : [callback]
        };

        this.lastConnection = YAHOO.util.Connect.asyncRequest('POST', urlAndParams.url, callback, paramsArray.join("&"));
        this.events["loadstatechanged"].fireDirect(this, true);
    },

    abort: function()
    {
        if (this.lastConnection) {
            var callStatus = YAHOO.util.Connect.isCallInProgress(this.lastConnection);
            if (callStatus == true) {
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
    configureTimeout: function(config) {
        if (config.timeout) {
            this.timeout = config.timeout * 1000;
        }
        else {
            this.timeout = 30000;
        }
    },
    handleUnvisible: function() {
        this.abort();
        this.pollTask.cancel();
    },

    clearData: function() {
    }
});
YAHOO.util.Connect.initHeader('Pragma', 'no-cache', true);
YAHOO.util.Connect.initHeader('Cache-Control', 'no-cache', true);