
<rui:flexPieChart id="${uiElement.name.encodeAsHTML()}" url="../${uiElement.url.encodeAsHTML()}" rootTag="${uiElement.rootTag.encodeAsHTML()}"
        swfURL="../images/rapidjs/component/chart/PieChart.swf" title="${uiElement.title.encodeAsHTML()}"
<%
    uiElement.getActionTrigers().each{eventName, actionTriggers->
         def actionString = uiElement.getActionsString(actionTriggers);
    %>
        on${eventName.substring(0,1).toUpperCase().encodeAsHTML()}${eventName.substring(1).encodeAsHTML()}="${actionString}"
    <%
    }
    %>
>
</rui:flexPieChart>