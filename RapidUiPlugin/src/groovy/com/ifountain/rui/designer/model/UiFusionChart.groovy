package com.ifountain.rui.designer.model
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Feb 12, 2010
 * Time: 1:24:29 PM
 */
class UiFusionChart extends UiComponent {
    public static final String Column2D = "Column 2D"
    public static final String Column3D = "Column 3D"
    public static final String Pie2D = "Pie 2D"
    public static final String Pie3D = "Pie 3D"
    public static final String Line = "Line 2D"
    public static final String Bar2D = "Bar 2D"
    public static final String Area2D = "Area 2D"
    public static final String Doughnut2D = "Doughnut 2D"
    public static final String Doughnut3D = "Doughnut 3D"
    public static final String Spline = "Spline 2D"
    public static final String SplineArea = "Spline Area 2D"
    public static final String Funnel = "Funnel 3D"
    public static final String Pyramid = "Pyramid 3D"
    public static final String MSColumn2D = "Multiseries Column 2D"
    public static final String MSColumn3D = "Multiseries Column 3D"
    public static final String MSLine = "Multiseries Line 2D"
    public static final String MSArea = "Multiseries Area 2D"
    public static final String MSBar2D = "Multiseries Bar 2D"
    public static final String MSBar3D = "Multiseries Bar 3D"
    public static final String MSSpline = "Multiseries Spline Line 2D"
    public static final String MSSplineArea = "Multiseries Spline Area 2D"
    public static final String StackedColumn2D = "Stacked Column 2D"
    public static final String StackedColumn3D = "Stacked Column 3D"
    public static final String StackedArea2D = "Stacked Area 2D"
    public static final String StackedBar2D = "Stacked Bar 2D"
    public static final String StackedBar3D = "Stacked Bar 3D"
    public static final String MSCombi2D = "2D Single Y Combination"
    public static final String MSCombi3D = "3D Single Y Combination"
    public static final String MSColumnLine3D = "Column 3D + Line Single Y"
    public static final String MSCombiDY2D = "2D Dual Y Combination"
    public static final String MSColumn3DLineDY = "Column 3D + Line Dual Y"
    public static final String StackedColumn3DLineDY = "Stacked Column 3D + Line Dual Y"
    public static final String Scatter = "Scatter (XY Plot) Chart"
    public static final String Bubble = "Bubble Chart"
    public static final String ScrollColumn2D = "Scroll Column 2D Chart"
    public static final String ScrollLine2D = "Scroll Line 2D Chart"
    public static final String ScrollArea2D = "Scroll Area 2D Chart"
    public static final String ScrollStackedColumn2D = "Scroll Stacked Column 2D Chart"
    public static final String ScrollCombi2D = "Scroll Combination 2D Chart"
    public static final String ScrollCombiDY2D = "Scroll Combination (Dual Y) 2D Chart"
    public static final String LogMSColumn2D = "Logarithmic Column 2D"
    public static final String LogMSLine = "Logarithmic Line 2D"
    public static final String InverseMSColumn2D = "Inverse y-Axis Column 2D"
    public static final String InverseMSLine = "Inverse y-Axis Line 2D"
    public static final String InverseMSArea = "Inverse y-Axis Area"
    public static final String AngularGauge = "Angular Gauge"
    public static final String HLinearGauge = "Horizontal Linear Gauge"
    public static final String HLED = "Horizontal LED Gauge"
    public static final String VLED = "Vertical LED Gauge"
    public static final String Bulb = "Bulb Gauge"
    public static final String Cylinder = "Cylinder Gauge"
    public static final String Thermometer = "Thermometer Gauge"
    public static final String SparkLine = "Spark Line Chart"
    public static final String SparkColumn = "Spark Column Chart"
    public static final String SparkWinLoss = "Spark Win/Loss Chart"
    public static final String HBullet = "Horizontal Bullet Graph"
    public static final String VBullet = "Vertical Bullet Graph"
    public static final String Gantt = "Gantt Chart"
    public static final String Radar = "Radar Chart"

    String url = "";
    Long pollingInterval = 0;
    Long timeout = 30;
    String type = Column2D;

    public static Map metaData()
    {
        Map metaData = [
                help: "FusionChart Component.html",
                designerType: "FusionChart",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/chart_bar.png",
                imageCollapsed: "images/rapidjs/designer/chart_bar.png",
                propertyConfiguration: [
                        type: [descr: 'Type of the chart', validators: [blank: false, nullable: false, inList: [
                                Column2D, Column3D, Pie2D, Pie3D, Line, Bar2D, Area2D, Doughnut2D, Doughnut3D, Spline, SplineArea, Funnel, Pyramid,
                                MSColumn2D, MSColumn3D, MSLine, MSArea, MSBar2D, MSBar3D, MSSpline, MSSplineArea, StackedColumn2D, StackedColumn3D, StackedArea2D,
                                StackedBar2D, StackedBar3D, MSCombi2D, MSCombi3D, MSColumnLine3D, MSCombiDY2D,
                                MSColumn3DLineDY, StackedColumn3DLineDY, Scatter, Bubble, ScrollColumn2D,
                                ScrollLine2D, ScrollArea2D, ScrollStackedColumn2D, ScrollCombi2D, ScrollCombiDY2D, LogMSColumn2D,
                                LogMSLine, InverseMSColumn2D, InverseMSLine, InverseMSArea, AngularGauge, HLinearGauge, HLED,
                                VLED, Bulb, Cylinder, Thermometer, SparkLine, SparkColumn, SparkWinLoss, HBullet,
                                VBullet, Gantt, Radar
                        ]]],
                        url: [descr: "The default URL to be used for requests to the server to retrieve the data. ", validators: [blank: false, nullable: false]],
                        pollingInterval: [descr: "Time delay between two server requests.", required: true],
                        timeout: [descr: "The time interval in seconds to wait the server request completes successfully before aborting."]

                ],
                childrenConfiguration: []
        ];
        def parentMetaData = UiComponent.metaData();
        def propConfig = [:]
        propConfig.putAll(parentMetaData.propertyConfiguration)
        propConfig.putAll(metaData.propertyConfiguration)
        metaData.propertyConfiguration = propConfig;
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;
    }
}