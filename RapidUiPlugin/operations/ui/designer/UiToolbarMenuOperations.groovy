package ui.designer

import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import com.ifountain.rui.util.DesignerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 6, 2009
* Time: 10:13:57 AM
* To change this template use File | Settings | File Templates.
*/
class UiToolbarMenuOperations extends AbstractDomainOperation
{

    public static Map metaData()
    {
        Map metaData = [
                help:"ObjectMap ToolBarMenu.html",
                designerType: "ToolbarMenu",
                canBeDeleted: true,
                displayFromProperty: "label",
                imageExpanded: "images/rapidjs/designer/application_view_icons.png",
                imageCollapsed: "images/rapidjs/designer/application_view_icons.png",
                propertyConfiguration: [
                        label: [descr: "The label of the menu", required:true]
                ],
                childrenConfiguration: [                                                                                                                          
                    [designerType: "MenuItem", propertyName: "menuItems", isMultiple: true, isVisible:{component-> return component.type == "toolbar" && component.parentMenuItem == null}]
                ]
        ];
        return metaData;
    }

    def static addUiElement(xmlNode, parentElement)
    {
        def attributes = xmlNode.attributes();
        attributes.objectMap = parentElement
        def menuItems = [];
        xmlNode.UiElement.each{menuItemNode->
            menuItemNode.attributes()["type"] = "toolbar";
            menuItems.add(UiMenuItem.addUiElement(menuItemNode, parentElement));
        }
        attributes.menuItems = menuItems;        
        return DesignerUtils.addUiObject(UiToolbarMenu, attributes, xmlNode);
    }

}