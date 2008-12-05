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
    this.format = "html";
    this.params = {componentId:this.id}
    this.render();
    this.url = null
    this.events['error'].subscribe(function(){this.hideMask()}, this, true)

};

YAHOO.lang.extend(YAHOO.rapidjs.component.Html, YAHOO.rapidjs.component.PollingComponentContainer, {
    render: function()
    {
        var dh = YAHOO.ext.DomHelper;
        var wrp = dh.append(this.container, {tag:'div'});
        this.header = dh.append(wrp, {tag:'div'})
        this.toolbar = new YAHOO.rapidjs.component.tool.ButtonToolBar(this.header, {title:this.title || ""});
        YAHOO.util.Dom.setStyle(this.toolbar.el, 'border-top', '1px solid #e0e3ef');
        if(this.iframe != true){
            this.toolbar.addTool(new YAHOO.rapidjs.component.tool.LoadingTool(document.body, this));    
        }
        this.toolbar.addTool(new YAHOO.rapidjs.component.tool.ErrorTool(document.body, this));

        if (this.iframe == true)
        {
            this.body = dh.append(wrp, {tag: 'iframe', frameborder:0, scrolling:"no", height:this.height - this.header.offsetHeight, width:this.width }, true);
        }
        else
        {
            this.mask = dh.append(wrp, {tag:'div', cls:'rcmdb-form-mask'}, true);
            this.maskMessage = dh.append(wrp, {tag:'div', cls:'rcmdb-form-mask-loadingwrp', html:'<div class="rcmdb-form-mask-loading">Loading...</div>'}, true)
            this.hideMask();
            this.body = dh.append(wrp, {tag:'div', style:'overflow:auto'}, true);
        }
    },
    handleSuccess: function(response, keepExisting, removeAttribute)
    {
        this.body.update("<div>" + response.responseText + "</div>", true);
        this.hideMask();
    },

    clearData: function() {
        this.hideMask();
    },

    _show: function(url, title)
    {
        if (title != null) {
            this.setTitle(title)
        }
        this.body.update("");
        if (url)
        {
            this.url = url;
        }
        if (this.iframe == true)
        {
            this.body.dom.src = this.url;
        }
        else
        {
            this.doRequest(this.url, this.params);
        }
        this.showMask();
    },

    show: function(url, title) {
        this._show(url, title);
        this.saveHistoryChange(this.url + "!::!" + this.title);
    },
    historyChanged: function(state) {
        if (state != "noAction") {
            var params = state.split("!::!");
            this._show(params[0], params[1]);
        }
    },
    showMask: function() {
        this.mask.show();
        this.maskMessage.show();
        var region = this.body.getRegion();
        this.mask.setRegion(region)
        this.maskMessage.center(this.mask.dom);
    },
    hideMask: function() {
        this.mask.hide();
        this.maskMessage.hide();
    },

    resize: function(width, height){
        this.body.setHeight(height - this.header.offsetHeight);
        this.body.setWidth(width);
    }
})

