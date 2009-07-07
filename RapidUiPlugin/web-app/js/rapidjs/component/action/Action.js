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
YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.action');
YAHOO.rapidjs.component.action.RequestAction = function(config, requestParams, components)
{
    this.url = config.url;
    this.id = config.id;
    this.submitType = config.submitType || 'GET'
    this.condition = config.condition;
    this.components = components;
    this.allowMultipleRequests = config.allowMultipleRequests;
    this.events = {
        'success' : new YAHOO.util.CustomEvent('success'),
        'error' : new YAHOO.util.CustomEvent('error'),
        'timeout' : new YAHOO.util.CustomEvent('timeout'),
        'unknownUrl' : new YAHOO.util.CustomEvent('unknownUrl'),
        'internalServerError' : new YAHOO.util.CustomEvent('internalServerError'),
        'serverDown' : new YAHOO.util.CustomEvent('serverDown')
    };
    this.lastConnection = null;
    this.requestParams = requestParams;
    this.timeout = config.timeout != null ? config.timeout : 30000;
    YAHOO.rapidjs.Actions[this.id] = this;
};
YAHOO.rapidjs.component.action.RequestAction.prototype = {

    execute: function(params) {
        try {
            var conditionResult = true;
            if (this.condition) {
                var conditionResult = eval(this.condition);
            }
            if (conditionResult) {
                var reqParams = {};
                for (var param in this.requestParams) {
                    reqParams[param] = eval('(' + this.requestParams[param] + ')');
                }
                this._execute(reqParams)
            }
        }
        catch(err) {
            this.events['error'].fireDirect([err.description])
            throw err.description;
        }

    },
    _execute : function(requestParams) {
        if (this.allowMultipleRequests != true)
        {
            this.abort();
        }
        var callback = {
            success:this.processSuccess,
            failure: this.processFailure,
            scope: this,
            timeout: this.timeout
        };
        var urlAndParams = parseURL(this.url);
        var params = urlAndParams.params;
        for(var param in requestParams){
            params[param] = requestParams[param];
        }
        if(!params['format']){
            params["format"] = "xml";
        }
        var postDataArray = [];
        for(var param in params){
            postDataArray[postDataArray.length] = param + "=" + encodeURIComponent(params[param]) 
        }
        if (this.submitType == 'GET') {

            var tmpUrl = urlAndParams.url + "?" + postDataArray.join("&")
            this.lastConnection = YAHOO.util.Connect.asyncRequest('GET', tmpUrl, callback);
        }
        else {
            this.lastConnection = YAHOO.util.Connect.asyncRequest('POST', urlAndParams.url, callback, postDataArray.join("&"));
        }
    },
    processSuccess: function(response)
    {
        YAHOO.rapidjs.ErrorManager.serverUp();
        try
        {

            if (YAHOO.rapidjs.Connect.checkAuthentication(response) == false)
            {
                return;
            }
            else if (YAHOO.rapidjs.Connect.containsError(response) == false)
            {
                this.handleSuccess(response);
                this.events['success'].fireDirect(response);
            }
            else
            {
                this.handleErrors(response);
                this.events['error'].fireDirect(YAHOO.rapidjs.Connect.getErrorMessages(response.responseXML));
            }
        }
        catch(e)
        {
        }

    },
    processFailure: function(response)
    {
        var st = response.status;
        if (st == -1) {
            this._fireErrors(response, ['Request received timeout.']);
            this.events['timeout'].fireDirect();
        }
        else if (st == 404) {
            this._fireErrors(response, ['Specified url cannot be found.']);
            this.events['unknownUrl'].fireDirect();
        }
         else if (st == 500) {
            this._fireErrors(response, ['Internal Server Error. Please see the log files.']);
            this.events['internalServerError'].fireDirect();
        }
        else if (st == 0) {
            YAHOO.rapidjs.ErrorManager.serverDown();
            this.events['serverDown'].fireDirect();
        }

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
    },
    handleSuccess: function(response) {
        var componentList = this.components;
        if (componentList && componentList.length) {
            for (var i = 0; i < componentList.length; i++) {
                componentList[i].events['success'].fireDirect(response);
            }
        }
    },
    handleErrors: function(response) {
        var errors = YAHOO.rapidjs.Connect.getErrorMessages(response.responseXML);
        this._fireErrors(response, errors);

    },
    _fireErrors : function(response, errors) {
        var componentList = this.components;
        if (componentList && componentList.length) {
            for (var i = 0; i < componentList.length; i++) {
                componentList[i].events['error'].fireDirect(componentList[i], errors);
            }
        }
        YAHOO.rapidjs.ErrorManager.errorOccurred(this, errors);
    }
};

YAHOO.rapidjs.component.action.MergeAction = function(config, requestParams, components) {
    YAHOO.rapidjs.component.action.MergeAction.superclass.constructor.call(this, config, requestParams, components);
    this.removeAttribute = config.removeAttribute;
};

YAHOO.lang.extend(YAHOO.rapidjs.component.action.MergeAction, YAHOO.rapidjs.component.action.RequestAction, {
    handleSuccess: function(response)
    {
        if (this.components) {
            var componentList = this.components;
            for (var i = 0; i < componentList.length; i++) {
                componentList[i].events['success'].fireDirect();
                componentList[i].handleSuccess(response, true, this.removeAttribute)
            }
        }
    }
});

YAHOO.rapidjs.component.action.FunctionAction = function(id, component, fnc, condition, arguments) {
    this.component = component;
    this.targetFunction = fnc;
    this.arguments = arguments;
    this.condition = condition;
    this.id = id;
    this.events = {
        'success' : new YAHOO.util.CustomEvent('success'),
        'error' : new YAHOO.util.CustomEvent('error')
    };
    YAHOO.rapidjs.Actions[this.id] = this;
};

YAHOO.rapidjs.component.action.FunctionAction.prototype = {
    execute: function(params) {
        try {
            var conditionResult = true;
            if (this.condition) {
                var conditionResult = eval(this.condition);
            }
            if (conditionResult) {
                if (this.component.popupWindow) {
                    this.component.popupWindow.show();
                }
                var args = [];
                for (var i = 0; i < this.arguments.length; i++) {
                    args[args.length] = eval('(' + this.arguments[i] + ')');
                }
                this.targetFunction.apply(this.component, args);
            }
            this.events['success'].fireDirect();
        }
        catch(err) {
            this.events['error'].fireDirect([err.description])
        }

    }
};
YAHOO.rapidjs.component.action.LinkAction = function(id, urlExp, condition) {
    this.condition = condition;
    this.urlExp = urlExp;
    this.id = id;
    this.events = {
        'error' : new YAHOO.util.CustomEvent('error')
    };
    YAHOO.rapidjs.Actions[this.id] = this;
};

YAHOO.rapidjs.component.action.LinkAction.prototype = {
    execute: function(params) {
        try {
            var conditionResult = true;
            if (this.condition) {
                var conditionResult = eval(this.condition);
            }
            if (conditionResult) {
                var url = eval(this.urlExp)
                window.location = url;
            }
        }
        catch(err) {
            this.events['error'].fireDirect([err.description])
        }
    }
};
YAHOO.rapidjs.Actions = {};