package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.UiElmnt
import groovy.util.slurpersupport.GPathResult

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 22, 2009
* Time: 3:04:03 PM
*/
class UiFlexPieChart extends UiComponent{
    String rootTag = "";
    String url = "";
    Long pollingInterval= 0;
    Long timeout= 30;

    public static Map metaData()
    {
        Map metaData = [
                help:"FlexPieChart Component.html",
                designerType: "FlexPieChart",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/chart_pie.png",
                imageCollapsed: "images/rapidjs/designer/chart_pie.png",
                propertyConfiguration: [
                        url: [descr: "The default URL to be used for requests to the server to retrieve the data. ", validators:[blank:false, nullable:false]],
                        rootTag: [descr: "The root node name of AJAX response which FlexPieChart takes as starting point to get its data.", validators:[blank:false, nullable:false]],
                        pollingInterval: [descr: "Time delay between two server requests.", required:true],
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

    protected void addChildElements(GPathResult node, UiElmnt parent) {}

}