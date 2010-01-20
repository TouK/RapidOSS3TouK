package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.UiElmnt
import groovy.util.slurpersupport.GPathResult
import com.ifountain.rui.designer.DesignerSpace

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 21, 2009
* Time: 5:45:57 PM
*/
class UiActionTrigger extends UiElmnt {
    public static final String MENU_TYPE = "Menu"
    public static final String COMPONENT_TYPE = "Component event"
    public static final String ACTION_TYPE = "Action event"
    public static final String GLOBAL_TYPE = "Global event"

    String event = "";
    String type = COMPONENT_TYPE;
    String actionId = ""
    String triggeringActionId = ""
    String componentId = "";
    String menuId = "";

    public static Map metaData()
    {
        Map metaData = [
                help: "FunctionAction Trigger.html",
                designerType: "ActionTrigger",
                canBeDeleted: true,
                display: "Trigger",
                imageExpanded: "images/rapidjs/designer/lightning.png",
                imageCollapsed: "images/rapidjs/designer/lightning.png",
                propertyConfiguration: [
                        type: [descr: "The type of the triggering event", validators: [blank: false, nullable: false, inList: [MENU_TYPE, COMPONENT_TYPE, ACTION_TYPE, GLOBAL_TYPE]]],
                        component: [descr: ""],
                        triggeringAction: [descr: ""],
                        event: [descr: "", validators: [blank: false, nullable: false]],
                        actionId: [validators: [blank: false, nullable: false], isVisible: false]
                ],
                childrenConfiguration: []
        ];
        return metaData;
    }

    protected void addChildElements(GPathResult node, UiElmnt parent) {}

    protected void populateStringAttributes(GPathResult node, UiElmnt parent) {
        super.populateStringAttributes(node, parent);
        attributesAsString["actionId"] = parent._designerKey;
        def triggerType = node.@type.toString();
        if (triggerType == MENU_TYPE || triggerType == COMPONENT_TYPE) {
            UiComponent component = null;
            def compAtt = node.@component.toString();
            if (compAtt != "")
            {
                component = (UiComponent) DesignerSpace.getInstance().getUiElement(UiComponent, "${parent.tabId}_${compAtt}")
                attributesAsString["componentId"] = component._designerKey;
            }
            if (component == null) {
                throw new Exception("Property component cannot be null for ActionTrigger if type ${triggerType} with event ${node.@event}".toString())
            }
            if (triggerType == MENU_TYPE) {
                UiMenuItem menuItem = (UiMenuItem) DesignerSpace.getInstance().getUiElement(UiMenuItem, "${component._designerKey}_${node.@event}".toString())
                if (menuItem == null) {
                    throw new Exception("Property menu cannot be null for ActionTrigger if type ${triggerType} with event ${node.@event}".toString())
                }
                attributesAsString["menuId"] = menuItem._designerKey;
            }
        }
        else if (triggerType == ACTION_TYPE) {
            UiAction triggeringAction = null;
            def triggeringActionAtt = node.@triggeringAction.toString()
            if (triggeringActionAtt != "")
            {
                triggeringAction = (UiAction) DesignerSpace.getInstance().getUiElement(UiAction, "${parent.tabId}_${triggeringActionAtt}")
                attributesAsString["triggeringActionId"] = triggeringAction._designerKey;
            }
            if (triggeringAction == null) {
                throw new Exception("Property triggeringAction cannot be null for ActionTrigger if type ${triggerType} with event ${node.@event}")
            }
        }
    }


    public UiAction getAction() {
        def actions = DesignerSpace.getInstance().getUiElements(UiAction).values().findAll {it._designerKey == actionId};
        if (actions.size() > 0) {
            return actions[0]
        }
        return null;
    }

    protected List getExtraPropsNeededInXml() {
        def props = super.getExtraPropsNeededInXml();
        props.addAll(["component", "triggeringAction"])
        return props;
    }

}