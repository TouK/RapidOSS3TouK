YAHOO.rapidjs.component.layout.ServerStatusTool = function()
{
	this.state = 'available';
	this.refreshState(this.state);
};

YAHOO.rapidjs.component.layout.ServerStatusTool.prototype = 
{
	render: function(parentElement)
	{
		this.wrapper = document.createElement("td");
		this.wrapper.innerHTML = '<img src="../images/layout/network-offline.png"/>';
		YAHOO.util.Dom.setStyle(this.wrapper, 'display', 'none');
		parentElement.appendChild(this.wrapper);
	},
	
	refreshState: function(isServerAvailable){
		if(isServerAvailable == true && this.state=='unavailable'){
			if(this.wrapper){
				YAHOO.util.Dom.setStyle(this.wrapper, 'display', 'none');
			}
			this.state = 'available';
		}
		else if(isServerAvailable == false && this.state == 'available'){
			if(this.wrapper){
				YAHOO.util.Dom.setStyle(this.wrapper, 'display', '');
			}
			this.state = 'unavailable';
		}
	}
}
