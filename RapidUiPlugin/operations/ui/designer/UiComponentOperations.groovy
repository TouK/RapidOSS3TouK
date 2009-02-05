package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 30, 2009
 * Time: 8:54:45 AM
 * To change this template use File | Settings | File Templates.
 */
class UiComponentOperations extends UiLayoutUnitOperations{
    public static Map metaData()
    {
        Map metaData = [
                propertyConfiguration:
                [
                    name:[descr:"Name of component"],
                    title:[descr:"Title of component"]
                ],
                childrenConfiguration:[]
        ];
        return metaData;
    }

    def getActionTrigers()
    {
        def triggers = [];
        ui.designer.UiActionTrigger.list().each{actionTrigger->
            if(actionTrigger.component != null && actionTrigger.component.name == name && !actionTrigger.isMenuItem)
            {
                triggers.add(actionTrigger)
            }
        }
        return triggers;
    }
}