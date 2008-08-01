YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.tool');
YAHOO.rapidjs.component.tool.ErrorTool = function(container, component) {
    var config = {className:'r-tool-error'};
    YAHOO.rapidjs.component.tool.ErrorTool.superclass.constructor.call(this, container, component, config);
    this.component.events['success'].subscribe(this.success, this, true);
    this.component.events['error'].subscribe(this.error, this, true);
    this.button.el.removeListener('mouseover');
    this.button.el.removeListener('mouseout');
    this.button.el.removeListener('mousedown');
    this.button.el.removeListener('mouseup');
    ErrorTooltip.add(this.button.el.dom, '');
};

YAHOO.lang.extend(YAHOO.rapidjs.component.tool.ErrorTool, YAHOO.rapidjs.component.tool.BasicTool, {
    performAction : function() {},
    containerChanged: function(newContainer){
        YAHOO.util.Dom.setStyle(newContainer, 'display', 'none');
    },
    success: function(){
       YAHOO.util.Dom.setStyle(this.button.el.dom.parentNode, 'display', 'none');
    },
    error: function(component, errors){
        ErrorTooltip.update(this.button.el.dom, errors.join("<br>"));
        YAHOO.util.Dom.setStyle(this.button.el.dom.parentNode, 'display', '');
    }
});