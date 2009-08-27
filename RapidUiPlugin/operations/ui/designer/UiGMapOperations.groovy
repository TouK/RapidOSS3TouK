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
                help:"GMap Component.html",
                designerType: "GMap",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/map.png",
                imageCollapsed: "images/rapidjs/designer/map.png",
                propertyConfiguration: [
                        url:[descr:"The default URL to be used for requests to the server to retrieve the data. "],
                        googleKey:[descr:"Google Maps API key for your domain.See Google Maps API documentation for further information."],
                        pollingInterval:[descr:"Time delay between two server requests.", required:true],
                        locationTagName:[descr:"The node names of AJAX response which will be used as location data. "],
                        lineTagName:[descr:"The node names of AJAX response which will be used as line data. "],
                        iconTagName:[descr:"The node names of AJAX response which will be used as icon data. "],
                        lineSize:[descr:"Pixel width of the lines."],
                        defaultIconWidth:[descr:"Default width of icons in pixels if not specified in AJAX response. Default is 32."],
                        defaultIconHeight:[descr:"Default height of icons in pixels if not specified in AJAX response. Default is 32."],
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
        return DesignerUtils.addUiObject(UiGMap, attributes, xmlNode);
    }

}
