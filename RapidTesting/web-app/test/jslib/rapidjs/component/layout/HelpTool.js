YAHOO.rapidjs.component.layout.HelpTool = function(layout, regionName, component, config)
{
	YAHOO.rapidjs.component.layout.HelpTool.superclass.constructor.call(this, layout, regionName,component, config);
	
	this.toolComp.dom.setAttribute('title', this.getToolName());
	this.toolInnerComp.addClass("layout-helptool");
	
	var dialogDiv = document.createElement("div");
	document.body.appendChild(dialogDiv);
	this.helpWindow = new YAHOO.rapidjs.component.windows.HtmlWindow(dialogDiv, config);
	this.popupWindow = new YAHOO.rapidjs.component.PopUpWindow(this.helpWindow, {modal:false,shadow:true,width:600,height:450,minWidth:600,minHeight:450,title:this.getToolName()});
};

YAHOO.extendX(YAHOO.rapidjs.component.layout.HelpTool, YAHOO.rapidjs.component.layout.LayoutTool, {
	
	getToolName: function()
	{
		return 'Help';
	},
	
	performAction: function()
	{
		this.popupWindow.show();
	},
	render: function()
	{
		var wrapper = document.createElement("td");
		wrapper.innerHTML = '<a href="javascript:logout();" class="whitelink">Logout</a>';
		this.region.getTabs().toolsArea.appendChild(wrapper);
	}
});