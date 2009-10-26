package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.UiElmnt
import groovy.util.slurpersupport.GPathResult
import com.ifountain.rui.designer.DesignerSpace

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 22, 2009
* Time: 1:54:27 PM
*/
class UiRequestParameter extends UiElmnt {
    String key = "";
    String value = "";
    String actionId = "";

    public static Map metaData()
    {
        Map metaData = [
                help: "RequestParameter.html",
                designerType: "RequestParameter",
                canBeDeleted: true,
                displayFromProperty: "key",
                propertyConfiguration: [
                        actionId: [validators: [key: true], isVisible: false],
                        key: [descr: "The URL parameter name", required: true, validators: [key: true]],
                        value: [descr: "The JavaScript expression that will be evaluated to determine the value of the URL parameter", required: true, type: "Expression"]
                ],
                childrenConfiguration: []
        ];
        return metaData;
    }
    public static UiElmnt addUiElement(GPathResult xmlNode, UiElmnt parentElement)
    {
        def attributes = [:];
        attributes.putAll(xmlNode.attributes());
        attributes.actionId = parentElement._designerKey;
        return DesignerSpace.getInstance().addUiElement(UiRequestParameter, attributes)
    }

}