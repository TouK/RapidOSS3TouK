package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.UiElmnt
import groovy.util.slurpersupport.GPathResult
import com.ifountain.rui.designer.DesignerSpace

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 22, 2009
* Time: 5:31:41 PM
*/
class UiObjectMapContent extends UiElmnt {
    String name = "";
    Long x = 0;
    Long y = 0;
    Long width = 0;
    Long height = 0;
    String type = "text";
    String dataKey = "";
    String componentId = "";

    public static Map metaData()
    {
        Map metaData = [
                help: "ObjectMap NodeContent.html",
                designerType: "ObjectMapContent",
                canBeDeleted: true,
                display: "NodeContent",
                imageExpanded: "images/rapidjs/designer/report.png",
                imageCollapsed: "images/rapidjs/designer/report.png",
                propertyConfiguration: [
                        componentId:[isVisible:false, validators:[key:true]],
                        type:[isVisible:false, validators:[inList:["text", "image", "gauge"]]],
                        name: [descr: "The unique name of the node content configuration", validators:[key:true]],
                        dataKey: [descr: "The attribute in node data which the mapping will be applied according to", validators:[blank:false, nullable:false]],
                        x: [descr: "Sets how far the left edge of the image is to the left edge of the node", validators:[nullable:false]],
                        y: [descr: "Sets how far the top edge of the image is to the top edge of the node", validators:[nullable:false]],
                        width: [descr: "Width of the image", validators:[nullable:false]],
                        height: [descr: "Height of the image", validators:[nullable:false]]
                ],
                childrenConfiguration: []
        ];
        return metaData;
    }

    public static UiElmnt addUiElement(GPathResult xmlNode, UiElmnt parentElement)
    {
        def attributes = xmlNode.attributes();
        attributes.componentId = parentElement._designerKey
        return DesignerSpace.getInstance().addUiElement(UiObjectMapContent, attributes);
    }
}