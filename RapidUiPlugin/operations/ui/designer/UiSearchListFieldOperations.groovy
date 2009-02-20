package ui.designer

import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import com.ifountain.rui.util.DesignerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 3, 2009
* Time: 2:11:34 PM
* To change this template use File | Settings | File Templates.
*/
class UiSearchListFieldOperations extends AbstractDomainOperation
{

    public static Map metaData()
    {
        Map metaData = [
                help:"SearchList Component.html",
                designerType: "SearchListField",
                canBeDeleted: true,
                display: "Field",
                imageExpanded: "images/rapidjs/designer/layout_content.png",
                imageCollapsed: "images/rapidjs/designer/layout_content.png",
                propertyConfiguration: [
                        fields: [descr: "Property list that will be displayed if the expression evaluates to true"],
                        exp: [descr: "The JavaScript expression evaluated on row data to determine whether the property list is valid or not", type:"Expression"]
                ]
        ];
        return metaData;
    }

    def static addUiElement(xmlNode, parentElement)
    {
        def attributes = xmlNode.attributes();
        attributes.component = parentElement
        return DesignerUtils.addUiObject(UiSearchListField, attributes, xmlNode);
    }
}
