package ui.designer

import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation

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
                designerType: "RootImage",
                canBeDeleted: true,
                display: "RootImage",
                imageExpanded: "images/rapidjs/designer/layout_content.png",
                imageCollapsed: "images/rapidjs/designer/layout_content.png",
                propertyConfiguration: [
                        expanded: [descr: "The image url which will be shown when the row is expanded"],
                        collapsed: [descr: "The image url which will be shown when the row is collapsed"],
                        visible: [descr: "The JavaScript expression evaluated on row data to determine whether the image is displayed or not"]
                ],
                childrenConfiguration: [:]
        ];
        return metaData;
    }

}