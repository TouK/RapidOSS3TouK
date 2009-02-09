package ui.designer

import com.ifountain.rui.util.DesignerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 6, 2009
* Time: 8:58:29 AM
* To change this template use File | Settings | File Templates.
*/
class UiGMapOperations extends UiComponentOperations
{
    public static Map metaData()
    {
        Map metaData = [
                designerType: "GMap",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/component.gif",
                imageCollapsed: "images/rapidjs/designer/component.gif",
                propertyConfiguration: [
                        url:[descr:"The default URL to be used for requests to the server to retrieve the data. "],
                        contentPath:[descr:"The node names of AJAX response which will be used as location data. "],
                        latitudeField:[descr:"The attribute in data which specifies the latitude of the location. "],
                        longitudeField:[descr:"The attribute in data which specifies the longitude of the location. "],
                        addressField:[descr:"The attribute in data which specifies open location address. "],
                        markerField:[descr:"The marker image url. "],
                        tooltipField:[descr:"The attribute in data which will be displayed in tooltip. "],
                        title:[descr:"GMap title."],
                        pollingInterval:[descr:"Time delay between two server requests.", required:true],
                        timeout:[descr:"The time interval in seconds to wait the server request completes successfully before aborting."]
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
        return DesignerUtils.addUiObject(UiGMap, attributes, xmlNode);
    }

}
