package ui.designer

import com.ifountain.rui.util.DesignerUtils
import com.ifountain.rui.util.exception.UiElementCreationException

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 2, 2009
* Time: 2:09:19 PM
* To change this template use File | Settings | File Templates.
*/
class UiCombinedActionOperations extends UiActionOperations {
    public static Map metaData()
    {
        Map metaData = [
                designerType: "CombinedAction",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/layout_content.png",
                imageCollapsed: "images/rapidjs/designer/layout_content.png",
                propertyConfiguration: [
                        actions: [descr: "The list of action names that will be executed in order", required:true]
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
        def actions = [];
        attributes.actions.split(",").each{
            if(it != "")
            {
                def subAction = UiAction.get(name:it, tab:parentElement, isActive:true);
                if(subAction)
                {
                    actions.add(subAction);
                }
                else
                {
                    throw new UiElementCreationException(UiCombinedAction, "Action ${it} could not found for combined action ${attributes.name}".toString());
                }
            }
        }
        attributes.actions = actions;
        def addedAction = DesignerUtils.addUiObject(UiCombinedAction, attributes, xmlNode);
        addTriggers(xmlNode, addedAction);
        return addedAction;
    }
}