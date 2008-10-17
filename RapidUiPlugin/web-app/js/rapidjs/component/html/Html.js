YAHOO.namespace('rapidjs', 'rapidjs.component');
YAHOO.rapidjs.component.Html = function(container, config)
{
    YAHOO.rapidjs.component.Html.superclass.constructor.call(this, container, config);
    this.width = config.width;
    this.height = config.height;
    this.dialog = new YAHOO.rapidjs.component.Dialog({width:this.width,height:this.height, close:true});

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
        var wrp = dh.append(this.dialog.body, {tag:'div'});
        this.header = dh.append(wrp, {tag:'div'})
        this.toolbar = new YAHOO.rapidjs.component.tool.ButtonToolBar(this.header, {});
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
            this.body = dh.append(wrp, {tag:'div'}, true);
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
            this.dialog.setTitle(title)
        }
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
        this.body.update("");
        this.dialog.show();
        this.showMask();
    },

    show: function(url, title) {
        this._show(url, title);
        this.saveHistoryChange(this.url + "!::!" + this.dialog.getTitle());
    },
    hide: function()
    {
        this.abort();
        this.dialog.hide();
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
        var region = getEl(this.dialog.body).getRegion();
        var bodyRegion = new YAHOO.util.Region(region.top + this.header.offsetHeight, region.right, region.bottom, region.left)
        this.mask.setRegion(bodyRegion)
        this.maskMessage.center(this.mask.dom);

    },
    hideMask: function() {
        this.mask.hide();
        this.maskMessage.hide();
    }
})

