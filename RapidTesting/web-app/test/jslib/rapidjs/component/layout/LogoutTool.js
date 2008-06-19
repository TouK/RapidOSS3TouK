YAHOO.rapidjs.component.layout.LogoutTool = function(layout, regionName)
{
	this.layout = layout;
	this.region = layout.getRegion(regionName);
	this.render();
};

YAHOO.rapidjs.component.layout.LogoutTool.prototype = 
{
	render: function()
	{
		var wrapper = document.createElement("td");
		wrapper.innerHTML = '<div class="rapid-logout-wrp"><a href="javascript:YAHOO.rapidjs.Login.logout();" class="whitelink">Logout</a></div>';
		this.region.getTabs().toolsArea.appendChild(wrapper);
	}
}
