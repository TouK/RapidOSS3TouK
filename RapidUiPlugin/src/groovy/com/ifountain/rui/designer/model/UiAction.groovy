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
    protected void populateStringAttributes(GPathResult node, UiElmnt parent) {
        super.populateStringAttributes(node, parent);
        attributesAsString["tabId"] = parent._designerKey;
    }

    protected void addChildElements(GPathResult node, UiElmnt parent) {
        addTriggers(node);
    }

    protected void addTriggers(GPathResult xmlNode)
    {
        def triggersNode = xmlNode."${UIELEMENT_TAG}".find {it.@"${DESIGNER_TYPE}".text() == "ActionTriggers"}
        removeUnneccessaryAttributes(triggersNode);
        triggersNode."${UIELEMENT_TAG}".each {
            create(it, this)
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