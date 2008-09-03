YAHOO.namespace('rapidjs', 'rapidjs.component');
YAHOO.rapidjs.component.Html = function(config)
{
    YAHOO.rapidjs.component.Html.superclass.constructor.call(this, null, config);
    this.width = config.width;
    this.height = config.height;
    this.dialog = new YAHOO.rapidjs.component.Dialog({width:this.width,height:this.height});

    this.iframe = config.iframe;
    this.format = "html";
    this.render();
    this.url = null

};

YAHOO.lang.extend(YAHOO.rapidjs.component.Html, YAHOO.rapidjs.component.PollingComponentContainer, {
    render: function()
    {
        if (this.iframe == true)
        {
            var dh = YAHOO.ext.DomHelper;
            this.body = dh.append(this.dialog.body, {tag: 'iframe', frameborder:0, scrolling:"no", height:this.height, width:this.width });
        }
        else
        {
            this.body = this.dialog.body;
        }
        this.dialog.show();


    },
    handleSuccess: function(response, keepExisting, removeAttribute)
    {
        this.body.innerHTML = "<div>" + response.responseText + "</div>";
    },

    show: function(url, title)
    {
        if(title != null){
            this.dialog.setTitle(title)
        }
        if (url)
        {
            this.url = url;
        }
        if (this.iframe == true)
        {
            this.body.src = this.url;
        }
        else
        {
            this.doRequest(this.url);
        }
        this.saveHistoryChange(this.url + "!::!" + this.dialog.getTitle());
        this.dialog.show();
    },
    hide: function()
    {
        this.abort();
        this.dialog.hide();
    },
    historyChanged: function(state) {
        if (state != "noAction") {
            var params = state.split("!::!");
            this.show(params[0], params[1]);
        }
    }
})

