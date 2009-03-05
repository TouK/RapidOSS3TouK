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
                help:"TreeGrid Column.html",
                designerType: "TreeGridColumn",
                canBeDeleted: true,
                displayFromProperty: "attributeName",
                display:"Column",
                imageExpanded: "images/rapidjs/designer/view_sidetree.png",
                imageCollapsed: "images/rapidjs/designer/view_sidetree.png",
                propertyConfiguration: [
                    sortType: [descr: "specifies whether the values will be sorted as a int, string, upper cased string, float or date", required:false],
                ],
                childrenConfiguration: [
                        [
                                help:"TreeGridColumn Images.html",
                                designerType: "TreeGridColumnImages",
                                metaData: [
                                        designerType: "TreeGridColumnImages",
                                        display: "Images",
                                        imageExpanded: "images/rapidjs/designer/images.png",
                                        imageCollapsed: "images/rapidjs/designer/images.png",
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
        def propConfig = [:]
        propConfig.putAll(parentMetaData.propertyConfiguration)
        propConfig.putAll(metaData.propertyConfiguration)
        metaData.propertyConfiguration = propConfig;
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;
    }

    def static addUiElement(xmlNode, parentElement)
    {
        def imagesNode = xmlNode.UiElement.find {it.@designerType.text() == "TreeGridColumnImages"};
        def attributes = xmlNode.attributes();
        attributes.component = parentElement
        attributes.componentId = parentElement.id
        if (imagesNode.UiElement.size() == 0)
        {
            attributes.type = "text";
        }
        else
        {
            attributes.type = "image";
        }
        def treeGridColumn = DesignerUtils.addUiObject(UiTreeGridColumn, attributes, xmlNode);
        imagesNode.UiElement.each {
            UiImage.addUiElement(it, treeGridColumn);
        }
        return treeGridColumn;
    }

}