package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.UiElmnt
import groovy.util.slurpersupport.GPathResult
import com.ifountain.rui.designer.DesignerSpace

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

    public static UiElmnt addUiElement(GPathResult xmlNode, UiElmnt parentElement)
    {
        def attributes = xmlNode.attributes();
        if (parentElement instanceof UiComponent)
        {
            attributes.componentId = parentElement._designerKey
        }
        else
        {
            attributes.columnId = parentElement._designerKey
        }
        return DesignerSpace.getInstance().addUiElement(UiImage, attributes);
    }
}