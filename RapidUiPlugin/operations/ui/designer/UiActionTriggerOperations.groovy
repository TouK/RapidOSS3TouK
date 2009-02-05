package ui.designer

import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import com.ifountain.rui.util.DesignerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 2, 2009
* Time: 2:04:38 PM
* To change this template use File | Settings | File Templates.
*/
class UiActionTriggerOperations extends AbstractDomainOperation{
    public static Map metaData()
    {
        Map metaData = [
                designerType:"ActionTrigger",
                canBeDeleted: true,
                display: "ActionTrigger",
                propertyConfiguration: [
                        name:[descr:""],
                        component:[descr:""],
                        isMenuItem:[descr:""]
                ],
                childrenConfiguration:[]
        ];
        return metaData;
    }

    def static addUiElement(xmlNode, parentElement)
    {
        def attributes = xmlNode.attributes();
        def component = null;
        if(attributes.component != "")
        {
            component = UiComponent.get(tab:parentElement.tab, name:attributes.component, isActive:true);
        }
        def isMenuItem = attributes.isMenuItem == "true";
        attributes.action = parentElement;
        attributes.component = component;
        attributes.isMenuItem = isMenuItem;
        return DesignerUtils.addUiObject(UiActionTrigger, attributes, xmlNode);
    }
}