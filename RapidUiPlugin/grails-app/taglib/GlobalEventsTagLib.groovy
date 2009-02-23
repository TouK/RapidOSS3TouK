/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 23, 2009
 * Time: 9:17:11 AM
 * To change this template use File | Settings | File Templates.
 */
class GlobalEventsTagLib {
    static namespace = "rui"

    def globalEvent = {attrs, body->
        def availableEvents = ["onDOMReady", "onErrorOccurred", "onServerDown", "onServerUp"]
        availableEvents.each{eventName->
            def actions = attrs[eventName];
            if (actions != null) {
                out << render(template:"/tagLibTemplates/globalEvents/${eventName}", model:[actions:getActionsArray(actions)]);
            }
        }
    }

    static def getActionsArray(actionAttribute) {
        def actions = [];
        if (actionAttribute instanceof List) {
            actions.addAll(actionAttribute);
        }
        else {
            actions.add(actionAttribute);
        }
        return actions;
    }
}