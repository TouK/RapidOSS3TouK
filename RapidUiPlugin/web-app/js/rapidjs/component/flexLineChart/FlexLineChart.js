YAHOO.namespace('rapidjs', 'rapidjs.component');

YAHOO.rapidjs.component.FinanceChart = function(container, config) {
    YAHOO.rapidjs.component.FinanceChart.superclass.constructor.call(this, container, config);

	this.requiredMajorVersion = config.requiredMajorVersion || "9";
	this.requiredMinorVersion = config.requiredMinorVersion || "0";
	this.requiredRevision = config.requiredRevision || 124;
	this.chartSrc = "GoogleFinanceEmbedded.swf";//config.chartSrc
	this.chartWidth = config.chartWidth || "950";
	this.chartHeight = config.chartHeight || "550";
	this.chartAlignment = config.chartAlignment || "middle";
	this.chartId = config.chartId  || "GoogleFinanceEmbedded";
	this.chartQuality = config.chartQuality || "high";
	this.chartBgcolor = config.Bgcolor || "#869ca7"
	this.chartName = config.chartName || "GoogleFinanceEmbedded";
	this.allowScriptAccess = config.allowScriptAccess || "sameDomain";
	this.applicationType = config.applicationType || "application/x-shockwave-flash";
	this.pluginsPage = config.pluginsPage || "http://www.adobe.com/go/getflashplayer";

	this.body = YAHOO.ext.DomHelper.append(this.container,{tag:'div', id:"financeDiv", style:'height:550; width:900' });

	this.chartIsready = false;
	this.renderTask = new YAHOO.ext.util.DelayedTask(this.render, this);
    YAHOO.util.Event.onDOMReady(function() {
        this.renderTask.delay(500);
    },this, true);

}

function setChartReady(){
	this.chartIsready = true;
}
function isChartReady(){
	return this.chartIsReady;
}

function getFlexApp(appName)
{
  if (navigator.appName.indexOf ("Microsoft") !=-1)
  {
	var version = findVersion();
    if (version>6 ){
      return document[appName];
	}

    return window[appName];
  }
  else
  {
    return document[appName];
  }
}
function findVersion(){
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
}

YAHOO.extend(YAHOO.rapidjs.component.FinanceChart, YAHOO.rapidjs.component.PollingComponentContainer, {
    render: function() {
		var dh = YAHOO.ext.DomHelper;

		//alert('name: '+navigator.appName+'\nversion: '+navigator.appVersion);

		var embedTag = document.createElement("embed");
		embedTag.setAttribute("id","embedGoogleFinanceEmbedded");
		embedTag.setAttribute("src","GoogleFinanceEmbedded.swf");
		embedTag.setAttribute("quality","high");
		embedTag.setAttribute("bgcolor","#869ca7");
		embedTag.setAttribute("width","900");
		embedTag.setAttribute("height","550");
		embedTag.setAttribute("name","GoogleFinanceEmbedded");
		embedTag.setAttribute("align","middle");
		embedTag.setAttribute("play","true");
		embedTag.setAttribute("loop","false");
		embedTag.setAttribute("allowScriptAccess","sameDomain");
		embedTag.setAttribute("type","application/x-shockwave-flash");
		embedTag.setAttribute("pluginspage","http://www.adobe.com/go/getflashplayer");

		/*
		var objectString = "<object classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000' \
							id='GoogleFinanceEmbedded' width='900' height='550' \
							codebase='http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab'> \
							<param name='movie' value='GoogleFinanceEmbedded.swf' /> \
							<param name='quality' value='high' /> \
							<param name='bgcolor' value='#869ca7' /> \
							<param name='allowScriptAccess' value='sameDomain' /> \
							<embed src='GoogleFinanceEmbedded.swf' quality='high' bgcolor='#869ca7' \
								width='900' height='550' name='GoogleFinanceEmbedded' align='middle' \
								play='true'  \
								loop='false' \
								quality='high' \
								allowScriptAccess='sameDomain' \
								type='application/x-shockwave-flash' \
								pluginspage='http://www.adobe.com/go/getflashplayer'> \
							</embed> \
						</object>"

		*/
		var objectString = "<object classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000' \
							id='"+this.chartId+"' width='"+this.chartWidth+"' height='"+this.chartHeight+"' \
							codebase='http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab'> \
							<param name='movie' value='GoogleFinanceEmbedded.swf' /> \
							<param name='quality' value='"+this.chartQuality+"' /> \
							<param name='bgcolor' value='"+this.chartBgcolor+"' /> \
							<param name='allowScriptAccess' value='"+this.allowScriptAccess+"' /> \
							<embed src='"+this.chartSrc+"' quality='"+this.chartQuality+"' bgcolor='"+this.chartBgcolor+"' \
								width='"+this.chartWidth+"' height='"+this.chartHeight+"' name='"+this.chartName+"' align='"+this.chartAlignment+"' \
								play='true'  \
								loop='false' \
								quality='"+this.chartQuality+"' \
								allowScriptAccess='"+this.allowScriptAccess+"' \
								type='application/x-shockwave-flash' \
								pluginspage='http://www.adobe.com/go/getflashplayer'> \
							</embed> \
						</object>"
		/*
		var objectString = "<object classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000' "+
							"id="+this.chartId+" width="+this.chartWidth+" height="+this.chartHeight+
							"quality="+this.chartQuality+" bgcolor="+this.chartBgcolor+" allowScriptAccess="+this.allowScriptAccess+
							"><embed"+" width="+this.chartWidth+" height="+this.chartHeight+"quality="+this.chartQuality+
							" bgcolor="+this.chartBgcolor+" allowScriptAccess="+this.allowScriptAccess+" align="+this.chartAlignment+
							" play=true loop=false type='application/x-shockwave-flash' pluginspage='http://www.adobe.com/go/getflashplayer'"
							"/></object>"

		*/

		document.getElementById("financeChartContainer").innerHTML = objectString;

		/*
		this.objectTag = YAHOO.ext.DomHelper.append(this.body, {tag:'object', classid:'clsid:D27CDB6E-AE6D-11cf-96B8-444553540000',
											id:this.chartId, width:this.chartWidth, height:this.chartHeight,
											quality:this.chartQuality, bgcolor:this.chartBgcolor, allowScriptAccess:this.allowScriptAccess});
		/**/
		/*
		var tempInput = document.createElement("input");
		var element = document.getElementById(this.chatId);
		alert(element.width);

		element.appendChild(tempInput);
		*/


		/*
		this.objectEmbed = YAHOO.ext.DomHelper.append(this.objectTag.container,{tag:'embed',
							src:'GoogleFinanceEmbedded.swf',
							quality:"high",
							bgcolor:"#869ca7",
							width:"900",
							height:"550",
							name:"GoogleFinanceEmbedded",
							align:"middle",
							play:"true",
							loop:"false",
							allowScriptAccess:"sameDomain",
							type:"application/x-shockwave-flash",
							pluginspage:"http://www.adobe.com/go/getflashplayer"
						} );
		/**/



		this.flashTimer = new YAHOO.ext.util.DelayedTask(this.isFlashLoaded, this);
        this.flashTimer.delay(100);


    },
    handleSuccess:function(response, keepExisting, removeAttribute) {
	    var dh = YAHOO.ext.DomHelper;

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

		setRangeData(dateArray, valueArray);

    },
    isFlashLoaded: function()
    {
        try
        {
            if(getFlexApp(this.chartName) != null)
            {
				return true;
            }
            else
            {
                this.flashTimer.delay(100);
            }
        }catch(e)
        {
            this.flashTimer.delay(100);
        }
    }
})

    function drawFinanceChart(){
        var container = document.getElementById("financeChartContainer")
        new YAHOO.rapidjs.component.FinanceChart(container, {id:"financeChart", subscribeToHistoryChange:false, url:"script/run/rrdXmlLoader?name=yahooUtil"});
    }

	function setRangeData(dates,values){
		var application = getFlexApp('GoogleFinanceEmbedded');

		if(application == null){
			alert("application is null")
		}

		try{
			application.setRangeData(dates,values);
		}catch(e){alert('setRangeData: '+e);}
	}

    function init(){
        var container = document.getElementById("financeChartContainer");
        new YAHOO.rapidjs.component.FinanceChart(container, {id:"financeChart", subscribeToHistoryChange:false, url:"script/run/rrdXmlLoader?name=yahooUtil"});
    }


    function poll(){
        var financeChart = YAHOO.rapidjs.Components['financeChart'];
		var input = document.getElementById('rrdInput').value;
		if(input){
			var url = financeChart.url.split("?")[0] +"?name="+ input;
			financeChart.url = url;
		}
        financeChart.poll();
    }
	function showMessage(message){
		getFlexApp("GoogleFinanceEmbedded").showMessage(message);
	}
	function retrieveData(){
		alert("data should go");
		poll();
	}