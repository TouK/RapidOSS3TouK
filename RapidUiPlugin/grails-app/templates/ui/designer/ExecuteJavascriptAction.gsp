<%
    def jsActionConditionPropertyName = "jsActionCondition"+uiElement._designerKey+ "Condition";
    println com.ifountain.rui.util.DesignerTemplateUtils.declareVariable(jsActionConditionPropertyName, uiElement.condition, true);
%>
<rui:action id="${uiElement.name}" type="javascript"  ${uiElement.condition != ""?"condition=\"\$"+functionActionConditionPropertyName+"\"":""}
<%
    uiElement.getSubscribedTriggers().each{eventName, actionTriggers->
         def actionString = uiElement.getSubscribedActionsString(actionTriggers);
    %>
        on${eventName.substring(0,1).toUpperCase()}${eventName.substring(1)}="${actionString}"
    <%
    }
    %>
>
    <rui:actionJavascript><![CDATA[${uiElement.javascript}]]></rui:actionJavascript>

</rui:action>