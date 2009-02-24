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
                help:"Action Trigger.html",
                designerType: "ActionTrigger",
                canBeDeleted: true,
                display: "Trigger",
                imageExpanded: "images/rapidjs/designer/bookmark.png",
                imageCollapsed: "images/rapidjs/designer/bookmark.png",
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
                component = UiComponent.get(tab: parentElement.tab, name: attributes.component, isActive: true);
                attributes.component = component;
            }
            if (triggerType == UiActionTrigger.MENU_TYPE && component != null) {
                def menuItem = UiMenuItem.get(component: component, isActive: true, name: attributes.event);
                attributes.menu = menuItem;
            }
        }
        else if (triggerType == UiActionTrigger.ACTION_TYPE) {
            if (attributes.triggeringAction != "")
            {
                def triggeringAction = UiAction.get(tab: parentElement.tab, name: attributes.triggeringAction, isActive: true);
                attributes.triggeringAction = triggeringAction;
            }
        }
        attributes.action = parentElement;
        return DesignerUtils.addUiObject(UiActionTrigger, attributes, xmlNode);
    }
}