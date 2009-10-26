package com.ifountain.rui.designer.model

import groovy.util.slurpersupport.GPathResult
import com.ifountain.rui.designer.UiElmnt
import com.ifountain.rui.designer.DesignerSpace

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 22, 2009
* Time: 4:16:12 PM
*/
class UiSearchGridColumn extends UiColumn {
    String type = "text";

    public static Map metaData()
    {
        Map metaData = [
                help: "SearchGrid Column.html",
                designerType: "SearchGridColumn",
                canBeDeleted: true,
                displayFromProperty: "attributeName",
                display: "Column",
                imageExpanded: "images/rapidjs/designer/SearchGridColumn.png",
                imageCollapsed: "images/rapidjs/designer/SearchGridColumn.png",
                propertyConfiguration: [
                        type: [descr: "Column type.", required: false, validators: [inList: ["text", "link", "image"]]]
                ],
                childrenConfiguration: [
                        [
                                help: "TreeGridColumn Images.html",
                                designerType: "SearchGridColumnImages",
                                metaData: [
                                        designerType: "SearchGridColumnImages",
                                        display: "Images",
                                        imageExpanded: "images/rapidjs/designer/images.png",
                                        imageCollapsed: "images/rapidjs/designer/images.png",
                                        canBeDeleted: false,
                                        propertyConfiguration: [:],
                                        childrenConfiguration: [
                                                [designerType: "Image", isMultiple: true]
                                        ]
                                ],
                                isMultiple: false
                        ]
                ]
        ];
        def parentMetaData = UiColumn.metaData();
        metaData.propertyConfiguration.putAll(parentMetaData.propertyConfiguration);
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;
    }

    public static UiElmnt addUiElement(GPathResult xmlNode, UiElmnt parentElement)
    {
        def attributes = xmlNode.attributes();
        attributes.componentId = parentElement._designerKey;
        def searchGridColumn = DesignerSpace.getInstance().addUiElement(UiSearchGridColumn, attributes);
        if (attributes.type == "image") {
            def imagesNode = xmlNode.UiElement.find {it.@designerType.text() == "SearchGridColumnImages"};
            imagesNode.UiElement.each {
                UiImage.addUiElement(it, searchGridColumn);
            }
        }
        return searchGridColumn;
    }

    public List getImages() {
        return DesignerSpace.getInstance().getUiElements(UiImage).values().findAll {it.columnId == _designerKey};
    }
}