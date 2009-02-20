package ui.designer

import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import com.ifountain.rui.util.DesignerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 2, 2009
* Time: 2:43:02 PM
* To change this template use File | Settings | File Templates.
*/
class UiMenuItemOperations extends AbstractDomainOperation
{

    public static Map metaData()
    {
        Map metaData = [
                help:"MenuItem.html",
                designerType: "MenuItem",
                displayFromProperty: "name",
                canBeDeleted: "true",
                imageExpanded: "images/rapidjs/designer/view_menu.gif",
                imageCollapsed: "images/rapidjs/designer/view_menu.gif",
                propertyConfiguration: [
                        name: [descr: "Unique name of the menu item"],
                        label: [descr: "The label of the menu item"],
                        visible: [descr: "The JavaScript expression evaluated on row data to determine whether the item is displayed or not", required: true, type: "Expression"],
                ],
                childrenConfiguration: [
                        [
                                designerType: "MenuItem", propertyName: "childMenuItems", isMultiple: true
                        ]
                ]
        ];
        return metaData;
    }

    def static addUiElement(xmlNode, parentElement)
    {
        def attributes = xmlNode.attributes();
        attributes.component = parentElement;
        return DesignerUtils.addUiObject(UiMenuItem, attributes, xmlNode);
    }

    def getActions()
    {
        def menuActions = [];
        subscribedEvents.each {UiActionTrigger trigger ->
            menuActions.add(trigger.action);
        }
        return menuActions;
    }

    def getActionString() {
        def actions = getActions();
        def actionNames = actions.name;
        if (actionNames.size() > 0) {
            return "\${['" + actionNames.join("','") + "']}"
        }
        return null;
    }

}