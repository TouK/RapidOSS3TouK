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
YAHOO.rapidjs.component.Html = function(container, config)
{
    YAHOO.rapidjs.component.Html.superclass.constructor.call(this, container, config);
    this.iframe = config.iframe;
    this.evaluateScripts = config.evaluateScripts != null ? config.evaluateScripts : true
    this.format = "html";
    this.resetUrlParams();
    this.render();
    var events = {
        'bodyCleared': new YAHOO.util.CustomEvent('bodyCleared')
    }
    YAHOO.ext.util.Config.apply(this.events, events);
    this.events['error'].subscribe(function() {
        this.hideMask()
    }, this, true)

};

YAHOO.lang.extend(YAHOO.rapidjs.component.Html, YAHOO.rapidjs.component.PollingComponentContainer, {
    resetUrlParams: function(){
       this.params = {componentId:this.id} 
    },
    render: function()
    {
        var dh = YAHOO.ext.DomHelper;
        var wrp = dh.append(this.container, {tag:'div'});
        this.header = dh.append(wrp, {tag:'div'})
        this.toolbar = new YAHOO.rapidjs.component.tool.ButtonToolBar(this.header, {title:this.title || ""});
        YAHOO.util.Dom.setStyle(this.toolbar.el, 'border-top', '1px solid #e0e3ef');
        if (this.iframe != true) {
            this.toolbar.addTool(new YAHOO.rapidjs.component.tool.LoadingTool(document.body, this));
        }
        this.toolbar.addTool(new YAHOO.rapidjs.component.tool.ErrorTool(document.body, this));

        if (this.iframe == true)
        {
            this.body = dh.append(wrp, {tag: 'iframe',cls:'rcmdb-html-body', frameborder:0, scrolling:"no", height:this.height - this.header.offsetHeight, width:this.width }, true);
        }
        else
        {
            this.mask = dh.append(wrp, {tag:'div', cls:'rcmdb-form-mask'}, true);
            this.maskMessage = dh.append(wrp, {tag:'div', cls:'rcmdb-form-mask-loadingwrp', html:'<div class="rcmdb-form-mask-loading">Loading...</div>'}, true)
            this.hideMask();
            this.body = dh.append(wrp, {tag:'div', cls:'rcmdb-html-body',style:'overflow:auto'}, true);
        }
    },
    inPopupWindow: function() {
        YAHOO.util.Dom.setStyle(this.header, 'display', 'none');
        YAHOO.util.Dom.setStyle(this.toolbar.toolsEl, 'right', '35px');
        YAHOO.util.Dom.setStyle(this.toolbar.toolsEl, 'width', '45px');
        this.popupWindow.dialog.container.appendChild(this.toolbar.toolsEl);
    },
    handleSuccess: function(response, keepExisting, removeAttribute)
    {
        this.fireBodyClear();
        this.body.update("<div>" + response.responseText + "</div>", this.evaluateScripts);
        this.hideMask();
    },
    fireBodyClear: function() {
        this.events['bodyCleared'].fireDirect();
    },
    clearData: function() {
        this.hideMask();
    },

    _show: function(url, title, resetParams)
    {
        if (title != null) {
            this.setTitle(title)
        }
        this.fireBodyClear();
        if (url)
        {
            this._setUrlAndParams(url, resetParams)
        }
        if (this.iframe == true)
        {
            this.body.dom.src = url;
        }
        else
        {
            this.body.update("");
            this.doPostRequest(this.url, this.params);
        }
        this.showMask();
    },

    poll: function() {
        if(this.isVisible()){
            this._show(this.url, this.title, false);    
        }
    },

    refresh: function(params, title) {
        if (params) {
            for (var param in params) {
                this.params[param] = params[param];
            }
        }
        var paramsArray = [];
        for (var param in this.params) {
            paramsArray[paramsArray.length] = param + '=' + this.params[param];
        }
        var tmpUrl = this.url;
        if (paramsArray.length > 0) {
            if (tmpUrl.indexOf("?") >= 0)
            {
                tmpUrl = tmpUrl + "&" + paramsArray.join('&');
            }
            else
            {
                tmpUrl = tmpUrl + "?" + paramsArray.join('&');
            }
        }
        this.show(tmpUrl, title);
    },

    _setUrlAndParams: function(url, resetParams) {
        if (url) {
            if(resetParams !== false){
                this.resetUrlParams();    
            }
            var queryIndex = url.indexOf("?");
            if (queryIndex >= 0)
            {
                this.url = url.substring(0, queryIndex);
                var postData = url.substring(queryIndex + 1, url.length)
                var keyValuePairs = postData.split("&");
                for (var i = 0; i < keyValuePairs.length; i++) {
                    var keyValuePair = keyValuePairs[i].split("=");
                    this.params[keyValuePair[0]] = decodeURIComponent(keyValuePair[1]);
                }
            }
            else {
                this.url = url;
            }
        }
    },

    show: function(url, title) {
        this._show(url, title);
        this.saveHistoryChange(url + "!::!" + this.title);
    },
    historyChanged: function(state) {
        if (state != "noAction") {
            var params = state.split("!::!");
            this._show(params[0], params[1]);
        }
    },
    showMask: function() {
        if (this.mask) {
            this.mask.show();
            this.maskMessage.show();
            var region = this.body.getRegion();
            this.mask.setRegion(region)
            this.maskMessage.center(this.mask.dom);
        }

    },
    hideMask: function() {
        if (this.mask) {
            this.mask.hide();
            this.maskMessage.hide();
        }
    },

    resize: function(width, height) {
        var bodyHeight;
        if (YAHOO.util.Dom.getStyle(this.header, 'display') != 'none') {
            bodyHeight = height - this.header.offsetHeight
        }
        else {
            bodyHeight = height
        }
        this.body.setHeight(bodyHeight);
        this.body.setWidth(width);
    }
})


