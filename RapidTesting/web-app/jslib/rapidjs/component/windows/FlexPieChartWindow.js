YAHOO.rapidjs.component.windows.FlexPieChartWindow = function(container, config){
	YAHOO.rapidjs.component.windows.FlexPieChartWindow.superclass.constructor.call(this,container, config);
	YAHOO.ext.util.Config.apply(this, config);
	this.configureTimeout(config);
	this.gradients = {};
	this.renderTask = new YAHOO.ext.util.DelayedTask(this.render, this);
	this.response = null;
	this.eventListenerAdded = false;
	this.initializeTask = new YAHOO.ext.util.DelayedTask(this.handleSuccess, this);
	this.freeTabLockTask = new YAHOO.ext.util.DelayedTask(this.freeTabLock, this);
	this.panel = new YAHOO.rapidjs.component.layout.RapidPanel(this.container, {title:this.title, fitToFrame:true});
	this.subscribeToPanel();
	this.render();
	this.tabLocked = false;//boolean used to wait 1 seconds during tab switching.
};


YAHOO.extendX(YAHOO.rapidjs.component.windows.FlexPieChartWindow, YAHOO.rapidjs.component.PollingComponentContainer, {
	processSuccess : function(response){
		this.response = response;
		YAHOO.rapidjs.ServerStatus.refreshState(true);
		if(YAHOO.rapidjs.Connect.isAuthenticated(response) == true){
			this.handleSuccess();
		}
		else{
			window.location = "login.html?page=" + window.location.pathname;
		}
		
	}, 
	
	handleSuccess: function(){
		if(!FABridge[this.id] || this.tabLocked == true){
			this.initializeTask.delay(100);
		}
		else
		{
			if(this.eventListenerAdded == false)
			{
				try
				{
					var scope = this;
					if(this.handlerFunc)
						FABridge[this.id].root().getMyChart().removeEventListener("itemClick", this.handlerFunc);
					this.handlerFunc = function(e){
						scope.handleItemClick(e);
					};
					FABridge[this.id].root().getMyChart().addEventListener("itemClick", this.handlerFunc);
					this.eventListenerAdded = true;
				}
				catch(e)
				{
					this.initializeTask.delay(10);
				}
			}
			try
			{
				if(YAHOO.rapidjs.Connect.containsError(this.response) == false)
				{
					this.events["erroroccurred"].fireDirect(this, false,'');
					this.processData(this.response);
				}
				else
				{
					this.events["erroroccurred"].fireDirect(this, true, this.response.responseXML);
				}
				
			}
			catch(e)
			{
			}
			this.events["loadstatechanged"].fireDirect(this, false);
			if(this.pollInterval > 0 && this.panel.isVisible)
			{
				this.pollTask.delay(this.pollInterval*1000);
			}
		}
	},
	
	handleItemClick: function(e)
	{
		var index = e.getHitData().getChartItem().getIndex();
		if(index < 0)
		{
			return;
		}
		var mySeries = e.bridge.root().getMySeries();
		var oldRadiuses = mySeries.getPerWedgeExplodeRadius();
		var newRadiuses = new Array();
		for(var i = 0 ; i < oldRadiuses.length ; i++)
		{
			newRadiuses[i] = oldRadiuses[i];
		}
		var value = oldRadiuses[index];
		if(!value || value == 0)
		{
			value = 0.1;	
		}
		else
		{
			value = 0.0;
		}
		newRadiuses[index] = value;
		mySeries.setPerWedgeExplodeRadius(newRadiuses);
		
		var dataProvider = e.bridge.root().getMyChart().getDataProvider();
		var selectedData = dataProvider.getItemAt(index);
		var dataToSend = new YAHOO.rapidjs.data.RapidXmlNode(null, null, 1, null);
		dataToSend.attributes["chartId"] = this.id;
		var tmpColData = this.columnData[selectedData["Label"]];
		for (var propName in tmpColData) 
		{
			var propValue = tmpColData[propName];
			dataToSend.attributes[propName] = propValue;
		}
		this.sendOutputs(dataToSend);
	},
	
	processData: function(response){
		var dataString = response.responseText;
		var dataTag = response.responseXML.getElementsByTagName(this.rootTag)[0];
		var slices = dataTag.getElementsByTagName("set");
		var arrayOfSlices = new Array();
		var arrayOfGradients = new Array();
		var bridgeObj = FABridge[this.id];
		this.columnData = {};
		for(var i = 0 ; i < slices.length ; i++)
		{
			var allAttrs = slices[i].attributes;
			var colLabel = slices[i].getAttribute("label");
			var closureForSlice = {Label:colLabel, Data:slices[i].getAttribute("value")};
			this.columnData[colLabel] = {};
			for(var index=0; index < allAttrs.length; index++)
			{
				var attNode = allAttrs.item(index);
				this.columnData[colLabel][attNode.nodeName] = attNode.nodeValue;
			}
			arrayOfSlices[i] = closureForSlice;
			arrayOfGradients[i] = this.getGradient(bridgeObj, slices[i].getAttribute('color'));
			if(arrayOfGradients[i] == null)
			{
				return;
			}
		}
		bridgeObj.root().getMySeries().setStyle("fills", arrayOfGradients);
		bridgeObj.root().getMyChart().setDataProvider( arrayOfSlices );
		var title = dataTag.getAttribute("Title");
		if(title)
		{
			bridgeObj.root().getMyPanel().setTitle( title );
		}
	},
	
	getGradient: function(bridgeObj, colorStr)
	{
		if(this.gradients[colorStr]){
			return this.gradients[colorStr];
		}
		else{
			try
			{
				var radialGradient = bridgeObj.create("mx.graphics.RadialGradient");
				var ge1 = bridgeObj.create("mx.graphics.GradientEntry");
				ge1.setColor("0xFFFFFF");
				ge1.setRatio(0.0);
				ge1.setAlpha(1.0);
				var ge2 = bridgeObj.create("mx.graphics.GradientEntry");
				ge2.setColor(colorStr);
				ge2.setRatio(1.0);
				ge2.setAlpha(0.5);
				var entries = new Array();
				entries[0] = ge1;
				entries[1] = ge2;
				radialGradient.setEntries(entries);
				this.gradients[colorStr] = radialGradient;
				return radialGradient;
			}
			catch(e)
			{
				this.eventListenerAdded = false;
				this.initializeTask.delay(10);
				return null;
			}
		}
		
	},
	
	render : function(){
		if(this.panel.region && this.panel.isLayoutUpdating == false){
			
			var requiredMajorVersion = 9;
			var requiredMinorVersion = 0;
			var requiredRevision = 0;
			
			var hasProductInstall = DetectFlashVer(6, 0, 65);
			var hasRequestedVersion = DetectFlashVer(requiredMajorVersion, requiredMinorVersion, requiredRevision);
			var containerElement = (typeof this.container == 'string') ? document.getElementById(this.container) : this.container;
			if(!hasProductInstall || !hasRequestedVersion)
			{
				containerElement.innerHTML = "This application requires Flash player version " + requiredMajorVersion + ". Click <a href='http://www.adobe.com/shockwave/download/download.cgi?P1_Prod_Version=ShockwaveFlash'>here</a> to download";
			}
			else
			{
				var sb = new Array();
				sb[sb.length] = "<object id='flexApp_" + this.id + "' classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000' codebase='http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=8,5,0,0' height='100%' width='100%'>";
				sb[sb.length] = "<param name='flashvars' value='bridgeName=" + this.id + "'/>";
				sb[sb.length] = "<param name='wmode' value='transparent'/>";
				sb[sb.length] = "<param name='src' value='" + this.chartSWF + "'/>";
				sb[sb.length] = "<embed name='flexApp' wmode='transparent' pluginspage='http://www.macromedia.com/go/getflashplayer' src='" + this.chartSWF + "' height='100%' width='100%' flashvars='bridgeName=" + this.id + "'/>";
				sb[sb.length] = "</object>";
				
				var chartHtml = sb.join('');
				containerElement.innerHTML = chartHtml;
			}
		}
		else{
			this.renderTask.delay(100);
		}
	},
	
	clearData:function(){
		
	},
	
	handleVisible : function(){
		this.gradients = {};
		this.eventListenerAdded = false;
		YAHOO.rapidjs.component.windows.FlexPieChartWindow.superclass.handleVisible.call(this);
		this.tabLocked = true;
		this.freeTabLockTask.delay(1000);//we need to wait some time. 1 second is secure enough.
		//it is also possible to make a loop to check that accessing to the root of the bridge gives an error(for exp. bridgeObj.root().getMySeries()) or not and when the error
		//is gone, break the loop. I tried this and it did not work. Sometimes it is OK, but sometimes the chart disappears.
	},
	
	freeTabLock: function()
	{
		this.tabLocked = false;
	}
});