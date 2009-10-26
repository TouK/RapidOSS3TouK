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
class UiFunctionAction extends UiAction{
     String function = "";
     String component = "";

     public static Map metaData()
    {
        Map metaData = [
                help:"FunctionAction.html",
                designerType: "FunctionAction",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/javascript.gif",
                imageCollapsed: "images/rapidjs/designer/javascript.gif",
                propertyConfiguration: [
                        component: [descr: "The name of the component", validators:[blank:false, nullable:false]],
                        function: [descr: "The method of the component that will called.", validators:[blank:false, nullable:false]],
                ],
                childrenConfiguration: [
                        [
                                designerType: "FunctionArguments",
                                metaData: [
                                        help:"FunctionAction.html",
                                        designerType: "FunctionArguments",
                                        display: "Arguments",
                                        canBeDeleted: false,
                                        imageExpanded: "images/rapidjs/designer/bookmark_folder.png",
                                        imageCollapsed: "images/rapidjs/designer/bookmark_folder.png",
                                        propertyConfiguration: [
                                            designerHidden: [descr: "", type: "String", name:"designerHidden"]
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

     public static UiElmnt addUiElement(GPathResult xmlNode, UiElmnt parentElement)
    {
        def attributes = xmlNode.attributes();
        attributes.tabId = parentElement._designerKey;
        UiComponent component = DesignerSpace.getInstance().getUiElement(UiComponent, "${parentElement._designerKey}_${attributes.component}")
        if(!component){
            throw new Exception("Component <${attributes.component}> cannot be found for function action ${attributes.name}");
        }
        def addedAction = DesignerSpace.getInstance().addUiElement(UiFunctionAction, attributes)
        def functionArgumentsNode = xmlNode.UiElement.find {it.@designerType.text() == "FunctionArguments"}
        functionArgumentsNode.UiElement.each {
            UiFunctionArgument.addUiElement(it, addedAction);
        }
        addTriggers(xmlNode, addedAction);
        return addedAction;
    }

    public List getArguments(){
        return DesignerSpace.getInstance().getUiElements(UiFunctionArgument).values().findAll{it.actionId == _designerKey};
    }
}