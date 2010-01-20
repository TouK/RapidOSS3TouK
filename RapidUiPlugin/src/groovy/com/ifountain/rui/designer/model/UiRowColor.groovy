package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.UiElmnt
import groovy.util.slurpersupport.GPathResult

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 22, 2009
* Time: 4:00:45 PM
*/
class UiRowColor extends UiElmnt {
    String color = "#ffffff";
    String textColor = "#000000";
    String visible = "true";
    String gridId = ""

    public static Map metaData()
    {
        Map metaData = [
                help: "SearchGrid RowColor.html",
                designerType: "RowColor",
                canBeDeleted: true,
                display: "RowColor",
                imageExpanded: "images/rapidjs/designer/color_wheel.png",
                imageCollapsed: "images/rapidjs/designer/color_wheel.png",
                propertyConfiguration: [
                        gridId: [isVisible: false, validators: [blank: false, nullable: false]],
                        color: [descr: "Row background color.", validators: [blank: false, nullable: false]],
                        visible: [descr: "The JavaScript expression evaluated on row data to determine whether the color is applied or not.", type: "Expression", validators: [blank: false, nullable: false]],
                        textColor: [descr: "Cell text color."]
                ],
                childrenConfiguration: [:]
        ];
        return metaData;
    }

    protected void populateStringAttributes(GPathResult node, UiElmnt parent) {
        super.populateStringAttributes(node, parent);
        attributesAsString["gridId"] = parent._designerKey;
    }

    protected void addChildElements(GPathResult node, UiElmnt parent) {}
    
}