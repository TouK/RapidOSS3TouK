<rui:action id="${uiElement.name.encodeAsHTML()}" type="link" url="${uiElement.url.encodeAsHTML()}" ${uiElement.condition != ""?"condition=\""+uiElement.condition.encodeAsHTML()+"\"":""}
<%
    uiElement.getSubscribedTriggers().each{eventName, actionTriggers->
         def actionString = uiElement.getSubscribedActionsString(actionTriggers);
    %>
        on${eventName.substring(0,1).toUpperCase().encodeAsHTML()}${eventName.substring(1).encodeAsHTML()}="${actionString}"
    <%
    }
    %>
>
</rui:action>