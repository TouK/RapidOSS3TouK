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

function getUrlPrefix() {
    var pathName = window.location.pathname;
    var splits = pathName.split('/');
    var urlPrefix = '';
    //minus 1 because index start from 0 not 1 , another minus 1 for the last url split itself
    //add ../ to urlPrefix until RapidSuite is found
    for (var index = splits.length - 2 ; index >= 0; index--) {
        if(splits[index]=="RapidSuite")
        {
            break;
        }
        urlPrefix += '../';
    }
    return urlPrefix;
}
function createURL(url, params)
{
    if (params == null)
    {
        params = {};
    }
    var postData = "";
    for (var paramName in params) {
        postData = postData + paramName + "=" + encodeURIComponent(params[paramName]) + "&";
    }
    if (postData != "")
    {
        postData = postData.substring(0, postData.length - 1);
        if (url.indexOf("?") >= 0)
        {
            url = url + "&" + postData;
        }
        else
        {
            url = url + "?" + postData;
        }
    }
    if (url.indexOf('http') != 0 && url.indexOf('www') != 0) {
        return getUrlPrefix() + url;
    }
    return url
}

function parseURL(completeUrl) {
    var params = {};
    var tmpUrl = completeUrl;
    var postData = '';
    var questionMarkIndex = completeUrl.indexOf("?")
    if (questionMarkIndex >= 0)
    {
        tmpUrl = completeUrl.substring(0, questionMarkIndex)
        postData = completeUrl.substring(questionMarkIndex + 1, completeUrl.length)
    }
    if (postData.length > 0) {
        var keyValuePairs = postData.split("&");
        for (var i = 0; i < keyValuePairs.length; i++) {
            var keyValueArray = keyValuePairs[i].split("=")
            if (keyValueArray.length == 2) {
                params[keyValueArray[0]] = keyValueArray[1];
            }
        }
    }
    return {url:tmpUrl, params:params};
}


String.prototype.trim = function() {
    var a = this.replace(/^\s+/, '');
    return a.replace(/\s+$/, '');
};
String.prototype.toQuery = function() {
    return  '"' + this.escapeQuery() + '"';
}
String.prototype.escapeQuery = function() {
    var a = this.replace(/\\/g, '\\\\');
    var a = a.replace(/"/g, '\\"');
    var a = a.replace(/\(/g, '\\(');
    var a = a.replace(/\)/g, '\\)');
    return a;
};
String.prototype.toExactQuery = function() {
    var a = '"(' + this.escapeQuery() + ')"'
    return a;
};
YAHOO.rapidjs.ArrayUtils = new function()
{
    this.remove = function(arrayP, index)
    {
        if (index >= arrayP.length || index < 0) return;
        var lastIndex = arrayP.length - 1;
        arrayP[index] = arrayP[lastIndex];
        arrayP.splice(lastIndex, 1);
    };
    this.contains = function(array, element) {
        for (var i = 0; i < array.length; i++) {
            if (array[i] == element)
            {
                return true;
            }
        }
        return false;
    };
    this.collect = function(array, collectFunc) {
        var collection = [];
        var nOfItems = array.length;
        for (var i = 0; i < nOfItems; i++) {
            collection.push(collectFunc(array[i]));
        }
        return collection;
    };
    this.find = function(array, findFunc) {
        var nOfItems = array.length;
        for (var i = 0; i < nOfItems; i++) {
            var item = array[i]
            if (findFunc(item)) {
                return item;
            }
        }
        return null;
    };
    this.findIndex = function(array, findFunc) {
        var nOfItems = array.length;
        for (var i = 0; i < nOfItems; i++) {
            var item = array[i]
            if (findFunc(item)) {
                return i;
            }
        }
        return -1;
    };
    this.findAll = function(array, findFunc) {
        var collection = [];
        var nOfItems = array.length;
        for (var i = 0; i < nOfItems; i++) {
            var item = array[i]
            if (findFunc(item)) {
                collection.push(item);
            }
        }
        return collection;
    }
}();
var ArrayUtils = YAHOO.rapidjs.ArrayUtils
YAHOO.rapidjs.FlashUtils = new function()
{
    // JavaScript helper required to detect Flash Player PlugIn version information
    this.GetSwfVer = function () {
        // NS/Opera version >= 3 check for Flash plugin in plugin array
        var flashVer = -1;

        if (navigator.plugins != null && navigator.plugins.length > 0) {
            if (navigator.plugins["Shockwave Flash 2.0"] || navigator.plugins["Shockwave Flash"]) {
                var swVer2 = navigator.plugins["Shockwave Flash 2.0"] ? " 2.0" : "";
                var flashDescription = navigator.plugins["Shockwave Flash" + swVer2].description;
                var descArray = flashDescription.split(" ");
                var tempArrayMajor = descArray[2].split(".");
                var versionMajor = tempArrayMajor[0];
                var versionMinor = tempArrayMajor[1];
                if (descArray[3] != "") {
                    tempArrayMinor = descArray[3].split("r");
                } else {
                    tempArrayMinor = descArray[4].split("r");
                }
                var versionRevision = tempArrayMinor[1] > 0 ? tempArrayMinor[1] : 0;
                var flashVer = versionMajor + "." + versionMinor + "." + versionRevision;
            }
        }
            // MSN/WebTV 2.6 supports Flash 4
        else if (navigator.userAgent.toLowerCase().indexOf("webtv/2.6") != -1) flashVer = 4;
            // WebTV 2.5 supports Flash 3
        else if (navigator.userAgent.toLowerCase().indexOf("webtv/2.5") != -1) flashVer = 3;
            // older WebTV supports Flash 2
        else if (navigator.userAgent.toLowerCase().indexOf("webtv") != -1) flashVer = 2;
        else if (YAHOO.util.Event.isIE) {
            flashVer = YAHOO.rapidjs.FlashUtils.ControlVersion();
        }
        return flashVer;
    };

    // When called with reqMajorVer, reqMinorVer, reqRevision returns true if that version or greater is available
    this.DetectFlashVer = function (reqMajorVer, reqMinorVer, reqRevision)
    {


        var versionStr = YAHOO.rapidjs.FlashUtils.GetSwfVer();
        if (versionStr == -1) {
            return false;
        } else if (versionStr != 0) {
            if (YAHOO.util.Event.isIE) {
                // Given "WIN 2,0,0,11"
                tempArray = versionStr.split(" "); // ["WIN", "2,0,0,11"]
                tempString = tempArray[1]; // "2,0,0,11"
                versionArray = tempString.split(","); // ['2', '0', '0', '11']
            } else {
                versionArray = versionStr.split(".");
            }
            var versionMajor = versionArray[0];
            var versionMinor = versionArray[1];
            var versionRevision = versionArray[2];

	 // is the major.revision >= requested major.revision AND the minor version >= requested minor
            if (versionMajor > parseFloat(reqMajorVer)) {
                return true;
            } else if (versionMajor == parseFloat(reqMajorVer)) {
                if (versionMinor > parseFloat(reqMinorVer))
                    return true;
                else if (versionMinor == parseFloat(reqMinorVer)) {
                    if (versionRevision >= parseFloat(reqRevision))
                        return true;
                }
            }
            return false;
        }
    };

    this.ControlVersion = function()
    {
        var version;
        var axo;
        var e;

		// NOTE : new ActiveXObject(strFoo) throws an exception if strFoo isn't in the registry

        try {
            // version will be set for 7.X or greater players
            axo = new ActiveXObject("ShockwaveFlash.ShockwaveFlash.7");
            version = axo.GetVariable("$version");
        } catch (e) {
        }

        if (!version)
        {
            try {
                // version will be set for 6.X players only
                axo = new ActiveXObject("ShockwaveFlash.ShockwaveFlash.6");

				// installed player is some revision of 6.0
                // GetVariable("$version") crashes for versions 6.0.22 through 6.0.29,
                // so we have to be careful.

                // default to the first public version
                version = "WIN 6,0,21,0";

				// throws if AllowScripAccess does not exist (introduced in 6.0r47)
                axo.AllowScriptAccess = "always";

				// safe to call for 6.0r47 or greater
                version = axo.GetVariable("$version");

            } catch (e) {
            }
        }

        if (!version)
        {
            try {
                // version will be set for 4.X or 5.X player
                axo = new ActiveXObject("ShockwaveFlash.ShockwaveFlash.3");
                version = axo.GetVariable("$version");
            } catch (e) {
            }
        }

        if (!version)
        {
            try {
                // version will be set for 3.X player
                axo = new ActiveXObject("ShockwaveFlash.ShockwaveFlash.3");
                version = "WIN 3,0,18,0";
            } catch (e) {
            }
        }

        if (!version)
        {
            try {
                // version will be set for 2.X player
                axo = new ActiveXObject("ShockwaveFlash.ShockwaveFlash");
                version = "WIN 2,0,0,11";
            } catch (e) {
                version = -1;
            }
        }

        return version;
    }
}();
YAHOO.rapidjs.ObjectUtils = new function()
{
    this.clone = function(obj, deep)
    {
        var objectClone = new obj.constructor();
        for (var property in obj)
            if (!deep)
                objectClone[property] = obj[property];
            else if (typeof obj[property] == 'object')
                objectClone[property] = YAHOO.rapidjs.ObjectUtils.clone(obj[property], deep);
            else
                objectClone[property] = obj[property];
        return objectClone;
    };
}();

YAHOO.rapidjs.Connect = new function()
{
    this.containsError = function(response)
    {
        try
        {
            if (response.responseXML)
            {
                var errors = response.responseXML.getElementsByTagName("Errors");
                if (!errors)
                {
                    return false;
                }
                if (errors.length != null)
                {
                    return errors.length > 0;
                }
                else
                {
                    return errors != null;
                }
            }
            else
            {
                //if response.responseXML is undefined, than we have no errors since RI sends errors in XML.
                return false;
            }
        }
        catch(e)
        {
            return response.responseText.indexOf("Errors") >= 0
        }

    };

    this.getErrorMessages = function(xmlDoc) {
        var errors = [];
        var errorNodes = xmlDoc.getElementsByTagName('Error');
        for (var i = 0; i < errorNodes.length; i++) {
            errors.push(errorNodes[i].getAttribute('error'));
        }
        return errors;
    };
    this.getSuccessMessage = function(xmlDoc) {
        var success = xmlDoc.getElementsByTagName('Successful');
        if (success && success.length > 0 && success[0].firstChild)
        {
            return success[0].firstChild.nodeValue;
        }
        else
        {
            return "";
        }
    };

    this.checkAuthentication = function(xmlDoc) {
        if (xmlDoc.responseXML == null) return true;
        var authenticate = xmlDoc.responseXML.getElementsByTagName('Authenticate');
        if (authenticate && authenticate.length > 0)
        {
            var location = window.location.pathname.substring("/RapidSuite".length);
            if (window.location.search != "")
            {
                location = location + "?" + window.location.search;
            }
            window.location = getUrlPrefix() + "auth/login?targetUri=" + encodeURI(location);
            return false;
        }
        else
        {
            return true;
        }
    };
}();

YAHOO.rapidjs.DomUtils = new function()
{
    this.findParent = function(element, className) {
        element = element.parentNode;
        while (element)
        {
            if (YAHOO.util.Dom.hasClass(element, className))
            {
                return element;
            }
            element = element.parentNode;
        }
        return null;
    };
    this.findChild = function(element, className) {
        var childNodes = element.childNodes;
        for (var index = 0; index < childNodes.length; index++) {
            if (YAHOO.util.Dom.hasClass(childNodes[index], className))
            {
                return childNodes[index];
            }
        }
        return null;
    };
    this.getElementFromChild = function(childEl, parentClass) {
        if (!childEl || (YAHOO.util.Dom.hasClass(childEl, parentClass))) {
            return childEl;
        }
        var p = childEl.parentNode;
        var b = document.body;
        while (p && p != b) {
            if (YAHOO.util.Dom.hasClass(p, parentClass)) {
                return p;
            }
            p = p.parentNode;
        }
        return null;
    };
}();

YAHOO.rapidjs.CursorManager = new function() {
    this.processes = new Array();
    this.idle = function() {
        this.processes.pop();
        if (this.processes.length == 0) {
            document.body.style.cursor = '';
        }
    };
    this.busy = function() {
        this.processes.push('');
        document.body.style.cursor = 'wait';
    };
}();

YAHOO.rapidjs.ErrorManager = new function() {
    this.errorOccurredEvent = new YAHOO.util.CustomEvent('errorOccurred');
    this.serverDownEvent = new YAHOO.util.CustomEvent('serverDown');
    this.serverUpEvent = new YAHOO.util.CustomEvent('serverUp');
    this.errorOccurred = function(obj, messages) {
        this.errorOccurredEvent.fireDirect(obj, messages)
    };

    this.serverDown = function() {
        this.serverDownEvent.fireDirect()
    };
    this.serverUp = function() {
        this.serverUpEvent.fireDirect()
    };
}();



