<%
    def compNameString = "'"+uiElement.components.name.join("','")+"'";
    if(uiElement.components.isEmpty())
    {
        compNameString = "";
    }
    compNameString = "\${["+compNameString+"]}";
%>
<rui:action id="${uiElement.name.encodeAsHTML()}" type="request" url="../${uiElement.url.encodeAsHTML()}" components="${compNameString}" ${uiElement.condition != ""?"condition=\""+uiElement.condition.encodeAsHTML()+"\"":""}
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
        uiElement.parameters.each{parameter->
    %>
    <rui:requestParam key="${parameter.key.encodeAsHTML()}" value="${parameter.value.encodeAsHTML()}"></rui:requestParam>
    <%
        }
    %>
</rui:action>