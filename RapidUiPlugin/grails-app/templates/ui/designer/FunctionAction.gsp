<%
    def functionActionConditionPropertyName = "functionActionCondition"+uiElement._designerKey+ "Condition";
    println com.ifountain.rui.util.DesignerTemplateUtils.declareVariable(functionActionConditionPropertyName, uiElement.condition, true);
%>
<rui:action id="${uiElement.name}" type="function" function="${uiElement.function}" componentId="${uiElement.component}" ${uiElement.condition != ""?"condition=\"\$"+functionActionConditionPropertyName+"\"":""}
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