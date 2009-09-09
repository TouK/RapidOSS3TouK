YAHOO.namespace('rapidjs', 'rapidjs.component');

function   timeRangeSelectorJsFunction(params)
{
    var componentId = params.componentId
    var functionName = params.functionName
    if(functionName == "intervalChanged")
    {
        var leftData = params.leftData
        var rightData = params.rightData
        YAHOO.rapidjs.TimeRangeSelectors[componentId].fireRangeChanged(leftData, rightData);
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
    this.flexMethodCaller = new YAHOO.rapidjs.component.FlexApplicationMethodCaller(this.id, "embed"+this.id);
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
							<param name='allowScriptAccess' value='always' /> \
							<param name='swliveconnect' value='true' /> \
							<param value='Transparent' name='wmode'/> \
							<embed src='"+this.swfUrl+"' id='embed"+this.id+"' quality='high' \
								width='100%' height='100%' name='embed"+this.id+"' align='middle' \
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


		this.configurationTimer = new YAHOO.ext.util.DelayedTask(this.loadConfiguration, this);
        this.configurationTimer.delay(300);
    },
    loadButtons: function(buttons)
    {
        this.flexMethodCaller.callMethod("loadButtons", [buttons]);
    },
    loadData: function(responseXml)
    {
        var rootTag = responseXml.getElementsByTagName(this.rootTag)[0];
        var datum = rootTag.getElementsByTagName(this.dataTag)
        var dataForFlex = [];
        for(var i=0; i < datum.length; i++)
        {
            var nodeData = {};
            var attributeNodes = datum[i].attributes;
            if (attributeNodes != null)
            {
                var nOfAtts = attributeNodes.length
                for (var index = 0; index < nOfAtts; index++) {
                    var attNode = attributeNodes.item(index);
                    nodeData[attNode.nodeName] = attNode.nodeValue;
                }
            }

            dataForFlex[dataForFlex.length] = nodeData;
        }
        this.flexMethodCaller.callMethod("reset", []);
        this.flexMethodCaller.callMethod("loadData", [dataForFlex]);

    },
    fireRangeChanged: function(leftData, rightData){
        this.events.rangeChanged.fireDirect(leftData, rightData);
    },
    fireButtonClicked: function(buttonData){
        this.events.buttonClicked.fireDirect(buttonData);
    },
    loadConfiguration: function(){
        this.flexMethodCaller.callMethod("loadConfiguration", [this.config]);
    }
}

