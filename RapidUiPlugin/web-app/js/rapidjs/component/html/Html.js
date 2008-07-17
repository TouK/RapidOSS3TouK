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
        this.container = dh.append(document.body, {tag: 'div', cls:'resizable-panel-body'});
        this.header = dh.append(this.container, {tag: 'div', cls:'resizable-panel-header'});
        this.body = dh.append(this.container, {tag: 'div', cls:'resizable-panel-body'});
        this.footer = dh.append(this.container, {tag: 'div', cls:'resizable-panel-footer'});
        var IE_QUIRKS = (YAHOO.env.ua.ie && document.compatMode == "BackCompat");

            // UNDERLAY/IFRAME SYNC REQUIRED
            var IE_SYNC = (YAHOO.env.ua.ie == 6 || (YAHOO.env.ua.ie == 7 && IE_QUIRKS));

            // PADDING USED FOR BODY ELEMENT (Hardcoded for example)
            var PANEL_BODY_PADDING = (10*2) // 10px top/bottom padding applied to Panel body element. The top/bottom border width is 0

            // Create a panel Instance, from the 'resizablepanel' DIV standard module markup
            this.panel = new YAHOO.widget.Panel(this.container , {
                draggable: true,
                width: '500px',
                height: '500px'
            });
            this.panel.render();

            // Create Resize instance, binding it to the 'resizablepanel' DIV
            var resize = new YAHOO.util.Resize(this.container, {
                handles: ['br'],
                autoRatio: false,
                minWidth: 300,
                minHeight: 100,
                status: true
            });

            // Setup resize handler to update the size of the Panel's body element
            // whenever the size of the 'resizablepanel' DIV changes
            resize.on('resize', function(args) {

                var panelHeight = args.height;

                var headerHeight = this.header.offsetHeight; // Content + Padding + Border
                var footerHeight = this.footer.offsetHeight; // Content + Padding + Border

                var bodyHeight = (panelHeight - headerHeight - footerHeight);
                var bodyContentHeight = (IE_QUIRKS) ? bodyHeight : bodyHeight - PANEL_BODY_PADDING;

                YAHOO.util.Dom.setStyle(this.body, 'height', bodyContentHeight + 'px');

                if (IE_SYNC) {

                    // Keep the underlay and iframe size in sync.

                    // You could also set the width property, to achieve the
                    // same results, if you wanted to keep the panel's internal
                    // width property in sync with the DOM width.

                    this.sizeUnderlay();

                    // Syncing the iframe can be expensive. Disable iframe if you
                    // don't need it.

                    this.syncIframe();
                }
            }, this.panel, true);

    },
    handleSuccess: function(response)
    {
        this.contentContainer.dom.innerHTML = response.responseText;
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

