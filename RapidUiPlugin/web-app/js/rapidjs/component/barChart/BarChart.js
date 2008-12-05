/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.chart');
YAHOO.rapidjs.component.BarChart = function(container, config) {
    YAHOO.rapidjs.component.BarChart.superclass.constructor.call(this,container, config);

    this.dataUrl = config.url;
    this.dataType = config.dataType;
    this.fields = config.fields;
    this.xField = config.xField;
    this.yField = config.yField;
    this.width = config.width || 500;
    this.resultNode = config.resultNode;
    this.container = container;
    this.swfURL = config.swfURL;
    this.imageURL = config.imageURL;
	this.chartTitle = config.chartTitle;
	this.padding = config.padding || 2;
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
	        yAxis: this.yAxis,
            wmode: "transparent"
        });
	 	YAHOO.util.Dom.setStyle(this.body.dom,'width', this.width);
	},
	defineSeries : function() {
		this.seriesDef = [];
		for(var i = 0; i < this.fields.length; i++)
		{
			this.seriesDef.push(
					{
						yField: this.fields[i],
						displayName: this.fields[i],
						style:
						{
							image: this.imageURL,
							color: this.colors[this.fields[i]],
							size: 40
						}
					}
			);
			this.seriesDef.push({style:{size: this.padding}}

			);
		}
		this.seriesDef.pop();

	},
	handleSuccess: function (response)
	{
		if(this.IE_SYNC && !this.chart)
			this.render();

		var respond =  this.dataSource.parseXMLData(null,response.responseXML);
		respond.results[0][this.xField] = new Object();
		respond.results[0][this.xField] = this.chartTitle;
		this.chart._loadDataHandler("", respond, false);
	},
    resize: function(width, height){
	    var bodyHeight =  height - this.header.offsetHeight - 10;
        this.body.setHeight(bodyHeight);
    }

});