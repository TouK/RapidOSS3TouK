<%
    def actionsNameString = "'"+uiElement.actions.name.join("','")+"'";
    if(uiElement.actions.isEmpty())
    {
        actionsNameString = "";
    }
    actionsNameString = "\${["+actionsNameString+"]}";
%>
<rui:action id="${uiElement.name}" type="combined" actions="${actionsNameString}" ${uiElement.condition != ""?"condition='"+uiElement.condition+"'":""}>
</rui:action>