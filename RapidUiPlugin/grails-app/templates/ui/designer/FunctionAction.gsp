<rui:action id="${uiElement.name.encodeAsHTML()}" type="function" function="${uiElement.function.encodeAsHTML()}" ${uiElement.component != null?"componentId='"+uiElement.component.name.encodeAsHTML()+"'":""} ${uiElement.condition != ""?"condition=\""+uiElement.condition.encodeAsHTML()+"\"":""}
<%
    uiElement.getSubscribedTriggers().each{eventName, actionTriggers->
         def actionString = uiElement.getSubscribedActionsString(actionTriggers);
    %>
        on${eventName.substring(0,1).toUpperCase().encodeAsHTML()}${eventName.substring(1).encodeAsHTML()}="${actionString}"
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