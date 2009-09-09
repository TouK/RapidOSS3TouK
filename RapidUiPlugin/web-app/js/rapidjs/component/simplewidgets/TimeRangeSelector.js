YAHOO.namespace('rapidjs', 'rapidjs.component');

function   timeRangeSelectorJsFunction(params)
{
    var componentId = params.componentId
    var functionName = params.functionName
    if(functionName == "intervalChanged")
    {
        var leftData = params.leftData
        var rightData = params.rightData
        var fieldData = params.selectedFieldData
        YAHOO.rapidjs.TimeRangeSelectors[componentId].fireRangeChanged(leftData, rightData, fieldData);
    }
    else if(functionName == "fieldChanged")
    {
        var leftData = params.leftData
        var rightData = params.rightData
        var fieldData = params.selectedFieldData
        YAHOO.rapidjs.TimeRangeSelectors[componentId].fireFieldChanged(leftData, rightData, fieldData);
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
        'fieldChanged': new YAHOO.util.CustomEvent("fieldChanged"),
        'buttonClicked': new YAHOO.util.CustomEvent("buttonClicked")
    }
    YAHOO.rapidjs.TimeRangeSelectors[this.id] = this;
    this.flexMethodCaller = new YAHOO.rapidjs.component.FlexApplicationMethodCaller(this.id, "embed"+this.id, this.swfUrl);
}


YAHOO.rapidjs.component.TimeRangeSelector.prototype =
{
    render: function(container) {
        this.container = container;
        this.flexMethodCaller.render(this.container);
        this.loadConfiguration();
    },
    loadButtonsAndFields: function(buttonsAndFields)
    {
        this.flexMethodCaller.callMethod("loadButtonsAndFields", [buttonsAndFields]);
    },
    loadData: function(responseXml)
    {
        var rootTag = responseXml.getElementsByTagName(this.rootTag)[0];
        var datum = rootTag.getElementsByTagName(this.dataTag)
        var dataForFlex = [];
        for(var i=0; i < datum.length; i++)
        {
            dataForFlex[dataForFlex.length] = YAHOO.rapidjs.data.DataUtils.convertToMap(datum[i]);
        }
        this.flexMethodCaller.callMethod("reset", []);
        this.flexMethodCaller.callMethod("loadData", [dataForFlex]);

    },
    fireRangeChanged: function(leftData, rightData, fieldChanged){
        this.events.rangeChanged.fireDirect(leftData, rightData, fieldChanged);
    },
    fireFieldChanged: function(leftData, rightData, fieldChanged){
        this.events.fieldChanged.fireDirect(leftData, rightData, fieldChanged);
    },
    fireButtonClicked: function(buttonData){
        this.events.buttonClicked.fireDirect(buttonData);
    },
    loadConfiguration: function(){
        this.flexMethodCaller.callMethod("loadConfiguration", [this.config]);
    }
}

