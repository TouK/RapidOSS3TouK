YAHOO.rapidjs.component.layout.LoadingTool = function(layout, regionName,component)
{
	YAHOO.rapidjs.component.layout.LoadingTool.superclass.constructor.call(this, layout, regionName,component);
	if(component.events["loadstatechanged"])
	{
		component.events["loadstatechanged"].subscribe(this.refreshState, this, true);
	}
	this.toolComp.dom.setAttribute('title', 'Update');
	this.loadingStyle = 'ri-layout-loading';
	this.nonloadingStyle = 'layout-refreshtool';
	this.toolInnerComp.addClass(this.nonloadingStyle);
	//YAHOO.util.Event.purgeElement(this.toolComp.dom, false, 'mouseover');
};

YAHOO.extendX(YAHOO.rapidjs.component.layout.LoadingTool, YAHOO.rapidjs.component.layout.LayoutTool, {
	getToolName: function()
	{
		return 'LoadingTool';
	},
	
	refreshState : function(component, loading){
		if(loading == true){
			this.toolInnerComp.replaceClass(this.nonloadingStyle, this.loadingStyle);
		}	
		else{
			this.toolInnerComp.replaceClass(this.loadingStyle, this.nonloadingStyle);
		}
	},
	
	performAction: function()
	{
		if(this.component.poll)
		{
			this.component.poll();
		}
	}
});