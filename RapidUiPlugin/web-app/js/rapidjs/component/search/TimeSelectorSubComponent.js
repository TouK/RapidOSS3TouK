YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.search');
YAHOO.rapidjs.component.search.TimeSelectorSubComponent = function(searchList) {
    YAHOO.rapidjs.component.search.TimeSelectorSubComponent.superclass.constructor.call(this, searchList);
    this.config = this.searchList.config.timeRangeConfig;
    this.fieldName = "changedAt"
    this.url = this.config.url;
    this.buttonConfigurationUrl = this.config.buttonConfigurationUrl;
    this.config.xField = this.config.fromTimeProperty;
    this.config.categoryAxisLabelProperty = this.config.timeAxisLabelProperty;
    this.config.initialTimeProperty = this.config.fromTimeProperty;
    this.config.finalTimeProperty = this.config.toTimeProperty;
    this.config.finalStringTimeProperty = this.config.stringToTimeProperty;
    this.config.initialStringTimeProperty = this.config.stringFromTimeProperty;
    this.config.yFields = this.config.valueProperties;
    this.config.id = this.searchList.id + "timeRangeSelector"
    this.timeRangeSelector = new YAHOO.rapidjs.component.TimeRangeSelector(this.config);
    this.timeRangeSelector.events.rangeChanged.subscribe(this.rangeChaged, this, true)
    this.timeRangeSelector.events.buttonClicked.subscribe(this.buttonClicked, this, true)
    this.requester = new YAHOO.rapidjs.Requester(this.processSuccess, this.processFailure, this);
    this.buttonConfigRequester = new YAHOO.rapidjs.Requester(this.processButtonConfiguration, this.processFailure, this);
    this.selectedButtonQuery = null;
    this.lastSelectedButtonData = null;
    this.buttonConfigRequester.doGetRequest(this.buttonConfigurationUrl, {}, null);
    this.renderTask = new YAHOO.ext.util.DelayedTask(this._render, this);
};


YAHOO.lang.extend(YAHOO.rapidjs.component.search.TimeSelectorSubComponent, YAHOO.rapidjs.component.search.SearchListSubComponent, {
    rangeChaged: function(leftData, rightData)
    {
        this.searchList.addFilter(this, this.fieldName+":["+leftData[this.config.initialTimeProperty] + " TO "+ rightData[this.config.finalTimeProperty] + "]")
        this.searchList._poll();
    },
    buttonClicked: function(buttonData)
    {
        this.lastSelectedButtonData = buttonData;
        this.selectedButtonQuery = null
        if(buttonData.displayName != "All")
        {
            this.selectedButtonQuery = this.fieldName+":"+buttonData.query;
        }
        this.searchList.addFilter(this, this.selectedButtonQuery)
        this.searchList.handleSearch(null);
    },
    render : function(container) {
        var dh = YAHOO.ext.DomHelper;
        this.subComponentWrapper = dh.append(container, {tag:'div', cls:'rcmdb-search-time-range-selector'});
        this.renderTask.delay(1000);
    },
    _render: function()
    {
        this.timeRangeSelector.render(this.subComponentWrapper);    
    },
    processButtonConfiguration: function(response)
    {
        var buttons = response.responseXML.getElementsByTagName("Button")
        var buttonsArray = [];
        for(var i=0; i < buttons.length; i++)
        {
            var attributes = {}
            var xmlAttributes = buttons[i].attributes
            var xmlAttributes = buttons[i].attributes;
            if (xmlAttributes != null)
            {
                var nOfAtts = xmlAttributes.length
                for (var index = 0; index < nOfAtts; index++) {
                    var attNode = xmlAttributes.item(index);
                    attributes[attNode.nodeName] = attNode.nodeValue;
                }
            }
            buttonsArray[i] = attributes;
        }

        buttonsArray[buttonsArray.length] = {displayName:"All", selected:true}

        this.timeRangeSelector.loadButtons(buttonsArray);

    },
    processSuccess: function(response){
        this.timeRangeSelector.loadData(response.responseXML);
        this.firePollCompleted();
    },
    processFailure: function(errors, statusCodes){
        this.searchList.processFailure(errors, statusCodes);
        this.firePollCompleted();
    },
    preparePoll: function()
    {
        this.searchList.addFilter(this, this.selectedButtonQuery)
    },
    poll: function()
    {
        this.firePollStarted();
        if(this.lastSelectedButtonData == null || this.lastSelectedButtonData.displayName == "ALL")
        {
            this.requester.doGetRequest(this.url, {query:this.searchList.getCurrentlyExecutingQuery(), field:this.fieldName, searchClass:this.searchList.getSearchClass()})        
        }
        else
        {
            this.requester.doGetRequest(this.url, {query:this.searchList.getCurrentlyExecutingQuery(), lastSelectedButton:this.lastSelectedButtonData.displayName, field:this.fieldName, searchClass:this.searchList.getSearchClass()})
        }

    }
})