package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.DesignerSpace
import com.ifountain.rui.designer.UiElmnt
import com.ifountain.rui.util.DesignerTemplateUtils

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 20, 2009
* Time: 6:32:11 PM
*/
class UiComponent extends UiElmnt {

    String name = "";
    String title = "";
    String tabId = "";

    public static Map metaData()
    {
        Map metaData = [
                propertyConfiguration:
                [
                        tabId: [isVisible: false, validators: [key: true]],
                        name: [descr: "Name of component", validators: [key: true, matches: "[a-z_A-z]\\w*"]],
                        title: [descr: "Title of component", required: true]
                ],
                childrenConfiguration: []
        ];
        return metaData;
    }

    public List getTriggers() {
        return DesignerSpace.getInstance().getUiElements(UiActionTrigger).values().findAll {it.componentId == _designerKey};
    }

    public List getMenuItems() {
        return DesignerSpace.getInstance().getUiElements(UiMenuItem).values().findAll {it.componentId == _designerKey};
    }
    public Map getActionTrigers()
    {
        def actionTriggers = [:];
        getTriggers().each {UiActionTrigger actionTrigger ->
            if (actionTrigger.type == UiActionTrigger.COMPONENT_TYPE)
            {
                def triggerArray = actionTriggers.get(actionTrigger.event);
                if (triggerArray == null) {
                    triggerArray = [];
                    actionTriggers.put(actionTrigger.event, triggerArray)
                }
                triggerArray.add(actionTrigger)
            }
        }
        return actionTriggers;
    }

    public String getActionsString(List actionTriggers){
        return DesignerTemplateUtils.getActionsString(actionTriggers);
    }
}