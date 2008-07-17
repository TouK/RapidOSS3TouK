YAHOO.namespace('rapidjs', 'rapidjs.component');
YAHOO.rapidjs.component.Html = function(config)
{
    YAHOO.rapidjs.component.Html.superclass.constructor.call(this, null, config);

    this.width = config.width;
    this.height = config.height;
    this.render();
    this.url = null

};

YAHOO.lang.extend(YAHOO.rapidjs.component.Html, YAHOO.rapidjs.component.PollingComponentContainer, {
    render: function()
    {  var dh = YAHOO.ext.DomHelper;
        this.container = dh.append(document.body, {tag: 'div', cls:'resizable-panel'});
        this.body = dh.append(document.body, {tag: 'div', cls:'resizable-panel-body'});
        this.footer = dh.append(document.body, {tag: 'div', cls:'resizable-panel-footer'});
            // Create a panel Instance, from the 'resizablepanel' DIV standard module markup
            this.panel = new YAHOO.widget.Panel(this.container , {
                draggable: true,
                fixedcenter:true,
                width:this.width+"px",
                height:this.height+"px"
            });
            this.panel.setBody(this.body);
            this.panel.setFooter(this.footer);
            this.panel.render();
            var IE_QUIRKS = (YAHOO.env.ua.ie && document.compatMode == "BackCompat");

            // UNDERLAY/IFRAME SYNC REQUIRED
            var IE_SYNC = (YAHOO.env.ua.ie == 6 || (YAHOO.env.ua.ie == 7 && IE_QUIRKS));
            // Create Resize instance, binding it to the 'resizablepanel' DIV
            this.resize = new YAHOO.util.Resize(this.container, {
                handles: ['br'],
                autoRatio: false,
                status: false,
                minWidth: 300,
                minHeight: 100
            });

            // Setup resize handler to update the size of the Panel's body element
            // whenever the size of the 'resizablepanel' DIV changes
            var func = function(args) {

                var panelHeight = args.height;
                var panelWidth = args.width;

                var headerHeight = this.header.offsetHeight; // Content + Padding + Border
                var footerHeight = this.footer.offsetHeight; // Content + Padding + Border

                var bodyHeight = (panelHeight - headerHeight - footerHeight);
                var bodyWidth = (panelWidth - 20);
                var bodyContentHeight = bodyHeight-20;

                YAHOO.util.Dom.setStyle(this.body, 'height', bodyContentHeight + 'px');
                YAHOO.util.Dom.setStyle(this.body.childNodes[0], 'height', bodyContentHeight + 'px');
                YAHOO.util.Dom.setStyle(this.body.childNodes[0], 'width', bodyWidth + 'px');
                if (IE_SYNC) {
                    this.sizeUnderlay();
                    this.syncIframe();
                }
            }
            this.resize.on('resize', func, this.panel, true);
            var args = new Object();
            args.width = this.width;
            args.height = this.height;
            func.createDelegate(this.panel,args,true).call(args);

    },
    handleSuccess: function(response)
    {
        this.body.innerHTML = "<div>"+response.responseText+"</div>";
    },


    handleFailure: function(response)
    {
       
    },

    show: function(url)
    {
        if(url)
        {
            this.url = url;
        }
        this.doRequest(this.url);
        this.panel.show();
    },
    hide: function()
    {
        this.abort();
        this.dialog.hide();
    }
})

