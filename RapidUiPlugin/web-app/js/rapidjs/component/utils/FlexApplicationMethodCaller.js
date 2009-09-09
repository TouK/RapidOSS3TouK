YAHOO.namespace('rapidjs', 'rapidjs.component');

function applicationIsReady(appId)
{
    var caller = YAHOO.rapidjs.component.FlexApplicationMethodCallersById[appId];
    if(caller)   {
        caller.ready();
    }
    else{
        YAHOO.rapidjs.component.FlexApplicationMethodCallersByEmbedId[appId].ready();    
    }

}
YAHOO.rapidjs.component.FlexApplicationMethodCallersById = [];
YAHOO.rapidjs.component.FlexApplicationMethodCallersByEmbedId = [];
YAHOO.rapidjs.component.FlexApplicationMethodCaller = function(flexObjectId, flexEmbedId, swfUrl) {
	//xml data attribute names:
    this.swfUrl = swfUrl;
    this.flexObjectId = flexObjectId;
    this.flexEmbedId = flexEmbedId;
    this.methodsToBeCalled = {};
    this.methodCallOrder = [];
    YAHOO.rapidjs.component.FlexApplicationMethodCallersById[flexObjectId] = this;
    YAHOO.rapidjs.component.FlexApplicationMethodCallersByEmbedId[flexEmbedId] = this;
    this.methodCallTimer = new YAHOO.ext.util.DelayedTask(this.callMethods, this);
    this.isReady = false;
    this.calledForFirstTime = false;
    this.renderTask = new YAHOO.ext.util.DelayedTask(this._render, this);
    this.container = null;
}


YAHOO.rapidjs.component.FlexApplicationMethodCaller.prototype =
{
    ready: function()
    {
        this.isReady = true;
    },

    configureTimer: function(delay)
    {
        this.methodCallTimer.cancel();
        this.methodCallTimer = new YAHOO.ext.util.DelayedTask(this.callMethods, this);
        this.methodCallTimer.delay(delay);
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
							<embed src='"+this.swfUrl+"' id='"+this.flexEmbedId+"' quality='high' \
								width='100%' height='100%' name='"+this.flexEmbedId+"' align='middle' \
								play='true'  \
								loop='false' \
								quality='high' \
								swliveconnect='true' \
								wmode = 'Transparent' \
								allowScriptAccess='always' \
								type='application/x-shockwave-flash' \
								pluginspage='http://www.adobe.com/go/getflashplayer'> \
							</embed> \
						</object>"
		this.body.dom.innerHTML = objectString;
        this.configureTimer(1000)
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
    callMethod: function(methodName, args)
    {
        try{
            if(!this.calledForFirstTime)
            {
                this.methodCallOrder.push(methodName);
                this.methodsToBeCalled[methodName] = {methodName:methodName, args:args};
                return;
            }
            var app = this.getFlexApp();
            var mt = app[methodName];
            if(mt != null)
            {
                mt.apply(app, args)
            }
            else
            {
                this.methodCallOrder.push(methodName);
                this.methodsToBeCalled[methodName] = {methodName:methodName, args:args};
                this.configureTimer(300)
            }
        }catch(e)
        {
            this.methodCallOrder.push(methodName);
            this.methodsToBeCalled[methodName] = {methodName:methodName, args:args};
            this.configureTimer(300)
        }

    },
    callMethods: function()
    {
        var allProcessed = true;
        var l =this.methodCallOrder.length;
        var tmpMethodCallOrder = this.methodCallOrder.slice(0, l-1);
        for(var i =0; i < tmpMethodCallOrder.length; i++)
        {
            var methodConf = this.methodsToBeCalled[tmpMethodCallOrder[i]]
            if(methodConf != null)
            {
                try{
                    var app = this.getFlexApp();
                    var mt = app[methodConf.methodName];
                    if(mt != null)
                    {
                        mt.apply(app, methodConf.args);
                        this.methodsToBeCalled[tmpMethodCallOrder[i]] = null;
                    }
                    else
                    {
                        allProcessed = false;
                        break;
                    }
                }catch(e)
                {
                    allProcessed = false;
                    break;
                }
            }
        }
        if(allProcessed)
        {
            this.calledForFirstTime = true;
            this.methodCallOrder.splice(0,l);
        }
        else
        {
            this.configureTimer(300)
        }
    }
}

