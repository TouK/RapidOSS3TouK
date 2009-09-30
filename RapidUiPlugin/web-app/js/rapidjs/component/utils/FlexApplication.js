YAHOO.namespace('rapidjs', 'rapidjs.component');
function flexApplicationEventHandler(appId, event)
{
    var application = YAHOO.rapidjs.component.FlexApplications[appId]
    if(application != null)
    {
        if(event == "swfInitializing")
        {
            application.initializing();
        }
        if(event == "swfReady")
        {
            application.ready();
        }
        else if(event == "swfHidden")
        {
            application.unloaded();
        }
    }
}
YAHOO.rapidjs.component.FlexApplications = {};

YAHOO.rapidjs.component.FlexApplication = function(flexObjectId, swfUrl, methodToCheck) {
	//xml data attribute names:
    this.swfUrl = swfUrl;
    this.methodToCheck = methodToCheck;
    this.flexObjectId = flexObjectId;
    this.flexEmbedId = "embed"+flexObjectId;
    YAHOO.rapidjs.component.FlexApplications[this.flexObjectId] = this;
    YAHOO.rapidjs.component.FlexApplications[this.flexEmbedId] = this;
    this.applicationIsReady = false;
    this.renderTask = new YAHOO.ext.util.DelayedTask(this._render, this);
    this.events = {
        ready:new YAHOO.util.CustomEvent('ready'),
        initializing:new YAHOO.util.CustomEvent('initializing'),
        hide:new YAHOO.util.CustomEvent('hide')
    }
    this.methodsToBeCalled = [];
    this.methodCallTask = new YAHOO.ext.util.DelayedTask(this.executeMethods, this);
}


YAHOO.rapidjs.component.FlexApplication.prototype =
{
    ready: function()
    {
        this.events.ready.fireDirect(this);
        this.applicationIsReady = true;
    },
    initializing: function()
    {
        this.applicationIsReady = false;
        this.events.initializing.fireDirect(this);
    },
    unloaded: function()
    {
        this.applicationIsReady = false;
        this.events.hide.fireDirect(this);
    },
    render: function(container)
    {
        this.container = container;
        this.renderTask.delay(1500);
    },
    _render: function()
    {
        this.body = YAHOO.ext.DomHelper.append(this.container,{tag:'div'},true);
        var objectString = "<object classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000' \
							id='"+this.flexObjectId+"' name='"+this.flexObjectId+"' width='100%' height='100%' \
							codebase='http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab'> \
							<param name='movie' value='"+this.swfUrl+"' /> \
							<param name='quality' value='high' /> \
							<param name='allowScriptAccess' value='always' /> \
							<param name='swliveconnect' value='true' /> \
							<param value='Transparent' name='wmode'/> \
							<param value='eventHandler=flexApplicationEventHandler' name='flashvars'/> \
							<embed src='"+this.swfUrl+"' id='"+this.flexEmbedId+"' quality='high' \
								width='100%' height='100%' name='"+this.flexEmbedId+"' align='middle' \
								play='true'  \
								loop='false' \
								flashvars='eventHandler=flexApplicationEventHandler' \
								quality='high' \
								swliveconnect='true' \
								wmode = 'Transparent' \
								allowScriptAccess='always' \
								type='application/x-shockwave-flash' \
								pluginspage='http://www.adobe.com/go/getflashplayer'> \
							</embed> \
						</object>"
		this.body.dom.innerHTML = objectString;
    },

    getFlexApp: function(){
      if (navigator.appName.indexOf ("Microsoft") !=-1)
      {
        var version = this.findVersion();
        if (version>6 ){
          return document[this.flexObjectId];
        }

        return window[this.flexObjectId];
      }
      else{
        return document[this.flexEmbedId];
      }

    },
    findVersion: function(){
        var version = navigator.appVersion;
        var versionArray = version.split(";");
        for(var i=0; i<versionArray.length; i++){
            if(versionArray[i].indexOf("MSIE") >-1){
                version = versionArray[i];
                version = version.replace("MSIE","");
                version = parseFloat(version);
                return version ;
            }
        }
        return -1;
    },
    executeMethod: function(owner, func, args, functionName)
    {
        if(this.applicationIsReady == true && this.methodsToBeCalled.length == 0 )
        {
            try{
                func.apply(owner, args);
            }catch(e)
            {
                this.applicationIsReady = false;
                this.methodsToBeCalled.push([owner, func, args, functionName]);
                this.executeMethods();
            }
        }
        else
        {
            this.methodsToBeCalled.push([owner, func, args, functionName]);
            this.executeMethods();
        }
    },
    executeMethods:function()
    {
        if(this.applicationIsReady == true)
        {
            var l = this.methodsToBeCalled.length;
            for(var i=0; i < l; i++)
            {
                var args = this.methodsToBeCalled[i];
                args[1].apply(args[0], args[2]);
            }
            this.methodsToBeCalled.splice(0, l)
        }
        if(this.methodsToBeCalled.length > 0)
        {
            this.methodCallTask.cancel();
            this.methodCallTask = new YAHOO.ext.util.DelayedTask(this.executeMethods, this);
            this.methodCallTask.delay(300);    
        }
    }
}

