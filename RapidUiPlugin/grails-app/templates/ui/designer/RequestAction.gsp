<rui:action id="${uiElement.name}" type="request" url="${uiElement.url}" components="${uiElement.components.name}" ${uiElement.condition != ""?"condition='"+uiElement.condition+"'":""}>
    <%
        uiElement.parameters.each{parameter->
    %>
    <rui:requestParam key="${parameter.key}" value="${parameter.value}"></rui:requestParam>
    <%
        }
    %>
</rui:action>