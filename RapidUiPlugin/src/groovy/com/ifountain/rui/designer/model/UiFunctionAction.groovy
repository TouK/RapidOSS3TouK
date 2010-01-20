package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.DesignerSpace
import com.ifountain.rui.designer.UiElmnt
import groovy.util.slurpersupport.GPathResult

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 22, 2009
* Time: 2:21:22 PM
*/
class UiFunctionAction extends UiAction {
    String function = "";
    String component = "";

    public static Map metaData()
    {
        Map metaData = [
                help: "FunctionAction.html",
                designerType: "FunctionAction",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/javascript.gif",
                imageCollapsed: "images/rapidjs/designer/javascript.gif",
                propertyConfiguration: [
                        component: [descr: "The name of the component", validators: [blank: false, nullable: false]],
                        function: [descr: "The method of the component that will called.", validators: [blank: false, nullable: false]],
                ],
                childrenConfiguration: [
                        [
                                designerType: "FunctionArguments",
                                metaData: [
                                        help: "FunctionAction.html",
                                        designerType: "FunctionArguments",
                                        display: "Arguments",
                                        canBeDeleted: false,
                                        imageExpanded: "images/rapidjs/designer/bookmark_folder.png",
                                        imageCollapsed: "images/rapidjs/designer/bookmark_folder.png",
                                        propertyConfiguration: [
                                                designerHidden: [descr: "", type: "String", name: "designerHidden"]
                                        ],
                                        childrenConfiguration: [
                                                [designerType: "FunctionArgument", isMultiple: true]
                                        ]
                                ],
                                isMultiple: false
                        ]
                ]
        ];
        def parentMetaData = UiAction.metaData();
        def propConfig = [:]
        propConfig.put("tabId", parentMetaData.propertyConfiguration.remove("tabId"))
        propConfig.put("name", parentMetaData.propertyConfiguration.remove("name"))
        propConfig.putAll(metaData.propertyConfiguration)
        propConfig.putAll(parentMetaData.propertyConfiguration)
        metaData.propertyConfiguration = propConfig;
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;
    }

    protected void populateStringAttributes(GPathResult node, UiElmnt parent) {
        super.populateStringAttributes(node, parent);
        UiComponent comp = (UiComponent)DesignerSpace.getInstance().getUiElement(UiComponent, "${parent._designerKey}_${node.@component}")
        if (!comp) {
            throw new Exception("Component <${node.@component}> cannot be found for function action ${node.@name}");
        }
    }

    protected void addChildElements(GPathResult node, UiElmnt parent) {
        super.addChildElements(node, parent);
        def functionArgumentsNode = node."${UIELEMENT_TAG}".find {it.@"${DESIGNER_TYPE}".text() == "FunctionArguments"}
        removeUnneccessaryAttributes(functionArgumentsNode)
        functionArgumentsNode."${UIELEMENT_TAG}".each {
            create(it, this)
        }
    }

    public List getArguments() {
        return DesignerSpace.getInstance().getUiElements(UiFunctionArgument).values().findAll {it.actionId == _designerKey};
    }
}