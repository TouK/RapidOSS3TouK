package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.UiElmnt
import groovy.util.slurpersupport.GPathResult
import com.ifountain.rui.designer.DesignerSpace

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 21, 2009
* Time: 5:53:56 PM
*/
class UiDialog extends UiElmnt {
    Long width = 400;
    Long height = 300;
    Long maxHeight = 0;
    Long minHeight = 0;
    Long minWidth = 0;
    Long maxWidth = 0;
    Boolean resizable = true;
    Long x = 0;
    Long y = 0;
    String title = "";

    String componentId;
    String tabId;

    public static Map metaData()
    {
        Map metaData = [
                help: "Dialog.html",
                designerType: "Dialog",
                imageExpanded: "images/rapidjs/designer/application_double.png",
                imageCollapsed: "images/rapidjs/designer/application_double.png",
                displayFromProperty: "component",
                canBeDeleted: true,
                propertyConfiguration:
                [
                        component: [descr: "RapidInsight component that will be displayed as pop up dialog", required: true],
                        title: [descr: "Title of component", required: true],
                        width: [descr: "The width of the dialog", validators: [nullable: false]],
                        x: [descr: "The x position of the dialog"],
                        y: [descr: "The y position of the dialog"],
                        height: [descr: "The height of the dialog", validators: [nullable: false]],
                        maxHeight: [descr: "Maximum height of the dialog"],
                        minHeight: [descr: "Minimum height of the dialog"],
                        minWidth: [descr: "Minimum width of the dialog"],
                        maxWidth: [descr: "Maximum width of the dialog"],
                        resizable: [descr: "Boolean value to represent whether the dialog is resizable or not."],
                        componentId: [validators: [key: true], isVisible: false]
                ],
                childrenConfiguration: []
        ];
        return metaData;
    }

    protected void populateStringAttributes(GPathResult node, UiElmnt parent) {
        super.populateStringAttributes(node, parent);
        def componentName = node.@component;
        UiComponent comp = (UiComponent) DesignerSpace.getInstance().getUiElement(UiComponent, "${parent._designerKey}_${componentName}")
        if (comp) {
            attributesAsString["componentId"] = comp._designerKey;
        }
        attributesAsString["tabId"] = parent._designerKey;
    }

    protected List getExtraPropsNeededInXml() {
        def props = super.getExtraPropsNeededInXml();
        props.addAll(["component"])
        return props;
    }

    protected void addChildElements(GPathResult node, UiElmnt parent) {}

    public UiComponent getComponent() {
        def components = DesignerSpace.getInstance().getUiElements(UiComponent).values().findAll {it._designerKey == componentId};
        if (components.size() > 0) {
            return components[0]
        }
        return null;
    }
}