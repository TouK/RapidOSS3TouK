package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.UiElmnt
import groovy.util.slurpersupport.GPathResult
import com.ifountain.rui.designer.DesignerSpace

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 22, 2009
* Time: 4:10:57 PM
*/
class UiColumn extends UiElmnt {
    String attributeName = "";
    String colLabel = "";
    Long width = 100;
    Long columnIndex = 0;
    Boolean sortBy = false;
    String sortOrder = "asc";
    String componentId = "";

    public static Map metaData()
    {
        Map metaData = [
                help: "Column.html",
                designerType: "Column",
                canBeDeleted: true,
                displayFromProperty: "attributeName",

                propertyConfiguration: [
                        componentId: [isVisible: false, validators: [key: true]],
                        attributeName: [descr: "The data node attribute which will be shown in the column", validators: [key: true]],
                        colLabel: [descr: "Title of the column", validators: [nullable: false]],
                        width: [descr: "Width of the column", validators:[nullable:false]],
                        sortBy: [descr: "Parameter to render component whether sorted according to this column or not"],
                        sortOrder: [descr: "The order of the sort when \"sortBy\" property is \"true\"", required: false, validators: [inList: ["asc", "desc"]]],
                        columnIndex: [descr: "The order of the column in the grid.", validators:[nullable:false]]

                ],
                childrenConfiguration: []
        ];
        return metaData;
    }

    public static UiElmnt addUiElement(GPathResult xmlNode, UiElmnt parentElement)
    {
        def attributes = xmlNode.attributes();
        attributes.componentId = parentElement._designerKey;
        return DesignerSpace.getInstance().addUiElement(UiColumn, attributes);
    }
}