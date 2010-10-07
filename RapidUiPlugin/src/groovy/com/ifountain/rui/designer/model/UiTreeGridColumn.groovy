package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.DesignerSpace
import com.ifountain.rui.designer.UiElmnt
import groovy.util.slurpersupport.GPathResult

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 22, 2009
* Time: 4:35:37 PM
*/
class UiTreeGridColumn extends UiColumn {
    String type = "text";
    String sortType = "string";

    public static Map metaData()
    {
        Map metaData = [
                help: "TreeGrid Column.html",
                designerType: "TreeGridColumn",
                canBeDeleted: true,
                displayFromProperty: "attributeName",
                display: "Column",
                imageExpanded: "images/rapidjs/designer/view_sidetree.png",
                imageCollapsed: "images/rapidjs/designer/view_sidetree.png",
                propertyConfiguration: [
                        type: [isVisible: false, validators: [blank: false, nullable: false, inList: ["text", "image"]]],
                        sortType: [descr: "specifies whether the values will be sorted as a int, string, upper cased string, float or date", required: false,
                                validators: [blank: false, nullable: false, inList: ["string", "ucString", "int", "date", "float"]]],
                ],
                childrenConfiguration: [
                        [
                                help: "TreeGridColumn Images.html",
                                designerType: "TreeGridColumnImages",
                                metaData: [
                                        designerType: "TreeGridColumnImages",
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
        def propConfig = [:]
        propConfig.putAll(parentMetaData.propertyConfiguration)
        propConfig.putAll(metaData.propertyConfiguration)
        metaData.propertyConfiguration = propConfig;
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;
    }

    protected void populateStringAttributes(GPathResult node, UiElmnt parent) {
        super.populateStringAttributes(node, parent);
        def imagesNode = node."${UIELEMENT_TAG}".find {it.@"${DESIGNER_TYPE}".text() == "TreeGridColumnImages"};
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
        def imagesNode = node."${UIELEMENT_TAG}".find {it.@"${DESIGNER_TYPE}".text() == "TreeGridColumnImages"};
        imagesNode."${UIELEMENT_TAG}".each {
            create(it, this)
        }
        removeUnneccessaryAttributes(imagesNode)
    }

    public List getImages() {
        return DesignerSpace.getInstance().getUiElements(UiImage).values().findAll {it.columnId == _designerKey};
    }
}