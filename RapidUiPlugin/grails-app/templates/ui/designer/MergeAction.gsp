<%
    def compNameString = "'"+uiElement.actions.name.join("','")+"'";
    if(uiElement.actions.isEmpty())
    {
        compNameString = "";
    }
    compNameString = "\${["+compNameString+"]}";
%>
<rui:action id="${uiElement.name}" type="merge" url="../${uiElement.url}" components="${compNameString}" ${uiElement.removeAttribute != ""? "removeAttribute='"+uiElement.removeAttribute+"'":""} ${uiElement.condition != ""?"condition='"+uiElement.condition+"'":""}>
    <%
        uiElement.parameters.each{parameter->
    %>
    <rui:requestParam key="${parameter.key}" value="${parameter.value}"></rui:requestParam>
    <%
        }
    %>
</rui:action>