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
    this.requester = new YAHOO.rapidjs.Requester(this.processSuccess, this.processFailure, this);
    this.params = null;
    this.format = "xml";
    this.rootTag = config.rootTag;
    this.lastConnection = null;
    this.configureTimeout(config);
};
YAHOO.lang.extend(YAHOO.rapidjs.component.PollingComponentContainer, YAHOO.rapidjs.component.ComponentContainer, {

    poll : function() {
        if(this.isVisible()){
            this.requester.doRequest(this.url, this.params);
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
        this.requester.doRequest(this.url, this.params);
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

        if (YAHOO.rapidjs.Connect.checkAuthentication(response) == false)
        {
            this.handleAuthenticate();
            return;
        }
        else
        {
            this.fireSuccessEvent();
            this.handleSuccess(response);
        }

        this.events["loadstatechanged"].fireDirect(this, false);
        if (this.pollingInterval > 0)
        {
            this.pollTask.delay(this.pollingInterval * 1000);
        }
    },

    handleSuccess: function(response, keepExisting, removeAttribute)
    {
    },
    handleErrors: function(errors)
    {
        this.clearData();
        this.events["error"].fireDirect(this, errors);
        YAHOO.rapidjs.ErrorManager.errorOccurred(this, errors);
    },
    handleAuthenticate: function(response)
    {
    },
    processFailure : function(errors, statusCodes)
    {
        this.events["loadstatechanged"].fireDirect(this, false);
        this.handleErrors(errors);
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

        if (params == null)
        {
            params = {};
        }
        if (this.format)
        {
            params["format"] = this.format;
        }
        this.requester.doRequest(url, params, callback)
        this.events["loadstatechanged"].fireDirect(this, true);
    },
    doGetRequest : function(url, params, callback)
    {
        this.doRequest(url, params, callback);
    },
    doPostRequest : function(url, params, callback)
    {
        if (params == null)
        {
            params = {};
        }
        if (this.format)
        {
            params["format"] = this.format;
        }
        this.requester.doPostRequest(url, params,  callback);
        this.events["loadstatechanged"].fireDirect(this, true);
    },

    abort: function()
    {
        this.requester.abort();
        this.events["loadstatechanged"].fireDirect(this, false);
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
        this.requester.timeout = this.timeout;
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