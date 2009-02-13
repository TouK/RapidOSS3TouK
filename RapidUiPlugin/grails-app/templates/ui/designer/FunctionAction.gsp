<rui:action id="${uiElement.name}" type="function" function="${uiElement.function}" ${uiElement.component != null?"componentId='"+uiElement.component.name+"'":""} ${uiElement.condition != ""?"condition='"+uiElement.condition+"'":""}>
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
    <rui:functionArg>${functionArgument.value}</rui:functionArg>
    <%
            }
        }
    %>
</rui:action>