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
YAHOO.rapidjs.component.Autocomplete = function(container, config) {
    YAHOO.rapidjs.component.Autocomplete.superclass.constructor.call(this, container, config);
    this.contentPath = null;
    this.suggestionAttribute = null;
    this.cacheSize = 0;
    this.animated = false;
    YAHOO.ext.util.Config.apply(this, config);
    var events = {
        'submit': new YAHOO.util.CustomEvent('submit')
    }
    YAHOO.ext.util.Config.apply(this.events, events);
    this.render();

}
YAHOO.lang.extend(YAHOO.rapidjs.component.Autocomplete, YAHOO.rapidjs.component.ComponentContainer, {
    render:function() {
        var dh = YAHOO.ext.DomHelper;
        this.wrapper = dh.append(this.container, {tag: 'div', cls:'r-autocomplete'});
        this.header = dh.append(this.wrapper, {tag:'div'}, true);
        this.toolbar = new YAHOO.rapidjs.component.tool.ButtonToolBar(this.header.dom, {title:this.title});
        this.toolbar.addTool(new YAHOO.rapidjs.component.tool.ErrorTool(document.body, this));
        this.body = dh.append(this.wrapper, {tag: 'div', cls:'r-autocomplete-body',
            html:'<div class="r-autocomplete-bwrp">' +
                 '<form action="javascript:void(0)">' +
                 '<div>' +
                 '<input class="r-autocomplete-input" type="text"></input>' +
                 '<div class="r-autocomplete-swrp"></div>' +
                 '</div></form></div>'});
        this.searchInput = this.body.getElementsByTagName('input')[0];
        this.suggestion = dh.append(document.body,{tag:'div', cls:'r-autocomplete-suggestion'});
        var buttonWrp = YAHOO.util.Dom.getElementsByClassName('r-autocomplete-swrp', 'div', this.body)[0];
        this.submitButton = new YAHOO.widget.Button(buttonWrp, {label:'Search', type:'submit'});

        this.datasource = new YAHOO.util.XHRDataSource(this.url);
        this.datasource.responseType = YAHOO.util.XHRDataSource.TYPE_XML;

        this.datasource.responseSchema = {
            resultNode: this.contentPath,
            fields: [this.suggestionAttribute]
        };
        this.datasource.maxCacheEntries = this.cacheSize;
        if(this.timeout){
            this.datasource.connTimeout = this.timeout * 1000
        }
        else{
            this.datasource.connTimeout = 30000;
        }
        this.autoComp = new YAHOO.widget.AutoComplete(this.searchInput, this.suggestion, this.datasource);
        this.autoComp.useIFrame = true;
        this.autoComp.allowBrowserAutocomplete = false;
        this.autoComp.queryMatchCase = true;
        this.autoComp.useShadow = true;
        this.autoComp.animVert = this.animated;
        //this.autoComp.forceSelection = true;
        this.autoComp.doBeforeExpandContainer = function(oTextbox, oContainer, sQuery, aResults) {
            var pos = YAHOO.util.Dom.getXY(oTextbox);
            pos[1] += YAHOO.util.Dom.get(oTextbox).offsetHeight + 2;
            YAHOO.util.Dom.setXY(oContainer, pos);
            return true;
        };
        this.datasource.doBeforeParseData = this.doBeforeParseData.createDelegate(this);
        this.autoComp.doBeforeLoadData = this.doBeforeLoadData.createDelegate(this);
        this.autoComp.generateRequest = this.generateRequest.createDelegate(this);
        YAHOO.util.Event.addListener(this.body.getElementsByTagName('form')[0], 'submit', this.handleSubmit, this, true);
    },
    handleSubmit: function(e) {
        YAHOO.util.Event.preventDefault(e);
        var value = this.searchInput.value;
        if (value.trim() != "") {
            this.events['submit'].fireDirect(this.searchInput.value);
        }
    },
    doBeforeParseData: function(oRequest, oFullResponse, oCallback) {
        var mockResponse = {responseXML:oFullResponse};
        if (YAHOO.rapidjs.Connect.checkAuthentication(mockResponse) == true)
        {
            if (YAHOO.rapidjs.Connect.containsError(mockResponse) == true) {
                var errors = YAHOO.rapidjs.Connect.getErrorMessages(oFullResponse);
                this.events["error"].fireDirect(this, errors);
                YAHOO.rapidjs.ErrorManager.errorOccurred(this, errors);
            }
            else {
                this.events["success"].fireDirect(this);
            }
        }
        return oFullResponse;
    },
    doBeforeLoadData: function(sQuery, oResponse, oPayload) {
        if (oResponse.error) {
            var st = oResponse.status;
            if (st == -1) {
                this.events["error"].fireDirect(this, ['Request received timeout.']);
                YAHOO.rapidjs.ErrorManager.errorOccurred(this, ['Request received timeout.']);
            }
            else if (st == 404) {
                this.events["error"].fireDirect(this, ['Specified url cannot be found.']);
                YAHOO.rapidjs.ErrorManager.errorOccurred(this, ['Specified url cannot be found.']);
            }
            else if (st == 500) {
                this.events["error"].fireDirect(this, ['Internal Server Error. Please see the log files.']);
                YAHOO.rapidjs.ErrorManager.errorOccurred(this, ['Internal Server Error. Please see the log files.']);
            }
            else if (st == 0) {
                YAHOO.rapidjs.ErrorManager.serverDown();
            }
            return false;
        }
        return true;
    },
    generateRequest: function(query){
        var url = this.url;
        if(url.indexOf("?") >= 0)
        {
            return "&query=" + query;
        }
        else
        {
            return "?query=" + query;
        }
    }

})