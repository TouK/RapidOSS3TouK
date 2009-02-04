package ui.designer

import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import com.ifountain.rui.util.DesignerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 3, 2009
* Time: 10:51:47 AM
* To change this template use File | Settings | File Templates.
*/
class UiTreeGridColumnOperations extends UiColumnOperations
{

    public static Map metaData()
    {
        Map metaData = [
                designerType: "TreeGridColumn",
                canBeDeleted: true,
                displayFromProperty: "attributeName",
                imageExpanded: "images/rapidjs/designer/layout_content.png",
                imageCollapsed: "images/rapidjs/designer/layout_content.png",
                propertyConfiguration: [
                        type: [descr: "Column type. Available types are text and image"],
                ],
                childrenConfiguration: [
                        [
                                designerType: "TreeGridColumnImages",
                                metaData: [
                                        designerType: "TreeGridColumnImages",
                                        display: "Images",
                                        canBeDeleted: false,
                                        propertyConfiguration: [:],
                                        childrenConfiguration: [
                                                [designerType: "Image", isMultiple: true, propertyName: "images"]
                                        ]
                                ],
                                isMultiple: false
                        ]
                ]
        ];
        def parentMetaData = UiColumnOperations.metaData();
        metaData.propertyConfiguration.putAll(parentMetaData.propertyConfiguration);
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;
    }

    def static addUiElement(xmlNode, parentElement)
    {
        def attributes = xmlNode.attributes();
        attributes.component = parentElement
        def treeGridColumn = DesignerUtils.addUiObject(UiTreeGridColumn, attributes, xmlNode);
        def imagesNode = xmlNode.UiElement.find {it.@designerType.text() == "TreeGridColumnImages"};
        imagesNode.UiElement.each{
            UiImage.addUiElement(it, treeGridColumn);
        }
        return treeGridColumn;
    }

}