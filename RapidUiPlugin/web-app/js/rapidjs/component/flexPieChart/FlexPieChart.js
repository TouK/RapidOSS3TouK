YAHOO.namespace('rapidjs', 'rapidjs.component');

var isIE  = (navigator.appVersion.indexOf("MSIE") != -1) ? true : false;
var isWin = (navigator.appVersion.toLowerCase().indexOf("win") != -1) ? true : false;
var isOpera = (navigator.userAgent.indexOf("Opera") != -1) ? true : false;

YAHOO.rapidjs.component.FlexPieChart = function(container, config) {
    YAHOO.rapidjs.component.FlexPieChart.superclass.constructor.call(this, container, config);
    YAHOO.ext.util.Config.apply(this, config);

    this.configureTimeout(config);
    this.gradients = {};
    this.renderTask = new YAHOO.ext.util.DelayedTask(this.render, this);
    this.response = null;
    this.eventListenerAdded = false;
    this.initializeTask = new YAHOO.ext.util.DelayedTask(this.handleChart, this);
   // this.freeTabLockTask = new YAHOO.ext.util.DelayedTask(this.freeTabLock, this);


    this.header = YAHOO.ext.DomHelper.append(this.container, {tag:'div'});
    this.toolbar = new YAHOO.rapidjs.component.tool.ButtonToolBar(this.header, {title: this.title});
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.LoadingTool(document.body, this));
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.SettingsTool(document.body, this));
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.ErrorTool(document.body, this));
    this.body = YAHOO.ext.DomHelper.append(this.container, {tag:'div'}, true);

    this.render();
    this.tabLocked = false;//boolean used to wait 1 seconds during tab switching.
    this.isRendered = false;
};


YAHOO.extend(YAHOO.rapidjs.component.FlexPieChart, YAHOO.rapidjs.component.PollingComponentContainer, {

    handleSuccess: function(response) {
	    this.response = response;
        this.handleChart();

            /*this.events["loadstatechanged"].fireDirect(this, false);
            if (this.pollInterval > 0)
            {
                this.pollTask.delay(this.pollInterval * 1000);
            }
            */

    },

    handleChart : function()
    {

	    if (!FABridge[this.id] || this.tabLocked == true) {
            this.initializeTask.delay(100);
        }
        else
        {
            if (this.eventListenerAdded == false)
            {
                try
                {
                    var scope = this;
                    if (this.handlerFunc)
                        FABridge[this.id].root().getMyChart().removeEventListener("itemClick", this.handlerFunc);
                    this.handlerFunc = function(e) {
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
            this.processData(this.response);
        }
    },

    handleItemClick: function(e)
    {
        var index = e.getHitData().getChartItem().getIndex();
        if (index < 0)
        {
            return;
        }
        var mySeries = e.bridge.root().getMySeries();
        var oldRadiuses = mySeries.getPerWedgeExplodeRadius();
        var newRadiuses = new Array();
        for (var i = 0; i < oldRadiuses.length; i++)
        {
            newRadiuses[i] = oldRadiuses[i];
        }
        var value = oldRadiuses[index];
        if (!value || value == 0)
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
        //this.sendOutputs(dataToSend);
    },

    processData: function(response) {
        var dataString = response.responseText;
        var dataTag = response.responseXML.getElementsByTagName(this.rootTag)[0];
        var slices = dataTag.getElementsByTagName("set");
        var arrayOfSlices = new Array();
        var arrayOfGradients = new Array();
        var bridgeObj = FABridge[this.id];
        this.columnData = {};
        for (var i = 0; i < slices.length; i++)
        {
            var allAttrs = slices[i].attributes;
            var colLabel = slices[i].getAttribute("label");
            var closureForSlice = {Label:colLabel, Data:slices[i].getAttribute("value")};
            this.columnData[colLabel] = {};
            for (var index = 0; index < allAttrs.length; index++)
            {
                var attNode = allAttrs.item(index);
                this.columnData[colLabel][attNode.nodeName] = attNode.nodeValue;
            }
            arrayOfSlices[i] = closureForSlice;
            arrayOfGradients[i] = this.getGradient(bridgeObj, slices[i].getAttribute('color'));
            if (arrayOfGradients[i] == null)
            {
                return;
            }
        }
        bridgeObj.root().getMySeries().setStyle("fills", arrayOfGradients);
        bridgeObj.root().getMyChart().setDataProvider(arrayOfSlices);
        var title = dataTag.getAttribute("Title");
        /*if(title)
          {
              bridgeObj.root().getMyPanel().setTitle( title );
          }*/
    },

    getGradient: function(bridgeObj, colorStr)
    {
        if (this.gradients[colorStr]) {
            return this.gradients[colorStr];
        }
        else {
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

    render : function() {

        var requiredMajorVersion = 9;
        var requiredMinorVersion = 0;
        var requiredRevision = 0;

        var hasProductInstall = this.DetectFlashVer(6, 0, 65);
        var hasRequestedVersion = this.DetectFlashVer(requiredMajorVersion, requiredMinorVersion, requiredRevision);
        var containerElement = this.body.dom;
        if (!hasProductInstall || !hasRequestedVersion)
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
        this.isRendered = true;

    },

    clearData:function() {

    },

    handleVisible : function() {
        this.gradients = {};
        this.eventListenerAdded = false;
        YAHOO.rapidjs.component.FlexPieChart.superclass.handleVisible.call(this);
        this.tabLocked = true;
        this.freeTabLockTask.delay(1000);//we need to wait some time. 1 second is secure enough.
        //it is also possible to make a loop to check that accessing to the root of the bridge gives an error(for exp. bridgeObj.root().getMySeries()) or not and when the error
        //is gone, break the loop. I tried this and it did not work. Sometimes it is OK, but sometimes the chart disappears.
    },

    freeTabLock: function()
    {
        this.tabLocked = false;
    },

    resize : function(width, height) {
        this.body.setHeight(height - this.header.offsetHeight);
        if( !this.isRendered )
        	this.render();
        this.poll();
    },
    // JavaScript helper required to detect Flash Player PlugIn version information
    GetSwfVer : function () {
        // NS/Opera version >= 3 check for Flash plugin in plugin array
        var flashVer = -1;

        if (navigator.plugins != null && navigator.plugins.length > 0) {
            if (navigator.plugins["Shockwave Flash 2.0"] || navigator.plugins["Shockwave Flash"]) {
                var swVer2 = navigator.plugins["Shockwave Flash 2.0"] ? " 2.0" : "";
                var flashDescription = navigator.plugins["Shockwave Flash" + swVer2].description;
                var descArray = flashDescription.split(" ");
                var tempArrayMajor = descArray[2].split(".");
                var versionMajor = tempArrayMajor[0];
                var versionMinor = tempArrayMajor[1];
                if (descArray[3] != "") {
                    tempArrayMinor = descArray[3].split("r");
                } else {
                    tempArrayMinor = descArray[4].split("r");
                }
                var versionRevision = tempArrayMinor[1] > 0 ? tempArrayMinor[1] : 0;
                var flashVer = versionMajor + "." + versionMinor + "." + versionRevision;
            }
        }
            // MSN/WebTV 2.6 supports Flash 4
        else if (navigator.userAgent.toLowerCase().indexOf("webtv/2.6") != -1) flashVer = 4;
            // WebTV 2.5 supports Flash 3
        else if (navigator.userAgent.toLowerCase().indexOf("webtv/2.5") != -1) flashVer = 3;
            // older WebTV supports Flash 2
        else if (navigator.userAgent.toLowerCase().indexOf("webtv") != -1) flashVer = 2;
        else if (isIE && isWin && !isOpera) {
            flashVer = ControlVersion();
        }
        return flashVer;
    },

    // When called with reqMajorVer, reqMinorVer, reqRevision returns true if that version or greater is available
    DetectFlashVer : function (reqMajorVer, reqMinorVer, reqRevision)
    {


        versionStr = this.GetSwfVer();
        if (versionStr == -1) {
            return false;
        } else if (versionStr != 0) {
            if (isIE && isWin && !isOpera) {
                // Given "WIN 2,0,0,11"
                tempArray = versionStr.split(" "); // ["WIN", "2,0,0,11"]
                tempString = tempArray[1]; // "2,0,0,11"
                versionArray = tempString.split(","); // ['2', '0', '0', '11']
            } else {
                versionArray = versionStr.split(".");
            }
            var versionMajor = versionArray[0];
            var versionMinor = versionArray[1];
            var versionRevision = versionArray[2];

	 // is the major.revision >= requested major.revision AND the minor version >= requested minor
            if (versionMajor > parseFloat(reqMajorVer)) {
                return true;
            } else if (versionMajor == parseFloat(reqMajorVer)) {
                if (versionMinor > parseFloat(reqMinorVer))
                    return true;
                else if (versionMinor == parseFloat(reqMinorVer)) {
                    if (versionRevision >= parseFloat(reqRevision))
                        return true;
                }
            }
            return false;
        }
    }

});