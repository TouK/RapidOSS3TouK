YAHOO.namespace('rapidjs', 'rapidjs.component');

YAHOO.rapidjs.component.ConfirmBox = function(config) {
    this.width = 300;
    this.text = "Do you want to continue?";
    this.handler = null;
    this.scope = null;
    YAHOO.ext.util.Config.apply(this, config);

}

YAHOO.rapidjs.component.ConfirmBox.prototype = {
    render: function() {
        var container = YAHOO.ext.DomHelper.append(document.body, {tag: 'div'});
        YAHOO.util.Dom.generateId(container, 'r-confirm-')
        this.dialog = new YAHOO.widget.SimpleDialog(container,
        { width: this.width,
            fixedcenter: true,
            visible: false,
            draggable: false,
            close: true,
            modal:true,
            text: this.text,
            icon: YAHOO.widget.SimpleDialog.ICON_HELP,
            constraintoviewport: true,
            buttons: [ { text:"Yes", handler:this.handler.createDelegate(this.scope), isDefault:true },
                { text:"No",  handler:this.hide.createDelegate(this), scope:this } ]
        });
        this.dialog.setHeader("Are you sure?")
        this.dialog.render();
    },
    show: function(text) {
        if (!this.dialog) {
            this.render();
        }
        if (typeof text != 'undefined') {
            this.dialog.cfg.setProperty("text", text)
        }
        this.dialog.show();
    },
    hide:function() {
        if(this.dialog){
            this.dialog.hide();    
        }

    }

}