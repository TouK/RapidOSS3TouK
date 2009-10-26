<%
    def components = uiElement.components.split(",").toList().findAll{it.trim() != ""}.collect{"'${it}'"};
    def compNameString = components.join(",")
    compNameString = "\${["+compNameString+"]}";
    def requestActionConditionPropertyName = "requestActionCondition"+uiElement._designerKey+ "Condition";
    println com.ifountain.rui.util.DesignerTemplateUtils.declareVariable(requestActionConditionPropertyName, uiElement.condition, true);
%>
<rui:action id="${uiElement.name}" type="request" url="../${uiElement.url}" components="${compNameString}" submitType="${uiElement.submitType}" ${uiElement.condition != ""?"condition=\"\$"+requestActionConditionPropertyName+"\"":""}
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
            def parameterVisiblePropertyName = "parameter"+parameter._designerKey+ "Visible";
            println com.ifountain.rui.util.DesignerTemplateUtils.declareVariable(parameterVisiblePropertyName, parameter.value, true);
    %>
    <rui:requestParam key="${parameter.key}" value="\${${parameterVisiblePropertyName}}"></rui:requestParam>
    <%
        }
    %>
</rui:action>