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
    this.swfURL = config.swfURL;
    this.imageURL = config.imageURL;

    this.yAxis = new YAHOO.widget.NumericAxis();
    this.yAxis.minimum = config.yAxisMin || 0;
    this.yAxis.maximum = config.yAxisMax || 100;
    this.seriesDef = [];
    this.colors = config.colors;

    this.chart = null;
    YAHOO.widget.Chart.SWFURL = this.swfURL;

    this.header = YAHOO.ext.DomHelper.append(this.container, {tag:'div'});
    this.toolbar = new YAHOO.rapidjs.component.tool.ButtonToolBar(this.header, {title:this.title});
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.LoadingTool(document.body, this));
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.SettingsTool(document.body, this));
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.ErrorTool(document.body, this));
    this.body = YAHOO.ext.DomHelper.append(this.container, {tag:'div'}, true);

    this.defineSeries();

    this.dataSource = new YAHOO.util.DataSource();
	this.dataSource.responseType = YAHOO.util.DataSource[this.dataType];
   	this.dataSource.responseSchema =
    {
	    resultNode: this.resultNode,
	    fields: this.fields,
	    metaFields: {
	        location: this.resultNode
	    }

    };
    var IE_QUIRKS = (YAHOO.env.ua.ie && document.compatMode == "BackCompat");
    this.IE_SYNC = (YAHOO.env.ua.ie == 6 || (YAHOO.env.ua.ie == 7 && IE_QUIRKS));
    if(!this.IE_SYNC)
       	this.render();


};
YAHOO.lang.extend(YAHOO.rapidjs.component.BarChart, YAHOO.rapidjs.component.PollingComponentContainer, {
	render: function()
	{
		this.chart = new YAHOO.widget.ColumnChart(this.body.dom, null,
	    {
	        series: this.seriesDef,
	        xField: this.xField,
	        yAxis: this.yAxis
	    });
	 	YAHOO.util.Dom.setStyle(this.body.dom,'width', this.width);
	 	YAHOO.util.Dom.setStyle(this.body.dom,'overflow', 'hidden');
	 	if(this.IE_SYNC)
	 	{
		 	this.body.dom.firstChild.childNodes[4].setAttribute('value','transparent')
		 	this.body.dom.firstChild.childNodes[5].setAttribute('value','bridgeName=chartsDiv')
		}
	 	this.chart._swf.setAttribute('wmode','transparent');

	},
	defineSeries : function() {
		this.seriesDef =
		[
			{
				yField: this.fields[1],
				displayName: this.fields[1],
				style:
				{
					image: this.imageURL,
					color: this.colors[this.fields[1]],
					size: 40
				}
			},
			{	yField: this.fields[2],
				displayName: this.fields[2],
				style:
				{
					image: this.imageURL,
					color: this.colors[this.fields[2]],
					size: 40
				}},
			{	yField: this.fields[3],
				displayName: this.fields[3],
				style:
				{
					image: this.imageURL,
					color: this.colors[this.fields[3]],
					size: 40
				}},
			{	yField: this.fields[4],
				displayName: this.fields[4],
				style:
				{
					image: this.imageURL,
					color: this.colors[this.fields[4]],
					size: 40
				}},
			{	yField: this.fields[5],
				displayName: this.fields[5],
				style:
				{
					image: this.imageURL,
					color: this.colors[this.fields[5]],
					size: 40
				}},
			{	yField: this.fields[6],
				displayName: this.fields[6],
				style:
				{
					image: this.imageURL,
					color: this.colors[this.fields[6]],
					size: 40
				}}
		];
	},

	handleSuccess: function (response)
	{
		if(this.IE_SYNC && !this.chart)
			this.render();

		var respond =  this.dataSource.parseXMLData(null,response.responseXML);
		this.chart._loadDataHandler("", respond, false);
	},
    resize: function(width, height){
	    var bodyHeight =  height - this.header.offsetHeight - 10;
        this.body.setHeight(bodyHeight);
    }

});