YAHOO.namespace('rapidjs', 'rapidjs.component');
YAHOO.rapidjs.component.Dialog = function(config)
{
    this.width = config.width;
    this.height = config.height;
   	this.minHeight = config.minHeight;
   	this.minWidth = config.minWidth;
   	this.title = config.title;
    this.buttons =config.buttons;
    if(this.buttons)
    	this.buttonNumber = config.buttons.length;
    this.render();
};

YAHOO.rapidjs.component.Dialog.prototype = {
    render: function()
    {
        var dh = YAHOO.ext.DomHelper;
        this.container = dh.append(document.body, {tag: 'div', cls:'resizable-panel'});
        YAHOO.util.Dom.generateId(this.container, 'r-dialog-')
        this.body = dh.append(document.body, {tag: 'div', cls:'resizable-panel-body'});
        this.footer = dh.append(document.body, {tag: 'div', cls: 'resizable-panel-footer'});

		this.panelConfig = {
            draggable: true,
            constraintoviewport: true,
            fixedcenter:true,
            visible:false,
            width:this.width + "px",
            height:this.height + "px"
        };

        this.panel = new YAHOO.widget.Panel(this.container,this.panelConfig);
        if(this.title)
        	this.panel.setHeader(this.title);
        this.panel.setBody(this.body);
        this.panel.setFooter(this.footer);


        if(this.buttons)
		{
			for(var i = 0; i < this.buttonNumber ; i++)
	   		{
		   		var oButton = new YAHOO.widget.Button(
		   		{
	                type: "button",
	                label: this.buttons[i].text,
	                container: this.footer
	            });
	            if(YAHOO.lang.isFunction(this.buttons[i].handler))
	             	oButton.set("onclick", { fn: this.buttons[i].handler,
                                obj: oButton, scope: this.buttons[i].scope || this });
				if(this.buttons[i].isDefault)
				{
					YAHOO.util.Dom.setStyle(oButton.get("element"),'background-position','0pt -1400px');
					YAHOO.util.Dom.setStyle(oButton.get("element"),'border-color:','#304369');
					YAHOO.util.Dom.setStyle(oButton.get("element").getElementsByTagName('button')[0],'color','#FFFFFF');
				}
			}
	   		YAHOO.util.Dom.setStyle(this.body, 'background-color', '#F2F2F2');
    		YAHOO.util.Dom.setStyle(this.footer.parentNode, 'border-top', 'medium none');

		}
        this.panel.render();
        var IE_QUIRKS = (YAHOO.env.ua.ie && document.compatMode == "BackCompat");

            // UNDERLAY/IFRAME SYNC REQUIRED
        var IE_SYNC = (YAHOO.env.ua.ie == 6 || (YAHOO.env.ua.ie == 7 && IE_QUIRKS));
            // Create Resize instance, binding it to the 'resizablepanel' DIV
        this.resize = new YAHOO.util.Resize(this.container, {
            handles: ['br'],
            autoRatio: false,
            status: false,
            minHeight:this.minHeight || 300,
            minWidth: this.minWidth || 100,
            maxWidth: 900,
            maxHeight: 600

        });

            // Setup resize handler to update the size of the Panel's body element
        // whenever the size of the 'resizablepanel' DIV changes
        this.func = function(args) {

            var panelHeight = args.height;
            var panelWidth = args.width;

            var headerHeight = this.header.offsetHeight; // Content + Padding + Border
            var footerHeight = this.footer.offsetHeight; // Content + Padding + Border

            var bodyHeight = (panelHeight - headerHeight - footerHeight - 1);
            var bodyWidth = (panelWidth - 20);
            var bodyContentHeight = bodyHeight - 20;

            YAHOO.util.Dom.setStyle(this.body, 'height', bodyContentHeight + 'px');
            YAHOO.util.Dom.setStyle(this.body.childNodes[0], 'height', bodyContentHeight + 'px');
            YAHOO.util.Dom.setStyle(this.body.childNodes[0], 'width', bodyWidth + 'px');

            if (IE_SYNC) {
                this.sizeUnderlay();
                this.syncIframe();
            }
        }
        this.resize.on('resize', this.func, this.panel, true);
        this.func.createDelegate(this.panel, {width: this.width, height: this.height }, true).call({width: this.width, height: this.height});
        this.panel.hideEvent.subscribe(this.handleHide, this, true);
        YAHOO.util.Dom.setStyle(this.container.parentNode, "top", -15000);
    },

    show: function(url)
    {
        this.panel.show();
    },
    hide: function()
    {
	   this.panel.hide();
    },
    handleHide : function()
    {
       YAHOO.util.Dom.setStyle(this.container.parentNode, "top", -15000);
    }
};