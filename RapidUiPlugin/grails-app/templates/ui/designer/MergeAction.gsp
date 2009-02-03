<rui:action id="${uiElement.name}" type="merge" url="${uiElement.url}" components="${uiElement.components.name}" ${uiElement.removeAttribute != ""? "removeAttribute='"+uiElement.removeAttribute+"'":""} ${uiElement.condition != ""?"condition='"+uiElement.condition+"'":""}>
    <%
        uiElement.parameters.each{parameter->
    %>
    <rui:requestParam key="${parameter.key}" value="${parameter.value}"></rui:requestParam>
    <%
        }
    %>
</rui:action>