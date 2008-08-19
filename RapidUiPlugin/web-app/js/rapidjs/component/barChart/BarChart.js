YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.chart');
YAHOO.rapidjs.component.BarChart = function(container, config) {
    YAHOO.rapidjs.component.BarChart.superclass.constructor.call(this,container, config);

    this.dataUrl = config.url;
    this.dataType = config.dataType;
    this.fields = config.fields;
    this.xField = config.xField;
    this.yField = config.yField;
    this.width = config.width || 400;
    this.resultNode = config.resultNode;
    this.container = container;
    this.dataSource = new YAHOO.util.DataSource(this.dataUrl);
    this.dataSource.responseType = YAHOO.util.DataSource.TYPE_XML;
    this.yAxis = new YAHOO.widget.NumericAxis();
    this.yAxis.minimum = config.yAxisMin || 0;
    this.yAxis.maximum = config.yAxisMax || 100;
    this.seriesDef = [];
    this.colors = config.colors;
    this.dataSource.responseSchema =
    {

	    resultNode: this.resultNode,
        fields: this.fields
    };
    this.chart = null;
    YAHOO.widget.Chart.SWFURL = "js/yui/charts/assets/charts.swf";
    this.defineSeries();
    this.render();

};
YAHOO.lang.extend(YAHOO.rapidjs.component.BarChart, YAHOO.rapidjs.component.PollingComponentContainer, {

	defineSeries : function() {

		for( var i = 1; i <= this.fields.length; i++ )
		{
			this.seriesDef.push(
				{
					yField : this.fields[i],
					displayName : this.fields[i],
					style:
					{
						image: "tube.png",
						mode: "no-repeat",
						color: this.colors[this.fields[i]],
						size: 40
					}
				}
			);
		}

	},
    render: function() {

        this.chart = new YAHOO.widget.ColumnChart( this.container, this.dataSource,
        {
	        series: this.seriesDef,
            xField: this.xField,
            yAxis: this.yAxis
        });
        YAHOO.util.Dom.setStyle(this.container, 'width', this.width);
    }
});