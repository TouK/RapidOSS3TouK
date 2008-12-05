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
YAHOO.widget.AutoComplete.prototype.willSelectFirstItem = false;
YAHOO.widget.AutoComplete.prototype._onTextboxKeyDown = function(v,oSelf) {
    var nKeyCode = v.keyCode;

    switch (nKeyCode) {
        case 9: // tab
            if(oSelf.delimChar && (oSelf._nKeyCode != nKeyCode)) {
                if(oSelf._bContainerOpen) {
                    YAHOO.util.Event.stopEvent(v);
                }
            }
            // select an item or clear out
            if(oSelf._oCurItem) {
                oSelf._selectItem(oSelf._oCurItem);
            }
            else {
                oSelf._toggleContainer(false);
            }
            break;
        case 13: // enter
            if(oSelf._nKeyCode != nKeyCode) {
                if(oSelf._bContainerOpen) {
                    YAHOO.util.Event.stopEvent(v);
                }
            }
            if(oSelf._oCurItem) {
                oSelf._selectItem(oSelf._oCurItem);
            }
            else {
            	if(oSelf.queryDelay > 0 && oSelf._nDelayID == -1){
            		var sText = oSelf._oTextbox.value; //string in textbox
				    // Set timeout on the request
				    if (oSelf.queryDelay > 0) {
				        var nDelayID =
				            setTimeout(function(){oSelf._sendQuery(sText);},(oSelf.queryDelay * 1000));
				
				        if (oSelf._nDelayID != -1) {
				            clearTimeout(oSelf._nDelayID);
				        }
				
				        oSelf._nDelayID = nDelayID;
				    }
				    else {
				        // No delay so send request immediately
				        oSelf._sendQuery(sText);
				    }
            	}
            	oSelf.willSelectFirstItem = true;
                oSelf._toggleContainer(false);
            }
            break;
        case 27: // esc
            oSelf._toggleContainer(false);
            return;
        case 39: // right
            oSelf._jumpSelection();
            break;
        case 38: // up
            YAHOO.util.Event.stopEvent(v);
            oSelf._moveSelection(nKeyCode);
            break;
        case 40: // down
            YAHOO.util.Event.stopEvent(v);
            oSelf._moveSelection(nKeyCode);
            break;
        default:
            break;
    }
};
YAHOO.widget.AutoComplete.prototype._populateList = function(sQuery, aResults, oSelf) {
    if(aResults === null) {
        oSelf.dataErrorEvent.fire(oSelf, sQuery);
    }
    if(oSelf.willSelectFirstItem == true){
    	if(aResults && aResults[0] && aResults[0][0] == sQuery){
    		oSelf._cancelIntervalDetection(oSelf);
    		oSelf.itemSelectEvent.fire(oSelf, null, aResults[0]);
    	}
    	oSelf.willSelectFirstItem = false;
    	return;
    }
    if (!oSelf._bFocused || !aResults) {
        return;
    }

    var isOpera = (navigator.userAgent.toLowerCase().indexOf("opera") != -1);
    var contentStyle = oSelf._oContainer._oContent.style;
    contentStyle.width = (!isOpera) ? null : "";
    contentStyle.height = (!isOpera) ? null : "";

    var sCurQuery = decodeURIComponent(sQuery);
    oSelf._sCurQuery = sCurQuery;
    oSelf._bItemSelected = false;

    if(oSelf._maxResultsDisplayed != oSelf.maxResultsDisplayed) {
        oSelf._initList();
    }

    var nItems = Math.min(aResults.length,oSelf.maxResultsDisplayed);
    oSelf._nDisplayedItems = nItems;
    if (nItems > 0) {
        oSelf._initContainerHelpers();
        var aItems = oSelf._aListItems;

        // Fill items with data
        for(var i = nItems-1; i >= 0; i--) {
            var oItemi = aItems[i];
            var oResultItemi = aResults[i];
            oItemi.innerHTML = oSelf.formatResult(oResultItemi, sCurQuery);
            oItemi.style.display = "list-item";
            oItemi._sResultKey = oResultItemi[0];
            oItemi._oResultData = oResultItemi;

        }

        // Empty out remaining items if any
        for(var j = aItems.length-1; j >= nItems ; j--) {
            var oItemj = aItems[j];
            oItemj.innerHTML = null;
            oItemj.style.display = "none";
            oItemj._sResultKey = null;
            oItemj._oResultData = null;
        }

        if(oSelf.autoHighlight) {
            // Go to the first item
            var oFirstItem = aItems[0];
            oSelf._toggleHighlight(oFirstItem,"to");
            oSelf.itemArrowToEvent.fire(oSelf, oFirstItem);
            oSelf._typeAhead(oFirstItem,sQuery);
        }
        else {
            oSelf._oCurItem = null;
        }

        // Expand the container
        var ok = oSelf.doBeforeExpandContainer(oSelf._oTextbox, oSelf._oContainer, sQuery, aResults);
        oSelf._toggleContainer(ok);
    }
    else {
        oSelf._toggleContainer(false);
    }
    oSelf.dataReturnEvent.fire(oSelf, sQuery, aResults);
};
YAHOO.widget.AutoComplete.prototype._sendQuery = function(sQuery) {
    // Widget has been effectively turned off
    if(this.minQueryLength == -1) {
        this._toggleContainer(false);
        return;
    }
    // Delimiter has been enabled
    var aDelimChar = (this.delimChar) ? this.delimChar : null;
    if(aDelimChar) {
        // Loop through all possible delimiters and find the latest one
        // A " " may be a false positive if they are defined as delimiters AND
        // are used to separate delimited queries
        var nDelimIndex = -1;
        for(var i = aDelimChar.length-1; i >= 0; i--) {
            var nNewIndex = sQuery.lastIndexOf(aDelimChar[i]);
            if(nNewIndex > nDelimIndex) {
                nDelimIndex = nNewIndex;
            }
        }
        // If we think the last delimiter is a space (" "), make sure it is NOT
        // a false positive by also checking the char directly before it
        if(aDelimChar[i] == " ") {
            for (var j = aDelimChar.length-1; j >= 0; j--) {
                if(sQuery[nDelimIndex - 1] == aDelimChar[j]) {
                    nDelimIndex--;
                    break;
                }
            }
        }
        // A delimiter has been found so extract the latest query
        if (nDelimIndex > -1) {
            var nQueryStart = nDelimIndex + 1;
            // Trim any white space from the beginning...
            while(sQuery.charAt(nQueryStart) == " ") {
                nQueryStart += 1;
            }
            // ...and save the rest of the string for later
            this._sSavedQuery = sQuery.substring(0,nQueryStart);
            // Here is the query itself
            sQuery = sQuery.substr(nQueryStart);
        }
        else if(sQuery.indexOf(this._sSavedQuery) < 0){
            this._sSavedQuery = null;
        }
    }

    // Don't search queries that are too short
    if (sQuery && (sQuery.length < this.minQueryLength) || (!sQuery && this.minQueryLength > 0)) {
        if (this._nDelayID != -1) {
            clearTimeout(this._nDelayID);
        }
        this._toggleContainer(false);
        this._nDelayID = -1;
        this.willSelectFirstItem = false;
        return;
    }

    sQuery = encodeURIComponent(sQuery);
    this._nDelayID = -1;    // Reset timeout ID because request has been made
    this.dataRequestEvent.fire(this, sQuery);
    this.dataSource.getResults(this._populateList, sQuery, this);
};

YAHOO.widget.DataSource.prototype._init = function() {
    // Validate and initialize public configs
    var maxCacheEntries = this.maxCacheEntries;
    if(isNaN(maxCacheEntries) || (maxCacheEntries < 0)) {
        maxCacheEntries = 0;
    }
    // Initialize local cache
    if(maxCacheEntries > 0 && !this._aCache) {
        this._aCache = [];
    }
    
    this._sName = "instance" + YAHOO.widget.DataSource._nIndex;
    YAHOO.widget.DataSource._nIndex++;
    
    this.queryEvent = new YAHOO.util.CustomEvent("query", this);
    this.cacheQueryEvent = new YAHOO.util.CustomEvent("cacheQuery", this);
    this.getResultsEvent = new YAHOO.util.CustomEvent("getResults", this);
    this.getCachedResultsEvent = new YAHOO.util.CustomEvent("getCachedResults", this);
    this.dataErrorEvent = new YAHOO.util.CustomEvent("dataError", this);
    this.cacheFlushEvent = new YAHOO.util.CustomEvent("cacheFlush", this);
    this.errorOccurredEvent = new YAHOO.util.CustomEvent("erroroccurred", this);
    this.loadStateChangedEvent = new YAHOO.util.CustomEvent("loadstatechanged", this);
};

YAHOO.widget.DS_XHR.prototype.parseResponse = function(sQuery, oResponse, oParent)
{
	var aSchema = this.schema;
    var aResults = [];
    var bError = false;

    // Strip out comment at the end of results
    var nEnd = ((this.responseStripAfter !== "") && (oResponse.indexOf)) ?
        oResponse.indexOf(this.responseStripAfter) : -1;
    if(nEnd != -1) {
        oResponse = oResponse.substring(0,nEnd);
    }

    switch (this.responseType) {
        case YAHOO.widget.DS_XHR.TYPE_XML:
            // Get the collection of results
            var xmlList = oResponse.getElementsByTagName(aSchema[0]);
            if(!xmlList) {
                bError = true;
                break;
            }
            
            if(this.schema.length == 1)
			{
		        if(xmlList.length > 0)
		        {
		        	var attrs = xmlList[0].attributes;	
		        	for(var index=0; index<attrs.length; index++) {
		        		this.schema[index+1] = attrs[index].nodeName;
		        	}
		        }
			}
            // Loop through each result
            for(var k = xmlList.length-1; k >= 0 ; k--) {
                var result = xmlList.item(k);
                var aFieldSet = [];
                // Loop through each data field in each result using the schema
                for(var m = aSchema.length-1; m >= 1 ; m--) {
                    var sValue = null;
                    // Values may be held in an attribute...
                    var xmlAttr = result.attributes.getNamedItem(aSchema[m]);
                    if(xmlAttr) {
                        sValue = xmlAttr.value;
                    }
                    // ...or in a node
                    else{
                        var xmlNode = result.getElementsByTagName(aSchema[m]);
                        if(xmlNode && xmlNode.item(0) && xmlNode.item(0).firstChild) {
                            sValue = xmlNode.item(0).firstChild.nodeValue;
                        }
                        else {
                            sValue = "";
                        }
                    }
                    // Capture the schema-mapped data field values into an array
                    aFieldSet.unshift(sValue);
                }
                // Capture each array of values into an array of results
                aResults.unshift(aFieldSet);
            }
            break;
        default:
            break;
    }
    sQuery = null;
    oResponse = null;
    oParent = null;
    if(bError) {
        return null;
    }
    else {
        return aResults;
    }
};

YAHOO.widget.DS_XHR.prototype.doQuery = function(oCallbackFn, sQuery, oParent) {
	this.oParent = oParent;
    var charToAppend = "?";
    if(this.scriptURI.indexOf("?") > -1)
    {
    	charToAppend = "&";
    }
    var sUri = this.scriptURI + charToAppend + this.scriptQueryParam+"="+sQuery;
    if(this.scriptQueryAppend.length > 0) {
        sUri += "&" + this.scriptQueryAppend;
    }
    var oResponse = null;
    
    var oSelf = this;
    /*
     * Sets up ajax request callback
     *
     * @param {object} oReq          HTTPXMLRequest object
     * @private
     */
    
    var oCallback = {
        success:this.responseSuccess,
        failure:this.responseFailure, 
        scope: this,
        argument: [oCallbackFn, sQuery]
    };
    
    if(!isNaN(this.connTimeout) && this.connTimeout > 0) {
        oCallback.timeout = this.connTimeout;
    }
    
    if(this._oConn && YAHOO.util.Connect.isCallInProgress(this._oConn) == true) {
        this.connMgr.abort(this._oConn);
        oSelf.loadStateChangedEvent.fireDirect(false);
    }
    
    oSelf._oConn = this.connMgr.asyncRequest("GET", sUri, oCallback, null);
    oSelf.loadStateChangedEvent.fireDirect(true);
};

YAHOO.widget.DS_XHR.prototype.responseSuccess =  function(oResp){
	YAHOO.rapidjs.ServerStatus.refreshState(true);
	var isXML = (this.responseType == YAHOO.widget.DS_XHR.TYPE_XML);
	var oSelf = this;
	oCallbackFn = oResp.argument[0];
	var sQuery = oResp.argument[1];
    if(!oSelf._oConn || (oResp.tId != oSelf._oConn.tId)) {
        oSelf.dataErrorEvent.fire(oSelf, oSelf.oParent, sQuery, YAHOO.widget.DataSource.ERROR_DATANULL);
        return;
    }
    if(YAHOO.rapidjs.Connect.isAuthenticated(oResp) == true){
    	if(YAHOO.rapidjs.Connect.containsError(oResp) == false)
		{
			oSelf.errorOccurredEvent.fireDirect(false,'');
			if(!isXML) {
	            oResp = oResp.responseText;
	        }
	        else { 
	            oResp = oResp.responseXML;
	        }
	        if(oResp === null) {
	            oSelf.dataErrorEvent.fire(oSelf, oSelf.oParent, sQuery, YAHOO.widget.DataSource.ERROR_DATANULL);
	            oSelf.loadStateChangedEvent.fireDirect(false);
	            return;
	        }
	
	        var aResults = oSelf.parseResponse(sQuery, oResp, oSelf.oParent);
	        var resultObj = {};
	        resultObj.query = decodeURIComponent(sQuery);
	        resultObj.results = aResults;
	        if(aResults === null) {
	            oSelf.dataErrorEvent.fire(oSelf, oSelf.oParent, sQuery, YAHOO.widget.DataSource.ERROR_DATAPARSE);
	            aResults = [];
	        }
	        else {
	            oSelf.getResultsEvent.fire(oSelf, oSelf.oParent, sQuery, aResults);
	            oSelf._addCacheElem(resultObj);
	        }
	        oCallbackFn(sQuery, aResults, oSelf.oParent);
		}
		else
		{
			oSelf.errorOccurredEvent.fireDirect(true, oResp.responseXML);
		}
		oSelf.loadStateChangedEvent.fireDirect(false);
    }
    else{
    	window.location = "login.html?page=" + window.location.pathname;
    }
};
YAHOO.widget.DS_XHR.prototype.responseFailure =  function(oResp){
	var oSelf = this;
	var sQuery = oResp.argument[1];
	oSelf.oParent.willSelectFirstItem = false;
    oSelf.dataErrorEvent.fire(oSelf, oSelf.oParent, sQuery, YAHOO.widget.DS_XHR.ERROR_DATAXHR);
    if(!oSelf._oConn|| YAHOO.util.Connect.isCallInProgress(oSelf._oConn) == false){
		
		oSelf.loadStateChangedEvent.fireDirect(false);
	}
	var st = oResp.status;
	if(st == -1){
		oSelf.errorOccurredEvent.fireDirect(true, 'Request received a timeout');
	}
	else if(st == 404){
		oSelf.errorOccurredEvent.fireDirect(true, 'Specified url cannot be found');
	}
	else if(st == 0){
		oSelf.errorOccurredEvent.fireDirect(true, 'Server is not available');
		YAHOO.rapidjs.ServerStatus.refreshState(false);
	}
    return;
};
