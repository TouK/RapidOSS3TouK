YAHOO.namespace('rapidjs', 'rapidjs.component');
function FC_Loaded(DOMId) {
    if (DOMId.indexOf('_fusionChart') > -1) {
        var compId = DOMId.substr(0, DOMId.indexOf('_fusionChart'))
        var comp = YAHOO.rapidjs.Components[compId];
        if (comp) {
            comp.flashLoaded();
        }
    }
}
function FC_SetClicked(attString) {
    var atts = attString.split(':fc_split:');
    var data = {};
    for (var i = 0; i < atts.length; i++) {
        var attParts = atts[i].split('!fc_split!')
        data[attParts[0]] = attParts[1];
    }
    var componentId = data.componentId;
    var comp = YAHOO.rapidjs.Components[componentId];
    if (comp) {
        delete data.componentId;
        comp.itemClicked(data);
    }
}
YAHOO.rapidjs.component.FusionChart = function(container, config) {
    YAHOO.rapidjs.component.FusionChart.superclass.constructor.call(this, container, config);
    this.type = null;
    YAHOO.ext.util.Config.apply(this, config);
    this.configureTimeout(config);
    var events = {
        'itemClicked': new YAHOO.util.CustomEvent('itemClicked')
    };
    YAHOO.ext.util.Config.apply(this.events, events);
    this.header = YAHOO.ext.DomHelper.append(this.container, {tag:'div'});
    this.toolbar = new YAHOO.rapidjs.component.tool.ButtonToolBar(this.header, {title:this.title});
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.LoadingTool(document.body, this));
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.SettingsTool(document.body, this));
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.ErrorTool(document.body, this));
    this.body = YAHOO.ext.DomHelper.append(this.container, {tag:'div'}, true);
    this.renderTask = new YAHOO.ext.util.DelayedTask(this.render, this);
    this.initializeTask = new YAHOO.ext.util.DelayedTask(this._handleSuccess, this);
    this.ready = false;
    var oSelf = this;
    YAHOO.util.Event.onDOMReady(function() {
        oSelf.renderTask.delay(500);
    });
}
YAHOO.lang.extend(YAHOO.rapidjs.component.FusionChart, YAHOO.rapidjs.component.PollingComponentContainer, {
    render: function() {
        this.chart = new FusionCharts(this.getChartSwf(), this.id + '_fusionChart', "100%", "100%", "0", "1", "FFFFFF", "exactFit");
        this.chart.setTransparent(true)
        this.chart.setDataXML('<chart/>');
        this.chart.render(this.body.id)
    },
    handleSuccess: function (response)
    {
        this.lastResponse = response;
        this._handleSuccess();
    },
    _handleSuccess: function() {
        if (this.ready) {
            var str = this.modifyResponseForEvents(this.lastResponse.responseXML);
            this.chart.setDataXML(str);
        }
        else {
            this.initializeTask.delay(50);
        }
    },
    resize : function(width, height) {
        this.body.setHeight(height - this.header.offsetHeight);
    },

    flashLoaded: function() {
        this.ready = true;
    },

    itemClicked:function(data) {
        this.events['itemClicked'].fireDirect(data);
    },

    modifyResponseForEvents : function(xmlDoc) {
        var sets = xmlDoc.getElementsByTagName('set')
        var nOfSets = sets.length;
        var categories = [];
        if (nOfSets > 0) {
            var categoryNodes = xmlDoc.getElementsByTagName('category');
            var nOfCategories = categoryNodes.length;
            for (var i = 0; i < nOfCategories; i++) {
                categories[categories.length] = categoryNodes[i].getAttribute('label');
            }
        }
        for (var i = 0; i < nOfSets; i++) {
            var setNode = sets[i];
            var paramStrArray = [];
            paramStrArray[paramStrArray.length] = 'componentId!fc_split!' + this.id
            var attributeNodes = setNode.attributes;
            if (attributeNodes != null)
            {
                var nOfAtts = attributeNodes.length
                for (var index = 0; index < nOfAtts; index++) {
                    var attNode = attributeNodes.item(index);
                    paramStrArray[paramStrArray.length] = attNode.nodeName + '!fc_split!' + attNode.nodeValue;
                }
            }
            if (setNode.parentNode.nodeName == 'dataset') {
                paramStrArray[paramStrArray.length] = 'seriesName' + '!fc_split!' + setNode.parentNode.getAttribute('seriesName');
            }
            if (categories.length > 0) {
                paramStrArray[paramStrArray.length] = 'label' + '!fc_split!' + categories[i % categories.length];
            }
            setNode.setAttribute('link', 'j-FC_SetClicked-' + paramStrArray.join(':fc_split:'));
        }
        var str;
        try {
            str = (new XMLSerializer()).serializeToString(xmlDoc);
        }
        catch (e) {
            try {
                str = xmlDoc.xml;
            }
            catch (e2)
            {
                alert('Xmlserializer not supported');
            }
        }
        return str;
    },

    getChartSwf : function() {
        var swfFile;
        switch (this.type) {
            case 'Column 2D':swfFile = 'Column2D.swf';break;
            case 'Column 3D':swfFile = 'Column3D.swf';break;
            case 'Pie 2D':swfFile = 'Pie2D.swf';break;
            case 'Pie 3D':swfFile = 'Pie3D.swf';break;
            case 'Line 2D':swfFile = 'Line.swf';break;
            case 'Bar 2D':swfFile = 'Bar2D.swf';break;
            case 'Area 2D':swfFile = 'Area2D.swf';break;
            case 'Doughnut 2D':swfFile = 'Doughnut2D.swf';break;
            case 'Doughnut 3D':swfFile = 'Doughnut3D.swf';break;
            case 'Spline 2D':swfFile = 'Spline.swf';break;
            case 'Spline Area 2D':swfFile = 'SplineArea.swf';break;
            case 'Funnel 3D':swfFile = 'Funnel.swf';break;
            case 'Pyramid 3D':swfFile = 'Pyramid.swf';break;
            case 'Multiseries Column 2D':swfFile = 'MSColumn2D.swf';break;
            case 'Multiseries Column 3D':swfFile = 'MSColumn3D.swf';break;
            case 'Multiseries Line 2D':swfFile = 'MSLine.swf';break;
            case 'Multiseries Area 2D':swfFile = 'MSArea.swf';break;
            case 'Multiseries Bar 2D':swfFile = 'MSBar2D.swf';break;
            case 'Multiseries Bar 3D':swfFile = 'MSBar3D.swf';break;
            case 'Multiseries Spline Line 2D':swfFile = 'MSSpline.swf';break;
            case 'Multiseries Spline Area 2D':swfFile = 'MSSplineArea.swf';break;
            case 'Stacked Column 2D':swfFile = 'StackedColumn2D.swf';break;
            case 'Stacked Column 3D':swfFile = 'StackedColumn3D.swf';break;
            case 'Stacked Area 2D':swfFile = 'StackedArea2D.swf';break;
            case 'Stacked Bar 2D':swfFile = 'StackedBar2D.swf';break;
            case 'Stacked Bar 3D':swfFile = 'StackedBar3D.swf';break;
            case '2D Single Y Combination':swfFile = 'MSCombi2D.swf';break;
            case '3D Single Y Combination':swfFile = 'MSCombi3D.swf';break;
            case 'Column 3D + Line Single Y':swfFile = 'MSColumnLine3D.swf';break;
            case '2D Dual Y Combination':swfFile = 'MSCombiDY2D.swf';break;
            case 'Column 3D + Line Dual Y':swfFile = 'MSColumn3DLineDY.swf';break;
            case 'Stacked Column 3D + Line Dual Y':swfFile = 'StackedColumn3DLineDY.swf';break;
            case 'Scroll Column 2D Chart':swfFile = 'ScrollColumn2D.swf';break;
            case 'Scroll Line 2D Chart':swfFile = 'ScrollLine2D.swf';break;
            case 'Scroll Area 2D Chart':swfFile = 'ScrollArea2D.swf';break;
            case 'Scroll Stacked Column 2D Chart':swfFile = 'ScrollStackedColumn2D.swf';break;
            case 'Scroll Combination 2D Chart':swfFile = 'ScrollCombi2D.swf';break;
            case 'Scroll Combination (Dual Y) 2D Chart':swfFile = 'ScrollCombiDY2D.swf';break;
            case 'Scatter (XY Plot) Chart':swfFile = 'Scatter.swf';break;
            case 'Bubble Chart':swfFile = 'Bubble.swf';break;
            case 'Logarithmic Column 2D':swfFile = 'LogMSColumn2D.swf';break;
            case 'Logarithmic Line 2D':swfFile = 'LogMSLine.swf';break;
            case 'Inverse y-Axis Column 2D':swfFile = 'InverseMSColumn2D.swf';break;
            case 'Inverse y-Axis Line 2D':swfFile = 'InverseMSLine.swf';break;
            case 'Inverse y-Axis Area':swfFile = 'InverseMSArea.swf';break;
            case 'Angular Gauge':swfFile = 'AngularGauge.swf';break;
            case 'Horizontal Linear Gauge':swfFile = 'HLinearGauge.swf';break;
            case 'Horizontal LED Gauge':swfFile = 'HLED.swf';break;
            case 'Vertical LED Gauge':swfFile = 'VLED.swf';break;
            case 'Bulb Gauge':swfFile = 'Bulb.swf';break;
            case 'Cylinder Gauge':swfFile = 'Cylinder.swf';break;
            case 'Thermometer Gauge':swfFile = 'Thermometer.swf';break;
            case 'Spark Line Chart':swfFile = 'SparkLine.swf';break;
            case 'Spark Column Chart':swfFile = 'SparkColumn.swf';break;
            case 'Spark Win/Loss Chart':swfFile = 'SparkWinLoss.swf';break;
            case 'Horizontal Bullet Graph':swfFile = 'HBullet.swf';break;
            case 'Vertical Bullet Graph':swfFile = 'VBullet.swf';break;
            case 'Gantt Chart':swfFile = 'Gantt.swf';break;
            case 'Radar Chart':swfFile = 'Radar.swf';break;
            default:break;
        }
        return getUrlPrefix() + "images/rapidjs/component/fusionChart/" + swfFile + '?ChartNoDataText=&nbsp;'
    }

});