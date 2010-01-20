package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.UiElmnt
import groovy.util.slurpersupport.GPathResult

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 22, 2009
* Time: 5:00:39 PM
*/
class UiSearchListField extends UiElmnt{
    String fields = "";
    String exp = "true";
    String componentId = "";

    public static Map metaData()
    {
        Map metaData = [
                help: "SearchList Field.html",
                designerType: "SearchListField",
                canBeDeleted: true,
                display: "Field",
                imageExpanded: "images/rapidjs/designer/textfield.png",
                imageCollapsed: "images/rapidjs/designer/textfield.png",
                propertyConfiguration: [
                        componentId: [isVisible: false, validators: [blank: false, nullable: false]],
                        fields: [descr: "Property list that will be displayed if the expression evaluates to true", validators: [blank: false, nullable: false]],
                        exp: [descr: "The JavaScript expression evaluated on row data to determine whether the property list is valid or not", type: "Expression", validators: [blank: false, nullable: false]]
                ]
        ];
        return metaData;
    }

    protected void populateStringAttributes(GPathResult node, UiElmnt parent) {
        super.populateStringAttributes(node, parent);
        attributesAsString["componentId"] = parent._designerKey;
    }

    protected void addChildElements(GPathResult node, UiElmnt parent) {}

}