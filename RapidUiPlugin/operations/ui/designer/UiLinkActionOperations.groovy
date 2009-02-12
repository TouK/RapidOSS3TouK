package ui.designer

import com.ifountain.rui.util.DesignerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 2, 2009
* Time: 2:09:19 PM
* To change this template use File | Settings | File Templates.
*/
class UiLinkActionOperations extends UiActionOperations {
    public static Map metaData()
    {
        Map metaData = [
                designerType: "LinkAction",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/layout_content.png",
                imageCollapsed: "images/rapidjs/designer/layout_content.png",
                propertyConfiguration: [
                        url: [descr: "JavaScript expression that will be evaluated to determine the URL to redirect to", type:"Expression"]
                ],
                childrenConfiguration: []
        ];
        def parentMetaData = UiActionOperations.metaData();
        metaData.propertyConfiguration.putAll(parentMetaData.propertyConfiguration);
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;

    }

    def static addUiElement(xmlNode, parentElement)
    {
        def attributes = xmlNode.attributes();
        attributes.tab = parentElement;
        def addedAction = DesignerUtils.addUiObject(UiLinkAction, attributes, xmlNode);
        addTriggers(xmlNode, addedAction);
        return addedAction;
    }
}