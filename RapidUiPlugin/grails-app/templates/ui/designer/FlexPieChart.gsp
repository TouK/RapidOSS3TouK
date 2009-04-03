
<rui:flexPieChart id="${uiElement.name}" url="../${uiElement.url}" rootTag="${uiElement.rootTag}"  timeout="${uiElement.timeout}"
        swfURL="../images/rapidjs/component/chart/PieChart.swf" title="${uiElement.title}" pollingInterval="${uiElement.pollingInterval}"
<%
    uiElement.getActionTrigers().each{eventName, actionTriggers->
         def actionString = uiElement.getActionsString(actionTriggers);
    %>
        on${eventName.substring(0,1).toUpperCase()}${eventName.substring(1)}="${actionString}"
    <%
    }
    %>
>
</rui:flexPieChart>