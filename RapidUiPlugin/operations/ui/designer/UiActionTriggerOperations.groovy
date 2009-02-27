package ui.designer

import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import com.ifountain.rui.util.DesignerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 2, 2009
* Time: 2:04:38 PM
* To change this template use File | Settings | File Templates.
*/
class UiActionTriggerOperations extends AbstractDomainOperation {
    public static Map metaData()
    {
        Map metaData = [
                help: "Action Trigger.html",
                designerType: "ActionTrigger",
                canBeDeleted: true,
                display: "Trigger",
                imageExpanded: "images/rapidjs/designer/lightning.png",
                imageCollapsed: "images/rapidjs/designer/lightning.png",
                propertyConfiguration: [
                        type: [descr: "The type of the triggering event"],
                        component: [descr: "", formatter: {object -> return object.component ? object.component.name : ""}],
                        triggeringAction: [descr: "", formatter: {object -> return object.triggeringAction ? object.triggeringAction.name : ""}],
                        event: [descr: ""]

                ],
                childrenConfiguration: []
        ];
        return metaData;
    }

    def static addUiElement(xmlNode, parentElement)
    {
        def attributes = xmlNode.attributes();
        def triggerType = attributes.type;
        if (triggerType == UiActionTrigger.MENU_TYPE || triggerType == UiActionTrigger.COMPONENT_TYPE) {
            def component = null;
            if (attributes.component != "")
            {
                component = UiComponent.get(tabId: parentElement.tab.id, name: attributes.component, isActive: true);
                attributes.component = component;
            }
            if (component == null) {
                throw new Exception("Property component cannot be null for ActionTrigger if type ${triggerType} with event ${attributes.event}")
            }
            if (triggerType == UiActionTrigger.MENU_TYPE) {
                def menuItem = UiMenuItem.get(componentId: component.id, isActive: true, name: attributes.event);
                if (menuItem == null) {
                    throw new Exception("Property menu cannot be null for ActionTrigger if type ${triggerType} with event ${attributes.event}")
                }
                attributes.menu = menuItem;
            }
        }
        else if (triggerType == UiActionTrigger.ACTION_TYPE) {
            def triggeringAction = null;
            if (attributes.triggeringAction != "")
            {
                triggeringAction = UiAction.get(tabId: parentElement.tab.id, name: attributes.triggeringAction, isActive: true);
                attributes.triggeringAction = triggeringAction;
            }
            if (triggeringAction == null) {
                throw new Exception("Property triggeringAction cannot be null for ActionTrigger if type ${triggerType} with event ${attributes.event}")
            }
        }
        attributes.action = parentElement;
        return DesignerUtils.addUiObject(UiActionTrigger, attributes, xmlNode);
    }
}