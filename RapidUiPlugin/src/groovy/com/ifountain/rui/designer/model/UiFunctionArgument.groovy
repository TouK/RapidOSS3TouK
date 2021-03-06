package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.UiElmnt
import groovy.util.slurpersupport.GPathResult

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 22, 2009
* Time: 2:25:35 PM
*/
class UiFunctionArgument extends UiElmnt {

    String value = "";
    String actionId = "";

    public static Map metaData()
    {
        Map metaData = [
                help: "FunctionAction.html",
                designerType: "FunctionArgument",
                canBeDeleted: true,
                display: "FunctionArgument",
                propertyConfiguration: [
                        actionId: [isVisible: false, validators: [blank: false, nullable: false]],
                        value: [descr: "JavaScript expression evaluated and passed to the function as argument", isRequired: true, type: "Expression"]
                ],
                childrenConfiguration: []
        ];
        return metaData;
    }

    protected void populateStringAttributes(GPathResult node, UiElmnt parent) {
        super.populateStringAttributes(node, parent);
        attributesAsString["actionId"] = parent._designerKey;
    }

    protected void addChildElements(GPathResult node, UiElmnt parent) {}
}