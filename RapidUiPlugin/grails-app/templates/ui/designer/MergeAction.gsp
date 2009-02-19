<%
    def compNameString = "'"+uiElement.components.name.join("','")+"'";
    if(uiElement.components.isEmpty())
    {
        compNameString = "";
    }
    compNameString = "\${["+compNameString+"]}";
%>
<rui:action id="${uiElement.name}" type="merge" url="../${uiElement.url}" components="${compNameString}" ${uiElement.removeAttribute != ""? "removeAttribute='"+uiElement.removeAttribute+"'":""} ${uiElement.condition != ""?"condition='"+uiElement.condition+"'":""}
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
    %>
    <rui:requestParam key="${parameter.key}" value="${parameter.value}"></rui:requestParam>
    <%
        }
    %>
</rui:action>