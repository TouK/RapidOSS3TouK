YAHOO.namespace('rapidjs', 'rapidjs.component');


YAHOO.rapidjs.component.FlexApplicationMethodCaller = function(flexObjectId, flexEmbedId) {
	//xml data attribute names:
    this.flexObjectId = flexObjectId;
    this.flexEmbedId = flexEmbedId;
    this.methodsToBeCalled = {};
    this.methodCallTimer = new YAHOO.ext.util.DelayedTask(this.callMethods, this);
}


YAHOO.rapidjs.component.FlexApplicationMethodCaller.prototype =
{
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
            var app = this.getFlexApp();
            var mt = app[methodName];
            if(mt != null)
            {
                mt.apply(app, args)
            }
            else
            {
                this.methodsToBeCalled[methodName] = {methodName:methodName, args:args};
                this.methodCallTimer.cancel();
                this.methodCallTimer = new YAHOO.ext.util.DelayedTask(this.callMethods, this);
                this.methodCallTimer.delay(300);
            }
        }catch(e)
        {
            this.methodsToBeCalled[methodName] = {methodName:methodName, args:args};
            this.methodCallTimer.cancel();
            this.methodCallTimer = new YAHOO.ext.util.DelayedTask(this.callMethods, this);
            this.methodCallTimer.delay(300);
        }

    },
    callMethods: function()
    {
        var allProcessed = true;
        for(var i in this.methodsToBeCalled)
        {
            var methodConf = this.methodsToBeCalled[i]
            if(methodConf != null)
            {
                try{
                    var app = this.getFlexApp();
                    var mt = app[methodConf.methodName];
                    if(mt != null)
                    {
                        mt.apply(app, methodConf.args);
                        this.methodsToBeCalled[i] = null;
                    }
                    else
                    {
                        allProcessed = false;
                    }
                }catch(e)
                {
                    allProcessed = false;
                }
            }
        }
        if(!allProcessed)
        {
            this.methodCallTimer.cancel();
            this.methodCallTimer = new YAHOO.ext.util.DelayedTask(this.callMethods, this);
            this.methodCallTimer.delay(300);
        }
    }
}

