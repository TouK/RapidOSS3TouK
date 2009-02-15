package ui.designer

import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import com.ifountain.rui.util.DesignerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 15, 2009
* Time: 1:03:20 PM
* To change this template use File | Settings | File Templates.
*/
class UiRowColorOperations  extends AbstractDomainOperation{
   public static Map metaData()
    {
        Map metaData = [
                designerType: "RowColor",
                canBeDeleted: true,
                display: "RowColor",
                imageExpanded: "images/rapidjs/designer/layout_content.png",
                imageCollapsed: "images/rapidjs/designer/layout_content.png",
                propertyConfiguration: [
                        color: [descr: "Row background color."],
                        visible: [descr: "The JavaScript expression evaluated on row data to determine whether the color is applied or not.", type:"Expression"],
                        textColor: [descr: "Cell text color."]
                ],
                childrenConfiguration: [:]
        ];
        return metaData;
    }

    def static addUiElement(xmlNode, parentElement)
    {
        def attributes = xmlNode.attributes();
        if(parentElement instanceof UiSearchGrid)
        {
            attributes.grid = parentElement
        }
        return DesignerUtils.addUiObject(UiRowColor, attributes, xmlNode);
    }
}