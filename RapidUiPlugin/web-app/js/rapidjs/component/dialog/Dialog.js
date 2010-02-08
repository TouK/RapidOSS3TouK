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
YAHOO.rapidjs.component.Dialog = function(config)
{
    this.width = null;
    this.height = null;
    this.minHeight = 50;
    this.minWidth = 100;
    this.maxWidth = null;
    this.maxHeight = null;
    this.effect = null;
    this.close = false;
    this.title = "&#160;";
    this.buttons = null;
    this._buttons = [];
    this.resizable = true;
    this.x = 0;
    this.y = 0;
    this.fixedcenter = false;
    this.modal = false;
    this.mask = false;
    YAHOO.ext.util.Config.apply(this, config);
    if (!this.x && !this.y) {
        this.fixedcenter = true;
    }
    this.events = {
        'resize': new YAHOO.util.CustomEvent('resize')
    }
    this.render();
};

YAHOO.rapidjs.component.Dialog.prototype = {
    focusFirst: function () {
        try{
            this.panel.firstElement.focus();
        }catch(err) {
            // Ignore
        }
    },
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
            modal:this.modal,
            x:this.x,
            y:this.y,
            effect:this.effect
        };


        this.panel = new YAHOO.widget.Panel(this.container, panelConfig);
        this.panel.setBody(this.body);
        this.panel.setFooter(this.footer);

        if (this.mask) {
            this.mask = dh.append(this.container, {tag:'div', cls:'rcmdb-form-mask'}, true);
            this.maskMessage = dh.append(this.container, {tag:'div', cls:'rcmdb-form-mask-loadingwrp', html:'<div class="rcmdb-form-mask-loading">Loading...</div>'}, true)
            this.hideMask();
        }
        if (this.buttons)
        {
            for (var i = 0; i < this.buttons.length; i++)
            {
                var oButton = new YAHOO.widget.Button(
                {
                    type: "button",
                    label: this.buttons[i].text,
                    disabled: this.buttons[i].disabled,
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
                this._buttons[this._buttons.length] = oButton;
            }
            YAHOO.util.Dom.setStyle(this.body, 'background-color', '#F2F2F2');
            YAHOO.util.Dom.setStyle(this.footer.parentNode, 'border-top', 'medium none');

        }
        this.panel.render();
        YAHOO.rapidjs.component.OVERLAY_MANAGER.register(this.panel)
        this.func = function(args) {
            var panelHeight = args.height;
            var panelWidth = args.width;
            this.adjustSize(panelWidth, panelHeight);
        }

        if (this.resizable) {
            YAHOO.util.Dom.addClass(this.container, 'resizable-panel');
            YAHOO.util.Dom.addClass(this.body, 'resizable-panel-body');
            YAHOO.util.Dom.addClass(this.footer, 'resizable-panel-footer');
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
            this.resize.on('resize', this.func, this, true);
            this.adjustSize(this.width, this.height);
        }
        this.panel.hideEvent.subscribe(this.handleHide, this, true);
        if (YAHOO.env.ua.gecko) {
            this.panel.hideEvent.subscribe(function() {
                YAHOO.util.Dom.setStyle(this.panel.body, 'overflow', 'hidden');
            }, this, true)
            this.panel.beforeShowEvent.subscribe(function() {
                YAHOO.util.Dom.setStyle(this.panel.body, 'overflow', 'auto');
            }, this, true)
        }
        YAHOO.util.Dom.setStyle(this.container.parentNode, "top", "-15000px");
        this.setTitle(this.title)
    },

    getButtons :function() {
        return this._buttons;
    },

    showMask: function() {
        if (this.mask) {
            this.mask.show();
            this.maskMessage.show();
            var region = getEl(this.panel.body).getRegion();
            this.mask.setRegion(region)
            this.maskMessage.center(this.mask.dom);
            for (var i = 0; i < this._buttons.length; i++) {
                var button = this._buttons[i];
                button.set("disabled", true)
            }
        }

    },
    hideMask: function() {
        if (this.mask) {
            this.mask.hide();
            this.maskMessage.hide();
            for (var i = 0; i < this._buttons.length; i++) {
                var button = this._buttons[i];
                button.set("disabled", false)
            }
        }
    },


    show: function(url)
    {
        var y = 0;
        if (this.y)
        {
            y = this.y;
        }
        var y = this.panel.cfg.setProperty("y", y);
        var newWidth = this.panel.cfg.getProperty("width");
        if (this.panel.cfg.getProperty("x") + this.panel.cfg.getProperty("width") > document.body.clientWidth)
        {
            newWidth = document.body.clientWidth - this.panel.cfg.getProperty("x");
        }
        var newHeight = this.panel.cfg.getProperty("height");
        if (this.panel.cfg.getProperty("y") + this.panel.cfg.getProperty("height") > document.body.clientHeight)
        {
            newHeight = document.body.clientHeight - this.panel.cfg.getProperty("y");
        }
        this.panel.cfg.setProperty("height", newHeight);
        this.panel.cfg.setProperty("width", newWidth);
        this.adjustSize(newWidth, newHeight);
        this.panel.show();

        YAHOO.rapidjs.component.OVERLAY_MANAGER.bringToTop(this.panel);
        this.focusFirst();
    },
    hide: function()
    {
        this.panel.hide();
    },
    handleHide : function()
    {
        YAHOO.util.Dom.setStyle(this.container.parentNode, "top", "-15000px");
    },

    adjustSize : function(panelWidth, panelHeight, contentHeight) {
        // UNDERLAY/IFRAME SYNC REQUIRED
        var IE_QUIRKS = (YAHOO.env.ua.ie && document.compatMode == "BackCompat");
        var IE_SYNC = (YAHOO.env.ua.ie == 6 || (YAHOO.env.ua.ie >= 7 && IE_QUIRKS));
        var bodyWidth = (panelWidth - 20);
        YAHOO.util.Dom.setStyle(this.panel.body.childNodes[0], 'width', bodyWidth + 'px');
        var headerHeight = this.panel.header.offsetHeight; // Content + Padding + Border
        var footerHeight = this.panel.footer.offsetHeight; // Content + Padding + Border
        if (contentHeight != null) {
            var panelBodyEl = getEl(this.panel.body);
            var totalHeight = headerHeight + footerHeight + panelBodyEl.getPadding('tb');
            if(this.minHeight != null && (totalHeight + contentHeight) < this.minHeight){
                 contentHeight = this.minHeight - totalHeight
            }
            if(this.maxHeight != null && (totalHeight + contentHeight) > this.maxHeight){
                 contentHeight = this.maxHeight - totalHeight
            }
            this.bodyEl.setHeight(contentHeight);
            panelBodyEl.setHeight(contentHeight + panelBodyEl.getPadding('tb'));
            totalHeight = totalHeight + contentHeight;
            this.panel.cfg.setProperty("height", totalHeight);
        }
        else {
            var bodyHeight = (panelHeight - headerHeight - footerHeight - 1);
            var panelBodyEl = getEl(this.panel.body);
            panelBodyEl.setHeight(bodyHeight);
            var bodyContentHeight = bodyHeight - panelBodyEl.getPadding('tb');
            this.bodyEl.setHeight(bodyContentHeight)
        }

        this.events['resize'].fireDirect(this.bodyEl.getWidth(true), this.bodyEl.getHeight(true));
        if (IE_SYNC) {
            this.panel.sizeUnderlay();
            this.panel.syncIframe();
        }
    },

    adjustHeight: function(contentHeight) {
        this.adjustSize(this.panel.cfg.getProperty("width"), null, contentHeight);
    },
    setTitle: function(title) {
        this.title = title;
        this.panel.setHeader('<span style="overflow:hidden;white-space:nowrap">' + this.title + '</span>');
        var headerEl = getEl(this.panel.header);
        var bodyEl = getEl(this.panel.body);
        if (headerEl.getWidth() > bodyEl.getWidth()) {
            headerEl.setWidth(bodyEl.getWidth());
        }
    },
    getTitle: function() {
        return this.title;
    },

    isVisible: function() {
        return this.panel.cfg.getProperty("visible")
    }
};

YAHOO.rapidjs.component.OVERLAY_MANAGER = new YAHOO.widget.OverlayManager();