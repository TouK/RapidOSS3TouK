package ui.designer

import com.ifountain.rui.util.DesignerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 30, 2009
* Time: 8:59:57 AM
* To change this template use File | Settings | File Templates.
*/
class UiDialogOperations extends UiLayoutUnitOperations{
    public static Map metaData()
    {
        Map metaData = [
                designerType:"Dialog",
                imageExpanded:"images/rapidjs/designer/application_double.png",
                imageCollapsed:"images/rapidjs/designer/application_double.png",
                displayFromProperty:"component",
                canBeDeleted:true,
                propertyConfiguration:
                [
                    title:[descr:"Title of component", required:true],
                    width:[descr:"The width of the dialog"],
                    x:[descr:"The x position of the dialog"],
                    y:[descr:"The y position of the dialog"],
                    height:[descr:"The height of the dialog"],
                    maxHeight:[descr:"Maximum height of the dialog"],
                    minHeight:[descr:"Minimum height of the dialog"],
                    minWidth:[descr:"Minimum width of the dialog"],
                    maxWidth:[descr:"Maximum width of the dialog"],
                    resizable:[descr:"Boolean value to represent whether the dialog is resizable or not."],
                    component:[descr:"RapidInsight component that will be displayed as pop up dialog", formatter:{object-> return object.component?object.component.name:""}]
                ],
                childrenConfiguration:[]
        ];
        return metaData;
    }

    def static addUiElement(xmlNode, parentElement)
    {
        def attributes = [:];
        attributes.putAll (xmlNode.attributes());
        def componentName = xmlNode.@component;
        UiComponent comp = UiComponent.get(name:componentName, tab:parentElement, isActive:true);
        attributes["component"] = comp
        attributes["tab"] = parentElement;
        return DesignerUtils.addUiObject(UiDialog, attributes, xmlNode);
    }
}