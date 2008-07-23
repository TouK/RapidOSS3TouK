YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.tool');
YAHOO.rapidjs.component.tool.LoadingTool = function(container, component) {
    this.loadingStyle = 'r-tool-loading';
	this.nonloadingStyle = 'r-tool-refresh';
    var config = {tooltip:"Update", className:this.nonloadingStyle};
    YAHOO.rapidjs.component.tool.LoadingTool.superclass.constructor.call(this, container, component,config);
    if(component.events["loadstatechanged"])
	{
		component.events["loadstatechanged"].subscribe(this.refreshState, this, true);
	}
};

YAHOO.lang.extend(YAHOO.rapidjs.component.tool.LoadingTool, YAHOO.rapidjs.component.tool.BasicTool, {
    performAction : function() {
        if (this.component.poll)
        {
            this.component.poll();
        }
    },

    refreshState : function(component, loading) {
        if (loading == true) {
            YAHOO.util.Dom.replaceClass(this.button.inner, this.nonloadingStyle, this.loadingStyle)
        }
        else {
            YAHOO.util.Dom.replaceClass(this.button.inner, this.loadingStyle, this.nonloadingStyle)
        }
    }
});