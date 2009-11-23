package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.UiElmnt
import com.ifountain.rui.designer.DesignerSpace
import groovy.util.slurpersupport.GPathResult

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 22, 2009
* Time: 10:19:23 AM
*/
class UiMenuItem extends UiElmnt {

    String name = "";
    String label = "";
    String type = "component";
    String visible = "true";

    String componentId;
    String parentMenuItemId;
    String toolbarId;

    public static Map metaData()
    {
        Map metaData = [
                help: "SearchGrid MenuItem.html",
                designerType: "MenuItem",
                displayFromProperty: "name",
                canBeDeleted: "true",
                imageExpanded: "images/rapidjs/designer/view_menu.gif",
                imageCollapsed: "images/rapidjs/designer/view_menu.gif",
                propertyConfiguration: [
                        componentId: [validators: [key: true], isVisible: false],
                        name: [descr: "Unique name of the menu item", validators: [key: true, matches: "[a-z_A-z]\\w*"]],
                        label: [descr: "The label of the menu item", validators:[nullable:false]],
                        visible: [descr: "The JavaScript expression evaluated on row data to determine whether the item is displayed or not", required: true, type: "Expression"],
                        type: [validators: [blank: false, nullable: false, inList: ["component", "property", "toolbar", "multiple"]], isVisible: false]
                ],
                childrenConfiguration: [
                        [
                                designerType: "MenuItem", isMultiple: true
                        ]
                ]
        ];
        return metaData;
    }

    public static UiElmnt addUiElement(GPathResult xmlNode, UiElmnt parentElement)
    {
        def attributes = xmlNode.attributes();
        attributes.componentId = parentElement._designerKey;
        def childMenuItemNodes = xmlNode.UiElement
        def menuItem = DesignerSpace.getInstance().addUiElement(UiMenuItem, attributes);
        childMenuItemNodes.each {childMenuItem ->
            UiMenuItem cMenuItem = UiMenuItem.addUiElement(childMenuItem, parentElement);
            cMenuItem.parentMenuItemId = menuItem._designerKey;
        }
        return menuItem;
    }

    public List getActions()
    {
        def menuActions = [];
        subscribedEvents.each {UiActionTrigger trigger ->
            menuActions.add(trigger.action);
        }
        return menuActions;
    }

    public String getActionString() {
        def actions = getActions();
        def actionNames = actions.name;
        if (actionNames.size() > 0) {
            return "\${['" + actionNames.join("','") + "']}"
        }
        return null;
    }

    public List getChildMenuItems() {
        return DesignerSpace.getInstance().getUiElements(UiMenuItem).values().findAll {it.parentMenuItemId == _designerKey};
    }

    public UiMenuItem getParentMenuItem() {
        def menuItems = DesignerSpace.getInstance().getUiElements(UiMenuItem).values().findAll {it._designerKey == parentMenuItemId};
        if(menuItems.size() > 0){
            return menuItems[0]
        }
        return null;
    }

    public List getSubscribedEvents() {
        return DesignerSpace.getInstance().getUiElements(UiActionTrigger).values().findAll {it.menuId == _designerKey};
    }
}