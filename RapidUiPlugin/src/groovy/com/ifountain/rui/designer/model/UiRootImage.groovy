package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.UiElmnt
import groovy.util.slurpersupport.GPathResult
import com.ifountain.rui.designer.DesignerSpace

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 22, 2009
* Time: 4:30:51 PM
*/
class UiRootImage extends UiElmnt {
    String expanded = "";
    String collapsed = "";
    String visible = "true";
    String componentId = ""

    public static Map metaData()
    {
        Map metaData = [
                help: "TreeGrid RootImage.html",
                designerType: "RootImage",
                canBeDeleted: true,
                display: "RootImage",
                imageExpanded: "images/rapidjs/designer/image.png",
                imageCollapsed: "images/rapidjs/designer/image.png",
                propertyConfiguration: [
                        expanded: [descr: "The image url which will be shown when the row is expanded", validators: [blank: false, nullable: false]],
                        collapsed: [descr: "The image url which will be shown when the row is collapsed", validators: [blank: false, nullable: false]],
                        visible: [descr: "The JavaScript expression evaluated on row data to determine whether the image is displayed or not", required: true, type: "Expression"]
                ],
                childrenConfiguration: [:]
        ];
        return metaData;
    }

    public static UiElmnt addUiElement(GPathResult xmlNode, UiElmnt parentElement)
    {
        def attributes = xmlNode.attributes();
        attributes.componentId = parentElement._designerKey
        return DesignerSpace.getInstance().addUiElement(UiRootImage, attributes);
    }

}