<rui:action id="${uiElement.name}" type="function" function="${uiElement.function}" ${uiElement.component != null?"componentId='"+uiElement.component.name+"'":""} ${uiElement.condition != ""?"condition='"+uiElement.condition+"'":""}>
    <%
        uiElement.arguments.each{functionArgument->
            if(functionArgument.value != "")
            {
    %>
        <rui:functionArg>${functionArgument.value}</rui:functionArg>
    <%
            }
        }
    %>
</rui:action>