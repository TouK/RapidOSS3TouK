package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.UiElmnt
import com.ifountain.rui.designer.DesignerSpace
import groovy.util.slurpersupport.GPathResult

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 20, 2009
* Time: 6:30:57 PM
*/
class UiAction extends UiElmnt {
    String name = "";
    String tabId;
    String condition = "";
    public static Map metaData()
    {
        Map metaData = [
                propertyConfiguration: [
                        tabId: [validators: [key: true], isVisible: false],
                        name: [descr: "Unique name of the action", validators: [key: true, matches: "[a-z_A-z]\\w*"]],
                        condition: [descr: "The JavaScript expression that will be evaluated to determine whether to execute the action or not.", required: true, type: "Expression"]
                ],
                childrenConfiguration: [
                        [
                                designerType: "ActionTriggers",
                                metaData: [
                                        designerType: "ActionTriggers",
                                        display: "Triggers",
                                        canBeDeleted: false,
                                        help: "FunctionAction Triggers.html",
                                        imageExpanded: "images/rapidjs/component/tools/folder_open.gif",
                                        imageCollapsed: "images/rapidjs/component/tools/folder_open.gif",
                                        propertyConfiguration: [
                                        ],
                                        childrenConfiguration: [
                                                [designerType: "ActionTrigger", isMultiple: true]
                                        ]
                                ],
                                isMultiple: false
                        ]
                ]
        ];
        return metaData;
    }
    public static void addTriggers(GPathResult xmlNode, UiElmnt addedAction)
    {
        def triggersNode = xmlNode.UiElement.find {it.@designerType.text() == "ActionTriggers"}
        triggersNode.UiElement.each {
            UiActionTrigger.addUiElement(it, addedAction);
        }
    }
    public List getTriggers() {
        return DesignerSpace.getInstance().getUiElements(UiActionTrigger).values().findAll {it.actionId == _designerKey};
    }
    public List getSubscribedEvents() {
        return DesignerSpace.getInstance().getUiElements(UiActionTrigger).values().findAll {it.triggeringActionId == _designerKey};
    }

    public UiTab getTab() {
        def tabs = DesignerSpace.getInstance().getUiElements(UiTab).values().findAll {it._designerKey == tabId};
        if (tabs.size() > 0) {
            return tabs[0]
        }
        return null;
    }
    public Map getSubscribedTriggers() {
        def subscribedTriggers = [:];
        getSubscribedEvents().each {UiActionTrigger actionTrigger ->
            def triggerArray = subscribedTriggers.get(actionTrigger.event);
            if (triggerArray == null) {
                triggerArray = [];
                subscribedTriggers.put(actionTrigger.event, triggerArray)
            }
            triggerArray.add(actionTrigger)
        }
        return subscribedTriggers;
    }
    public String getSubscribedActionsString(List actionTriggers) {
        def actionNames = actionTriggers.action.name;
        def actionsString;
        if (actionNames.size() > 0) {
            return "\${['" + actionNames.join("','") + "']}"
        }
        else {
            return "\${[]}"
        }
        return actionsString
    }
}