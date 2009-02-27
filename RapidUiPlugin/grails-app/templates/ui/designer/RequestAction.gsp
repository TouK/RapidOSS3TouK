<%
    def compNameString = "'"+uiElement.components.name.join("','")+"'";
    if(uiElement.components.isEmpty())
    {
        compNameString = "";
    }
    compNameString = "\${["+compNameString+"]}";
    def requestActionConditionPropertyName = "requestActionCondition"+uiElement.id+ "Condition";
    println com.ifountain.rui.util.DesignerTemplateUtils.declareVariable(requestActionConditionPropertyName, uiElement.condition, true);
%>
<rui:action id="${uiElement.name}" type="request" url="../${uiElement.url}" components="${compNameString}" ${uiElement.condition != ""?"condition=\""+requestActionConditionPropertyName+"\"":""}
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
        uiElement.parameters.each{parameter->
            def parameterVisiblePropertyName = "parameter"+parameter.id+ "Visible";
            println com.ifountain.rui.util.DesignerTemplateUtils.declareVariable(parameterVisiblePropertyName, parameter.value, true);
    %>
    <rui:requestParam key="${parameter.key}" value="\${${parameterVisiblePropertyName}}"></rui:requestParam>
    <%
        }
    %>
</rui:action>