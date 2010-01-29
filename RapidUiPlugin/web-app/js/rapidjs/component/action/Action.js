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
        'error' : new YAHOO.util.CustomEvent('error')
    };
    this.lastConnection = null;
    this.requestParams = requestParams;
    this.timeout = config.timeout != null ? config.timeout : 30000;
    this.requester = new YAHOO.rapidjs.Requester(this.processSuccess, this.processFailure, this, this.timeout);
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
        var urlAndParams = parseURL(this.url);
        if(!urlAndParams.params['format']){
            urlAndParams.params["format"] = "xml";
        }
        var params = urlAndParams.params;
        for(var param in requestParams){
            params[param] = requestParams[param];
        }
        if (this.submitType == 'GET') {
            this.requester.doGetRequest(urlAndParams.url, params);
        }
        else {
            this.requester.doPostRequest(urlAndParams.url, params);
        }
        this.setComponentLoadState(true);

    },
    setComponentLoadState: function(loadState)
    {
        var componentList = this.components;
        if (componentList && componentList.length) {
            for (var i = 0; i < componentList.length; i++) {
                componentList[i].events['loadstatechanged'].fireDirect(componentList[i], loadState);
            }
        }    
    },
    processSuccess: function(response)
    {

            if (YAHOO.rapidjs.Connect.checkAuthentication(response) == false)
            {
                return;
            }
            else
            {
                this.handleSuccess(response);
                this.events['success'].fireDirect(response);
                this.setComponentLoadState(false);
            }

    },
    processFailure: function(errors, statusCodes)
    {
        this.events['error'].fireDirect();
        this._fireErrors(errors);
        this.setComponentLoadState(false);
    },
    handleSuccess: function(response) {
        var componentList = this.components;
        if (componentList && componentList.length) {
            for (var i = 0; i < componentList.length; i++) {
                componentList[i].events['success'].fireDirect(response);
            }
        }
    },
    _fireErrors : function( errors) {
        var componentList = this.components;
        if (componentList && componentList.length) {
            for (var i = 0; i < componentList.length; i++) {
                componentList[i].events['error'].fireDirect(componentList[i], errors);
            }
        }
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
YAHOO.rapidjs.component.action.ExecuteJavascriptAction = function(id, fnc, condition) {
    this.targetFunction = fnc;
    this.condition = condition;
    this.id = id;
    this.events = {
        'success' : new YAHOO.util.CustomEvent('success'),
        'error' : new YAHOO.util.CustomEvent('error')
    };
    YAHOO.rapidjs.Actions[this.id] = this;
};

YAHOO.rapidjs.component.action.ExecuteJavascriptAction.prototype = {
    execute: function(params) {
        try {
            var conditionResult = true;
            if (this.condition) {
                var conditionResult = eval(this.condition);
            }
            if (conditionResult) {
                this.targetFunction.apply(this.component, []);
            }
            this.events['success'].fireDirect();
        }
        catch(err) {
            this.events['error'].fireDirect([err.description])
        }

    }
};
YAHOO.rapidjs.component.action.LinkAction = function(id, urlExp, condition, target) {
    this.condition = condition;
    this.urlExp = urlExp;
    this.id = id;
    this.target = target || 'self';
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
                if(this.target == 'self'){
                    window.location = url;    
                }
                else{
                    window.open(url);
                }

            }
        }
        catch(err) {
            this.events['error'].fireDirect([err.description])
        }
    }
};
YAHOO.rapidjs.Actions = {};