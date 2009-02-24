package ui.designer

import com.ifountain.rui.util.DesignerUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.ifountain.rui.util.exception.UiElementCreationException

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 2, 2009
* Time: 2:09:19 PM
* To change this template use File | Settings | File Templates.
*/
class UiRequestActionOperations extends UiActionOperations {
    public static Map metaData()
    {
        Map metaData = [
                help:"RequestAction.html",
                designerType: "RequestAction",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/connect_creating.png",
                imageCollapsed: "images/rapidjs/designer/connect_creating.png",
                propertyConfiguration: [
                        url: [descr: "The URL to be used for requests to the server"],
                        timeout: [descr: "The time interval in seconds to wait the server request completes successfully before aborting.", required: true],
                        components: [descr: "The list of component names which are related with this action (For example which components' error dialog should show if an error occurred during request)", required: true, formatter: {object -> return object.components ? object.components.name.join(",") : ""}]
                ],
                childrenConfiguration: [
                        [
                                designerType: "RequestParameters",
                                metaData: [
                                        help:"RequestAction.html",
                                        designerType: "RequestParameters",
                                        display: "RequestParameters",
                                        canBeDeleted: false,
                                        imageExpanded: "images/rapidjs/designer/bookmark_folder.png",
                                        imageCollapsed: "images/rapidjs/designer/bookmark_folder.png",
                                        propertyConfiguration: [
                                        ],
                                        childrenConfiguration: [
                                                [designerType: "RequestParameter", isMultiple: true, propertyName: "triggers"]
                                        ]
                                ],
                                isMultiple: false
                        ]
                ]
        ];
        def parentMetaData = UiActionOperations.metaData();
        def childConfiguration = [];
        childConfiguration.addAll(parentMetaData.childrenConfiguration)
        childConfiguration.addAll(metaData.childrenConfiguration)
        metaData.propertyConfiguration.putAll(parentMetaData.propertyConfiguration);
        metaData.childrenConfiguration = childConfiguration;
        return metaData;

    }

    def static addUiElement(xmlNode, parentElement)
    {
        return _addUiElement(UiRequestAction, xmlNode, parentElement);
    }

    def static _addUiElement(Class classToBeAdded, xmlNode, parentElement)
    {
        def attributes = [:];
        attributes.putAll(xmlNode.attributes());
        attributes.tab = parentElement;
        def comps = [];
        if (attributes.components != null && attributes.components != "")
        {
            attributes.components.split(",").each {
                if (it != "")
                {
                    def comp = UiComponent.get(name: it, tab: parentElement, isActive: true);
                    if (comp)
                    {
                        comps.add(comp);
                    }
                    else
                    {
                        throw new UiElementCreationException(UiCombinedAction, "Component ${it} could not found for request action ${attributes.name}".toString());
                    }
                }
            }
        }
        attributes.components = comps;
        def addedAction = DesignerUtils.addUiObject(classToBeAdded, attributes, xmlNode);
        def requestParamsNode = xmlNode.UiElement.find {it.@designerType.text() == "RequestParameters"}
        requestParamsNode.UiElement.each {
            UiRequestParameter.addUiElement(it, addedAction);
        }
        addTriggers(xmlNode, addedAction);
        return addedAction;
    }
}