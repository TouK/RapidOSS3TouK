YAHOO.rapidjs.component.windows.HtmlWindow = function(container, config){
	YAHOO.rapidjs.component.windows.HtmlWindow.superclass.constructor.call(this,container, config);
	YAHOO.ext.util.Config.apply(this, config);
	this.panel = new YAHOO.rapidjs.component.layout.RapidPanel(this.container, {title:this.title, fitToFrame:true});
	this.subscribeToPanel();
	this.render();
};

YAHOO.extendX(YAHOO.rapidjs.component.windows.HtmlWindow, YAHOO.rapidjs.component.PollingComponentContainer, {
	render: function()
	{
		this.iFrame = document.createElement("iframe");
		this.iFrame.height = "100%";
		this.iFrame.width = "100%";
		this.iFrame.frameBorder = "0";
		this.iFrame.marginHeight = "0";
		this.iFrame.marginWidth = "0";
		this.container.appendChild(this.iFrame);
		YAHOO.util.Event.addListener(this.iFrame, 'load', this.onLoad, this,true);
	},
	unvisible: function()
	{
		this.pollTask.cancel();
		this.iFrame.src = "";
	},
	doRequest: function(url, params)
	{	
		this.pollTask.cancel();
		if(params == null)
		{
			params = {};
		}
		delete params[this.extraRequestParameterName];
		if (this.extraRequestParameterName != null && this.extraRequestParameterName != "" && this.extraRequestParameterName != undefined) {
			if (this.extraRequestParameterValue != null && this.extraRequestParameterValue != "" ) {
				params[this.extraRequestParameterName] = this.extraRequestParameterValue;
			}
		}
		
		var postData = "";
		for(var paramName in params) {
			postData = postData + paramName + "=" + escape(params[paramName])+"&";
		}
		if(postData != "")
		{
			postData = postData.substring(0, postData.length-1);
		}
		if(postData && postData != "")
		{
			if(url.indexOf("?") >= 0)
			{
				url = url + "&" + postData;
			}
			else
			{
				url = url + "?" + postData;
			}
		}
		this.iFrame.src = url;
		this.events["loadstatechanged"].fireDirect(this, true);
	},
	
	onLoad : function(e){
		this.events["loadstatechanged"].fireDirect(this, false);
		if(this.pollInterval > 0 && this.panel.isVisible)
		{
			this.pollTask.delay(this.pollInterval*1000);
		}
	}
});
	