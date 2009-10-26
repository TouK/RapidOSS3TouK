package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.UiElmnt
import groovy.util.slurpersupport.GPathResult
import com.ifountain.rui.designer.DesignerSpace

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

    public static UiElmnt addUiElement(GPathResult xmlNode, UiElmnt parentElement)
    {
        def imagesNode = xmlNode.UiElement.find {it.@designerType.text() == "TreeGridColumnImages"};
        def attributes = xmlNode.attributes();
        attributes.componentId = parentElement._designerKey
        if (imagesNode.UiElement.size() == 0)
        {
            attributes.type = "text";
        }
        else
        {
            attributes.type = "image";
        }
        def treeGridColumn = DesignerSpace.getInstance().addUiElement(UiTreeGridColumn, attributes);
        imagesNode.UiElement.each {
            UiImage.addUiElement(it, treeGridColumn);
        }
        return treeGridColumn;
    }
    public List getImages() {
        return DesignerSpace.getInstance().getUiElements(UiImage).values().findAll {it.columnId == _designerKey};
    }
}