<%
    def compNameString = "'"+uiElement.components.name.join("','")+"'";
    if(uiElement.components.isEmpty())
    {
        compNameString = "";
    }
    compNameString = "\${["+compNameString+"]}";
%>
<rui:action id="${uiElement.name}" type="request" url="../${uiElement.url}" components="${compNameString}" ${uiElement.condition != ""?"condition='"+uiElement.condition+"'":""}>
    <%
        uiElement.parameters.each{parameter->
    %>
    <rui:requestParam key="${parameter.key}" value="${parameter.value}"></rui:requestParam>
    <%
        }
    %>
</rui:action>