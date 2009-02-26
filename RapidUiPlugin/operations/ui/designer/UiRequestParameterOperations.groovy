package ui.designer

import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import com.ifountain.rui.util.DesignerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 2, 2009
* Time: 2:30:38 PM
* To change this template use File | Settings | File Templates.
*/
class UiRequestParameterOperations extends AbstractDomainOperation{
     public static Map metaData()
    {
        Map metaData = [
                help:"RequestAction.html",
                designerType:"RequestParameter",
                canBeDeleted: true,
                displayFromProperty: "key",
                propertyConfiguration: [
                        key:[descr:"The URL parameter name", required:true],
                        value:[descr:"The JavaScript expression that will be evaluated to determine the value of the URL parameter", required:true, type:"Expression"]
                ],
                childrenConfiguration:[]
        ];
        return metaData;
    }
    def static addUiElement(xmlNode, parentElement)
    {
        def attributes = [:];
        attributes.putAll (xmlNode.attributes());
        attributes.action = parentElement;
        attributes.actionId = parentElement.id;
        return DesignerUtils.addUiObject(UiRequestParameter, attributes, xmlNode);
    }
}