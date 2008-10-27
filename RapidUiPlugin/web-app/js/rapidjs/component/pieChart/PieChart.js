YAHOO.namespace('rapidjs', 'rapidjs.component');
YAHOO.rapidjs.component.PieChart = function(container, config) {
    YAHOO.rapidjs.component.PieChart.superclass.constructor.call(this, container, config);
    this.swfURL = null;
    this.legend = "none";
    this.dataField = null;
    this.categoryField = null;
    this.colors = null;
    this.fields = null;
    YAHOO.ext.util.Config.apply(this, config);
    this.chart = null;
    this.chartIsReady = false;
    YAHOO.widget.Chart.SWFURL = this.swfURL;
    this.datasource = new YAHOO.util.XHRDataSource(this.url);
    this.datasource.responseType = YAHOO.util.XHRDataSource.TYPE_XML;

    this.datasource.responseSchema = {
        resultNode: this.contentPath,
        fields: this.fields
    };
    this.header = YAHOO.ext.DomHelper.append(this.container, {tag:'div'});
    this.toolbar = new YAHOO.rapidjs.component.tool.ButtonToolBar(this.header, {title:this.title});
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.LoadingTool(document.body, this));
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.SettingsTool(document.body, this));
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.ErrorTool(document.body, this));
    this.body = YAHOO.ext.DomHelper.append(this.container, {tag:'div'}, true);
    this.renderTask = new YAHOO.ext.util.DelayedTask(this.render, this);
    this.initializeTask = new YAHOO.ext.util.DelayedTask(this._handleSuccess, this);
    var oSelf = this;
    YAHOO.util.Event.onDOMReady(function() {
        oSelf.renderTask.delay(500);
    });
}


YAHOO.extend(YAHOO.rapidjs.component.PieChart, YAHOO.rapidjs.component.PollingComponentContainer, {
    poll:function()
    {
        YAHOO.rapidjs.component.PieChart.superclass.poll.call(this);
    },
    render:function()
    {
        this.chart = new YAHOO.widget.PieChart(this.body.dom, null,
        {
            dataField: this.dataField,
            categoryField: this.categoryField,
            style:
            {
                padding: 10,
                legend:
                {
                    display: this.legend || "none",
                    padding: 10,
                    spacing: 5,
                    font:
                    {
                        family: "tahoma",
                        size: 13
                    }
                }
            },
            series: [
                {
                    style:{colors:this.colors || []}
                }
            ],
            wmode: "Transparent"
        });

       
        this.chart.subscribe("contentReady", function() {
            this.chartIsReady = true;
        }, this, true);
    },
    handleSuccess: function (response)
    {
        this.lastResponse = response;
        this._handleSuccess();
    },
    _handleSuccess: function() {
        if (this.chartIsReady) {
            var respond = this.datasource.parseXMLData(null, this.lastResponse.responseXML);
            this.chart._loadDataHandler("", respond, false);
        }
        else{
            this.initializeTask.delay(50);
        }
    },
    resize: function(width, height) {
        this.body.setHeight(height - this.header.offsetHeight);
        this.body.setWidth(width);
    }
}
        );
