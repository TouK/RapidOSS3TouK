YAHOO.namespace('rapidjs', 'rapidjs.component');

function   timeRangeSelectorJsFunction(params)
{
    var componentId = params.componentId
    var functionName = params.functionName
    if(functionName == "intervalChanged")
    {
        var fromDate = params.fromDate
        var toDate = params.toDate
        YAHOO.rapidjs.TimeRangeSelectors[componentId].fireRangeChanged(fromDate, toDate);
    }
    else if(functionName == "buttonClicked"){
        YAHOO.rapidjs.TimeRangeSelectors[componentId].fireButtonClicked(params.data);
    }
}

YAHOO.rapidjs.TimeRangeSelectors = {};

YAHOO.rapidjs.component.TimeRangeSelector = function(config) {
	//xml data attribute names:
    this.config = config;
    this.config.javascriptFunctionToCall = "timeRangeSelectorJsFunction";
    this.id = config.id;
    this.rootTag = config.dataRootTag || "Datum";
	this.dataTag = config.dataTag || "Data";
	this.isFlashLoaded = false;
    this.swfUrl = "../images/rapidjs/component/timeRangeSelector/TimeRangeSelector.swf";
    this.events = {
        'rangeChanged': new YAHOO.util.CustomEvent("rangeChanged"),
        'buttonClicked': new YAHOO.util.CustomEvent("buttonClicked")
    }
    YAHOO.rapidjs.TimeRangeSelectors[this.id] = this;
}


YAHOO.rapidjs.component.TimeRangeSelector.prototype =
{
    render: function(container) {
        this.container = container;
        this.body = YAHOO.ext.DomHelper.append(this.container,{tag:'div'},true);
        var objectString = "<object classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000' \
							id='"+this.id+"' name='"+this.id+"' width='100%' height='100%' \
							codebase='http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab'> \
							<param name='movie' value='"+this.swfUrl+"' /> \
							<param name='quality' value='high' /> \
							<param name='allowScriptAccess' value='sameDomain' /> \
							<param value='Transparent' name='wmode'/> \
							<embed src='"+this.swfUrl+"' id='embed"+this.id+"' quality='high' \
								width='100%' height='100%' name='embed"+this.id+"' align='middle' \
								play='true'  \
								loop='false' \
								quality='high' \
								wmode = 'Transparent' \
								allowScriptAccess='sameDomain' \
								type='application/x-shockwave-flash' \
								pluginspage='http://www.adobe.com/go/getflashplayer'> \
							</embed> \
						</object>"

		this.body.dom.innerHTML = objectString;


		this.configurationTimer = new YAHOO.ext.util.DelayedTask(this.checkFlashLoaded, this);
        this.configurationTimer.delay(300);
    },
    loadButtons: function(buttons)
    {
        if(this.isFlashLoaded == false)
        {
            this.loadButtonsTimer = new YAHOO.ext.util.DelayedTask(this.loadButtons, this, buttons);
            this.loadButtonsTimer.delay(300);
        }
        else
        {
            this.getFlexApp().loadButtons(buttons);
        }
    },
    loadData: function(responseXml)
    {
        if(this.isFlashLoaded == false)
        {
            this.loadDataTimer = new YAHOO.ext.util.DelayedTask(this.loadData, this, responseXml);
            this.loadDataTimer.delay(300);
        }
        else
        {
            var rootTag = responseXml.getElementsByTagName(this.rootTag)[0];
            var datum = rootTag.getElementsByTagName(this.dataTag)
            var dataForFlex = [];
            for(var i=0; i < datum.length; i++)
            {
                var xmlDataAttributes = datum[i].attributes
                var nodeData = {};
                for(var attrName in xmlDataAttributes)
                {
                    nodeData[attrName] = xmlDataAttributes[attrName];
                }
                dataForFlex[dataForFlex.length] = nodeData;
            }
            this.getFlexApp().loadData(dataForFlex);
        }

    },
    fireRangeChanged: function(fromDate, toDate){
        this.events.rangeChanged.fireDirect(fromDate, toDate);
    },
    fireButtonClicked: function(buttonData){
        this.events.buttonClicked.fireDirect(buttonData);
    },
    getFlexApp: function(){
      if (navigator.appName.indexOf ("Microsoft") !=-1)
      {
        var version = this.findVersion();
        if (version>6 ){
          return document[this.id];
        }

        return window[this.id];
      }
      else{
        return document["embed"+this.id];
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
    checkFlashLoaded: function(){
        if(this.isFlashLoaded == true) return true
        try{
            var application = this.getFlexApp();
            application.loadConfiguration(this.config);
            this.isFlashLoaded = true;
            return true;
        }catch(e){
            this.configurationTimer.delay(300);
        }
    }
}

