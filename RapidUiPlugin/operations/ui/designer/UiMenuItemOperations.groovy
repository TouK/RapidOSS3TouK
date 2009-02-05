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
                designerType:"MenuItem",
                displayFromProperty:"name",
                canBeDeleted:"true",
                imageExpanded: "images/rapidjs/designer/layout_content.png",
                imageCollapsed: "images/rapidjs/designer/layout_content.png",
                propertyConfiguration: [
                        name: [descr: "Unique name of the menu item"],
                        label: [descr: "The label of the menu item"],
                        visible: [descr: "The JavaScript expression evaluated on row data to determine whether the item is displayed or not"],
                ],
                childrenConfiguration: [
                        [
                                designerType: "MenuItem",propertyName:"childMenuItems", isMultiple: true
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

}