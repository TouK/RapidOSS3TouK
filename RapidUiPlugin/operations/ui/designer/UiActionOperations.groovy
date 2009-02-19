package ui.designer

import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation

public class UiActionOperations extends AbstractDomainOperation
{

    public static Map metaData()
    {
        Map metaData = [
                propertyConfiguration: [
                        name: [descr: "Unique name of the action"],
                        condition: [descr: "The JavaScript expression that will be evaluated to determine whether to execute the action or not.", required: true, type: "Expression"]
                ],
                childrenConfiguration: [
                        [
                                designerType: "ActionTriggers",
                                metaData: [
                                        designerType: "ActionTriggers",
                                        display: "Triggers",
                                        canBeDeleted: false,
                                        propertyConfiguration: [
                                        ],
                                        childrenConfiguration: [
                                                [designerType: "ActionTrigger", isMultiple: true, propertyName: "triggers"]
                                        ]
                                ],
                                isMultiple: false
                        ]
                ]
        ];
        return metaData;
    }

    def static void addTriggers(xmlNode, addedAction)
    {
        def triggersNode = xmlNode.UiElement.find {it.@designerType.text() == "ActionTriggers"}
        triggersNode.UiElement.each {
            UiActionTrigger.addUiElement(it, addedAction);
        }
    }

    def getSubscribedTriggers() {
        def subscribedTriggers = [:];
        subscribedEvents.each {UiActionTrigger actionTrigger ->
            def triggerArray = subscribedTriggers.get(actionTrigger.name);
            if (triggerArray == null) {
                triggerArray = [];
                subscribedTriggers.put(actionTrigger.name, triggerArray)
            }
            triggerArray.add(actionTrigger)
        }
        return subscribedTriggers;
    }
    def getSubscribedActionsString(actionTriggers) {
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