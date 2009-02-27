
<rui:flexPieChart id="${uiElement.name}" url="../${uiElement.url}" rootTag="${uiElement.rootTag}"
        swfURL="../images/rapidjs/component/chart/PieChart.swf" title="${uiElement.title}"
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