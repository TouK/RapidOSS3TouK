package com.ifountain.rui.designer.model

import groovy.util.slurpersupport.GPathResult
import com.ifountain.rui.designer.UiElmnt
import com.ifountain.rui.designer.DesignerSpace

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 22, 2009
* Time: 3:01:16 PM
*/
class UiFlexLineChart extends UiComponent {
    String rootTag = "RootTag";
    String url = "";
    String dataTag = "Data";
    String dataRootTag = "Variable";
    String annotationTag = "Annotation";
    String annTimeAttr = "time";
    String annLabelAttr = "label";
    String dateAttribute = "time";
    String valueAttribute = "value";
    String durations = "";
    Long pollingInterval = 0;
    Long timeout = 30;

    public static Map metaData()
    {
        Map metaData = [
                help: "FlexLineChart Component.html",
                designerType: "FlexLineChart",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/chart_line.png",
                imageCollapsed: "images/rapidjs/designer/chart_line.png",
                propertyConfiguration: [
                        url: [descr: "The default URL to be used for requests to the server to retrieve the data. ", validators:[blank:false, nullable:false]],
                        rootTag: [descr: "The root node name of AJAX response which FlexLineChart takes as starting point to get its data.", validators:[blank:false, nullable:false]],
                        dataRootTag: [descr: "The root node name which holds whole data related with one variable."],
                        dataTag: [descr: "Tag name of node which stores graph data.", validators:[blank:false, nullable:false]],
                        annotationTag: [descr: "Tag name of node which keeps annotations for graph."],
                        annLabelAttr: [descr: "Attribute for annotation label."],
                        annTimeAttr: [descr: "Attribute name of annotation time of  related annotaition."],
                        dateAttribute: [descr: "Name of timestamp attribute in dataTag.", validators:[blank:false, nullable:false]],
                        valueAttribute: [descr: "Name of value attribute in dataTag.", validators:[blank:false, nullable:false]],
                        durations: [descr: "time durations for zooming flex line chart."],
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

    public static UiElmnt addUiElement(GPathResult xmlNode, UiElmnt parentElement)
    {
        def attributes = [:];
        attributes.putAll(xmlNode.attributes());
        attributes.tabId = parentElement._designerKey;
        return DesignerSpace.getInstance().addUiElement(UiFlexLineChart, attributes);
    }
}