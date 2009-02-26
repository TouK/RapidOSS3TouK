package ui.designer

import com.ifountain.rui.util.DesignerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 5, 2009
* Time: 5:14:22 PM
* To change this template use File | Settings | File Templates.
*/
class UiTimelineOperations extends UiComponentOperations {
    public static Map metaData()
    {
        Map metaData = [
                help:"Timeline Component.html",
                designerType: "Timeline",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/date.png",
                imageCollapsed: "images/rapidjs/designer/date.png",
                propertyConfiguration: [
                        url: [descr: "The default URL to be used for requests to the server to retrieve the data."],
                        pollingInterval: [descr: "Time delay between two server requests.", required:true]

                ],
                childrenConfiguration:[[designerType: "TimelineBand", propertyName:"bands", isMultiple:"true"]]
        ];
        def parentMetaData = UiComponentOperations.metaData();
        metaData.propertyConfiguration.putAll(parentMetaData.propertyConfiguration);
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;
    }

    def static addUiElement(xmlNode, parentElement)
    {
        def attributes = xmlNode.attributes();
        attributes.tab = parentElement;
        attributes.tabId = parentElement.id;
        def searchGrid = DesignerUtils.addUiObject(UiTimeline, attributes, xmlNode);
        xmlNode.UiElement.each{
            UiTimelineBand.addUiElement(it, searchGrid);
        }
        return searchGrid;
    }
}