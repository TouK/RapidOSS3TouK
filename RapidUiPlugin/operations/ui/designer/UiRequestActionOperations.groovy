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
                designerType: "RequestAction",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/layout_content.png",
                imageCollapsed: "images/rapidjs/designer/layout_content.png",
                propertyConfiguration: [
                        url: [descr: "The URL to be used for requests to the server"],
                        timeout:[descr: "The time interval in seconds to wait the server request completes successfully before aborting."],
                        components:[descr:"The list of component names which are related with this action (For example which components' error dialog should show if an error occurred during request)"]
                ],
                childrenConfiguration: [
                        [designerType: "RequestParameter", isMultiple: true, propertyName: "parameters"]
                ]
        ];
        def parentMetaData = UiActionOperations.metaData();
        metaData.propertyConfiguration.putAll(parentMetaData.propertyConfiguration);
        metaData.childrenConfiguration.addAll(parentMetaData.childrenConfiguration);
        return metaData;

    }

    def static addUiElement(xmlNode, parentElement)
    {
        return _addUiElement(UiRequestAction, xmlNode, parentElement);
    }

    def static _addUiElement(Class classToBeAdded, xmlNode, parentElement)
    {
        def attributes = [:];
        attributes.putAll (xmlNode.attributes());
        attributes.tab = parentElement;
        def comps = [];
        if(attributes.components != null && attributes.components != "")
        {
            attributes.components.split(",").each{
                if(it != "")
                {
                    def comp = UiComponent.get(name:it, tab:parentElement, isActive:true);
                    if(comp)
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
        addTriggers(xmlNode, addedAction);
        return addedAction;
    }
}