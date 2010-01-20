package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.UiElmnt
import groovy.util.slurpersupport.GPathResult

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 22, 2009
* Time: 4:06:08 PM
*/
class UiImage extends UiElmnt{
    String src = "";
    String visible = "true";
    String align = "left";
    String componentId = "";
    String columnId = "";

    public static Map metaData()
    {
        Map metaData = [
                help: "SearchGrid Image.html",
                designerType: "Image",
                canBeDeleted: true,
                display: "Image",
                imageExpanded: "images/rapidjs/designer/image.png",
                imageCollapsed: "images/rapidjs/designer/image.png",
                propertyConfiguration: [
                        src: [descr: "Image url.", validators: [blank: false, nullable: false]],
                        visible: [descr: "The JavaScript expression evaluated on row data to determine whether the image is displayed or not.", required: true, type: "Expression"],
                        align: [descr: "Sets the starting position of a image. Available values are left, right and center", validators: [inList: ["left", "right", "center"]]]
                ],
                childrenConfiguration: [:]
        ];
        return metaData;
    }

    protected void populateStringAttributes(GPathResult node, UiElmnt parent) {
        super.populateStringAttributes(node, parent);
        if (parent instanceof UiComponent)
        {
            attributesAsString.componentId = parent._designerKey
        }
        else
        {
            attributesAsString.columnId = parent._designerKey
        }
    }

    protected void addChildElements(GPathResult node, UiElmnt parent) {}

}