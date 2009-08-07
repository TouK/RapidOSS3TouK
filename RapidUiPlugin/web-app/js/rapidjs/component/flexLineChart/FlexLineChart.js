YAHOO.namespace('rapidjs', 'rapidjs.component');

YAHOO.rapidjs.component.FlexLineChart = function(container, config) {
    YAHOO.rapidjs.component.FlexLineChart.superclass.constructor.call(this, container, config);
    var events = {
        'itemClicked': new YAHOO.util.CustomEvent('itemClicked'),
        'rangeChanged': new YAHOO.util.CustomEvent('rangeChanged')
    };
    YAHOO.ext.util.Config.apply(this.events, events);

	this.swfURL = config.swfURL;
	this.chartWidth = config.width || 680;
	this.chartHeight = config.height || 580;
	this.bgcolor = config.bgcolor || "#869ca7"
	this.url = config.url || "script/run/rrdXmlLoader?name=yahooUtil";  //todo: to be deleted
	this.application = null;

	//xml data attribute names:
	this.rootTag = config.rootTag || "RootTag";
		//graph constants
	this.dataRootTag = config.dataRootTag || "Variable";
	this.dataTag = config.dataTag || "Data";
	this.dateAttribute = config.dateAttribute  || "time";
	this.valueAttribute = config.valueAttribute  || "value";
		//annotation constants
	this.annotationTag = config.annotationTag  || "Annotation";
	this.annLabelAttr = config.annLabelAttr || "label";
	this.annTimeAttr = config.annTimeAttr  || "time";
		//durations for zooming flex line chart
	this.durations = config.durations  || "";

	this.header = YAHOO.ext.DomHelper.append(this.container, {tag:'div'});
    this.toolbar = new YAHOO.rapidjs.component.tool.ButtonToolBar(this.header, {title:this.title});
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.LoadingTool(document.body, this));
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.SettingsTool(document.body, this));
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.ErrorTool(document.body, this));

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
							<param value='Transparent' name='wmode'/> \
							<embed src='"+this.swfURL+"' id='embed"+this.id+"' quality='high' bgcolor='"+this.bgcolor+"' \
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


		this.flashTimer = new YAHOO.ext.util.DelayedTask(this.isFlashLoaded, this);
        this.flashTimer.delay(300);
    },
    handleSuccess:function(response, keepExisting, removeAttribute) {
	    var xmlDoc = response.responseXML;

		var root = xmlDoc.getElementsByTagName(this.rootTag)[0];
		this.loadRangeData(root);
		this.loadVolumeData(root);
		this.loadAnnotations(root);
		this.addDurationButtons();
    },
	loadRangeData: function(root){
		/*todo: dataroot will be used for multiple data. usage will be in this way:
			//@unchecked
			var dataRoots = getElementsByTagName(this.dataRootTag);
			var multiDataArray = new Array();
			for(var i=0; i<dataRoots.length; i++){
				multiDataArray[i] = {};
				var dataList = dataRoots[i].getElementsByTagName(this.dataTag);
				var mapArray = new Array();
				....  //same with below
				var rootAttributes = dataRoots[i].attributes;
				multiDataArray["content"] = mapArray;
				for(var j=0; j<rootAttributes.length; j++){
					var rootKey = rootAttributes.item(j).nodeName;
					var rootValue = rootAttributes.item(j).nodeValue;
					multiDataArray[rootKey] = rootValue;
				}
			}
		*/
		var dataRoot = root.getElementsByTagName(this.dataRootTag)[0];
		var dataList = dataRoot.getElementsByTagName(this.dataTag);

		var mapArray = new Array();
		//dataList.length
		for(i=0; i<dataList.length; i++){
			mapArray[i] = {};
			var node = dataList.item(i);
			var attributes = node.attributes;
			for(j=0; j<attributes.length; j++){
				var key = attributes.item(j).nodeName;
				var avalue = attributes.item(j).nodeValue;
				mapArray[i][key] = avalue;
			}
			var date = new Date(+(node.getAttribute(this.dateAttribute)));
			mapArray[i][this.dateAttribute] = date.format('Y-n-d H:i')+"";
			var avalue = +(attributes.getNamedItem(this.valueAttribute).nodeValue);
			if(avalue == 0) avalue = 500.9;
			mapArray[i][this.valueAttribute] = avalue;
			/*
			var volume = attributes.getNamedItem(this.volumeAttribute);
			if(volume ==null){
				mapArray[i][this.volumeAttribute] = avalue;
			}else{
				mapArray[i][this.volumeAttribute] = parseInt(volume.nodeValue);
			}
			*/
		}
		/*
		alert(dataList.item(100).getAttribute("time")+" " + dataList.item(100).getAttribute("value")+" " + mapArray[100]["time"]+" "+mapArray[100]["value"]+"\n"+
				dataList.item(101).getAttribute("time")+" " + dataList.item(101).getAttribute("value")+" " + mapArray[99]["time"]+" "+mapArray[1]["value"]+"\n"+
				dataList.item(102).getAttribute("time")+" " + dataList.item(102).getAttribute("value")+" " + mapArray[2]["time"]+" "+mapArray[2]["value"]+"\n"+
				dataList.item(103).getAttribute("time")+" " + dataList.item(103).getAttribute("value")+" " + mapArray[3]["time"]+" "+mapArray[3]["value"]+"\n"+
				dataList.item(104).getAttribute("time")+" " + dataList.item(104).getAttribute("value")+" " + mapArray[4]["time"]+" "+mapArray[4]["value"]+"\n");
		*/
				this.application.loadRangeData(mapArray, this.dateAttribute, this.valueAttribute);
	},
	loadVolumeData: function(root){
		var dataRoot = root.getElementsByTagName(this.dataRootTag);
		var rootIndex;
		for(i=0; i<dataRoot.length; i++){
			if(dataRoot[i].getAttribute("Target")=="LowerBand"){
				rootIndex=i;
				break;
			}
		}
		if(rootIndex == null){
			return;
		}
		var dataList = dataRoot[rootIndex].getElementsByTagName(this.dataTag);

		var mapArray = new Array();

		for(i=0; i<dataList.length; i++){
			mapArray[i] = {};
			var node = dataList.item(i);
			var attributes = node.attributes;
			for(j=0; j<attributes.length; j++){
				var key = attributes.item(j).nodeName;
				var avalue = attributes.item(j).nodeValue;
				mapArray[i][key] = avalue;
			}
			var date = new Date(parseInt(attributes.getNamedItem(this.dateAttribute).nodeValue));
			mapArray[i][this.dateAttribute] = date.format('Y-n-d H:i')+"";
			var avalue = parseInt(attributes.getNamedItem(this.valueAttribute).nodeValue);
			mapArray[i][this.valueAttribute] = avalue;
		}

		this.application.loadVolumeData(mapArray);
	},
    loadAnnotations: function(root) {
	    var annotationList = root.getElementsByTagName(this.annotationTag);

	    var annotationArray = new Array();
		var str = "";
		for(i=0; i<annotationList.length; i++){
			annotationArray[i] = {};
			var node = annotationList.item(i);
			var attributes = node.attributes;
			for(j=0; j<attributes.length; j++){
				var key = attributes.item(j).nodeName;
				var avalue = attributes.item(j).nodeValue;
				annotationArray[i][key] = avalue;
				str += key + ": "+avalue+"\n";
			}
		}

		this.application.loadAnnotations(annotationArray, this.annLabelAttr, this.annTimeAttr);
    },
    setAnnotation: function(annotationInfo) {
	    this.events["itemClicked"].fireDirect(annotationInfo)

	    /*
	    var infoString = "";
	    for(key in annotationInfo){
			infoString += key + ": " + annotationInfo[key]+"\n" ;
	    }
	    alert(infoString);
	    */
    },
    setBoundaries: function(rangeInfo) {
	    //setTimeout(alert("ager"),4000);
	    setTimeout(
	    this.events["rangeChanged"].fireDirect(rangeInfo),
	    		300);
    },
	//sample: this.addAnnotation({description:'Test Item', name:'added', index:23})
    addAnnotation: function(annotationInfo) {
	    this.application.addAnnotation(annotationInfo);
    },
    addDurationButtons: function() {
	    if(this.durations ==undefined || this.durations ==null ||
	    	this.durations =="") return;
	    var durationArray = new Array();

	    durationArray = this.durations.replace(/ /,"").split(",");

	    this.application.addDurationButtons(durationArray);
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
				this.application.applicationLoaded(this.id);
				this.chartReady();
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
	    if(this.application == null ){
		    alert("application is null");
		}
	    this.poll();
	},
	applicationResize: function(){
		this.application.applicationResize();
	},
    resize: function(width, height) {
        this.body.setHeight(height - this.header.offsetHeight);
        this.body.setWidth(width);
    },
    showMessage: function(message) {
        alert(message);
    }
})

