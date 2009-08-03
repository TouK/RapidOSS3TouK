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
                help:"LinkAction.html",
                designerType: "LinkAction",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/link.png",
                imageCollapsed: "images/rapidjs/designer/link.png",
                propertyConfiguration: [
                        url: [descr: "JavaScript expression that will be evaluated to determine the URL to redirect to", type:"Expression"],
                        target: [descr: "Specifies where to open the linked document"]
                ],
                childrenConfiguration: []
        ];
        def parentMetaData = UiActionOperations.metaData();
        def propConfig = [:]
        propConfig.put("name", parentMetaData.propertyConfiguration.remove("name"))
        propConfig.putAll(metaData.propertyConfiguration)
        propConfig.putAll(parentMetaData.propertyConfiguration)
        metaData.propertyConfiguration = propConfig;
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;

    }

    def static addUiElement(xmlNode, parentElement)
    {
        def attributes = xmlNode.attributes();
        attributes.tab = parentElement;
        attributes.tabId = parentElement.id;
        def addedAction = DesignerUtils.addUiObject(UiLinkAction, attributes, xmlNode);
        addTriggers(xmlNode, addedAction);
        return addedAction;
    }
}