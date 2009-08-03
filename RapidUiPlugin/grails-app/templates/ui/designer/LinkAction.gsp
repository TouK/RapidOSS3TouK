<%
    def urlVisiblePropertyName = "linkUrl"+uiElement.id+ "Visible";
    println com.ifountain.rui.util.DesignerTemplateUtils.declareVariable(urlVisiblePropertyName, uiElement.url, true);
    def linkActionConditionPropertyName = "linkActionCondition"+uiElement.id+ "Condition";
    println com.ifountain.rui.util.DesignerTemplateUtils.declareVariable(linkActionConditionPropertyName, uiElement.condition, true);
%>
<rui:action id="${uiElement.name}" type="link" url="\${${urlVisiblePropertyName}}" target="${uiElement.target}" ${uiElement.condition != ""?"condition=\"\$"+linkActionConditionPropertyName+"\"":""}
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