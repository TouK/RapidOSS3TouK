<rui:action id="${uiElement.name}" type="function" function="${uiElement.function}" ${uiElement.component != null?"componentId='"+uiElement.component.name+"'":""} ${uiElement.condition != ""?"condition='"+uiElement.condition+"'":""}
<%
    uiElement.getSubscribedTriggers().each{eventName, actionTriggers->
         def actionString = uiElement.getSubscribedActionsString(actionTriggers);
    %>
        on${eventName.substring(0,1).toUpperCase()}${eventName.substring(1)}="${actionString}"
    <%
    }
    %>
>
    <%
        uiElement.arguments.each {functionArgument ->
            if (functionArgument.value == null || functionArgument.value == "")
            {
    %>
    <rui:functionArg>null</rui:functionArg>
    <%
        }
        else {
    %>
    <rui:functionArg><![CDATA[${functionArgument.value}]]></rui:functionArg>
    <%
            }
        }
    %>
</rui:action>