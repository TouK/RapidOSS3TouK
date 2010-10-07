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

    protected void populateStringAttributes(GPathResult node, UiElmnt parent) {
        super.populateStringAttributes(node, parent);
        def imagesNode = node."${UIELEMENT_TAG}".find {it.@"${DESIGNER_TYPE}".text() == "SearchGridColumnImages"};
        if (imagesNode."${UIELEMENT_TAG}".size() == 0)
        {
            if(!attributesAsString["type"])
            {
                attributesAsString["type"] = "text";
            }
        }
        else
        {
            attributesAsString["type"] = "image";
        }
    }

    protected void addChildElements(GPathResult node, UiElmnt parent) {
        super.addChildElements(node, parent);
        def imagesNode = node."${UIELEMENT_TAG}".find {it.@"${DESIGNER_TYPE}".text() == "SearchGridColumnImages"};
        imagesNode."${UIELEMENT_TAG}".each {
            create(it, this);
        }
        removeUnneccessaryAttributes(imagesNode);

    }

    public List getImages() {
        return DesignerSpace.getInstance().getUiElements(UiImage).values().findAll {it.columnId == _designerKey};
    }
}