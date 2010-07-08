YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.search');
YAHOO.rapidjs.component.search.TimeSelectorSubComponent = function(searchList) {
    YAHOO.rapidjs.component.search.TimeSelectorSubComponent.superclass.constructor.call(this, searchList);
    this.config = this.searchList.config.timeRangeConfig;
    this.lastSelectedFieldData = null;
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
    this.timeRangeSelector.events.fieldChanged.subscribe(this.fieldChanged, this, true)
    this.timeRangeSelector.events.buttonClicked.subscribe(this.buttonClicked, this, true)
    this.requester = new YAHOO.rapidjs.Requester(this.processSuccess, this.processFailure, this);
    this.requester.timeout=searchList.timeout;
    this.buttonConfigRequester = new YAHOO.rapidjs.Requester(this.processButtonConfiguration, this.processFailure, this);
    this.selectedButtonQuery = null;
    this.lastSelectedButtonData = null;
    this.buttonConfigRequester.doGetRequest(this.buttonConfigurationUrl, {}, null);
    this.buttonConfigReceived = false;
    this.pollTask = new YAHOO.ext.util.DelayedTask(this.poll, this);
};


YAHOO.lang.extend(YAHOO.rapidjs.component.search.TimeSelectorSubComponent, YAHOO.rapidjs.component.search.SearchListSubComponent, {
    rangeChaged: function(leftData, rightData, fieldData)
    {
        if(leftData != null && rightData != null && fieldData != null)
        {
            this.searchList.addFilter(this, fieldData["name"]+":["+leftData[this.config.initialTimeProperty] + " TO "+ rightData[this.config.finalTimeProperty] + "]")
            this.searchList._poll();
        }
    },

    fieldChanged: function(leftData, rightData, fieldData)
    {
        this.lastSelectedFieldData = fieldData;
        this.buttonClicked(this.lastSelectedButtonData);
    },
    buttonClicked: function(buttonData)
    {
        if(this.lastSelectedFieldData == null || buttonData == null) return;
        this.lastSelectedButtonData = buttonData;
        this.buttonConfigReceived = true;
        this.selectedButtonQuery = this.lastSelectedFieldData.name+":"+buttonData.query;
        this.searchList.addFilter(this, this.selectedButtonQuery)
        this.searchList.handleSearch(null);
    },
    render : function(container) {
        var dh = YAHOO.ext.DomHelper;
        this.subComponentWrapper = dh.append(container, {tag:'div', cls:'rcmdb-search-time-range-selector'});
        this.timeRangeSelector.render(this.subComponentWrapper);
    },
    processButtonConfiguration: function(response)
    {
        var buttons = response.responseXML.getElementsByTagName("Button")
        var buttonsArray = [];
        for(var i=0; i < buttons.length; i++)
        {
            buttonsArray[i] = YAHOO.rapidjs.data.DataUtils.convertToMap(buttons[i]);
        }

        var fields = response.responseXML.getElementsByTagName("Field")
        var fieldsArray = [];
        for(var i=0; i < fields.length; i++)
        {
            fieldsArray[i] = YAHOO.rapidjs.data.DataUtils.convertToMap(fields[i]);
        }
        this.timeRangeSelector.loadButtonsAndFields({buttons:buttonsArray, fields:fieldsArray});

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
        if(!this.buttonConfigReceived)
        {
            this.pollTask.cancel();
            this.pollTask = new YAHOO.ext.util.DelayedTask(this.poll, this);
            this.pollTask.delay(100);
        }
        else
        {
            if(this.lastSelectedFieldData != null && this.lastSelectedButtonData != null)
            {
                this.requester.doGetRequest(this.url, {query:this.searchList.getCurrentlyExecutingQuery(), lastSelectedButton:this.lastSelectedButtonData.displayName, field:this.lastSelectedFieldData.name, searchClass:this.searchList.getSearchClass()})
            }
        }

    }
})