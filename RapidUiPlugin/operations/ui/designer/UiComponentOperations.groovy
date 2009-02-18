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
        def triggers = [:];
        ui.designer.UiActionTrigger.list().each {actionTrigger ->
            if (actionTrigger.component != null && actionTrigger.component.name == name && !actionTrigger.isMenuItem)
            {
                def triggerArray = triggers.get(actionTrigger.name);
                if (triggerArray == null) {
                    triggerArray = [];
                    triggers.put(actionTrigger.name, triggerArray)
                }
                triggerArray.add(actionTrigger)
            }
        }
        return triggers;
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