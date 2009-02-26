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
                help:"FunctionAction.html",
                designerType: "FunctionAction",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/javascript.gif",
                imageCollapsed: "images/rapidjs/designer/javascript.gif",
                propertyConfiguration: [
                        component: [descr: "The name of the component", formatter: {object -> return object.component ? object.component.name : ""}],
                        function: [descr: "The method of the component that will called."],
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
                                            designerHidden: [descr: "", type: "String", formatter:{object-> return true;}, name:"designerHidden"]
                                        ],
                                        childrenConfiguration: [
                                                [designerType: "FunctionArgument", isMultiple: true, propertyName: "arguments"]
                                        ]
                                ],
                                isMultiple: false
                        ]
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
        attributes.tabId = parentElement.id;
        def component = UiComponent.get(tabId: parentElement.id, name: attributes.component, isActive: true);
        attributes.component = component;
        def addedAction = DesignerUtils.addUiObject(UiFunctionAction, attributes, xmlNode);
        def functionArgumentsNode = xmlNode.UiElement.find {it.@designerType.text() == "FunctionArguments"}
        functionArgumentsNode.UiElement.each {
            UiFunctionArgument.addUiElement(it, addedAction);
        }
        addTriggers(xmlNode, addedAction);
        return addedAction;
    }
}