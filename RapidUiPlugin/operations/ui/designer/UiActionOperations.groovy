package ui.designer

import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation

public class UiActionOperations extends AbstractDomainOperation
{

    public static Map metaData()
    {
        Map metaData = [
                propertyConfiguration: [
                        name: [descr: "Unique name of the action"],
                        condition: [descr: "The JavaScript expression that will be evaluated to determine whether to execute the action or not.", required:true, type:"Expression"]
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
        triggersNode.UiElement.each{
            UiActionTrigger.addUiElement(it, addedAction);
        }
    }

}