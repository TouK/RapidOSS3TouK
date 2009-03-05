package ui.designer

import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import com.ifountain.rui.util.DesignerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 3, 2009
* Time: 1:31:36 PM
* To change this template use File | Settings | File Templates.
*/
class UiRootImageOperations  extends AbstractDomainOperation
{

    public static Map metaData()
    {
        Map metaData = [
                help:"TreeGrid RootImage.html",
                designerType: "RootImage",
                canBeDeleted: true,
                display: "RootImage",
                imageExpanded: "images/rapidjs/designer/image.png",
                imageCollapsed: "images/rapidjs/designer/image.png",
                propertyConfiguration: [
                        expanded: [descr: "The image url which will be shown when the row is expanded"],
                        collapsed: [descr: "The image url which will be shown when the row is collapsed"],
                        visible: [descr: "The JavaScript expression evaluated on row data to determine whether the image is displayed or not", required:true, type:"Expression"]
                ],
                childrenConfiguration: [:]
        ];
        return metaData;
    }

    def static addUiElement(xmlNode, parentElement)
    {
        def attributes = xmlNode.attributes();
        attributes.component = parentElement
        return DesignerUtils.addUiObject(UiRootImage, attributes, xmlNode);
    }

}