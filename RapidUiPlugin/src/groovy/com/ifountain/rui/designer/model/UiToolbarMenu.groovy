package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.UiElmnt
import groovy.util.slurpersupport.GPathResult
import com.ifountain.rui.designer.DesignerSpace

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 22, 2009
* Time: 5:16:32 PM
*/
class UiToolbarMenu extends UiElmnt {
    String label = "";
    String componentId = ""

    public static Map metaData()
    {
        Map metaData = [
                help: "ObjectMap ToolBarMenu.html",
                designerType: "ToolbarMenu",
                canBeDeleted: true,
                displayFromProperty: "label",
                imageExpanded: "images/rapidjs/designer/application_view_icons.png",
                imageCollapsed: "images/rapidjs/designer/application_view_icons.png",
                propertyConfiguration: [
                        label: [descr: "The label of the menu", validators: [blank: false, nullable: false]],
                        componentId: [isVisible: false, validators: [blank: false, nullable: false]]
                ],
                childrenConfiguration: [
                        [designerType: "MenuItem", isMultiple: true]
                ]
        ];
        return metaData;
    }

    protected void addChildElements(GPathResult node, UiElmnt parent) {
        node."${UIELEMENT_TAG}".each {menuItemNode ->
            create(menuItemNode, this);
        }
    }

    protected void populateStringAttributes(GPathResult node, UiElmnt parent) {
        super.populateStringAttributes(node, parent);
        attributesAsString["componentId"] = parent._designerKey;
    }
    

    public List getMenuItems() {
        return DesignerSpace.getInstance().getUiElements(UiMenuItem).values().findAll {it.toolbarId == _designerKey};
    }
}