package ui.designer

import com.ifountain.rui.util.DesignerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 2, 2009
* Time: 3:37:33 PM
* To change this template use File | Settings | File Templates.
*/
class UiFlexPieChartOperations extends UiComponentOperations
{

    public static Map metaData()
    {
        Map metaData = [
                designerType: "FlexPieChart",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/component.gif",
                imageCollapsed: "images/rapidjs/designer/component.gif",
                propertyConfiguration: [
                        url: [descr: "The default URL to be used for requests to the server to retrieve the data. "],
                        rootTag: [descr: "The root node name of AJAX response which FlexPieChart takes as starting point to get its data."],
                        pollingInterval: [descr: "Time delay between two server requests.", required:true]
                ],
                childrenConfiguration: []
        ];
        def parentMetaData = UiComponentOperations.metaData();
        metaData.propertyConfiguration.putAll(parentMetaData.propertyConfiguration);
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;
    }

    def static addUiElement(xmlNode, parentElement)
    {
        def attributes = [:];
        attributes.putAll (xmlNode.attributes());
        attributes.tab = parentElement;
        return DesignerUtils.addUiObject(UiFlexPieChart, attributes, xmlNode);
    }

}