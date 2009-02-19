package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 30, 2009
 * Time: 8:54:45 AM
 * To change this template use File | Settings | File Templates.
 */
class UiComponentOperations extends UiLayoutUnitOperations {
    public static Map metaData()
    {
        Map metaData = [
                propertyConfiguration:
                [
                        name: [descr: "Name of component"],
                        title: [descr: "Title of component", required: true]
                ],
                childrenConfiguration: []
        ];
        return metaData;
    }

    def getActionTrigers()
    {
        def actionTriggers = [:];
        triggers.each {UiActionTrigger actionTrigger ->
            if (actionTrigger.type == UiActionTrigger.COMPONENT_TYPE)
            {
                def triggerArray = actionTriggers.get(actionTrigger.name);
                if (triggerArray == null) {
                    triggerArray = [];
                    actionTriggers.put(actionTrigger.name, triggerArray)
                }
                triggerArray.add(actionTrigger)
            }
        }
        return actionTriggers;
    }

    def getActionsString(actionTriggers){
        def actionNames = actionTriggers.action.name;
        def actionsString;
        if(actionNames.size() > 0){
            return "\${['" + actionNames.join("','") + "']}"
        }
        else{
            return "\${[]}"
        }
        return actionsString
    }
}