<rui:action id="${uiElement.name}" type="link" url="../${uiElement.url}" ${uiElement.condition != ""?"condition='"+uiElement.condition+"'":""}
<%
    uiElement.getSubscribedTriggers().each{eventName, actionTriggers->
         def actionString = uiElement.getSubscribedActionsString(actionTriggers);
    %>
        on${eventName.substring(0,1).toUpperCase()}${eventName.substring(1)}="${actionString}"
    <%
    }
    %>
>
</rui:action>