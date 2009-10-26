package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.DesignerSpace
import com.ifountain.rui.designer.UiElmnt
import groovy.util.slurpersupport.GPathResult

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 22, 2009
* Time: 3:07:50 PM
*/
class UiPieChart extends UiComponent{
    String contentPath = "";
    String categoryField = "";
    String dataField = "";
    String legend = "none";
    String colors = "";
    String url = "";
    Long pollingInterval= 0;
    Long timeout= 30;

    public static Map metaData()
    {
        Map metaData = [
                help:"PieChart Component.html",
                designerType: "PieChart",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/chart_pie.png",
                imageCollapsed: "images/rapidjs/designer/chart_pie.png",
                propertyConfiguration: [
                        url:[descr:"The default URL to be used for requests to the server to retrieve the data.", validators:[blank:false, nullable:false]],
                        contentPath:[descr:"The node names of AJAX response which will be used as row data.", validators:[blank:false, nullable:false]],
                        categoryField:[descr:"The attribute in the data to define x series.", validators:[blank:false, nullable:false]],
                        dataField:[descr:"The attribute in the data to define y series.", validators:[blank:false, nullable:false]],
                        legend:[descr:"The position of the legend. Possible values are top, right, bottom, left and none.", validators:[blank:false, nullable:false, inList:["top", "right", "bottom", "left", "none"]]],
                        pollingInterval:[descr: "Time delay between two server requests.", required:true],
                        colors:[descr:"The color definition list of the slices. The order of the data is taken into consideration while matching the color to a category."],
                        timeout:[descr:"The time interval in seconds to wait the server request completes successfully before aborting."]
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

    public static UiElmnt addUiElement(GPathResult xmlNode, UiElmnt parentElement)
    {
        def attributes = [:];
        attributes.putAll(xmlNode.attributes());
        attributes.tabId = parentElement._designerKey;
        return DesignerSpace.getInstance().addUiElement(UiPieChart, attributes);
    }
}