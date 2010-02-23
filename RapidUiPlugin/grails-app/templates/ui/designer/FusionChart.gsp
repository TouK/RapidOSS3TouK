<rui:fusionChart id="${uiElement.name}" url="../${uiElement.url}" timeout="${uiElement.timeout}"
        title="${uiElement.title}" pollingInterval="${uiElement.pollingInterval}" type="${uiElement.type}"
<%
        uiElement.getActionTrigers().each{eventName, actionTriggers->
    def actionString = uiElement.getActionsString(actionTriggers);
%>
    on${eventName.substring(0,1).toUpperCase()}${eventName.substring(1)}="${actionString}"
    <%
    }
    %>
>
</rui:fusionChart>