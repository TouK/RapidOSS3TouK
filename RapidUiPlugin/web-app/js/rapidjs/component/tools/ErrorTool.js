YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.tool');
YAHOO.rapidjs.component.tool.ErrorTool = function(container, component) {
    var config = {className:'r-tool-error'};
    YAHOO.rapidjs.component.tool.ErrorTool.superclass.constructor.call(this, container, component, config);
    this.component.events['success'].subscribe(this.success, this, true);
    this.component.events['error'].subscribe(this.error, this, true);
    this.errorsToBeAppended = null;
};

YAHOO.lang.extend(YAHOO.rapidjs.component.tool.ErrorTool, YAHOO.rapidjs.component.tool.BasicTool, {
    performAction : function() {
        if(!this.dialog){
            this.dialog = new YAHOO.rapidjs.component.Dialog({width:550,height:350, close:true});
            this.body = YAHOO.ext.DomHelper.append(this.dialog.body, {tag:'div', cls:'r-errordialog-body'});
            this.appendErrors(this.errorsToBeAppended);
        }
        this.dialog.show();
    },
    containerChanged: function(newContainer){
        YAHOO.util.Dom.setStyle(newContainer, 'display', 'none');
    },
    success: function(){
       YAHOO.util.Dom.setStyle(this.button.el.dom.parentNode, 'display', 'none');
    },
    error: function(component, errors, willShow){
        YAHOO.util.Dom.setStyle(this.button.el.dom.parentNode, 'display', '');
        if(this.dialog){
            this.appendErrors(errors);
        }
        else{
            this.errorsToBeAppended = errors;
        }
        if(willShow){
            this.performAction();
        }
    },
    appendErrors : function(errors){
         for(var index = 0; index < errors.length; index++) {
             YAHOO.ext.DomHelper.append(this.body, {tag:'div', cls:'r-errordialog-item', html:errors[index]});
         }
    }
});