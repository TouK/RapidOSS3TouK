package ui.designer

import com.ifountain.rui.util.DesignerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 2, 2009
* Time: 3:37:33 PM
* To change this template use File | Settings | File Templates.
*/
class UiFlexLineChartOperations extends UiComponentOperations
{

    public static Map metaData()
    {
        Map metaData = [
                help:"FlexLineChart Component.html",
                designerType: "FlexLineChart",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/chart_line.png",
                imageCollapsed: "images/rapidjs/designer/chart_line.png",
                propertyConfiguration: [
                        url: [descr: "The default URL to be used for requests to the server to retrieve the data. "],
                        rootTag: [descr: "The root node name of AJAX response which FlexLineChart takes as starting point to get its data."],
                        dataRootTag: [descr: "The root node name which holds whole data related with one variable."],
                        dataTag: [descr: "Tag name of node which stores graph data."],
                        annotationTag: [descr: "Tag name of node which keeps annotations for graph."],
                        annLabelAttr: [descr: "Attribute for annotation label."],
                        annTimeAttr: [descr: "Attribute name of annotation time of  related annotaition."],
                        dateAttribute: [descr: "Name of timestamp attribute in dataTag."],
                        valueAttribute: [descr: "Name of value attribute in dataTag."],
                        durations: [descr: "time durations for zooming flex line chart."],
                        pollingInterval: [descr: "Time delay between two server requests.", required:true],
                        timeout:[descr:"The time interval in seconds to wait the server request completes successfully before aborting."]
                ],
                childrenConfiguration: []
        ];
        def parentMetaData = UiComponentOperations.metaData();
        def propConfig = [:]
        propConfig.putAll(parentMetaData.propertyConfiguration)
        propConfig.putAll(metaData.propertyConfiguration)
        metaData.propertyConfiguration = propConfig;
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;
    }

    def static addUiElement(xmlNode, parentElement)
    {
        def attributes = [:];
        attributes.putAll (xmlNode.attributes());
        attributes.tab = parentElement;
        attributes.tabId = parentElement.id;
        return DesignerUtils.addUiObject(UiFlexLineChart, attributes, xmlNode);
    }

}