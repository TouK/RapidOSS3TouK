package ui.designer

import com.ifountain.rui.util.DesignerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 23, 2009
* Time: 5:50:39 PM
* To change this template use File | Settings | File Templates.
*/
class UiSearchGridColumnOperations extends UiColumnOperations{
   public static Map metaData()
    {
        Map metaData = [
                help:"SearchGrid Column.html",
                designerType: "SearchGridColumn",
                canBeDeleted: true,
                displayFromProperty: "attributeName",
                display: "Column",
                imageExpanded: "images/rapidjs/designer/SearchGridColumn.png",
                imageCollapsed: "images/rapidjs/designer/SearchGridColumn.png",
                propertyConfiguration: [
                    type:[descr:"Column type.", required:false]
                ],
                childrenConfiguration: [
                    [
                                help:"TreeGridColumn Images.html",
                                designerType: "SearchGridColumnImages",
                                metaData: [
                                        designerType: "SearchGridColumnImages",
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
        metaData.propertyConfiguration.putAll(parentMetaData.propertyConfiguration);
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;
    }

    def static addUiElement(xmlNode, parentElement)
    {
        def attributes = xmlNode.attributes();
        attributes.component = parentElement
        attributes.componentId = parentElement.id
        def searchGridColumn = DesignerUtils.addUiObject(UiSearchGridColumn, attributes, xmlNode);
        if(attributes.type == "image"){
            def imagesNode = xmlNode.UiElement.find {it.@designerType.text() == "SearchGridColumnImages"};
            imagesNode.UiElement.each {
                UiImage.addUiElement(it, searchGridColumn);
            }
        }
        return searchGridColumn;
    }
}