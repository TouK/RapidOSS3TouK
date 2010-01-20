package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.DesignerSpace
import com.ifountain.rui.designer.UiElmnt
import groovy.util.slurpersupport.GPathResult

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

    protected void addChildElements(GPathResult node, UiElmnt parent) {
        super.addChildElements(node, parent);
        if (node.@type.toString() == "image") {
            def imagesNode = node."${UIELEMENT_TAG}".find {it.@"${DESIGNER_TYPE}".text() == "SearchGridColumnImages"};
            removeUnneccessaryAttributes(imagesNode);
            imagesNode."${UIELEMENT_TAG}".each {
                create(it, this);
            }
        }
    }

    public List getImages() {
        return DesignerSpace.getInstance().getUiElements(UiImage).values().findAll {it.columnId == _designerKey};
    }
}