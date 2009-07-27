YAHOO.namespace('rapidjs', 'rapidjs.component');

YAHOO.rapidjs.component.FlexLineChart = function(container, config) {
    YAHOO.rapidjs.component.FlexLineChart.superclass.constructor.call(this, container, config);

	this.swfURL = config.swfURL;
	this.chartWidth = config.width || 680;
	this.chartHeight = config.height || 580;
	this.bgcolor = config.bgcolor || "#869ca7"
	this.url = config.url || "script/run/rrdXmlLoader?name=yahooUtil";  //todo: to be deleted
	this.application = null;

	this.body = YAHOO.ext.DomHelper.append(this.container,{tag:'div'},true);

    this.renderTask = new YAHOO.ext.util.DelayedTask(this.render, this);
    YAHOO.util.Event.onDOMReady(function() {
        this.renderTask.delay(500);
    },this, true);
}


/*
//TODO:enable/disable extra chart parts from configuration
//these methods are to set size of chart parts: main graph, range graph, volume graph
function setRangeChartHeight(){
	var application = getFlexApp(this.id);
	var height = document.getElementById('rangeHeightInput').value;
	application.setRangeChartHeight(height);
}
function setMainChartHeight(){
	var application = getFlexApp(this.id);
	var height = document.getElementById(this.id).value;
	application.setMainChartHeight(height);
}
function setVolumeChartHeight(){
	var application = getFlexApp(this.id);
	var height = document.getElementById('volumeHeightInput').value;
	application.setVolumeChartHeight(height);
}
*/

YAHOO.extend(YAHOO.rapidjs.component.FlexLineChart, YAHOO.rapidjs.component.PollingComponentContainer, {
    render: function() {
		var objectString = "<object classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000' \
							id='object"+this.id+"' name='object"+this.id+"' width='100%' height='100%' \
							codebase='http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab'> \
							<param name='movie' value='"+this.swfURL+"' /> \
							<param name='quality' value='high' /> \
							<param name='bgcolor' value='"+this.bgcolor+"' /> \
							<param name='allowScriptAccess' value='sameDomain' /> \
							<embed src='"+this.swfURL+"' id='embed"+this.id+"' quality='high' bgcolor='"+this.bgcolor+"' \
								width='100%' height='100%' name='embed"+this.id+"' align='middle' \
								play='true'  \
								loop='false' \
								quality='high' \
								allowScriptAccess='sameDomain' \
								type='application/x-shockwave-flash' \
								pluginspage='http://www.adobe.com/go/getflashplayer'> \
							</embed> \
						</object>"

		this.body.dom.innerHTML = objectString;


		this.flashTimer = new YAHOO.ext.util.DelayedTask(this.isFlashLoaded, this);
        this.flashTimer.delay(300);
    },
    handleSuccess:function(response, keepExisting, removeAttribute) {
        var xmlDoc = response.responseXML;

		var dateArray = new Array();
		var valueArray = new Array();

		var root = xmlDoc.getElementsByTagName('rrd')[0];
		var dataList = root.getElementsByTagName('data');

		for(i=0; i<dataList.length; i++){
			var node = dataList.item(i);
			var date = node.getElementsByTagName('date')[0].childNodes.item(0).nodeValue;
			var value = node.getElementsByTagName('value')[0].childNodes.item(0).nodeValue;

			dateArray[i] = date;
			valueArray[i] = value;
		}

		this.setRangeData(dateArray, valueArray);
    },
    getFlexApp: function(appName){
      if (navigator.appName.indexOf ("Microsoft") !=-1)
      {
        var version = this.findVersion();
        if (version>6 ){
          return document["object"+appName];
        }

        return window["object"+appName];
      }
      else{
        return document["embed"+appName];
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
    isFlashLoaded: function(){
        try{
            if(this.getFlexApp(this.id) != null){
				this.application = this.getFlexApp(this.id);
				return true;
            }
            else{
                this.flashTimer.delay(100);
            }
        }catch(e){
            this.flashTimer.delay(300);
        }
    },

	chartReady: function(){
        var flexLineChart = this;
	    flexLineChart.poll();

	},
	applicationResize: function(){
		this.application.applicationResize();
	},
	setRangeData: function(dates,values){
		var application = this.getFlexApp(this.id);

		if(application == null){
			alert("application is null")
		}
		try{
			this.application.setRangeData(dates,values);
			this.applicationResize();
		}catch(e){alert('setRangeData: '+e);}
	}
})
