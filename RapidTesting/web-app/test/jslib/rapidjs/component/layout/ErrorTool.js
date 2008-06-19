YAHOO.rapidjs.component.layout.ErrorTool = function(layout, regionName,component)
{
	YAHOO.rapidjs.component.layout.ErrorTool.superclass.constructor.call(this, layout, regionName,component);
	if(component.events["erroroccurred"])
	{
		component.events["erroroccurred"].subscribe(this.refreshState, this, true);
	}
	this.toolInnerComp.addClass('ri-layout-error');
	YAHOO.util.Dom.setStyle(this.toolInnerComp.dom, 'display', 'none');
	this.toolComp.dom.setAttribute('title', 'Errors');
	this.errorDialog = new YAHOO.rapidjs.component.dialogs.ErrorDialog();
    this.state = 'normal';
};

YAHOO.extendX(YAHOO.rapidjs.component.layout.ErrorTool, YAHOO.rapidjs.component.layout.LayoutTool, {
	
	getToolName: function()
	{
		return 'ErrorTool';
	},
	
	refreshState : function(component, error, errorData){
		if(this.state == 'normal' && error == true){
			YAHOO.util.Dom.setStyle(this.toolInnerComp.dom, 'display', '');
			this.state = 'error';
		}
		else if(this.state == 'error' && error == false){
			YAHOO.util.Dom.setStyle(this.toolInnerComp.dom, 'display', 'none');
			this.state = 'normal';
		}
		if(typeof errorData != 'string'){
			this.setErrorText(YAHOO.rapidjs.Connect.getErrorMessages(errorData));
		}
		else{
			this.setErrorText(errorData);
		}
		
	}, 
	
	performAction: function()
	{
		if(this.state == 'error'){
			if(this.errorDialog.isVisible() != true){
				this.errorDialog.show();
			}
		}
	}, 
	
	setErrorText: function(text){
		this.errorDialog.setErrorText(text);
	}
});