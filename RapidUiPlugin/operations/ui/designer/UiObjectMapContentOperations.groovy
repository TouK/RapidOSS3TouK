package ui.designer

import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import com.ifountain.rui.util.DesignerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 6, 2009
* Time: 10:03:43 AM
* To change this template use File | Settings | File Templates.
*/
class UiObjectMapContentOperations  extends AbstractDomainOperation
{

    public static Map metaData()
    {
        Map metaData = [
                help:"ObjectMap NodeContent.html",
                designerType: "ObjectMapContent",
                canBeDeleted: true,
                display: "NodeContent",
                imageExpanded: "images/rapidjs/designer/report.png",
                imageCollapsed: "images/rapidjs/designer/report.png",
                propertyConfiguration: [
                        name: [descr: "The unique name of the node content configuration"],
                        x: [descr: "Sets how far the left edge of the image is to the left edge of the node"],
                        y: [descr: "Sets how far the top edge of the image is to the top edge of the node"],
                        width: [descr: "Width of the image"],
                        height: [descr: "Height of the image"],
                        dataKey: [descr: "The attribute in node data which the mapping will be applied according to"],
                ],
                childrenConfiguration: []
        ];
        return metaData;
    }

    def static addUiElement(xmlNode, parentElement)
    {
        def attributes = xmlNode.attributes();
        attributes.objectMap = parentElement
        attributes.objectMapId = parentElement.id
        return DesignerUtils.addUiObject(UiObjectMapContent, attributes, xmlNode);
    }

}