
YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.tool');
YAHOO.rapidjs.component.tool.BasicTool = function(container, component, config){
   YAHOO.ext.util.Config.apply(this, config);
   this.component = component;
   this.button = new YAHOO.rapidjs.component.Button(container, {className:this.className, scope:this, click:this.performAction, tooltip: this.tooltip});
};

YAHOO.rapidjs.component.tool.BasicTool.prototype = {
    containerChanged: function(){

    },
    performAction: function(){
        
    }
};