package ui.designer

import com.ifountain.rui.util.DesignerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 2, 2009
* Time: 2:09:19 PM
* To change this template use File | Settings | File Templates.
*/
class UiFunctionActionOperations extends UiActionOperations {
    public static Map metaData()
    {
        Map metaData = [
                designerType: "FunctionAction",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/layout_content.png",
                imageCollapsed: "images/rapidjs/designer/layout_content.png",
                propertyConfiguration: [
                        component: [descr: "The name of the component", formater:{object-> return object.component.name}],
                        function: [descr: "The method of the component that will called."],
                ],
                childrenConfiguration: [
                        [designerType: "FunctionArgument", isMultiple: true, propertyName: "arguments"]
                ]
        ];
        def parentMetaData = UiActionOperations.metaData();
        metaData.propertyConfiguration.putAll(parentMetaData.propertyConfiguration);
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;

    }

    def static addUiElement(xmlNode, parentElement)
    {
        def attributes = xmlNode.attributes();
        attributes.tab = parentElement;
        def component = UiComponent.get(tab:parentElement, name:attributes.component, isActive:true);
        attributes.component = component;
        def addedAction = DesignerUtils.addUiObject(UiFunctionAction, attributes, xmlNode);
        def functionArgumentNodes = xmlNode.UiElement.findAll {it.@designerType.text() == "FunctionArgument"}
        def functionArguments = [];
        functionArgumentNodes.each{
            UiFunctionArgument.addUiElement(it, addedAction);
        }
        addTriggers(xmlNode, addedAction);
        return addedAction;
    }
}