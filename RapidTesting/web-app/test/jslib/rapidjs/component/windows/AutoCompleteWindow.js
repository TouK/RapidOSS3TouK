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
YAHOO.rapidjs.component.windows.AutoCompleteWindow = function(container, config)
{
	YAHOO.rapidjs.component.windows.AutoCompleteWindow.superclass.constructor.call(this,container, config);
	this.contentPath = config.contentPath;
	this.scriptQueryParam = config.queryParamName;
	this.inputElementName = this.id + "_searchinput";
	this.render();
	this.datasource = new YAHOO.widget.DS_XHR(this.url, [this.contentPath]);
    this.datasource.responseType = YAHOO.widget.DS_XHR.TYPE_XML;
    this.datasource.scriptQueryParam = this.scriptQueryParam; 
    this.datasource.maxCacheEntries = 100;
    this.datasource.queryMatchCase = true;
    this.oAutoComp = new YAHOO.rapidjs.component.autocomplete.AutoComplete(this.inputElementName, this.suggestion.dom, this.datasource, null, this.searchButton);
    this.oAutoComp.allowBrowserAutocomplete = false; 
    this.oAutoComp.itemSelectEvent.subscribe(this.itemSelected, this,true);
    this.oAutoComp.dataRequestEvent.subscribe(this.positionSuggestion, this, true);
    this.oAutoComp.noResultFoundEvent.subscribe(this.noResultFound, this, true);
    this.datasource.errorOccurredEvent.subscribe(this.fireErrorOccured, this, true);
    this.datasource.loadStateChangedEvent.subscribe(this.fireLoadStateChanged, this, true);
    this.panel = new YAHOO.rapidjs.component.layout.RapidPanel(this.container, {title:this.title, fitToFrame:true});
    this.errorDlg = new YAHOO.rapidjs.component.dialogs.ErrorDialog();
    this.errorDlg.setErrorText('No result is found');
    this.subscribeToPanel();
};

YAHOO.extendX(YAHOO.rapidjs.component.windows.AutoCompleteWindow, YAHOO.rapidjs.component.ComponentContainer, {
	render: function()
	{
		var dh = YAHOO.ext.DomHelper;
		var wrapper = dh.append(this.container, {tag:'div', cls:'ri-autocomplete-wrapper', 
			html:'<div class="ri-autocomplete-pos"><table><tbody><tr>' +
					'<td><div>Device Name:&nbsp;&nbsp;&nbsp;&nbsp;</div></td>' +
					'<td><div class="ri-autocomplete-inputwrap"><input class="ri-autocomplete-input" id="' +this.inputElementName + '"></input></div></td>' +
					'<td><div class="ri-autocomplete-btwrap"></div></td>' +
					'</tr></tbody></table></div>'});
		this.inputElement = wrapper.getElementsByTagName('input')[0];
		var searchButtonWr = YAHOO.util.Dom.getElementsByClassName('ri-autocomplete-btwrap', 'div', wrapper)[0];
		var bconfig = {
            handler: this.handleButton,
            scope: this,
            text: 'Search',
            minWidth: 75
    	};
		this.suggestion = dh.append(document.body, {tag:'div', cls:'ri-autocomplete-suggestion'}, true);
		this.searchButton = new YAHOO.ext.Button(searchButtonWr, bconfig);
	},
	handleButton: function(){
		this.oAutoComp.handleSubmit(this.oAutoComp);
	}, 
	
	noResultFound : function(){
		this.errorDlg.show();
	},
	sendOutputs: function(contentNode)
	{
		for(var componentId in  this.linkedComponents) {
			var component = this.linkedComponents[componentId][0];
			var fparams = this.linkedComponents[componentId][1];
			var dparams = this.linkedComponents[componentId][2];
			var dynamicTitleAtt = this.linkedComponents[componentId][3];
			var params = {};
			for(var fParam in fparams) {
				params[fParam] = fparams[fParam];
			}
			for(var dParam in dparams) {
				params[dParam] = contentNode.getAttribute(dparams[dParam]);
			}
			if(component instanceof YAHOO.rapidjs.component.PopUpWindow){
				if(component.isVisible() == false){
					component.show();
				}
			}
			component.setFocusedContent(contentNode, params, dynamicTitleAtt);
		}
	},
	
	itemSelected: function(event,args)
	{
		var selectedItemData = new YAHOO.rapidjs.data.RapidXmlNode(null, null, 1, this.datasource.schema[0]);
		for(var index=0; index<args[2].length; index++) {
			selectedItemData.attributes[this.datasource.schema[index + 1]] = args[2][index];
		}
		this.sendOutputs(selectedItemData);
	},
	
	positionSuggestion: function(){
		var x = YAHOO.util.Dom.getX(this.inputElement);
		var y = YAHOO.util.Dom.getY(this.inputElement);
		this.suggestion.setX(x);
		this.suggestion.setY(y + this.inputElement.offsetHeight);
	}, 
	
	fireErrorOccured:function(error, text){
		this.events["erroroccurred"].fireDirect(this,error, text);
	}, 
	
	fireLoadStateChanged: function(loading){
		this.events["loadstatechanged"].fireDirect(this, loading);
	}
});

//Yahoo AutoComplete widget with button extension
YAHOO.rapidjs.component.autocomplete.AutoComplete = function(elInput,elContainer,oDataSource,oConfigs, searchButton){
	YAHOO.rapidjs.component.autocomplete.AutoComplete.superclass.constructor.call(this,elInput,elContainer,oDataSource,oConfigs);
	this.noResultFoundEvent = new YAHOO.util.CustomEvent("noresultfound", this);
	var oSelf = this;
	YAHOO.util.Event.addListener(searchButton.el.dom,"mouseover",oSelf.buttonMouseOver,oSelf);
     YAHOO.util.Event.addListener(searchButton.el.dom,"mouseout",oSelf.buttonMouseOut,oSelf);
     this.bMouseOver = false;
     
};

YAHOO.extendX(YAHOO.rapidjs.component.autocomplete.AutoComplete, YAHOO.widget.AutoComplete, {
	buttonMouseOver: function(v,oSelf){
		oSelf.bMouseOver = true;
	}, 
	
	buttonMouseOut: function(v,oSelf){
		oSelf.bMouseOver = false;
	}, 
	_onTextboxBlur : function (v,oSelf) {
	    // Don't treat as a blur if it was a selection via mouse click
	    if((!oSelf._bOverContainer || (oSelf._nKeyCode == 9)) && oSelf.bMouseOver == false) {

	        // Currnt query needs to be validated
	        if(!oSelf._bItemSelected) {
	            if(!oSelf._bContainerOpen || (oSelf._bContainerOpen && !oSelf._textMatchesOption())) {
	                if(oSelf.forceSelection) {
	                    oSelf._clearSelection();
	                }
	                else {
	                    oSelf.unmatchedItemSelectEvent.fire(oSelf, oSelf._sCurQuery);
	                }
	            }
	        }
	
	        if(oSelf._bContainerOpen) {
	            oSelf._toggleContainer(false);
	        }
	        oSelf._cancelIntervalDetection(oSelf);
	        oSelf._bFocused = false;
	        oSelf.textboxBlurEvent.fire(oSelf);
	    }
	}, 
	
	_populateList : function(sQuery, aResults, oSelf) {
	    if(aResults === null) {
	        oSelf.dataErrorEvent.fire(oSelf, sQuery);
	    }
	    if(oSelf.willSelectFirstItem == true){
	    	if(aResults && aResults[0] && aResults[0][0] == sQuery){
	    		oSelf._cancelIntervalDetection(oSelf);
	    		oSelf.itemSelectEvent.fire(oSelf, null, aResults[0]);
	    	}
	    	else{
	    		oSelf.noResultFoundEvent.fireDirect();
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
	}, 
	
	handleSubmit: function(oSelf){
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
	}
	
});

	