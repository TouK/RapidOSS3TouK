package ui.designer

import com.ifountain.rui.util.DesignerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 6, 2009
* Time: 9:03:48 AM
* To change this template use File | Settings | File Templates.
*/
class UiHtmlOperations extends UiComponentOperations
{
    public static Map metaData()
    {
        Map metaData = [
                designerType: "Html",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/html.png",
                imageCollapsed: "images/rapidjs/designer/html.png",
                propertyConfiguration: [
                        pollingInterval:[descr:"Time delay between two server requests.", required:true],
                        iframe:[descr:"Parameter to display the content in an iframe or embed it to Html component's body."]
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
        return DesignerUtils.addUiObject(UiHtml, attributes, xmlNode);
    }

}
