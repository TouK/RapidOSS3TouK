YAHOO.namespace('rapidjs', 'rapidjs.component');
YAHOO.rapidjs.component.Dialog = function(config)
{
    this.width = null;
    this.height = null;
    this.minHeight = 300;
    this.minWidth = 100;
    this.maxWidth = null;
    this.maxHeight = null;
    this.close = false;
    this.title = "&#160;";
    this.buttons = null;
    this.resizable = true;
    this.x = 0;
    this.y = 0;
    this.fixedcenter = false;
    YAHOO.ext.util.Config.apply(this, config);
    if(!this.x && !this.y){
        this.fixedcenter = true;
    }
    this.events = {
        'resize': new YAHOO.util.CustomEvent('resize')
    }
    this.render();
};

YAHOO.rapidjs.component.Dialog.prototype = {
    render: function()
    {
        var dh = YAHOO.ext.DomHelper;
        this.container = dh.append(document.body, {tag: 'div'});
        YAHOO.util.Dom.generateId(this.container, 'r-dialog-')
        this.body = dh.append(document.body, {tag: 'div'});
        this.bodyEl = YAHOO.ext.Element.get(this.body);
        this.footer = dh.append(document.body, {tag: 'div', style:'text-align:right;'});
        var panelConfig = {
            draggable: true,
            constraintoviewport: true,
            fixedcenter:this.fixedcenter,
            visible:false,
            close:this.close,
            width:this.width,
            height:this.height,
            autofillheight:false,
            x:this.x,
            y:this.y
        };


        this.panel = new YAHOO.widget.Panel(this.container, panelConfig);
        this.panel.setHeader(this.title);
        this.panel.setBody(this.body);
        this.panel.setFooter(this.footer);


        if (this.buttons)
        {
            for (var i = 0; i < this.buttons.length; i++)
            {
                var oButton = new YAHOO.widget.Button(
                {
                    type: "button",
                    label: this.buttons[i].text,
                    container: this.footer
                });
                if (YAHOO.lang.isFunction(this.buttons[i].handler))
                    oButton.set("onclick", { fn: this.buttons[i].handler,
                        obj: oButton, scope: this.buttons[i].scope || this });
                if (this.buttons[i].isDefault)
                {
                    YAHOO.util.Dom.setStyle(oButton.get("element"), 'background-position', '0pt -1400px');
                    YAHOO.util.Dom.setStyle(oButton.get("element"), 'border-color:', '#304369');
                    YAHOO.util.Dom.setStyle(oButton.get("element").getElementsByTagName('button')[0], 'color', '#FFFFFF');
                }
            }
            YAHOO.util.Dom.setStyle(this.body, 'background-color', '#F2F2F2');
            YAHOO.util.Dom.setStyle(this.footer.parentNode, 'border-top', 'medium none');

        }
        this.panel.render();
        YAHOO.rapidjs.component.OVERLAY_MANAGER.register(this.panel)


        if (this.resizable) {
            YAHOO.util.Dom.addClass(this.container, 'resizable-panel');
            YAHOO.util.Dom.addClass(this.body, 'resizable-panel-body');
            YAHOO.util.Dom.addClass(this.footer, 'resizable-panel-footer');
            var IE_QUIRKS = (YAHOO.env.ua.ie && document.compatMode == "BackCompat");

            // UNDERLAY/IFRAME SYNC REQUIRED
            var IE_SYNC = (YAHOO.env.ua.ie == 6 || (YAHOO.env.ua.ie == 7 && IE_QUIRKS));
            // Create Resize instance, binding it to the 'resizablepanel' DIV
            this.resize = new YAHOO.util.Resize(this.container, {
                handles: ['br'], 
                autoRatio: false,
                status: false,
                minHeight:this.minHeight,
                minWidth: this.minWidth,
                maxWidth: this.maxWidth,
                maxHeight: this.maxHeight

            });

            // Setup resize handler to update the size of the Panel's body element
            // whenever the size of the 'resizablepanel' DIV changes
            this.func = function(args) {

                var panelHeight = args.height;
                var panelWidth = args.width;

                var headerHeight = this.panel.header.offsetHeight; // Content + Padding + Border
                var footerHeight = this.panel.footer.offsetHeight; // Content + Padding + Border

                var bodyHeight = (panelHeight - headerHeight - footerHeight - 1);
                var bodyWidth = (panelWidth - 20);
                var bodyContentHeight = bodyHeight - 20;

                YAHOO.util.Dom.setStyle(this.panel.body, 'height', bodyContentHeight + 'px');
                YAHOO.util.Dom.setStyle(this.panel.body.childNodes[0], 'height', bodyContentHeight + 'px');
                YAHOO.util.Dom.setStyle(this.panel.body.childNodes[0], 'width', bodyWidth + 'px');
                this.events['resize'].fireDirect(this.bodyEl.getWidth(true), this.bodyEl.getHeight(true));
                if (IE_SYNC) {
                    this.panel.sizeUnderlay();
                    this.panel.syncIframe();
                }
            }
            this.resize.on('resize', this.func, this, true);
            this.func.createDelegate(this, {width: this.width, height: this.height }, true).call({width: this.width, height: this.height});
        }
        this.panel.hideEvent.subscribe(this.handleHide, this, true);
        YAHOO.util.Dom.setStyle(this.container.parentNode, "top", "-15000px");
    },

    show: function(url)
    {
        var y=0;
        if(this.y)
        {
            y = this.y;
        }
        var y = this.panel.cfg.setProperty("y", y);
        var newWidth = this.panel.cfg.getProperty("width");
        if(this.panel.cfg.getProperty("x")+this.panel.cfg.getProperty("width") > document.body.clientWidth)
        {
            newWidth =document.body.clientWidth-this.panel.cfg.getProperty("x");
        }
        var newHeight = this.panel.cfg.getProperty("height");
        if(this.panel.cfg.getProperty("y")+this.panel.cfg.getProperty("height") > document.body.clientHeight)
        {
            newHeight = document.body.clientHeight-this.panel.cfg.getProperty("y");
        }
        this.panel.cfg.setProperty("height", newHeight);
        this.panel.cfg.setProperty("width", newWidth);
        this.func.createDelegate(this, {width: newWidth, height: newHeight }, true).call({width: newWidth, height: newHeight});
        this.panel.show();

        YAHOO.rapidjs.component.OVERLAY_MANAGER.bringToTop(this.panel);
    },
    hide: function()
    {
        this.panel.hide();
    },
    handleHide : function()
    {
        YAHOO.util.Dom.setStyle(this.container.parentNode, "top", "-15000px");
    },

    setTitle: function(title) {
        this.title = title;
        this.panel.setHeader(this.title);
    },
    getTitle: function() {
        return this.title;
    },

    isVisible: function(){
        return this.panel.cfg.getProperty("visible")
    }
};

YAHOO.rapidjs.component.OVERLAY_MANAGER = new YAHOO.widget.OverlayManager();