YAHOO.namespace('rapidjs', 'rapidjs.component');

YAHOO.rapidjs.component.LoadingMask = function(config) {
    this.width = 240;
    this.text = "Loading, please wait...";
    YAHOO.ext.util.Config.apply(this, config);
}

YAHOO.rapidjs.component.LoadingMask.prototype = {
    render: function() {
        var container = YAHOO.ext.DomHelper.append(document.body, {tag: 'div'});
        YAHOO.util.Dom.generateId(container, 'r-loading-')
        this.panel = new YAHOO.widget.Panel(container, {
            width:this.width,
            fixedcenter:true,
            close:false,
            draggable:false,
            modal:true,
            visible:false
        });
        var body = YAHOO.ext.DomHelper.append(document.body, {tag:'div',
            html:'<div class="r-loading-mask-text">' + this.text + '</div><div class="r-loading-mask-image"></div>'})
        this.textEl = body.getElementsByTagName('div')[0];
        this.panel.setBody(body);
        this.panel.render();
    },
    show: function(text) {
        if (!this.panel) {
            this.render();
        }
        if (typeof text != 'undefined') {
            this.textEl.innerHTML = text;
        }
        this.panel.show();
        YAHOO.util.Dom.addClass(this.panel.mask, 'r-loadingmask-mask');
    },
    hide:function() {
        if(this.panel){
            this.panel.hide();
        }
    }

}