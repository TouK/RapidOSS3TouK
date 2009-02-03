package ui.designer

import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 3, 2009
* Time: 11:00:15 AM
* To change this template use File | Settings | File Templates.
*/
class UiImageOperations extends AbstractDomainOperation
{

    public static Map metaData()
    {
        Map metaData = [
                designerType: "Image",
                canBeDeleted: true,
                display: "Image",
                imageExpanded: "images/rapidjs/designer/layout_content.png",
                imageCollapsed: "images/rapidjs/designer/layout_content.png",
                propertyConfiguration: [
                        src: [descr: "Image url."],
                        visible: [descr: "The JavaScript expression evaluated on row data to determine whether the image is displayed or not."],
                        align: [descr: "Sets the starting position of a image. Available values are left, right and center"]
                ],
                childrenConfiguration: [:]
        ];
        return metaData;
    }

}